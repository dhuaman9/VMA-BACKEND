package pe.gob.sunass.vma.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import pe.gob.sunass.vma.assembler.UsuarioAssembler;
import pe.gob.sunass.vma.constants.Constants;
import pe.gob.sunass.vma.dto.CambiarPasswordUsuarioDTO;
import pe.gob.sunass.vma.dto.CambioPasswordDTO;
import pe.gob.sunass.vma.dto.RecuperarPasswordDTO;
import pe.gob.sunass.vma.dto.UsuarioDTO;
import pe.gob.sunass.vma.exception.FailledValidationException;
import pe.gob.sunass.vma.model.Empresa;
import pe.gob.sunass.vma.model.Role;
import pe.gob.sunass.vma.model.TokenPassword;
import pe.gob.sunass.vma.model.Usuario;
import pe.gob.sunass.vma.repository.EmpresaRepository;
import pe.gob.sunass.vma.repository.RoleRepository;
import pe.gob.sunass.vma.repository.UsuarioRepository;
import pe.gob.sunass.vma.util.LdapConnProperties;
import pe.gob.sunass.vma.util.LdapUtil;
import pe.gob.sunass.vma.util.UserUtil;

@Service
public class UsuarioService {

	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(UsuarioService.class);

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private EmpresaRepository empresaRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private LdapConnProperties propiedadesLdap;

	@Autowired
	private UserUtil userUtil;

	@Autowired
	LdapUtil ldapUtil;

//	@Autowired
//	private EmailService emailService;

	@Autowired
	private TokenPasswordService tokenPasswordService;

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public List<UsuarioDTO> findAll() throws Exception {
		List<Usuario> listUsuarios = this.usuarioRepository.findAllByOrderById();
		List<UsuarioDTO> listDTO = UsuarioAssembler.buildDtoDomainCollection(listUsuarios);
		return listDTO;
	}

	public Page<UsuarioDTO> findAllPageable(Integer page, Integer size, String search) {
		PageRequest pageRequest = PageRequest.of(page, size);
		Page<Usuario> usuariosByName = usuarioRepository.findUsuariosByName(search, pageRequest,
				userUtil.getCurrentUserId());

		return usuariosByName.map(UsuarioAssembler::buildDtoDomain);
	}

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public Page<UsuarioDTO> findAll(Pageable pageable) throws Exception {
		Page<Usuario> usuarioPage = this.usuarioRepository.findAllByOrderById(pageable);
		Page<UsuarioDTO> pageDTO = UsuarioAssembler.buildDtoDomainCollection(usuarioPage);
		return pageDTO;
	}

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public UsuarioDTO findById(Integer id) throws Exception {
		UsuarioDTO dto = null;
		Optional<Usuario> opt = this.usuarioRepository.findById(id);

		if (opt.isPresent()) {
			Usuario user = opt.get();
			dto = UsuarioAssembler.buildDtoDomain(user);

		}

		return dto;
	}

	// logear por username
	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public UsuarioDTO findByUserName(String username) throws Exception {

		UsuarioDTO dto = null;
		Optional<Usuario> opt = this.usuarioRepository.findByUserName(username);

		if (opt.isPresent()) {
			Usuario user = opt.get();
			dto = UsuarioAssembler.buildDtoDomain(user);
		}
		return dto;
	}

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public UsuarioDTO registrar(UsuarioDTO dto) throws Exception {

		List<Usuario> list = this.usuarioRepository.findByUserNameAndEstado(dto.getUserName(), true);
		if (list != null && list.size() > 0) {
			logger.info("FailledValidationException : "+ "El Usuario ya esta registrado");
			throw new FailledValidationException("El Usuario ya esta registrado.");
			
		}
		
		Optional<Role> optRole = this.roleRepository.findById(dto.getRole().getIdRole());

		Usuario usuario = new Usuario();

		if (dto.getTipo().equals(Constants.EMPRESA_SUNASS)) {
			Optional<Empresa> optEmpresa1 = this.empresaRepository.findEmpresaByName(Constants.EMPRESA_SUNASS);

			usuario.setTipo(Constants.EMPRESA_SUNASS);
			usuario.setRole(optRole.get());
			usuario.setNombres(Objects.nonNull(dto.getNombres()) ? dto.getNombres().toUpperCase() : null);
			usuario.setApellidos(Objects.nonNull(dto.getApellidos()) ? dto.getApellidos().toUpperCase() : null);
			usuario.setUserName(dto.getUserName().toLowerCase());
			usuario.setPassword("");// no se registra, sino se valida por el AD

			usuario.setUnidadOrganica(dto.getUnidadOrganica()); // se obtiene del AD
			usuario.setCorreo(dto.getCorreo()); // se obtiene del AD

			usuario.setEmpresa(optEmpresa1.get());

			usuario.setTelefono(dto.getTelefono());
			usuario.setEstado(true);
			usuario.setCreatedAt(new Date()); // auditoria
			usuario.setUpdatedAt(null); // auditoria

		} else if (dto.getTipo().equals(Constants.EMPRESA_EPS)) {

			Optional<Empresa> optEmpresa2 = this.empresaRepository.findByIdEmpresa(dto.getEmpresa().getIdEmpresa());

			usuario.setTipo(dto.getTipo());
			usuario.setRole(optRole.get());
			usuario.setNombres(Objects.nonNull(dto.getNombres()) ? dto.getNombres().toUpperCase() : null);
			usuario.setApellidos(Objects.nonNull(dto.getApellidos()) ? dto.getApellidos().toUpperCase() : null);
			usuario.setUserName(dto.getUserName().toLowerCase());
			usuario.setPassword(passwordEncoder.encode(dto.getPassword())); // el password se encriptara con el BCryptPasswordEncoder
			usuario.setUnidadOrganica("");
			usuario.setCorreo(dto.getCorreo());
			usuario.setEmpresa(optEmpresa2.get());
			usuario.setTelefono(dto.getTelefono());
			usuario.setEstado(Boolean.TRUE);
			usuario.setCreatedAt(new Date());
			usuario.setUpdatedAt(null);
			usuario.setPasswordCambiado(false);

		}

		usuario = this.usuarioRepository.save(usuario);

		// Se genera token y se envía correo para cambiar contraseña

		if (usuario.getTipo().equals("EPS")) {
			// String token = tokenPasswordService.crearToken(usuario);
			tokenPasswordService.crearToken(usuario);
//			emailService.sendEmail(dto, token);  // pendiente, no se necesita enviar correo, pero si crear token, para q tenga 5 dias habiles en cambiar su clave.
		}

		return UsuarioAssembler.buildDtoDomain(usuario);

	}

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public UsuarioDTO update(UsuarioDTO dto) throws Exception {
		if (dto == null) {
			throw new FailledValidationException("Los datos son obligatorios");
		}

		Usuario usuario = null;
		Optional<Usuario> optUser = this.usuarioRepository.findById(dto.getId());

		if (optUser.isPresent()) {
			usuario = optUser.get();

			if (dto.getUserName() != null && !dto.getUserName().isEmpty()) {
				if (!dto.getUserName().equals(usuario.getUserName())) {
					List<Usuario> list = this.usuarioRepository.findByUserNameAndIdNotAndEstado(
							dto.getUserName().toLowerCase(), dto.getId(), new Boolean(true));

					if (list != null && list.size() > 0) {
						throw new FailledValidationException("El usuario ya esta registrado."); // para validar y mostrarlo  en el  front
					}
					usuario.setUserName(dto.getUserName().toLowerCase());
				}
			}
			// si se necesita enviar correos, usar esto :
			/*
			 * if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
			 * 
			 * // si se necesita enviar correos, usar esto :
			 * if(usuario.getTipo().equals("EPS")) { String token =
			 * tokenPasswordService.crearToken(usuario); emailService.sendEmail(dto, token);
			 * } //.....
			 * 
			 * }
			 */

			if (dto.getTipo() != null && !dto.getTipo().isEmpty()) {
				if (!dto.getTipo().equals(usuario.getTipo())) {
					usuario.setTipo(dto.getTipo());
				}
			}
			if (dto.getEmpresa() != null && dto.getEmpresa().getIdEmpresa() != null) {

				Optional<Empresa> optEmpresa = this.empresaRepository.findById(dto.getEmpresa().getIdEmpresa());

				if (!optEmpresa.isPresent()) {
					throw new Exception(" la empresa no existe");
				}
				usuario.setEmpresa(optEmpresa.get());

			}
			if (dto.getNombres() != null && !dto.getNombres().isEmpty()) {
				if (!dto.getNombres().equals(usuario.getNombres())) {
					usuario.setNombres(dto.getNombres().toUpperCase());
				}
			}
			if (dto.getApellidos() != null && !dto.getApellidos().isEmpty()) {
				if (!dto.getApellidos().equals(usuario.getApellidos())) {
					usuario.setApellidos(dto.getApellidos().toUpperCase());
				}
			}
			if (dto.getCorreo() != null && !dto.getCorreo().isEmpty()) {
				if (!dto.getCorreo().equals(usuario.getCorreo())) {
					usuario.setCorreo(dto.getCorreo());
				}
			}
			if (dto.getTelefono() != null && !dto.getTelefono().isEmpty()) {
				if (!dto.getTelefono().equals(usuario.getTelefono())) {
					usuario.setTelefono(dto.getTelefono());
				}
			}

			// Se agrego este fragmento de codigo para actualizar el campo estado
			if (dto.getEstado() != null) {
				if (dto.getEstado() != usuario.getEstado()) {
					usuario.setEstado(dto.getEstado());
				}
			}

			if (dto.getRole() != null && dto.getRole().getIdRole() != null) {
				if (dto.getRole().getIdRole().intValue() != usuario.getRole().getIdRol().intValue()) {
					Optional<Role> optRole = this.roleRepository.findById(dto.getRole().getIdRole());

					if (!optRole.isPresent()) {
						throw new Exception(" el  rol no existe");
					}
					usuario.setRole(optRole.get());
				}
			}
			usuario.setUpdatedAt(new Date());
			usuario = this.usuarioRepository.save(usuario);
		}

		return UsuarioAssembler.buildDtoDomain(usuario);
	}

	public Optional<Usuario> findByUserNameLDAP(String userName, String password, String passwordEncode) {

		Usuario item = null;

		try {

			if (!userName.trim().equals("") && !password.trim().equals("")) {

				DirContext context = this.validarWithOutSimpleAuthenticacion(userName, password);

				if (context != null) {

					Optional<Usuario> tmpItem = this.usuarioRepository.findByUserName(userName);

					if (tmpItem.isPresent()) {

						item = tmpItem.get();
						item.setPassword(passwordEncode);
					}
				} else {
					Optional<Usuario> tmpItem = this.usuarioRepository.findByUserName(userName);

					if (tmpItem.isPresent()) {

						item = tmpItem.get();
					}
				}
			}
		} catch (Exception ex) {
			item = null;
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
		}

		Optional<Usuario> opt = Optional.ofNullable(item);

		return opt;
	}

	private DirContext validarWithOutSimpleAuthenticacion(String userName, String password) throws Exception {
		DirContext context = null;

		try {
			context = ldapUtil.validarWithOutSimpleAuthenticacion(userName, password);
			return context;

		} catch (Exception ex) {
			throw ex;
		} finally {

			if (context != null) {
				context.close();
			} else {
				return null;
			}
		}
	}

	// pendiente de usar
	/*
	 * public List<UsuarioLdap> obtenerUsuariosLdap2() { try {
	 * 
	 * List<UsuarioLdap> usuarios = ldapUtil.listarTodosUsuariosLDAP(); return
	 * usuarios; } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * }
	 */

	public List<UsuarioDTO> obtenerUsuariosLdap() {

		try {
			List<UsuarioDTO> listaUsuario = new ArrayList<>();
			DirContext ldapContext = conectarLDAP();
			SearchControls searchCtls = new SearchControls();
			String returnedAtts[] = { Constants.AD.APELLIDOS, Constants.AD.NOMBRES, Constants.AD.CORREO,
					Constants.AD.AREA, Constants.AD.USERNAME };
			searchCtls.setReturningAttributes(returnedAtts);
			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String searchFilter = "(&(objectClass=user))";

			String searchBaseLima = "OU=Lima,OU=Usuarios,DC=sunass,DC=gob,DC=pe";
			String searchBaseProvincia = "OU=Provincias,OU=Usuarios,DC=sunass,DC=gob,DC=pe";

			NamingEnumeration<SearchResult> answerLima = ldapContext.search(searchBaseLima, searchFilter, searchCtls);
			NamingEnumeration<SearchResult> answerProvincia = ldapContext.search(searchBaseProvincia, searchFilter,
					searchCtls);

			while (answerLima.hasMoreElements()) {
				SearchResult sr = (SearchResult) answerLima.next();
				Attributes attrs = sr.getAttributes();
				UsuarioDTO userLdap = new UsuarioDTO();
				userLdap.setCorreo(
						(String) (attrs.get(Constants.AD.CORREO) == null ? "" : attrs.get(Constants.AD.CORREO).get()));
				userLdap.setNombres(((String) (attrs.get(Constants.AD.NOMBRES) == null ? ""
						: attrs.get(Constants.AD.NOMBRES).get())).toUpperCase().trim());
				userLdap.setApellidos(((String) (attrs.get(Constants.AD.APELLIDOS) == null ? ""
						: attrs.get(Constants.AD.APELLIDOS).get())).toUpperCase().trim());
				userLdap.setUnidadOrganica(
						(String) (attrs.get(Constants.AD.AREA) == null ? "" : attrs.get(Constants.AD.AREA).get()));
				userLdap.setUserName((String) (attrs.get(Constants.AD.USERNAME) == null ? ""
						: attrs.get(Constants.AD.USERNAME).get()));

				listaUsuario.add(userLdap);
			}

			while (answerProvincia.hasMoreElements()) {
				SearchResult sr = (SearchResult) answerProvincia.next();
				Attributes attrs = sr.getAttributes();
				UsuarioDTO userLdap = new UsuarioDTO();
				userLdap.setCorreo((String) (attrs.get("mail") == null ? "" : attrs.get("mail").get()));
				userLdap.setNombres(((String) (attrs.get("givenName") == null ? "" : attrs.get("givenName").get()))
						.toUpperCase().trim());
				userLdap.setApellidos(
						((String) (attrs.get("sn") == null ? "" : attrs.get("sn").get())).toUpperCase().trim());
				userLdap.setUnidadOrganica((String) (attrs.get("physicalDeliveryOfficeName") == null ? ""
						: attrs.get("physicalDeliveryOfficeName").get()));
				userLdap.setUserName(
						(String) (attrs.get("SAMAccountName") == null ? "" : attrs.get("SAMAccountName").get()));

				listaUsuario.add(userLdap);
			}

			ldapContext.close();
			return listaUsuario;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	private DirContext conectarLDAP() throws Exception {

		DirContext dirContext = null;
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, propiedadesLdap.getUrl());
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, propiedadesLdap.getUser() + propiedadesLdap.getPrefix());
		env.put(Context.SECURITY_CREDENTIALS, propiedadesLdap.getPassword());
		env.put("com.sun.jndi.ldap.connect.timeout", propiedadesLdap.getTimeout());

		dirContext = new InitialDirContext(env);

		return dirContext;
	}

	// metodo para cambiar de contraseña, para usuarios tipo EPS, indistinto del rol  que tengan.
	public void cambiarPassword(String username, CambioPasswordDTO cambioPasswordDTO) {

		Usuario usuario = usuarioRepository.findByUserName(username)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		// Verificar la contraseña actual
		if (!passwordEncoder.matches(cambioPasswordDTO.getPasswordActual(), usuario.getPassword())) {
			throw new FailledValidationException("La contraseña actual es incorrecta");
		}

		// Verificar que la nueva contraseña y la confirmación coincidan
		if (!cambioPasswordDTO.getNuevaPassword().equals(cambioPasswordDTO.getConfirmarPassword())) {
			throw new FailledValidationException("La nueva contraseña y la confirmación no coinciden");
		}

		// Cambiar la contraseña
		usuario.setPassword(passwordEncoder.encode(cambioPasswordDTO.getNuevaPassword()));
		usuarioRepository.save(usuario);
	}

	public void cambiarPasswordUsuario(CambiarPasswordUsuarioDTO dto) throws Exception {
		Usuario usuario = usuarioRepository.findByUserName(dto.getUsername())
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		usuario.setPassword(passwordEncoder.encode(dto.getNuevaPassword()));
		usuario.setPasswordCambiado(false);
		usuarioRepository.save(usuario);
		tokenPasswordService.actualizarTiempoToken(usuario.getId());
		
	}

	@org.springframework.transaction.annotation.Transactional
	public void recuperarPassword(String token, RecuperarPasswordDTO dto) {
		TokenPassword tokenDB = tokenPasswordService.findTokenByToken(token);

		Usuario usuario = tokenDB.getUsuario();
		usuario.setPassword(passwordEncoder.encode(dto.getNuevaPassword()));
		usuario.setPasswordCambiado(true);
		usuarioRepository.save(usuario);
		tokenDB.setCompletado(true);
		tokenPasswordService.save(tokenDB);
	}

	@org.springframework.transaction.annotation.Transactional
	public void actualizarTokenPasswordUsuario(Integer userId) throws Exception {
		Usuario user = usuarioRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		tokenPasswordService.actualizarTiempoToken(user.getId());

		user.setPasswordCambiado(false);
		usuarioRepository.save(user);

	}

}
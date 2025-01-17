package pe.gob.sunass.vma.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import pe.gob.sunass.vma.assembler.RegistroVMAAssembler;
import pe.gob.sunass.vma.constants.Constants;
import pe.gob.sunass.vma.dto.ArchivoDTO;
import pe.gob.sunass.vma.dto.RegistroVMADTO;
import pe.gob.sunass.vma.dto.RegistroVMAFilterDTO;
import pe.gob.sunass.vma.dto.RegistroVMARequest;
import pe.gob.sunass.vma.dto.RespuestaDTO;
import pe.gob.sunass.vma.dto.anexos.AnexoCostoTotalMuestrasInopinadasDTO;
import pe.gob.sunass.vma.dto.anexos.AnexoCostoTotalUNDDTO;
import pe.gob.sunass.vma.dto.anexos.AnexoCostoTotalesIncurridosDTO;
import pe.gob.sunass.vma.dto.anexos.AnexoEvaluacionVmaAnexo1DTO;
import pe.gob.sunass.vma.dto.anexos.AnexoEvaluacionVmaAnexo2DTO;
import pe.gob.sunass.vma.dto.anexos.AnexoIngresosImplVmaDTO;
import pe.gob.sunass.vma.dto.anexos.AnexoPorcentajeMuestraInopinadaDTO;
import pe.gob.sunass.vma.dto.anexos.AnexoReclamosVMADTO;
import pe.gob.sunass.vma.dto.anexos.AnexoRegistroVmaDTO;
import pe.gob.sunass.vma.dto.anexos.AnexoRespuestaSiDTO;
import pe.gob.sunass.vma.dto.anexos.AnexoTresListadoEPDTO;
import pe.gob.sunass.vma.exception.ForbiddenException;
import pe.gob.sunass.vma.exception.ResourceNotFoundException;
import pe.gob.sunass.vma.model.Empresa;
import pe.gob.sunass.vma.model.FichaRegistro;
import pe.gob.sunass.vma.model.TipoEmpresa;
import pe.gob.sunass.vma.model.Usuario;
import pe.gob.sunass.vma.model.cuestionario.RegistroVMA;
import pe.gob.sunass.vma.model.cuestionario.RespuestaVMA;
import pe.gob.sunass.vma.repository.EmpresaRepository;
import pe.gob.sunass.vma.repository.FichaRepository;
import pe.gob.sunass.vma.repository.RegistroVMARepository;
import pe.gob.sunass.vma.repository.RegistroVMARepositoryCustom;
import pe.gob.sunass.vma.repository.RespuestaVMARepository;
import pe.gob.sunass.vma.repository.UsuarioRepository;
import pe.gob.sunass.vma.util.PreguntasAlternativasProperties;
import pe.gob.sunass.vma.util.UserUtil;

@Service
public class RegistroVMAService {

	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(UsuarioService.class);

	@Autowired
	private RegistroVMARepository registroVMARepository;

	@Autowired
	private RespuestaVMARepository respuestaVMARepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private FichaRepository fichaRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private AlfrescoService alfrescoService;

	@Autowired
	private RegistroVMARepositoryCustom registroVMARepositorycustom;

	@Autowired
	private EmpresaRepository empresaRepository;

	@Autowired
	private UserUtil userUtil;
	
	@Autowired
	private PreguntasAlternativasProperties preguntasAlternativasVMA;
	
	@Autowired
	private EmailService emailService;

	/*
	 * @Transactional(Transactional.TxType.REQUIRES_NEW) public List<RegistroVMADTO>
	 * findAllOrderById(String username) throws Exception { Usuario usuario =
	 * usuarioRepository.findByUserName(username).orElseThrow(); List<RegistroVMA>
	 * listRegistroVMA = null; if (usuario.getRole().getIdRol() ==
	 * Constants.Security.Roles.ID_AdministradorDF || (usuario.getRole().getIdRol()
	 * == Constants.Security.Roles.ID_Consultor &&
	 * usuario.getTipo().equals(Constants.EMPRESA_SUNASS))) { listRegistroVMA =
	 * this.registroVMARepository.findAllByOrderByIdRegistroVma();
	 * logger.info("usuario.getTipo() - "+usuario.getTipo()); } else {
	 * listRegistroVMA =
	 * this.registroVMARepository.registrosPorIdEmpresa(usuario.getEmpresa().
	 * getIdEmpresa()); logger.info("usuario.getTipo() - "+usuario.getTipo()); }
	 * 
	 * List<RegistroVMADTO> listRegistroVMADTO =
	 * RegistroVMAAssembler.buildDtoDomainCollection(listRegistroVMA); return
	 * listRegistroVMADTO; }
	 */

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public RegistroVMADTO findById(Integer id) throws Exception {
		RegistroVMADTO dto = null;
		Usuario usuario = usuarioRepository.findByUserName(userUtil.getCurrentUsername()).orElseThrow();
		Optional<RegistroVMA> opt = null;
		if (usuario.getRole().getIdRol() == 2
				|| (usuario.getRole().getIdRol() == 4 && usuario.getTipo().equals(Constants.EMPRESA_SUNASS))) { // rol2  es  admin  DF y  rol 4  es  consultor
			opt = this.registroVMARepository.findById(id);
		} else {
			opt = this.registroVMARepository.findByIdItem(id, userUtil.getCurrentUserIdEmpresa());
		}

		if (opt.isPresent()) {
			RegistroVMA registroVMA = opt.get();
			dto = RegistroVMAAssembler.buildDtoModel(registroVMA);
			return dto;
		} else {
			logger.info("No esta permitido ver otro ID vma");
			throw new ForbiddenException("Permiso denegado , no tiene acceso a otro registro  ");
		}
		// return dto;
	}

	@Transactional
	public Integer saveRegistroVMA(Integer idRegistroVMA, RegistroVMARequest registroRequest, String username) throws MessagingException, IOException {
		RegistroVMA registroVMA;
		Integer currentUserId = userUtil.getCurrentUserId();

		if (idRegistroVMA != null) {
			registroVMA = registroVMARepository.findById(idRegistroVMA).orElseThrow();
			registroVMA.setUpdatedAt(new Date());
			registroVMA.setEstado(
					registroRequest.isRegistroValido() ? Constants.ESTADO_COMPLETO : Constants.ESTADO_INCOMPLETO);
			saveRespuestas(registroRequest.getRespuestas(), registroVMA);
			registroVMA.setIdUsuarioActualizacion(currentUserId);
			registroVMA.setUpdatedAt(new Date());
			agregarDatosUsuarioSiEsVMACompleto(registroVMA, registroRequest);
			emailService.sendEmail(registroVMA); //se envia correo
			return registroVMA.getIdRegistroVma();
		} else {
			Usuario usuario = usuarioRepository.findByUserName(username).orElseThrow();
			RegistroVMA nuevoRegistro = new RegistroVMA();
			Empresa empresa = new Empresa();
			empresa.setIdEmpresa(registroRequest.getIdEmpresa());
			nuevoRegistro.setEmpresa(usuario.getEmpresa());
			nuevoRegistro.setUsername(username);
			nuevoRegistro.setEstado(
					registroRequest.isRegistroValido() ? Constants.ESTADO_COMPLETO : Constants.ESTADO_INCOMPLETO);
			nuevoRegistro.setFichaRegistro(fichaRepository.findFichaRegistroActual());
			nuevoRegistro.setCreatedAt(new Date());
			nuevoRegistro.setIdUsuarioRegistro(currentUserId);
			agregarDatosUsuarioSiEsVMACompleto(nuevoRegistro, registroRequest);
			RegistroVMA registroDB = registroVMARepository.save(nuevoRegistro);
			saveRespuestas(registroRequest.getRespuestas(), registroDB);
			emailService.sendEmail(nuevoRegistro); //se envia correo
			return registroDB.getIdRegistroVma();
		}
	}

	private void agregarDatosUsuarioSiEsVMACompleto(RegistroVMA registroVMA, RegistroVMARequest registroRequest) {
		if (registroRequest.isRegistroValido() && registroRequest.getDatosUsuarioRegistradorDto() != null) {
			registroVMA.setNombreCompleto(registroRequest.getDatosUsuarioRegistradorDto().getNombreCompleto());
			registroVMA.setEmail(registroRequest.getDatosUsuarioRegistradorDto().getEmail());
			registroVMA.setTelefono(registroRequest.getDatosUsuarioRegistradorDto().getTelefono());
		}
	}

	@Transactional
	public void saveRespuestaVMAArchivo(MultipartFile file, Integer registroVMAId, Integer preguntaId,
			Integer respuestaId) throws IOException {

		RegistroVMA optRegistroVMA = this.registroVMARepository.findByIdRegistroVma(registroVMAId)
				.orElseThrow(() -> new ResourceNotFoundException("Registro VMA no encontrado"));
		Integer currentUserId = userUtil.getCurrentUserId();
		ArchivoDTO archivoDTO = alfrescoService.uploadFile(file, optRegistroVMA, respuestaId);

		if (Objects.nonNull(respuestaId)) {

			Optional<RespuestaVMA> respuestaOpt = respuestaVMARepository.findById(respuestaId);

			if (respuestaOpt.isPresent()) {
				RespuestaVMA respuestaVMA = respuestaOpt.get();

				alfrescoService.deleteFile(respuestaVMA.getRespuesta());

				respuestaVMA.setRespuesta(archivoDTO.getNombreArchivo());
				respuestaVMA.setIdUsuarioActualizacion(currentUserId);
				respuestaVMA.setFechaActualizacion(new Date());
				respuestaVMARepository.save(respuestaVMA);
			} else {
				registrarNuevaRespuetaArchivo(registroVMAId, respuestaId, archivoDTO, preguntaId);
			}
		} else {
			registrarNuevaRespuetaArchivo(registroVMAId, null, archivoDTO, preguntaId);
		}

		optRegistroVMA.setIdUsuarioActualizacion(currentUserId);
		optRegistroVMA.setUpdatedAt(new Date());
		registroVMARepository.save(optRegistroVMA);
	}

	private void registrarNuevaRespuetaArchivo(Integer registroVMAId, Integer respuestaId, ArchivoDTO archivoDTO,
			Integer preguntaId) {
		RegistroVMA registroVMA = new RegistroVMA();
		registroVMA.setIdRegistroVma(registroVMAId);
		RespuestaVMA respuestaVMA = new RespuestaVMA(respuestaId, null, archivoDTO.getIdAlfresco(), registroVMA,
				preguntaId);
		respuestaVMA.setFechaRegistro(new Date());
		respuestaVMA.setIdUsuarioRegistro(userUtil.getCurrentUserId());
		respuestaVMARepository.save(respuestaVMA);
	}

	/**
	 * Realiza la consulta paginada de Registros VMA
	 * 
	 * @param empresaId
	 * @param estado
	 * @param startDate
	 * @param endDate
	 * @param year
	 * @param username
	 * @param page
	 * @param pageSize
	 * @param search
	 * @return
	 * 
	 * en el futuro, si DF solicita el campo estado en Empresas, considerarlo en el listado de registrosVMA, para excluir a EPS inactivas y con estado vma = SIN REGISTRO
	 */
	public Page<RegistroVMA> searchRegistroVMA(Integer empresaId, String estado, Date startDate, Date endDate,
			String year, String username, int page, int pageSize, String search) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);

		Root<Empresa> empresaRoot = query.from(Empresa.class);
		Root<FichaRegistro> fichaRegistro = query.from(FichaRegistro.class);

		Join<Empresa, RegistroVMA> registroVMA = empresaRoot.join("registrosVMA", JoinType.LEFT);
		registroVMA.on(cb.equal(registroVMA.get("fichaRegistro").get("idFichaRegistro"),
				fichaRegistro.get("idFichaRegistro")));

		// Realizar un join con la entidad TipoEmpresa
		Join<Empresa, TipoEmpresa> tipoEmpresaJoin = empresaRoot.join("tipoEmpresa", JoinType.LEFT);
		
		query.multiselect(empresaRoot.get("idEmpresa"), empresaRoot.get("nombre"), empresaRoot.get("regimen"),
				tipoEmpresaJoin.get("nombre"), registroVMA.get("idRegistroVma"), registroVMA.get("estado"),
				registroVMA.get("createdAt"), fichaRegistro.get("anio"), fichaRegistro.get("fechaInicio"), fichaRegistro.get("fechaFin"));

		Usuario usuario = usuarioRepository.findByUserName(username).orElseThrow();
		Pageable pageable = PageRequest.of(page, pageSize);

		if (usuario.getRole().getIdRol() == 2
				|| (usuario.getRole().getIdRol() == 4 && usuario.getTipo().equals(Constants.EMPRESA_SUNASS))) {
			List<Predicate> predicates = getPredicatesSearch(cb, empresaRoot, fichaRegistro, registroVMA, empresaId,
					estado, startDate, endDate, year, username, search);

			query.where(predicates.toArray(new Predicate[0]));
			query.orderBy(cb.desc(cb.coalesce(registroVMA.get("idRegistroVma"), 0)), cb.asc(empresaRoot.get("nombre")),
					cb.desc(fichaRegistro.get("anio")));

			TypedQuery<Object[]> typedQuery = entityManager.createQuery(query);

			// Calcular paginacion
			int firstResult = pageable.getPageNumber() * pageable.getPageSize();
			typedQuery.setFirstResult(firstResult);
			typedQuery.setMaxResults(pageable.getPageSize());
			
			//String nombreTipo = (String) objeto[3];
			// Suponiendo que tienes un servicio para buscar TipoEmpresa por nombre
//			TipoEmpresa tipoEmpresa = empresaRepository.findTipoEmpresaByNombre(nombreTipo);
//			emp.setTipoEmpresa(tipoEmpresa);
			

			List<RegistroVMA> resultList = typedQuery.getResultList().stream().map(objeto -> {
				Empresa emp = new Empresa();
				emp.setIdEmpresa((Integer) objeto[0]);
				emp.setNombre((String) objeto[1]);
				emp.setRegimen((String) objeto[2]);
				//emp.setTipo((String) objeto[3]);
				//emp.setTipoEmpresa((TipoEmpresa) objeto[3]); 
				String nombreTipo = (String) objeto[3];
				TipoEmpresa tipoEmpresa = empresaRepository.findTipoEmpresaByNombre(nombreTipo);
				emp.setTipoEmpresa(tipoEmpresa);
				
				FichaRegistro fReg = new FichaRegistro();
				fReg.setAnio((String) objeto[7]);
				RegistroVMA rVMA = new RegistroVMA();
				rVMA.setIdRegistroVma((Integer) objeto[4]);
				rVMA.setEstado((String) objeto[5]);
				rVMA.setCreatedAt((Date) objeto[6]);
				rVMA.setEmpresa(emp);
				rVMA.setFichaRegistro(fReg);
				return rVMA;
			}).collect(Collectors.toList());

			// Count query
			cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
			Root<Empresa> empresaCountRoot = countQuery.from(Empresa.class);
			Root<FichaRegistro> fichaRegistroCountRoot = countQuery.from(FichaRegistro.class);

			Join<Empresa, RegistroVMA> registroVMACountRoot = empresaCountRoot.join("registrosVMA", JoinType.LEFT);
			registroVMACountRoot.on(cb.equal(registroVMACountRoot.get("fichaRegistro").get("idFichaRegistro"),
					fichaRegistroCountRoot.get("idFichaRegistro")));

			List<Predicate> predicatesCount = getPredicatesSearch(cb, empresaCountRoot, fichaRegistroCountRoot,
					registroVMACountRoot, empresaId, estado, startDate, endDate, year, username, search);

			countQuery.select(cb.count(empresaCountRoot));
			countQuery.where(predicatesCount.toArray(new Predicate[0]));
			Long total = entityManager.createQuery(countQuery).getSingleResult();

			return new PageImpl<>(resultList, pageable, total);
		} else if ((usuario.getRole().getIdRol() == 3 || usuario.getRole().getIdRol() == 4)
				&& usuario.getTipo().equals(Constants.EMPRESA_EPS)) {
			
			List<Predicate> predicates = new ArrayList<>();
			 predicates = getPredicatesSearch(cb, empresaRoot, fichaRegistro, registroVMA, empresaId,
					estado, startDate, endDate, year, username, search);
			
			// Filtro por idEmpresa del usuario
			predicates.add(cb.equal(empresaRoot.get("idEmpresa"), usuario.getEmpresa().getIdEmpresa()));
				
			 query.where(predicates.toArray(new Predicate[0]));
				query.orderBy(cb.desc(cb.coalesce(registroVMA.get("idRegistroVma"), 0)), cb.asc(empresaRoot.get("nombre")),
						cb.desc(fichaRegistro.get("anio")));

				TypedQuery<Object[]> typedQuery = entityManager.createQuery(query);

				// Calcular paginacion
				int firstResult = pageable.getPageNumber() * pageable.getPageSize();
				typedQuery.setFirstResult(firstResult);
				typedQuery.setMaxResults(pageable.getPageSize());

				List<RegistroVMA> resultList = typedQuery.getResultList().stream().map(objeto -> {
					Empresa emp = new Empresa();
					emp.setIdEmpresa((Integer) objeto[0]);
					emp.setNombre((String) objeto[1]);
					emp.setRegimen((String) objeto[2]);
					//emp.setTipo((String) objeto[3]);
					//emp.setTipoEmpresa((TipoEmpresa) objeto[3]); 
					String nombreTipo = (String) objeto[3];
					TipoEmpresa tipoEmpresa = empresaRepository.findTipoEmpresaByNombre(nombreTipo);
					emp.setTipoEmpresa(tipoEmpresa);
					
					FichaRegistro fReg = new FichaRegistro();
					fReg.setAnio((String) objeto[7]);
					fReg.setFechaInicio((LocalDate) objeto[8]);
					fReg.setFechaFin((LocalDate) objeto[9]);
					RegistroVMA rVMA = new RegistroVMA();
					rVMA.setIdRegistroVma((Integer) objeto[4]);
					rVMA.setEstado((String) objeto[5]);
					rVMA.setCreatedAt((Date) objeto[6]);
					rVMA.setEmpresa(emp);
					rVMA.setFichaRegistro(fReg);
					return rVMA;
				}).collect(Collectors.toList());

				// Count query
				cb = entityManager.getCriteriaBuilder();
				CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
				Root<Empresa> empresaCountRoot = countQuery.from(Empresa.class);
				Root<FichaRegistro> fichaRegistroCountRoot = countQuery.from(FichaRegistro.class);

				Join<Empresa, RegistroVMA> registroVMACountRoot = empresaCountRoot.join("registrosVMA", JoinType.LEFT);
				registroVMACountRoot.on(cb.equal(registroVMACountRoot.get("fichaRegistro").get("idFichaRegistro"),
						fichaRegistroCountRoot.get("idFichaRegistro")));

				List<Predicate> predicatesCount = getPredicatesSearch(cb, empresaCountRoot, fichaRegistroCountRoot,
						registroVMACountRoot, empresaId, estado, startDate, endDate, year, username, search);

				countQuery.select(cb.count(empresaCountRoot));
				countQuery.where(predicatesCount.toArray(new Predicate[0]));
				Long total = entityManager.createQuery(countQuery).getSingleResult();

				return new PageImpl<>(resultList, pageable, total);
		}

		// En caso de no cumplir ninguna de las condiciones anteriores para el rol 3 , se devuelve los registros para otros usuarios.
		return this.registroVMARepository.registrosPorIdEmpresa(usuario.getEmpresa().getIdEmpresa(), pageable);

	}

	/**
	 * Establece las condiciones de la consulta paginada realizada en el metodo
	 * searchRegistroVMA
	 *
	 */
	private List<Predicate> getPredicatesSearch(CriteriaBuilder cb, Root empresaRoot, Root fichaRegistro,
			Join registroVMA, Integer empresaId, String estado, Date startDate, Date endDate, String year,
			String username, String search) {

		List<Predicate> predicates = new ArrayList<>();

		// se excluye a SUNASS, tiene idEmpresa = 1
		predicates.add(cb.notEqual(empresaRoot.get("idEmpresa"), 1));

		if (empresaId != null) {
			predicates.add(cb.equal(empresaRoot.get("idEmpresa"), empresaId));
		}
		if (estado != null) {
			if (estado.equals(Constants.ESTADO_SIN_REGISTRO)) {
				predicates.add(cb.isNull(registroVMA.get("estado")));
			} else {
				predicates.add(cb.equal(registroVMA.get("estado"), estado));
			}
		}
		if (startDate != null) {
			predicates.add(cb.greaterThanOrEqualTo(registroVMA.get("createdAt"), startDate));
		}
		if (endDate != null) {
			predicates.add(cb.lessThanOrEqualTo(registroVMA.get("createdAt"), agregarHoraFinDia(endDate)));
		}
		if (year != null) {
			predicates.add(cb.equal(fichaRegistro.get("anio"), year));
		}
		if (search != null) {
			Predicate namePredicate = cb.like(cb.lower(empresaRoot.get("nombre")), "%" + search.toLowerCase() + "%");
			Join<Empresa, TipoEmpresa> tipoEmpresaJoin = empresaRoot.join("tipoEmpresa");
			Predicate typePredicate = cb.like(
			    cb.lower(tipoEmpresaJoin.get("nombre")),
			    "%" + search.toLowerCase() + "%"
			);
			
			//Predicate typePredicate = cb.like(cb.lower(empresaRoot.get("tipo")), "%" + search.toLowerCase() + "%");
			Predicate regimenPredicate = cb.like(cb.lower(empresaRoot.get("regimen")),
					"%" + search.toLowerCase() + "%");
			Predicate estadoPredicate = cb.like(cb.lower(registroVMA.get("estado")), "%" + search.toLowerCase() + "%");
			Predicate anioPredicate = cb.like(cb.lower(fichaRegistro.get("anio")), "%" + search.toLowerCase() + "%");
			//Predicate fechaRegistroPredicate = cb.like(cb.lower(registroVMA.get("createdAt")), "%" + search.toLowerCase() + "%");
			
			//para la busqueda de la fecha de registro vma, segun el campo Buscar:
			
			Predicate fechaRegistroPredicate = filtrarPorFechaRegistro(search,cb,registroVMA,startDate); //CriteriaBuilder cb,Join registroVMA, Date startDate
			// metodo predicate filtrarPorFechaRegistro

			
			predicates.add(cb.or(namePredicate, typePredicate, regimenPredicate, estadoPredicate, anioPredicate, fechaRegistroPredicate));
		}

		return predicates;
	}

//	private void saveRespuestas(List<RespuestaDTO> respuestasRequest, RegistroVMA registro) {
//		respuestaVMARepository.saveAll(respuestasRequest.stream()
//				.map(respuesta -> respuestaDtoToRespuestaVMA(respuesta, registro)).collect(Collectors.toList()));
//	}

	private void saveRespuestas(List<RespuestaDTO> respuestasRequest, RegistroVMA registro) {
		respuestaVMARepository.saveAll(respuestasRequest.stream().map(respuesta -> {
			RespuestaVMA respuestaVMA = respuestaDtoToRespuestaVMA(respuesta, registro);

			respuestaVMA.setFechaRegistro(new Date());
			respuestaVMA.setIdUsuarioRegistro(userUtil.getCurrentUserId());
			// Setear otros campos de auditoria, si es necesario

			return respuestaVMA;
		}).collect(Collectors.toList()));
	}

	private RespuestaVMA respuestaDtoToRespuestaVMA(RespuestaDTO respuestaDTO, RegistroVMA registroVMA) {
		return new RespuestaVMA(respuestaDTO.getIdRespuesta(), respuestaDTO.getIdAlternativa(),
				respuestaDTO.getRespuesta(), registroVMA, respuestaDTO.getIdPregunta());
	}

	public boolean isRegistroCompletado(String username) {
		Usuario usuario = usuarioRepository.findByUserName(username).orElseThrow();

		return registroVMARepository.isRegistroCompletado(usuario.getEmpresa().getIdEmpresa());
	}

	public List<RegistroVMA> filterRegistros(RegistroVMAFilterDTO filterDTO) {
		return registroVMARepositorycustom.findByFilters(filterDTO);
	}

	public Date agregarHoraFinDia(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	/* En adelante se listan los Anexos : */

	// anexo 1
	public List<AnexoRegistroVmaDTO> listaDeAnexosRegistrosVmaDTO(String anhio) {
		List<Empresa> empresasDB = empresaRepository.findAll();

		return empresasDB.stream().filter(empresa -> !Constants.TIPO_EMPRESA_NINGUNO.equals(empresa.getTipoEmpresa().getNombre()))
				.sorted(Comparator.comparing(empresa -> 
		        empresa.getTipoEmpresa() != null ? empresa.getTipoEmpresa().getNombre() : "")) // Ordenar por tipo de empresa
				.map(empresa -> mapToAnexoRegistroVmaDTO(empresa, anhio))
				.collect(Collectors.toList());
	}

	private AnexoRegistroVmaDTO mapToAnexoRegistroVmaDTO(Empresa empresa, String anio) {
		RegistroVMA registroVmaPorAnhio = registroVMARepository.findRegistroVmaPorAnhio(empresa.getIdEmpresa(), anio);
		boolean registroCompleto = Objects.nonNull(registroVmaPorAnhio)
				&& registroVmaPorAnhio.getEstado().equals(Constants.ESTADO_COMPLETO);
		boolean remitioAnexoComplementario = false;
		if (Objects.nonNull(registroVmaPorAnhio)) {
			remitioAnexoComplementario = respuestaVMARepository.isRespuestaArchivoInformacionCompleto(
					preguntasAlternativasVMA.getId_pregunta_remitio_anexo_norma_complementaria(),  //se refiere si se adjunto el Anexo 2.2 de la Norma complementaria VMA.
					registroVmaPorAnhio.getIdRegistroVma());
		}

		return new AnexoRegistroVmaDTO(empresa.getNombre(), empresa.getTipoEmpresa().getNombre(),
				empresa.getRegimen().equals(Constants.Regimen.RAT), registroCompleto,
				registroCompleto && remitioAnexoComplementario);
	}

	// anexo 2
	public List<AnexoRespuestaSiDTO> listaDeAnexosRegistroMarcaronSi(String anhio) {
		
//		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
//				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());
//		
		
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
			    .sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipoEmpresa().getNombre()))
			    .collect(Collectors.toList());

		List<AnexoRespuestaSiDTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {
			RespuestaVMA respuesta = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					preguntasAlternativasVMA.getId_pregunta_si_no(), registroVMA.getIdRegistroVma());
			RespuestaVMA respuestaNroTrabajadores = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					preguntasAlternativasVMA.getId_pregunta_nro_trabajadores_eps(), registroVMA.getIdRegistroVma());

			anexos.add(new AnexoRespuestaSiDTO(registroVMA.getEmpresa().getNombre(), registroVMA.getEmpresa().getTipoEmpresa().getNombre(),
					respuesta.getRespuesta(), Integer.parseInt(respuestaNroTrabajadores.getRespuesta())));
		});

		return anexos;
	}

	/*
	 * anexo 3 : - Relación de las EP que han realizado avances en la inspección y/o
	 * inscripción en sus registros de los UND bajo su ámbito de influencia
	 */

	public List<AnexoTresListadoEPDTO> listaDeAnexosRelacionEPInspeccionados(String anhio) {
		
//		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
//				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());
		
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
			    .sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipoEmpresa().getNombre()))
			    .collect(Collectors.toList());

		List<AnexoTresListadoEPDTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {

			RespuestaVMA UNDidentificados = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					preguntasAlternativasVMA.getId_alternativa_nro_und_identificados_parcial(),
					registroVMA.getIdRegistroVma());

			RespuestaVMA UNDinspeccionados = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					preguntasAlternativasVMA.getId_alternativa_nro_und_inspeccionados_parcial(),
					registroVMA.getIdRegistroVma());

			RespuestaVMA UNDinscritos = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					preguntasAlternativasVMA.getId_alternativa_und_inscritos(), registroVMA.getIdRegistroVma());

			anexos.add(new AnexoTresListadoEPDTO(registroVMA.getEmpresa().getNombre(),
					registroVMA.getEmpresa().getTipoEmpresa().getNombre(), Integer.parseInt(UNDidentificados.getRespuesta()),
					Integer.parseInt(UNDinspeccionados.getRespuesta()), Integer.parseInt(UNDinscritos.getRespuesta())));
		});

		return anexos;
	}

	/* anexo 4 - Detalle de porcentaje de toma de muestra inopinada de las EP */

	public List<AnexoPorcentajeMuestraInopinadaDTO> listaDeAnexosTomaDeMuestrasInopinadas(String anhio) {
//		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
//				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());
		
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
			    .sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipoEmpresa().getNombre()))
			    .collect(Collectors.toList());

		List<AnexoPorcentajeMuestraInopinadaDTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {

			RespuestaVMA UNDinscritos = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					preguntasAlternativasVMA.getId_alternativa_und_inscritos(), registroVMA.getIdRegistroVma());

			RespuestaVMA muestrasInopinadas = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					preguntasAlternativasVMA.getId_pregunta_und_toma_muestra_inopinada(),
					registroVMA.getIdRegistroVma());

			int muestrasInopinadasvalor = Integer.parseInt(muestrasInopinadas.getRespuesta());
			int UNDinscritosvalor = Integer.parseInt(UNDinscritos.getRespuesta());
			double porcentaje = ((double) muestrasInopinadasvalor / (double) UNDinscritosvalor) * 100;

			// Redondea a un decimal usando Math.round
			double porcentajeRedondeado = Math.round(porcentaje * 10.0) / 10.0;

			anexos.add(new AnexoPorcentajeMuestraInopinadaDTO(registroVMA.getEmpresa().getNombre(),
					registroVMA.getEmpresa().getTipoEmpresa().getNombre(), UNDinscritosvalor, muestrasInopinadasvalor,
					porcentajeRedondeado));
		});

		return anexos;
	}

	/*
	 * anexo 5 - Detalle de las EP que han realizado la evaluación de los VMA del
	 * Anexo 1 del reglamento de VMA
	 */

	public List<AnexoEvaluacionVmaAnexo1DTO> listaDeAnexosEPevaluaronVMAAnexo1(String anhio) {
//		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
//				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());
//		
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
			    .sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipoEmpresa().getNombre()))
			    .collect(Collectors.toList());

		List<AnexoEvaluacionVmaAnexo1DTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {

			RespuestaVMA muestrasInopinadas = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					preguntasAlternativasVMA.getId_pregunta_und_toma_muestra_inopinada(),
					registroVMA.getIdRegistroVma());
			logger.info(" Integer.parseInt(muestrasInopinadas.getRespuesta()) preg11- "
					+ Integer.parseInt(muestrasInopinadas.getRespuesta()));
			RespuestaVMA UNDSobrepasanParametroAnexo1 = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					preguntasAlternativasVMA.getId_alternativa_und_sobrepasan_parametro_anexo1(),
					registroVMA.getIdRegistroVma());

			RespuestaVMA UNDFacturadosPagoAdicional = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					preguntasAlternativasVMA.getId_alternativa_und_facturaron_pago_adicional(),
					registroVMA.getIdRegistroVma());

			RespuestaVMA UNDRealizaronPagoAdicional = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					preguntasAlternativasVMA.getId_alternativa_und_realizaron_pago_adicional(),
					registroVMA.getIdRegistroVma());

			anexos.add(new AnexoEvaluacionVmaAnexo1DTO(registroVMA.getEmpresa().getNombre(),
					registroVMA.getEmpresa().getTipoEmpresa().getNombre(), Integer.parseInt(muestrasInopinadas.getRespuesta()),
					Integer.parseInt(UNDSobrepasanParametroAnexo1.getRespuesta()),
					Integer.parseInt(UNDFacturadosPagoAdicional.getRespuesta()),
					Integer.parseInt(UNDRealizaronPagoAdicional.getRespuesta())));
		});

		return anexos;
	}

	/*
	 * anexo 6 - Detalle de las EP que han realizado la evaluación de los VMA del
	 * Anexo 2 del reglamento de VMA
	 */

	public List<AnexoEvaluacionVmaAnexo2DTO> listaDeAnexosEPevaluaronVMAAnexo2(String anhio) {
		
//		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
//				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());
//		
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
			    .sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipoEmpresa().getNombre()))
			    .collect(Collectors.toList());

		List<AnexoEvaluacionVmaAnexo2DTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {

			RespuestaVMA muestrasInopinadas = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					preguntasAlternativasVMA.getId_pregunta_und_toma_muestra_inopinada(),
					registroVMA.getIdRegistroVma());

			RespuestaVMA UNDSobrepasanParametroAnexo2 = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					preguntasAlternativasVMA.getId_alternativa_und_sobrepasan_parametro_anexo2(),
					registroVMA.getIdRegistroVma());

			RespuestaVMA UNDConPlazoAdicional = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					preguntasAlternativasVMA.getId_alternativa_und_otorgado_plazo_adicional(),
					registroVMA.getIdRegistroVma());

			RespuestaVMA UNDSuscritoAcuerdo = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					preguntasAlternativasVMA.getId_alternativa_und_suscrito_plazo_otorgado(),
					registroVMA.getIdRegistroVma());

			anexos.add(new AnexoEvaluacionVmaAnexo2DTO(registroVMA.getEmpresa().getNombre(),
					registroVMA.getEmpresa().getTipoEmpresa().getNombre(), Integer.parseInt(muestrasInopinadas.getRespuesta()),
					Integer.parseInt(UNDSobrepasanParametroAnexo2.getRespuesta()),
					Integer.parseInt(UNDConPlazoAdicional.getRespuesta()),
					Integer.parseInt(UNDSuscritoAcuerdo.getRespuesta())));
		});

		return anexos;
	}

	// anexo 7 - Detalle de las EP que han realizado la atención de reclamos
	// referidos a VMA

	public List<AnexoReclamosVMADTO> listaDeAnexosEPSAtendieronReclamos(String anhio) {
//		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
//				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());
//		
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
			    .sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipoEmpresa().getNombre()))
			    .collect(Collectors.toList());
		

		List<AnexoReclamosVMADTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {

			RespuestaVMA UNDinscritos = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					preguntasAlternativasVMA.getId_alternativa_und_inscritos(), registroVMA.getIdRegistroVma());

			RespuestaVMA reclamosRecibidosVMA = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					preguntasAlternativasVMA.getId_pregunta_nro_reclamos_recibidos(), registroVMA.getIdRegistroVma());

			RespuestaVMA reclamosFundadosVMA = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					preguntasAlternativasVMA.getId_pregunta_nro_reclamos_fundados(), registroVMA.getIdRegistroVma());

			anexos.add(new AnexoReclamosVMADTO(registroVMA.getEmpresa().getNombre(), registroVMA.getEmpresa().getTipoEmpresa().getNombre(),
					Integer.parseInt(UNDinscritos.getRespuesta()),
					Integer.parseInt(reclamosRecibidosVMA.getRespuesta()),
					Integer.parseInt(reclamosFundadosVMA.getRespuesta())));
		});

		return anexos;
	}

	// anexo 8 - Detalle de los costos de identificación, inspección y registro de
	// los UND

	public List<AnexoCostoTotalUNDDTO> anexoDetalleCostosUND(String anhio) {
//		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
//				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());
		
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
			    .sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipoEmpresa().getNombre()))
			    .collect(Collectors.toList());

		List<AnexoCostoTotalUNDDTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {

			RespuestaVMA costoTotalAnualUND = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					preguntasAlternativasVMA.getId_pregunta_costo_total_anual_und(), registroVMA.getIdRegistroVma());

			RespuestaVMA UNDidentificados = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					preguntasAlternativasVMA.getId_alternativa_nro_und_identificados_parcial(),
					registroVMA.getIdRegistroVma());

			BigDecimal costoAnual = new BigDecimal(costoTotalAnualUND.getRespuesta())
					.divide(new BigDecimal(UNDidentificados.getRespuesta()), 1, RoundingMode.HALF_UP);

			anexos.add(new AnexoCostoTotalUNDDTO(registroVMA.getEmpresa().getNombre(),
					registroVMA.getEmpresa().getTipoEmpresa().getNombre(), new BigDecimal(costoTotalAnualUND.getRespuesta()),
					Integer.parseInt(UNDidentificados.getRespuesta()), costoAnual));
		});

		return anexos;
	}

	// anexo 9 - Detalle de los costos totales por toma de muestras inopinadas

	public List<AnexoCostoTotalMuestrasInopinadasDTO> listaAnexosCostosMuestrasInopinadas(String anhio) {
		
//		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
//				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());
		
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
			    .sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipoEmpresa().getNombre()))
			    .collect(Collectors.toList());

		List<AnexoCostoTotalMuestrasInopinadasDTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {

			RespuestaVMA costoTotalAnualMuestras = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					preguntasAlternativasVMA.getId_pregunta_costo_anual_muestras_inopinadas(),
					registroVMA.getIdRegistroVma());

			RespuestaVMA UNDMuestraInopinada = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					preguntasAlternativasVMA.getId_pregunta_und_toma_muestra_inopinada(),
					registroVMA.getIdRegistroVma());

			BigDecimal costoAnual = new BigDecimal(costoTotalAnualMuestras.getRespuesta())
					.divide(new BigDecimal(UNDMuestraInopinada.getRespuesta()), 2, RoundingMode.HALF_UP);

			anexos.add(new AnexoCostoTotalMuestrasInopinadasDTO(registroVMA.getEmpresa().getNombre(),
					registroVMA.getEmpresa().getTipoEmpresa().getNombre(), new BigDecimal(costoTotalAnualMuestras.getRespuesta()),
					Integer.parseInt(UNDMuestraInopinada.getRespuesta()), costoAnual));
		});

		return anexos;
	}

	// anexo 10 - Detalle de los costos totales incurridos por las Empresas
	// Prestadoras

	public List<AnexoCostoTotalesIncurridosDTO> listaAnexosCostosTotalesIncurridos(String anhio) {
		
//		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
//				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());
		
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
			    .sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipoEmpresa().getNombre()))
			    .collect(Collectors.toList());
		
		List<AnexoCostoTotalesIncurridosDTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {

			RespuestaVMA costoTotalAnualUND = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					preguntasAlternativasVMA.getId_pregunta_costo_total_anual_und(), registroVMA.getIdRegistroVma());

			RespuestaVMA costoTotalAnualMuestrasInopinadas = respuestaVMARepository
					.findRespuestaByPreguntaIdAndRegistro(
							preguntasAlternativasVMA.getId_pregunta_costo_anual_muestras_inopinadas(),
							registroVMA.getIdRegistroVma());

			RespuestaVMA costoOtrosGastosImplementacion = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					preguntasAlternativasVMA.getId_pregunta_otros_gastos_implementacion(),
					registroVMA.getIdRegistroVma());

			anexos.add(new AnexoCostoTotalesIncurridosDTO(registroVMA.getEmpresa().getNombre(),
					registroVMA.getEmpresa().getTipoEmpresa().getNombre(), new BigDecimal(costoTotalAnualUND.getRespuesta()),
					new BigDecimal(costoTotalAnualMuestrasInopinadas.getRespuesta()),
					new BigDecimal(costoOtrosGastosImplementacion.getRespuesta())));
		});

		return anexos;
	}

	// anexo 11 , Detalle de los ingresos facturados durante el año actual, por
	// conceptos de VMA

	public List<AnexoIngresosImplVmaDTO> listaDeAnexosIngresosVMA(String anhio) {
		
//		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
//				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());
		
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
			    .sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipoEmpresa().getNombre()))
			    .collect(Collectors.toList());
		
		List<AnexoIngresosImplVmaDTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {
			RespuestaVMA respuestaIngresos = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					preguntasAlternativasVMA.getId_pregunta_ingresos_facturados(), registroVMA.getIdRegistroVma());

			anexos.add(new AnexoIngresosImplVmaDTO(registroVMA.getEmpresa().getNombre(),
					registroVMA.getEmpresa().getTipoEmpresa().getNombre(), new BigDecimal(respuestaIngresos.getRespuesta())));
		});

		return anexos;
	}

	public void actualizarEstadoIncompleto(Integer id) {
		Optional<RegistroVMA> registro = registroVMARepository.findById(id);

		if (registro.isPresent()) {
			RegistroVMA registroVMA = registro.get();
			registroVMA.setEstado(Constants.ESTADO_INCOMPLETO);
			registroVMA.setUpdatedAt(new Date());
			registroVMARepository.save(registroVMA);
		} else {
			throw new RuntimeException("Registro no encontrado");
		}
	}

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public RegistroVMADTO obtenerEmpresaSinCompletarRegistroVMA() throws Exception {

		RegistroVMADTO dto = null;
		Optional<RegistroVMA> opt = this.registroVMARepository
				.findEmpresasCompletaronRegistro(userUtil.getCurrentUserId());

		if (opt.isPresent()) {
			RegistroVMA registrovma = opt.get();
			dto = RegistroVMAAssembler.buildDtoModel(registrovma);
			return dto;
		} else {
			return null;
		}

	}
	
	private Predicate filtrarPorFechaRegistro(String search , CriteriaBuilder cb,Join registroVMA, Date startDate) {
		
		Predicate fechaRegistroPredicate;
		//para la busqueda de la fecha de registro vma, segun el campo Buscar:
		// Formatos para fechas
		SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date date;
		Date startDate1;
		Date endDate1;

		try {
		    if (search.matches("\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}")) {
		        // Fecha completa con hora, minuto, segundo
		        date = fullDateFormat.parse(search);
		        fechaRegistroPredicate = cb.equal(registroVMA.get("createdAt"), date);
		    } else if (search.matches("\\d{2}-\\d{2}-\\d{4}")) {
		        // Solo fecha sin hora
		        startDate1 = dateFormat.parse(search);
		        endDate1 = new Date(startDate1.getTime() + 24 * 60 * 60 * 1000 - 1); // Fin del día
		        fechaRegistroPredicate = cb.between(registroVMA.get("createdAt"), startDate1, endDate1);
		    } else if (search.matches("\\d{2}-\\d{2}")) {
		        // Día y mes sin importar el año
		        String[] parts = search.split("-");
		        int day = Integer.parseInt(parts[0]);
		        int month = Integer.parseInt(parts[1]);

		        fechaRegistroPredicate = cb.and(
		            cb.equal(cb.function("day", Integer.class, registroVMA.get("createdAt")), day),
		            cb.equal(cb.function("month", Integer.class, registroVMA.get("createdAt")), month)
		        );
		    }else if (search.matches("\\d{2}-\\d{4}")) {
		        // Mes y año (se asume el primer día del mes)
		        dateFormat = new SimpleDateFormat("MM-yyyy");
		        startDate1 = dateFormat.parse(search);
		        Calendar cal = Calendar.getInstance();
		        cal.setTime(startDate1);
		        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH)); // Último día del mes
		        endDate1 = cal.getTime();
		        fechaRegistroPredicate = cb.between(registroVMA.get("createdAt"), startDate1, endDate1);
		    } else if (search.matches("\\d{4}")) {
		        // Solo año
		        startDate1 = dateFormat.parse("01-01-" + search);
		        endDate1 = dateFormat.parse("31-12-" + search);
		        fechaRegistroPredicate = cb.between(registroVMA.get("createdAt"), startDate1, endDate1);
		    } else {
		        // Búsqueda genérica
		        fechaRegistroPredicate = cb.like(
		            cb.lower(registroVMA.get("createdAt").as(String.class)),
		            "%" + search.toLowerCase() + "%"
		        );
		    }
		} catch (ParseException e) {
		    throw new IllegalArgumentException("Formato de fecha inválido: " + search, e);
		}

		return fechaRegistroPredicate;
	}

}
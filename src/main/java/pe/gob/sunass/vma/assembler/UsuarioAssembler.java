package pe.gob.sunass.vma.assembler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import pe.gob.sunass.vma.constants.Constants;
import pe.gob.sunass.vma.dto.UsuarioDTO;
import pe.gob.sunass.vma.model.Usuario;
import pe.gob.sunass.vma.util.DateUtil;

public class UsuarioAssembler {

	public UsuarioAssembler() {
	}

	public static UsuarioDTO buildDtoDomain(Usuario usuario) {
		UsuarioDTO dto = null;

		if (usuario != null) {
			dto = new UsuarioDTO();
			dto.setId(usuario.getId());
			dto.setRole(RoleAssembler.buildDtoModel(usuario.getRole()));
			dto.setEmpresa(EmpresaAssembler.buildDtoModel(usuario.getEmpresa()));
			dto.setUserName(usuario.getUserName());
			dto.setNombres(usuario.getNombres());
			dto.setApellidos(usuario.getApellidos());
			dto.setTipo(usuario.getTipo());
			dto.setUnidadOrganica(usuario.getUnidadOrganica());
			dto.setTelefono(usuario.getTelefono());
			dto.setCorreo(usuario.getCorreo());
			dto.setPassword(usuario.getPassword());
			dto.setEstado(usuario.getEstado());
			dto.setCreatedAt(DateUtil.format(Constants.Format.DateTime.DateTime, usuario.getCreatedAt()));
			dto.setUpdatedAt(usuario.getUpdatedAt() == null ? null
					: DateUtil.format(Constants.Format.DateTime.DateTime, usuario.getUpdatedAt()));
		}

		return dto;
	}

	public static List<UsuarioDTO> buildDtoDomainCollection(List<Usuario> listUser) throws Exception {
		List<UsuarioDTO> listDTO = new ArrayList<UsuarioDTO>();

		if (listUser != null) {
			for (Usuario domain : listUser) {
				listDTO.add(UsuarioAssembler.buildDtoDomain(domain));
			}
		}

		return listDTO;
	}

	public static List<UsuarioDTO> buildDtoDomainCollection(Set<Usuario> setUser) throws Exception {
		List<UsuarioDTO> listDTO = new ArrayList<UsuarioDTO>();

		if (setUser != null) {
			for (Usuario usuario : setUser) {
				listDTO.add(UsuarioAssembler.buildDtoDomain(usuario));
			}
		}

		return listDTO;
	}

	public static Page<UsuarioDTO> buildDtoDomainCollection(Page<Usuario> pageUsuario) throws Exception {
		List<UsuarioDTO> listDTO = new ArrayList<UsuarioDTO>();

		for (Usuario domain : pageUsuario) {
			listDTO.add(UsuarioAssembler.buildDtoDomain(domain));
		}

		Page<UsuarioDTO> pageDTO = new PageImpl<UsuarioDTO>(listDTO, pageUsuario.getPageable(),
				pageUsuario.getTotalElements());

		return pageDTO;
	}

}
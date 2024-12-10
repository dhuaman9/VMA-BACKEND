package pe.gob.sunass.vma.assembler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import pe.gob.sunass.vma.constants.Constants;
import pe.gob.sunass.vma.dto.EmpresaDTO;
import pe.gob.sunass.vma.dto.RegistroVMADTO;
import pe.gob.sunass.vma.model.Empresa;
import pe.gob.sunass.vma.model.cuestionario.RegistroVMA;
import pe.gob.sunass.vma.util.DateUtil;

public class RegistroVMAAssembler {

	public RegistroVMAAssembler() {
	}

	public static RegistroVMADTO buildDtoModel(RegistroVMA registroVMA) throws Exception {
		RegistroVMADTO dto = null;

		if (registroVMA != null) {
			dto = new RegistroVMADTO();
			dto.setIdRegistroVma(registroVMA.getIdRegistroVma());
			dto.setEmpresa(EmpresaAssembler.buildDtoModel(registroVMA.getEmpresa()));
			dto.setEstado(registroVMA.getEstado());
			dto.setFichaRegistro(FichaAssembler.buildDtoModel(registroVMA.getFichaRegistro()));
			dto.setUsername(registroVMA.getUsername());
			dto.setCreatedAt(DateUtil.format(Constants.Format.DateTime.DateTime, registroVMA.getCreatedAt()));
			dto.setUpdatedAt(registroVMA.getUpdatedAt() == null ? null
					: DateUtil.format(Constants.Format.DateTime.DateTime, registroVMA.getUpdatedAt()));
		}

		return dto;
	}

	public static List<RegistroVMADTO> buildDtoDomainCollection(List<RegistroVMA> listRegistroVMA) throws Exception {
		List<RegistroVMADTO> listDTO = new ArrayList<RegistroVMADTO>();

		if (listRegistroVMA != null) {
			for (RegistroVMA model : listRegistroVMA) {
				listDTO.add(RegistroVMAAssembler.buildDtoModel(model));
			}
		}

		return listDTO;
	}

	// paginacion
	public static Page<RegistroVMADTO> buildDtoModelCollection(Page<RegistroVMA> pageRegistroVMA) throws Exception {
		List<RegistroVMADTO> listDTO = new ArrayList<RegistroVMADTO>();

		for (RegistroVMA registroVMA : pageRegistroVMA) {
			listDTO.add(RegistroVMAAssembler.buildDtoModel(registroVMA));
		}

		Page<RegistroVMADTO> pageDTO = new PageImpl<RegistroVMADTO>(listDTO, pageRegistroVMA.getPageable(),
				pageRegistroVMA.getTotalElements());

		return pageDTO;
	}

	public static List<RegistroVMADTO> buildDtoDomainCollection(Set<RegistroVMA> setRegistroVMA) throws Exception {
		List<RegistroVMADTO> listDTO = new ArrayList<RegistroVMADTO>();

		if (setRegistroVMA != null) {
			for (RegistroVMA registroVMA : setRegistroVMA) {
				listDTO.add(RegistroVMAAssembler.buildDtoModel(registroVMA));
			}
		}

		return listDTO;
	}

}

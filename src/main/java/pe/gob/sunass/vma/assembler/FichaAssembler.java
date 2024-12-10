package pe.gob.sunass.vma.assembler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import pe.gob.sunass.vma.constants.Constants;
import pe.gob.sunass.vma.dto.FichaDTO;
import pe.gob.sunass.vma.model.FichaRegistro;
import pe.gob.sunass.vma.util.DateUtil;

public class FichaAssembler {

	public FichaAssembler() {
	}

	public static FichaDTO buildDtoModel(FichaRegistro fichaRegistro) {
		FichaDTO dto = null;

		if (fichaRegistro != null) {
			dto = new FichaDTO();
			dto.setIdFichaRegistro(fichaRegistro.getIdFichaRegistro());
			dto.setAnio(fichaRegistro.getAnio());
			dto.setFechaInicio(fichaRegistro.getFechaInicio());
			dto.setFechaFin(fichaRegistro.getFechaFin());
			dto.setIdUsuarioRegistro(fichaRegistro.getIdUsuarioRegistro());
			dto.setIdUsuarioActualizacion(fichaRegistro.getIdUsuarioActualizacion());
			dto.setCreatedAt(DateUtil.format(Constants.Format.DateTime.DateTime, fichaRegistro.getCreatedAt()));
			dto.setUpdatedAt(fichaRegistro.getUpdatedAt() == null ? null
					: DateUtil.format(Constants.Format.DateTime.DateTime, fichaRegistro.getUpdatedAt()));
		}

		return dto;
	}

	public static List<FichaDTO> buildDtoModelCollection(List<FichaRegistro> listFicha) throws Exception {
		List<FichaDTO> listDTO = new ArrayList<FichaDTO>();

		if (listFicha != null) {
			for (FichaRegistro fichaRegistro : listFicha) {
				listDTO.add(FichaAssembler.buildDtoModel(fichaRegistro));
			}
		}

		return listDTO;
	}

	public static List<FichaDTO> buildDtoModelCollection(Set<FichaRegistro> setFicha) throws Exception {
		List<FichaDTO> listDTO = new ArrayList<FichaDTO>();

		if (setFicha != null) {
			for (FichaRegistro fichaRegistro : setFicha) {
				listDTO.add(FichaAssembler.buildDtoModel(fichaRegistro));
			}
		}

		return listDTO;
	}

	public static Page<FichaDTO> buildDtoModelCollection(Page<FichaRegistro> pageFichaRegistro) throws Exception {
		List<FichaDTO> listDTO = new ArrayList<FichaDTO>();

		for (FichaRegistro fichaRegistro : pageFichaRegistro) {
			listDTO.add(FichaAssembler.buildDtoModel(fichaRegistro));
		}

		Page<FichaDTO> pageDTO = new PageImpl<FichaDTO>(listDTO, pageFichaRegistro.getPageable(),
				pageFichaRegistro.getTotalElements());

		return pageDTO;
	}

}

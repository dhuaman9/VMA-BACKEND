package pe.gob.sunass.vma.assembler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import pe.gob.sunass.vma.constants.Constants;
import pe.gob.sunass.vma.dto.EmpresaDTO;
import pe.gob.sunass.vma.model.Empresa;
import pe.gob.sunass.vma.util.DateUtil;

public class EmpresaAssembler {
	
	 public EmpresaAssembler() {}

	 public static EmpresaDTO buildDtoModel(Empresa empresa) throws Exception {
		  EmpresaDTO dto = null;

	    if (empresa != null) {
	      dto = new EmpresaDTO();
	      dto.setIdEmpresa(empresa.getIdEmpresa());
	      dto.setNombre(empresa.getNombre());
	      dto.setRegimen(empresa.getRegimen());
	      dto.setTipo(empresa.getTipo());
	      dto.setEstado(empresa.getEstado());
	      dto.setIdUsuarioRegistro(empresa.getIdUsuarioRegistro());
	      dto.setIdUsuarioActualizacion(empresa.getIdUsuarioActualizacion());
	      dto.setCreatedAt(DateUtil.format(Constants.Format.DateTime.DateTime,empresa.getCreatedAt()));
	      dto.setUpdatedAt(empresa.getUpdatedAt() == null ? null :DateUtil.format(Constants.Format.DateTime.DateTime,empresa.getUpdatedAt()));
	    }

	    return dto;
	  }

	  
	  public static List<EmpresaDTO> buildDtoModelCollection(List<Empresa> listEmpresa) throws Exception {
		    List<EmpresaDTO> listDTO = new ArrayList<EmpresaDTO>();

		    if (listEmpresa != null) {
		      for (Empresa empresa : listEmpresa) {
		        listDTO.add(EmpresaAssembler.buildDtoModel(empresa));
		      }
		    }

		    return listDTO;
		  }

		  public static List<EmpresaDTO> buildDtoModelCollection(Set<Empresa> setEmpresa) throws Exception {
		    List<EmpresaDTO> listDTO = new ArrayList<EmpresaDTO>();

		    if (setEmpresa != null) {
		      for (Empresa empresa : setEmpresa) {
		        listDTO.add(EmpresaAssembler.buildDtoModel(empresa));
		      }
		    }

		    return listDTO;
		  }

		  public static Page<EmpresaDTO> buildDtoModelCollection(Page<Empresa> pageEmpresa) throws Exception {
		    List<EmpresaDTO> listDTO = new ArrayList<EmpresaDTO>();

		    for (Empresa empresa : pageEmpresa) {
		      listDTO.add(EmpresaAssembler.buildDtoModel(empresa));
		    }

		    Page<EmpresaDTO> pageDTO = new PageImpl<EmpresaDTO>(listDTO,
		    		pageEmpresa.getPageable(),
		    		pageEmpresa.getTotalElements());


		    return pageDTO;
		  }
	  
	   

}

package pe.gob.sunass.vma.assembler;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import pe.gob.sunass.vma.constants.Constants;
import pe.gob.sunass.vma.dto.SeccionDTO;
import pe.gob.sunass.vma.dto.SeccionDTO;
import pe.gob.sunass.vma.model.Seccion;
import pe.gob.sunass.vma.model.Seccion;
import pe.gob.sunass.vma.util.DateUtil;

public class SeccionAssembler {
	
	
	public SeccionAssembler() {
		
	}
	
	public static SeccionDTO buildDtoModel(Seccion seccion) throws Exception {
		SeccionDTO dto = null;

	    if (seccion != null) {
	      dto = new SeccionDTO();
	      dto.setIdSeccion(seccion.getIdSeccion());
	      dto.setNombre(seccion.getNombre());
	      dto.setOrden(seccion.getOrden());
	      dto.setEstado(seccion.getEstado());
	      dto.setCreatedAt(DateUtil.format(Constants.Format.DateTime.DateTime,
	    		  seccion.getCreatedAt()));
	      dto.setUpdatedAt(seccion.getUpdatedAt() == null ? null :
	                             DateUtil.format(Constants.Format.DateTime.DateTime,
	                            		 seccion.getUpdatedAt()));
	      dto.setIdUsuarioRegistro(seccion.getIdUsuarioRegistro());
	      dto.setIdUsuarioActualizacion(seccion.getIdUsuarioActualizacion());
	    }

	    return dto;
	  }
	
	 public static List<SeccionDTO> buildDtoModelCollection(List<Seccion> listSeccion) throws Exception {
		    List<SeccionDTO> listDTO = new ArrayList<SeccionDTO>();

		    if (listSeccion != null) {
		      for (Seccion seccion : listSeccion) {
		        listDTO.add(SeccionAssembler.buildDtoModel(seccion));
		      }
		    }

		    return listDTO;
		  }

		  public static List<SeccionDTO> buildDtoModelCollection(Set<Seccion> setSeccion) throws Exception {
		    List<SeccionDTO> listDTO = new ArrayList<SeccionDTO>();

		    if (setSeccion != null) {
		      for (Seccion seccion : setSeccion) {
		        listDTO.add(SeccionAssembler.buildDtoModel(seccion));
		      }
		    }

		    return listDTO;
		  }

		  public static Page<SeccionDTO> buildDtoModelCollection(Page<Seccion> pageSeccion) throws Exception {
		    List<SeccionDTO> listDTO = new ArrayList<SeccionDTO>();

		    for (Seccion Seccion : pageSeccion) {
		      listDTO.add(SeccionAssembler.buildDtoModel(Seccion));
		    }

		    Page<SeccionDTO> pageDTO = new PageImpl<SeccionDTO>(listDTO,
		    		pageSeccion.getPageable(),
		    		pageSeccion.getTotalElements());


		    return pageDTO;
		  }
	  
	

}

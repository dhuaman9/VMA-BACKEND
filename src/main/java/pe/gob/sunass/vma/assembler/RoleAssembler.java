package pe.gob.sunass.vma.assembler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import pe.gob.sunass.vma.constants.Constants;
import pe.gob.sunass.vma.dto.RoleDTO;
import pe.gob.sunass.vma.model.Role;
import pe.gob.sunass.vma.util.DateUtil;



public class RoleAssembler {
  public RoleAssembler() {}

  public static RoleDTO buildDtoModel(Role role) throws Exception {
    RoleDTO dto = null;

    if (role != null) {
      dto = new RoleDTO();
      dto.setIdRole(role.getIdRol());
      dto.setNombre(role.getNombre());
      dto.setAuth(role.getAuth());
      dto.setCreatedAt(DateUtil.format(Constants.Format.DateTime.DateTime,
    		  role.getCreatedAt()));
      dto.setUpdatedAt(role.getUpdatedAt() == null ? null :
                             DateUtil.format(Constants.Format.DateTime.DateTime,
                            		 role.getUpdatedAt()));
    }

    return dto;
  }

  public static List<RoleDTO> buildDtoModelCollection(List<Role> listRole) throws Exception {
    List<RoleDTO> listDTO = null;

    if (listRole != null) {
      listDTO = new ArrayList<RoleDTO>();

      for (Role role : listRole) {
        listDTO.add(RoleAssembler.buildDtoModel(role));
      }
    }

    return listDTO;
  }

  public static List<RoleDTO> buildDtoModelCollection(Set<Role> setRole) throws Exception {
    List<RoleDTO> listDTO = null;

    if (setRole != null) {
      listDTO = new ArrayList<RoleDTO>();

      for (Role role : setRole) {
        listDTO.add(RoleAssembler.buildDtoModel(role));
      }
    }

    return listDTO;
  }

  public static Page<RoleDTO> buildDtoDomainCollection(Page<Role> pageRole) throws Exception {
    List<RoleDTO> listDTO = new ArrayList<RoleDTO>();

    for (Role role : pageRole) {
      listDTO.add(RoleAssembler.buildDtoModel(role));
    }

    Page<RoleDTO> pageDTO = new PageImpl<RoleDTO>(listDTO,
    		pageRole.getPageable(),
    		pageRole.getTotalElements());


    return pageDTO;
  }

 
  
}

package pe.gob.sunass.vma.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import pe.gob.sunass.vma.assembler.EmpresaAssembler;
import pe.gob.sunass.vma.assembler.FichaAssembler;
import pe.gob.sunass.vma.dto.EmpresaDTO;
import pe.gob.sunass.vma.dto.FichaDTO;
import pe.gob.sunass.vma.exception.FailledValidationException;
import pe.gob.sunass.vma.model.Empresa;
import pe.gob.sunass.vma.model.FichaRegistro;
import pe.gob.sunass.vma.repository.EmpresaRepository;
import pe.gob.sunass.vma.repository.FichaRepository;

@Service
public class FichaService {

	 @SuppressWarnings("unused")
	  private static Logger logger = LoggerFactory.getLogger(EmpresaService.class);

	  @Autowired
	  private FichaRepository fichaRepository;

	 
	  @Transactional(Transactional.TxType.REQUIRES_NEW)
	  public List<FichaDTO> findAll() throws Exception {
	    List<FichaRegistro> listFicha = this.fichaRepository.findAllByOrderByIdFichaRegistroDesc();
	    List<FichaDTO> listDTO = FichaAssembler.buildDtoModelCollection(listFicha);

	    return listDTO;
	  }

	  @Transactional(Transactional.TxType.REQUIRES_NEW)
	  public Page<FichaDTO> findAll(Pageable pageable) throws Exception {
	    Page<FichaRegistro> pageDomain = this.fichaRepository.findAllByOrderByIdFichaRegistroDesc(pageable);
	    Page<FichaDTO> pageDTO = FichaAssembler.buildDtoModelCollection(pageDomain);

	    return pageDTO;
	  }

	  
	  @Transactional(Transactional.TxType.REQUIRES_NEW)
	  public FichaDTO findById(Integer id) throws Exception {
		  
		  FichaDTO dto = null;
	    Optional<FichaRegistro> opt = this.fichaRepository.findById(id);

	    if (opt.isPresent()) {
	    	FichaRegistro ficha = opt.get();
	        dto = FichaAssembler.buildDtoModel(ficha);
	    }

	    return dto;
	  }
	  
	  
	  @Transactional(Transactional.TxType.REQUIRES_NEW)
	  public FichaDTO registrar(FichaDTO dto) throws Exception {
	    if (dto == null) {
	      throw new Exception("datos son obligatorios");
	    }
	    else if (dto.getAnio() == null || dto.getAnio().isEmpty()) {
	      throw new Exception("el [anio] es obligatorio");
	    }
	    else if (dto.getFechaInicio() == null ) {
	      throw new Exception("[FechaInicio] es obligatorio");
	    }
	    else if (dto.getFechaFin() == null ) {
		      throw new Exception("[FechaFin] es obligatorio");
		}
	    
	  
	    List<FichaRegistro> list = this.fichaRepository.existsByFecha(dto.getFechaInicio(),dto.getFechaFin() );
	    if (list != null && list.size() > 0) {
	      throw new FailledValidationException(" ya existe la fecha de inicio o fin");
	    }


	    FichaRegistro fichaRegistro = new FichaRegistro();
	    fichaRegistro.setAnio(dto.getAnio());
	    fichaRegistro.setFechaInicio(dto.getFechaInicio());
	    fichaRegistro.setFechaFin(dto.getFechaFin());
	    fichaRegistro.setCreatedAt(new Date());
	    fichaRegistro.setUpdatedAt(null);
	    fichaRegistro.setIdUsuarioRegistro(1); //dhr, por el momento, seteamos al usuario 1, pero   debe pasar el ID del user en sesion.
	    fichaRegistro.setIdUsuarioActualizacion(null);
	    
	    fichaRegistro = this.fichaRepository.save(fichaRegistro);

	    return FichaAssembler.buildDtoModel(fichaRegistro);
	  }

	  @Transactional(Transactional.TxType.REQUIRES_NEW)
	  public FichaDTO update(FichaDTO dto) throws Exception {
	    if (dto == null) {
	      throw new Exception("datos son obligatorios");
	    }
	    
	    FichaRegistro fichaRegistro = null;
	    Optional<FichaRegistro> optFichaRegistro = this.fichaRepository.findById(dto.getIdFichaRegistro());

	    if (optFichaRegistro.isPresent()) {
	    	fichaRegistro = optFichaRegistro.get();

	      if (dto.getAnio() != null && !dto.getAnio().isEmpty()) {
	          //int countAnio = this.fichaRepository.countByYear(dto.getAnio());

	         /* if ( countAnio > 2) {
	            throw new FailledValidationException("El [anio] no se puede registrar más de dos veces en el mismo año.");
	          }*/
	          fichaRegistro.setAnio(dto.getAnio());
	        
	      }

	      if (dto.getFechaInicio() != null ) {
	        if (!dto.getFechaInicio().equals(fichaRegistro.getFechaInicio())) {
	        	fichaRegistro.setFechaInicio(dto.getFechaInicio());
	        }
	      }
	      
	      if (dto.getFechaFin() != null ) {
		        if (!dto.getFechaFin().equals(fichaRegistro.getFechaFin())) {
		        	fichaRegistro.setFechaFin(dto.getFechaFin());
		        }
		   }

	      fichaRegistro.setUpdatedAt(new Date());
	      fichaRegistro = this.fichaRepository.save(fichaRegistro);
	    }

	    return FichaAssembler.buildDtoModel(fichaRegistro);
	    
	  }
	
}

package pe.gob.sunass.vma.service;

import java.time.LocalDate;
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

	  /*@Transactional(Transactional.TxType.REQUIRES_NEW)
	  public Page<FichaDTO> findAll(Pageable pageable) throws Exception {
	    Page<FichaRegistro> pageDomain = this.fichaRepository.findAllByOrderByIdFichaRegistroDesc(pageable);
	    Page<FichaDTO> pageDTO = FichaAssembler.buildDtoModelCollection(pageDomain);

	    return pageDTO;
	  }*/

	  
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
	      throw new FailledValidationException("Los datos son obligatorios");
	    }
	    else if (dto.getAnio() == null || dto.getAnio().isEmpty()) {
	      throw new FailledValidationException("el [anio] es obligatorio");
	    }
	    else if (dto.getFechaInicio() == null ) {
	      throw new FailledValidationException("La [FechaInicio] es obligatorio");
	    }
	    else if (dto.getFechaFin() == null ) {
		      throw new FailledValidationException("La [FechaFin] es obligatorio");
		}
	    
	  
	    List<FichaRegistro> listaFichas = this.fichaRepository.findAllByOrderByIdFichaRegistroDesc();
	    
	    if (listaFichas != null && !listaFichas.isEmpty()) {
	    	
	    	if (dto.getFechaInicio().isAfter(dto.getFechaFin()) || dto.getFechaInicio().isEqual(dto.getFechaFin())) {
	            logger.info("error: La fecha de inicio es mayor o igual que la fecha fin.");
	            throw new FailledValidationException("Error: La Fecha de Inicio es mayor o igual que la Fecha Fin");
	        }

	    	if (Integer.parseInt(dto.getAnio())>(dto.getFechaInicio().getYear())) {
	    		throw new FailledValidationException("Error: El año  no puede ser mayor que el año de la fecha de inicio.");
			}

	        if (validarFechas(dto.getFechaInicio(), dto.getFechaFin())) {  // Validar que el rango de fechas no interfiera con ningún rango existente.
	            
	        	logger.info("El rango de fechas es correcto, no se cruza con ningun rango.");
	            
	            
	        } else {
	            logger.info("Error, el rango de fechas se solapa con un rango de fechas existente.");
	            throw new FailledValidationException("Error: El rango de fechas se solapa con un rango registrado.");
	        }
	    
        } else {
            logger.info("No hay fichas en la base de datos.");
        }

	    FichaRegistro fichaRegistro = new FichaRegistro();
	    fichaRegistro.setAnio(dto.getAnio());
	    fichaRegistro.setFechaInicio(dto.getFechaInicio());
	    fichaRegistro.setFechaFin(dto.getFechaFin());
	    fichaRegistro.setCreatedAt(new Date());
	    fichaRegistro.setUpdatedAt(null);
	    fichaRegistro.setIdUsuarioRegistro(1); //dhr, pendiente por mejorar pasar el objeto  del user en sesion.
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

	         fichaRegistro.setAnio(dto.getAnio());
	        
	      }

	      if (dto.getFechaInicio() != null ) {
	        if (!dto.getFechaInicio().equals(fichaRegistro.getFechaInicio())) {
	        	 List<FichaRegistro> listaFichas = this.fichaRepository.existsByFecha(dto.getFechaInicio(),null);
	        	 
	        	 if (listaFichas != null && listaFichas.size() > 0) {
	                 throw new FailledValidationException("La fecha de inicio  ya existe!.");
	             }
	        
	        	fichaRegistro.setFechaInicio(dto.getFechaInicio());
	        }
	      }
	      
	      if (dto.getFechaFin() != null ) {
		        if (!dto.getFechaFin().equals(fichaRegistro.getFechaFin())) {
		        	List<FichaRegistro> listaFichas = this.fichaRepository.existsByFecha(dto.getFechaFin(),null);
		        	 
		        	if (listaFichas != null && listaFichas.size() > 0) {
		                 throw new FailledValidationException("La fecha fin  ya existe!.");
		            }
		        	
		        	fichaRegistro.setFechaFin(dto.getFechaFin());
		        }
		   }

	      List<FichaRegistro> listaFicha2 = this.fichaRepository.findAllByOrderByIdFichaRegistroDesc();  //lista para validar si esta en algun rango.
	      if (listaFicha2 != null && !listaFicha2.isEmpty()) {
		    	
	    	  logger.info("fecha inicio " + dto.getFechaInicio());
	    	  logger.info("fecha fin " + dto.getFechaFin());
		    	if (dto.getFechaInicio().isAfter(dto.getFechaFin()) || dto.getFechaInicio().isEqual(dto.getFechaFin())) {
		            logger.info("error: La fecha de inicio es mayor o igual que la fecha fin.");
		            throw new FailledValidationException("Error: La Fecha de Inicio es mayor o igual que la Fecha Fin");
		        }

		    	if (Integer.parseInt(dto.getAnio())>(dto.getFechaInicio().getYear())) {
		    		throw new FailledValidationException("Error: El año  no puede ser mayor que el año de la fecha de inicio.");
				}

		        if (validarFechasForUpdate(dto.getIdFichaRegistro(), dto.getFechaInicio(), dto.getFechaFin())) {  // Validar que el rango de fechas no interfiera con ningún rango existente.
		            
		        	logger.info("El rango de fechas es correcto, no se cruza con ningun rango.");
		            
		            
		        } else {
		            logger.info("Error, el rango de fechas se cruza con un rango de fechas existente.");
		            throw new FailledValidationException("Error: El rango de fechas se cruza con un rango registrado.");
		        }
		    
	        } else {
	            logger.info("No hay fichas en la base de datos.");
	        }
	      
	      fichaRegistro.setUpdatedAt(new Date());
	      fichaRegistro = this.fichaRepository.save(fichaRegistro);
	    }

	    return FichaAssembler.buildDtoModel(fichaRegistro);
	    
	  }
	  
	
	  //para exponer al front
	  public boolean validarFechaEnRango(LocalDate fechaactual) {
		  
		  boolean enRango=false;
		   List<FichaRegistro> listaFichas = this.fichaRepository.findAllByOrderByIdFichaRegistroDesc();
		   
		    if (listaFichas != null && !listaFichas.isEmpty()) {
		    	
			    for(FichaRegistro itemFicha : listaFichas) {
			    	
			    	if (((fechaactual.isAfter(itemFicha.getFechaInicio())  || fechaactual.isEqual(itemFicha.getFechaInicio())) &&
			    			(fechaactual.isBefore(itemFicha.getFechaFin())) || fechaactual.isEqual(itemFicha.getFechaFin())) ) {
			    		enRango=true;
			    		break;
					} 	
			    }
		    
		    } else {
	            logger.info("No hay periodos registrados en la base de datos.");
	           enRango=false;
	        }
		    return enRango;
		    
	  }
	  
	  public boolean validarFechas(LocalDate fechaInicio, LocalDate fechaFin) {
	        long count = fichaRepository.validarFechas(fechaInicio, fechaFin);
	        return count == 0;
	  }
	  
	  public boolean validarFechasForUpdate(Integer idFichaRegistro, LocalDate fechaInicio, LocalDate fechaFin) {
	        long count = fichaRepository.validarFechasForUpdate(idFichaRegistro, fechaInicio, fechaFin);
	        return count == 0;
	  }
	  
	  public FichaDTO fichaEnPeriodo(LocalDate fechaActual) throws Exception {
		  
			FichaDTO dto = null;
		    Optional<FichaRegistro> opt = this.fichaRepository.validarRangoConFecha(fechaActual);
		    logger.info("this.fichaRepository.validarRangoConFecha(fechaActual)-"+this.fichaRepository.validarRangoConFecha(fechaActual));
		    if (opt.isPresent()) {
		    	FichaRegistro ficha = opt.get();
		        dto = FichaAssembler.buildDtoModel(ficha);
		    }
		    logger.info("opt.isPresent() -"+opt.isPresent());
		    return dto;
	  }

	  
}

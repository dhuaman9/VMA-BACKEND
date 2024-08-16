package pe.gob.sunass.vma.service;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.sunass.vma.assembler.SeccionAssembler;
import pe.gob.sunass.vma.dto.SeccionDTO;
import pe.gob.sunass.vma.model.cuestionario.Seccion;
import pe.gob.sunass.vma.repository.SeccionRepository;

@Service
public class SeccionesService {

	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(EmpresaService.class);
	
	@Autowired
	private SeccionRepository seccionRepository;
	 

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	  public List<SeccionDTO> findAll() throws Exception {
	    List<Seccion> listaSecciones = this.seccionRepository.findAllByOrderByIdSeccion();
	    List<SeccionDTO> listaDTO = SeccionAssembler.buildDtoModelCollection(listaSecciones);

	    return listaDTO;
	 }

	
	
}

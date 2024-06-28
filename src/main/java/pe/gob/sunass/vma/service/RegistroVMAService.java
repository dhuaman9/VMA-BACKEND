package pe.gob.sunass.vma.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.sunass.vma.assembler.RegistroVMAAssembler;
import pe.gob.sunass.vma.dto.RegistroVMADTO;
import pe.gob.sunass.vma.dto.RegistroVMARequest;
import pe.gob.sunass.vma.dto.RespuestaDTO;
import pe.gob.sunass.vma.model.RegistroVMA;
import pe.gob.sunass.vma.model.RespuestaVMA;
import pe.gob.sunass.vma.repository.RegistroVMARepository;
import pe.gob.sunass.vma.repository.RespuestaVMARepository;

@Service
public class RegistroVMAService {
	
	@SuppressWarnings("unused")
	  private static Logger logger = LoggerFactory.getLogger(UsuarioService.class);
	
	 @Autowired
	  private RegistroVMARepository registroVMARepository;

	 @Autowired
	 private RespuestaVMARepository respuestaVMARepository;
	 
	 @Transactional(Transactional.TxType.REQUIRES_NEW)
	  public List<RegistroVMADTO> findAllOrderById() throws Exception {
	    List<RegistroVMA> listRegistroVMA = this.registroVMARepository.findAllByOrderByIdRegistroVma();
	    List<RegistroVMADTO> listRegistroVMADTO = RegistroVMAAssembler.buildDtoDomainCollection(listRegistroVMA);
	    return listRegistroVMADTO;
	  }
	 
	  @Transactional(Transactional.TxType.REQUIRES_NEW)
	  public RegistroVMADTO findById(Integer id) throws Exception {
		  RegistroVMADTO dto = null;
	    Optional<RegistroVMA> opt = this.registroVMARepository.findById(id);
	    
	    if (opt.isPresent()) {
	    	RegistroVMA registroVMA = opt.get();
	      dto = RegistroVMAAssembler.buildDtoModel(registroVMA);

	    }
	    return dto;
	  }

	  @Transactional
	  public void crearRegistroVMA(RegistroVMARequest registroRequest) {
		  RegistroVMA registroVMA = new RegistroVMA();
		  RegistroVMA registroVMADB = registroVMARepository.save(registroVMA);

		  respuestaVMARepository.saveAll(
				  registroRequest.getRespuestas()
						  .stream()
						  .map(respuesta -> respuestaDtoToRespuestaVMA(respuesta, registroVMADB))
						  .collect(Collectors.toList()));
	  }

	private RespuestaVMA respuestaDtoToRespuestaVMA(RespuestaDTO respuestaDTO, RegistroVMA registroVMA) {
		return new RespuestaVMA(respuestaDTO.getIdAlternativa(), respuestaDTO.getRespuesta(), registroVMA, respuestaDTO.getIdPregunta());
	}
	
	
}

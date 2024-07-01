package pe.gob.sunass.vma.service;

import java.util.Date;
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
import pe.gob.sunass.vma.model.Empresa;
import pe.gob.sunass.vma.model.RegistroVMA;
import pe.gob.sunass.vma.model.RespuestaVMA;
import pe.gob.sunass.vma.model.Usuario;
import pe.gob.sunass.vma.repository.RegistroVMARepository;
import pe.gob.sunass.vma.repository.RespuestaVMARepository;
import pe.gob.sunass.vma.repository.UsuarioRepository;

@Service
public class RegistroVMAService {
	
	@SuppressWarnings("unused")
	  private static Logger logger = LoggerFactory.getLogger(UsuarioService.class);
	
	 @Autowired
	  private RegistroVMARepository registroVMARepository;

	 @Autowired
	 private RespuestaVMARepository respuestaVMARepository;

	 @Autowired
	 private UsuarioRepository usuarioRepository;
	 
	 @Transactional(Transactional.TxType.REQUIRES_NEW)
	  public List<RegistroVMADTO> findAllOrderById(String username) throws Exception {
		 Usuario usuario = usuarioRepository.findByUserName(username).orElseThrow();
		 List<RegistroVMA> listRegistroVMA = null;
		 if(usuario.getRole().getIdRol() == 2) {
			 listRegistroVMA = this.registroVMARepository.findAllByOrderByIdRegistroVma();
		 } else {
			 listRegistroVMA = this.registroVMARepository.registrosPorIdEmpresa(usuario.getEmpresa().getIdEmpresa());
		 }

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
	  public void saveRegistroVMA(Integer idRegistroVMA, RegistroVMARequest registroRequest, String username) {
		  RegistroVMA registroVMA;
		 if(idRegistroVMA != null) {
			 registroVMA  = registroVMARepository.findById(idRegistroVMA).orElseThrow();
			 registroVMA.setUpdatedAt(new Date());
			 registroVMA.setEstado(registroRequest.isRegistroValido() ? "COMPLETO" : "INCOMPLETO");
			 saveRespuestas(registroRequest.getRespuestas(), registroVMA);
		 } else {
			 RegistroVMA nuevoRegistro = new RegistroVMA();
			 Empresa empresa = new Empresa();
			 empresa.setIdEmpresa(registroRequest.getIdEmpresa());
			 nuevoRegistro.setEmpresa(empresa);
			 nuevoRegistro.setUsername(username);
			 nuevoRegistro.setEstado(registroRequest.isRegistroValido() ? "COMPLETO" : "INCOMPLETO");
			 nuevoRegistro.setCreatedAt(new Date());
			 RegistroVMA registroDB = registroVMARepository.save(nuevoRegistro);
			 saveRespuestas(registroRequest.getRespuestas(), registroDB);
		 }
	  }

	  private void saveRespuestas(List<RespuestaDTO> respuestasRequest, RegistroVMA registro) {
		  respuestaVMARepository.saveAll(
				  respuestasRequest
						  .stream()
						  .map(respuesta -> respuestaDtoToRespuestaVMA(respuesta, registro))
						  .collect(Collectors.toList()));
	  }

	private RespuestaVMA respuestaDtoToRespuestaVMA(RespuestaDTO respuestaDTO, RegistroVMA registroVMA) {
		return new RespuestaVMA(respuestaDTO.getIdRespuesta(), respuestaDTO.getIdAlternativa(), respuestaDTO.getRespuesta(), registroVMA, respuestaDTO.getIdPregunta());
	}

	public boolean isRegistroCompletado(String username) {
		return registroVMARepository.isRegistroCompletado(username);
	}
}

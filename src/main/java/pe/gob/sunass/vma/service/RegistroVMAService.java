package pe.gob.sunass.vma.service;

import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.sunass.vma.assembler.RegistroVMAAssembler;
import pe.gob.sunass.vma.dto.RegistroVMADTO;
import pe.gob.sunass.vma.dto.RegistroVMAFilterDTO;
import pe.gob.sunass.vma.dto.RegistroVMARequest;
import pe.gob.sunass.vma.dto.RespuestaDTO;
import pe.gob.sunass.vma.model.Empresa;
import pe.gob.sunass.vma.model.RegistroVMA;
import pe.gob.sunass.vma.model.RespuestaVMA;
import pe.gob.sunass.vma.model.Usuario;
import pe.gob.sunass.vma.repository.FichaRepository;
import pe.gob.sunass.vma.repository.RegistroVMARepository;
import pe.gob.sunass.vma.repository.RegistroVMARepositoryCustom;
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
	 
	 @Autowired
	 private FichaRepository fichaRepository;

	@PersistenceContext
	private EntityManager entityManager;
	 
	 
	 @Autowired
	 private RegistroVMARepositoryCustom registroVMARepositorycustom;
	 
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
			 Usuario usuario = usuarioRepository.findByUserName(username).orElseThrow();
			 RegistroVMA nuevoRegistro = new RegistroVMA();
			 Empresa empresa = new Empresa();
			 empresa.setIdEmpresa(registroRequest.getIdEmpresa());
			 nuevoRegistro.setEmpresa(usuario.getEmpresa());
			 nuevoRegistro.setUsername(username);
			 nuevoRegistro.setEstado(registroRequest.isRegistroValido() ? "COMPLETO" : "INCOMPLETO");
			 nuevoRegistro.setFichaRegistro(fichaRepository.findFichaRegistroActual());
			 nuevoRegistro.setCreatedAt(new Date());
			 RegistroVMA registroDB = registroVMARepository.save(nuevoRegistro);
			 saveRespuestas(registroRequest.getRespuestas(), registroDB);
		 }
	  }

	public List<RegistroVMA> searchRegistroVMA(Integer empresaId, String estado, Date startDate, Date endDate, String year, String username) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<RegistroVMA> query = cb.createQuery(RegistroVMA.class);
		Root<RegistroVMA> registroVMA = query.from(RegistroVMA.class);
		Usuario usuario = usuarioRepository.findByUserName(username).orElseThrow();

		if(usuario.getRole().getIdRol() == 2 || usuario.getRole().getIdRol() == 4) {
			List<Predicate> predicates = new ArrayList<>();

			if (empresaId != null) {
				predicates.add(cb.or(cb.isNull(registroVMA.get("empresa")), cb.equal(registroVMA.get("empresa").get("idEmpresa"), empresaId)));
			}
			if (estado != null) {
				predicates.add(cb.equal(registroVMA.get("estado"), estado));
			}
			if (startDate != null) {
				predicates.add(cb.greaterThanOrEqualTo(registroVMA.get("createdAt"), startDate));
			}
			if (endDate != null) {
				predicates.add(cb.lessThanOrEqualTo(registroVMA.get("createdAt"), agregarHoraFinDia(endDate)));
			}
			if (year != null) {
				predicates.add(cb.equal(registroVMA.get("fichaRegistro").get("anio"), year));
			}

			query.where(predicates.toArray(new Predicate[0]));

			return entityManager.createQuery(query).getResultList();
		}

		return this.registroVMARepository.registrosPorIdEmpresa(usuario.getEmpresa().getIdEmpresa());
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
		Usuario usuario = usuarioRepository.findByUserName(username).orElseThrow();
		return registroVMARepository.isRegistroCompletado(usuario.getEmpresa().getIdEmpresa());
	}
	
	public List<RegistroVMA> filterRegistros(RegistroVMAFilterDTO filterDTO) {
        return registroVMARepositorycustom.findByFilters(filterDTO);
    }

	public Date agregarHoraFinDia(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}
}

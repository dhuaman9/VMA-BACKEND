package pe.gob.sunass.vma.service;

import java.io.IOException;
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

import org.springframework.web.multipart.MultipartFile;
import pe.gob.sunass.vma.assembler.RegistroVMAAssembler;
import pe.gob.sunass.vma.dto.*;
import pe.gob.sunass.vma.model.*;
import pe.gob.sunass.vma.repository.*;

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
	private AlfrescoService alfrescoService;

	@Autowired
	private ArchivoRepository archivoRepository;
	 
	 
	 @Autowired
	 private RegistroVMARepositoryCustom registroVMARepositorycustom;

    @Autowired
    private EmpresaRepository empresaRepository;

	private final int ID_PREGUNTA_REMITIO_INFORME = 31;

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
	  public Integer saveRegistroVMA(Integer idRegistroVMA, RegistroVMARequest registroRequest, String username) {
		  RegistroVMA registroVMA;
		 if(idRegistroVMA != null) {
			 registroVMA  = registroVMARepository.findById(idRegistroVMA).orElseThrow();
			 registroVMA.setUpdatedAt(new Date());
			 registroVMA.setEstado(registroRequest.isRegistroValido() ? "COMPLETO" : "INCOMPLETO");
			 saveRespuestas(registroRequest.getRespuestas(), registroVMA);
			 return registroVMA.getIdRegistroVma();
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
			 return registroDB.getIdRegistroVma();
		 }
	  }

	  @Transactional
	  public void saveRespuestaVMAArchivo(MultipartFile file, Integer registroVMAId, Integer preguntaId, Integer respuestaId) throws IOException {


		 ArchivoDTO archivoDTO = alfrescoService.uploadFile(file);

		 if(Objects.nonNull(respuestaId)) {

			 Optional<RespuestaVMA> respuestaOpt = respuestaVMARepository.findById(respuestaId);
             respuestaOpt.ifPresent(respuestaVMA -> {
				 Archivo archivoByIdAlfresco = archivoRepository.findArchivoByIdAlfresco(respuestaVMA.getRespuesta());
				 if(Objects.nonNull(archivoByIdAlfresco)) {
					 archivoRepository.deleteById(archivoByIdAlfresco.getIdArchivo());
				 }

				 alfrescoService.deleteFile(respuestaVMA.getRespuesta());
			 });
		 }

		  RegistroVMA registroVMA = new RegistroVMA();
		  registroVMA.setIdRegistroVma(registroVMAId);
		  respuestaVMARepository.save(new RespuestaVMA(respuestaId, null, archivoDTO.getIdAlfresco(), registroVMA, preguntaId));
	  }

	  public List<AnexoRegistroVmaDTO> listaDeAnexosRegistrosVmaDTO(String anhio) {
		  List<Empresa> empresasDB = empresaRepository.findAll();
		  
		 // empresasDB.sort(Comparator.comparing(Empresa::getTipo)); 
		 // return empresasDB.stream().map(empresa -> mapToAnexoRegistroVmaDTO(empresa, anhio)).collect(Collectors.toList());
		  
		  
		// Filtrar empresas de tipo "NINGUNO", ordenar por tipo y mapear a DTO
		    return empresasDB.stream()
		        .filter(empresa -> !"NINGUNO".equals(empresa.getTipo())) // Filtrar empresas de tipo "NINGUNO"
		        .sorted(Comparator.comparing(Empresa::getTipo)) // Ordenar por tipo, si es necesario
		        .map(empresa -> mapToAnexoRegistroVmaDTO(empresa, anhio)) // Mapear a DTO
		        .collect(Collectors.toList()); // Colectar en una lista
		  
	  }

	  private AnexoRegistroVmaDTO mapToAnexoRegistroVmaDTO(Empresa empresa, String anio) {
		  RegistroVMA registroVmaPorAnhio = registroVMARepository.findRegistroVmaPorAnhio(empresa.getIdEmpresa(), anio);
		  boolean registroCompleto = Objects.nonNull(registroVmaPorAnhio) && registroVmaPorAnhio.getEstado().equals("COMPLETO");
		  boolean remitioInforme = false;
		  if(Objects.nonNull(registroVmaPorAnhio)) {
			  remitioInforme = respuestaVMARepository.isRespuestaArchivoInformacionCompleto(ID_PREGUNTA_REMITIO_INFORME, registroVmaPorAnhio.getIdRegistroVma());
		  }

		  return new AnexoRegistroVmaDTO(
				  empresa.getNombre(),
				  empresa.getTipo(),
				  empresa.getRegimen().equals("RAT"),
				  registroCompleto,
				  registroCompleto && remitioInforme);
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
		logger.info("boo+leano registrovma- "+registroVMARepository.isRegistroCompletado(usuario.getEmpresa().getIdEmpresa()));
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

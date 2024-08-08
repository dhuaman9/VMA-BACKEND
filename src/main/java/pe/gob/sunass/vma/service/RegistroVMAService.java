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
	private final int PREGUNTA_SI_NO_ID = 1;
	private final int PREGUNTA_NUMERO_TRABAJADORES_EMPRESA_PRESTADORA_ID = 3;
	private final int ALTERNATIVA_UND_IDENTIFICADOS_PARCIAL_ID = 16;
    private final int ALTERNATIVA_UND_INSPECCIONADOS_PARCIAL_ID = 22;
    private final int ALTERNATIVA_UND_INSCRITOS_ID= 18;
    private final int PREGUNTA_UND_TOMA_MUESTRA_INOPINADA_ID= 11;
    
    private final int ALTERNATIVA_UND_FACTURARON_PAGO_ADICIONAL_ID= 3;
    private final int ALTERNATIVA_UND_REALIZARON_PAGO_ADICIONAL_ID= 35;
    private final int ALTERNATIVA_UND_SOBREPASAN_PARAMETRO_ANEXO1_ID= 1;
    
    private final int ALTERNATIVA_UND_SOBREPASAN_PARAMETRO_ANEXO2_ID= 26;
    private final int ALTERNATIVA_UND_OTORGADO_PLAZO_ADICIONAL_ID= 28;
    private final int ALTERNATIVA_UND_SUSCRITO_PLAZO_OTORGADO_ID= 30;
    private final int PREGUNTA_CANTIDAD_RECLAMOS_RECIBIDOS_VMA_ID= 19;
    private final int PREGUNTA_CANTIDAD_RECLAMOS_FUNDADOS_VMA_ID= 20;
    private final int PREGUNTA_COSTO_TOTAL_ANUAL_UND_ID= 23;
    
    

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

	  public List<AnexoRespuestaSiDTO> listaDeAnexosRegistroMarcaronSi(String anhio) {
		  List<RegistroVMA> registrosCompletos = registroVMARepository
				  .findRegistrosCompletos(anhio)
				  .stream()
				  .sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo()))
				  .collect(Collectors.toList());

		  List<AnexoRespuestaSiDTO> anexos = new ArrayList<>();

		  registrosCompletos.forEach(registroVMA -> {
			  RespuestaVMA respuesta = respuestaVMARepository
					  .findRespuestaByPreguntaIdAndRegistro(PREGUNTA_SI_NO_ID, registroVMA.getIdRegistroVma());
			  RespuestaVMA respuestaNroTrabajadores = respuestaVMARepository
					  .findRespuestaByPreguntaIdAndRegistro(PREGUNTA_NUMERO_TRABAJADORES_EMPRESA_PRESTADORA_ID, registroVMA.getIdRegistroVma());

			  anexos.add(new AnexoRespuestaSiDTO(
					  registroVMA.getEmpresa().getNombre(),
					  registroVMA.getEmpresa().getTipo(),
					  respuesta.getRespuesta(),
					  Integer.parseInt(respuestaNroTrabajadores.getRespuesta()))
			  );
		  });

		  return anexos;
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
		logger.info("booleano registrovma- "+registroVMARepository.isRegistroCompletado(usuario.getEmpresa().getIdEmpresa()));
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
	
	
	//anexo 3 - Relación de las EP que han realizado avances en la inspección y/o inscripción en sus registros de los UND bajo su ámbito de influencia
	/*private final int ALTERNATIVA_UND_IDENTIFICADOS_PARCIAL_ID = 16;
    private final int ALTERNATIVA_UND_INSPECCIONADOS_PARCIAL_ID = 22;
    private final int ALTERNATIVA_UND_INSCRITOS_ID= 18;**/
	
	public List<AnexoTresListadoEPDTO> listaDeAnexosRelacionEPInspeccionados(String anhio) {
		  List<RegistroVMA> registrosCompletos = registroVMARepository
				  .findRegistrosCompletos(anhio)
				  .stream()
				  .sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo()))
				  .collect(Collectors.toList());

		  List<AnexoTresListadoEPDTO> anexos = new ArrayList<>();

		  registrosCompletos.forEach(registroVMA -> {
//			  RespuestaVMA respuesta = respuestaVMARepository
//					  .findRespuestaByPreguntaIdAndRegistro(ALTERNATIVA_UND_IDENTIFICADOS_PARCIAL_ID, registroVMA.getIdRegistroVma());
//			  
			  RespuestaVMA UNDidentificados = respuestaVMARepository
					  .findRespuestaAlternativaPorRegistros(ALTERNATIVA_UND_IDENTIFICADOS_PARCIAL_ID, registroVMA.getIdRegistroVma());
			  
			  RespuestaVMA UNDinspeccionados = respuestaVMARepository
					  .findRespuestaAlternativaPorRegistros(ALTERNATIVA_UND_INSPECCIONADOS_PARCIAL_ID, registroVMA.getIdRegistroVma());
			  
			  RespuestaVMA UNDinscritos = respuestaVMARepository
					  .findRespuestaAlternativaPorRegistros(ALTERNATIVA_UND_INSCRITOS_ID, registroVMA.getIdRegistroVma());
			  
			 
			  anexos.add(new AnexoTresListadoEPDTO(
					  registroVMA.getEmpresa().getNombre(),
					  registroVMA.getEmpresa().getTipo(),
					  Integer.parseInt(UNDidentificados.getRespuesta()),
					  Integer.parseInt(UNDinspeccionados.getRespuesta()),
					  Integer.parseInt(UNDinscritos.getRespuesta())
					  )
			  );
		  });

		  return anexos;
	  }
	
	//anexo 4 - Detalle de porcentaje de toma de muestra inopinada de las EP  PREGUNTA_UND_TOMA_MUESTRA_INOPINADA_ID
	/*
	 * 
	 */
	public List<AnexoPorcentajeMuestraInopinadaDTO> listaDeAnexosTomaDeMuestrasInopinadas(String anhio) {
		  List<RegistroVMA> registrosCompletos = registroVMARepository
				  .findRegistrosCompletos(anhio)
				  .stream()
				  .sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo()))
				  .collect(Collectors.toList());

		  List<AnexoPorcentajeMuestraInopinadaDTO> anexos = new ArrayList<>();

		  registrosCompletos.forEach(registroVMA -> {

			 // RespuestaVMA UNDidentificados = respuestaVMARepository
			 //		  .findRespuestaAlternativaPorRegistros(ALTERNATIVA_UND_IDENTIFICADOS_PARCIAL_ID, registroVMA.getIdRegistroVma());
			 
			  RespuestaVMA UNDinscritos = respuestaVMARepository
					  .findRespuestaAlternativaPorRegistros(ALTERNATIVA_UND_INSCRITOS_ID, registroVMA.getIdRegistroVma());
			  
			  RespuestaVMA muestrasInopinadas = respuestaVMARepository
					  .findRespuestaByPreguntaIdAndRegistro(PREGUNTA_UND_TOMA_MUESTRA_INOPINADA_ID, registroVMA.getIdRegistroVma());
			 
			  int muestrasInopinadasvalor =  Integer.parseInt(muestrasInopinadas.getRespuesta());
			  int UNDinscritosvalor =  Integer.parseInt(UNDinscritos.getRespuesta());
			  double porcentaje =  ((double) muestrasInopinadasvalor / (double) UNDinscritosvalor) * 100;
			  
			// Redondea a un decimal usando Math.round
		        double porcentajeRedondeado = Math.round(porcentaje * 10.0) / 10.0;
		        
		        
			  //int muestrasInopinadas = Integer.parseInt(muestrasInopinadas.);
		       // int UNDinscritos = Integer.parseInt(UNDinscritosRespuesta);
			  //  MUESTRA INOPINADA  se debe calcular (N° MUESTRAS INOPINADAS / N° UND INSCRITOS EN EL REGISTRO DE UND) * 100
			  anexos.add(new AnexoPorcentajeMuestraInopinadaDTO(
					  registroVMA.getEmpresa().getNombre(),
					  registroVMA.getEmpresa().getTipo(),
					  UNDinscritosvalor,
					  muestrasInopinadasvalor,
					  porcentajeRedondeado
					  )
			  );
		  });

		  return anexos;
	  }
	
	//anexo 5 - Detalle de las EP que han realizado la evaluación de los VMA del Anexo 1 del reglamento de VMA
	/*
	 * N° MUESTRAS INOPINADAS  Se debe indicar el valor colocado en la pregunta “Número total de UND a los que se ha realizado la toma de muestra inopinada. (Parcial)” del formulario para el año seleccionado para la EPS indicada.
N° UND QUE SOPREPASAN PARAMETROS DEL ANEXO 1 Se debe indicar el valor colocado en la pregunta “Número de UND que sobrepasan algún(os) parámetro(s) del Anexo N° 1 del Reglamento de VMA(Parcial)” del formulario para el año seleccionado para la EPS indicada.
N° UND FACTURADOS POR ANEXO 1 Se debe indicar el valor colocado en la pregunta “Número de UND a los que se ha facturado por concepto de Pago adicional por exceso de concentración. (Parcial)” del formulario para el año seleccionado para la EPS indicada.
N° UND QUE REALIZARON PAGO POR ANEXO 1
	 */
	
	
	public List<AnexoEvaluacionVmaAnexo1DTO> listaDeAnexosEPevaluaronVMAAnexo1(String anhio) {
		  List<RegistroVMA> registrosCompletos = registroVMARepository
				  .findRegistrosCompletos(anhio)
				  .stream()
				  .sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo()))
				  .collect(Collectors.toList());

		  List<AnexoEvaluacionVmaAnexo1DTO> anexos = new ArrayList<>();

		  registrosCompletos.forEach(registroVMA -> {

			  
			  RespuestaVMA muestrasInopinadas = respuestaVMARepository
					  .findRespuestaByPreguntaIdAndRegistro(PREGUNTA_UND_TOMA_MUESTRA_INOPINADA_ID, registroVMA.getIdRegistroVma());
			 
			  RespuestaVMA UNDSobrepasanParametroAnexo1 = respuestaVMARepository
					  .findRespuestaAlternativaPorRegistros(ALTERNATIVA_UND_SOBREPASAN_PARAMETRO_ANEXO1_ID, registroVMA.getIdRegistroVma());
			  
			  RespuestaVMA UNDFacturadosPagoAdicional = respuestaVMARepository
					  .findRespuestaAlternativaPorRegistros(ALTERNATIVA_UND_FACTURARON_PAGO_ADICIONAL_ID, registroVMA.getIdRegistroVma());
			  
			  RespuestaVMA UNDRealizaronPagoAdicional = respuestaVMARepository
					  .findRespuestaAlternativaPorRegistros(ALTERNATIVA_UND_REALIZARON_PAGO_ADICIONAL_ID, registroVMA.getIdRegistroVma());
		
			  anexos.add(new AnexoEvaluacionVmaAnexo1DTO(
					  registroVMA.getEmpresa().getNombre(),
					  registroVMA.getEmpresa().getTipo(),
					  Integer.parseInt(muestrasInopinadas.getRespuesta()),
					  Integer.parseInt(UNDSobrepasanParametroAnexo1.getRespuesta()),
					  Integer.parseInt(UNDFacturadosPagoAdicional.getRespuesta()),
					  Integer.parseInt(UNDRealizaronPagoAdicional.getRespuesta())
					  )
			  );
		  });

		  return anexos;
	  }
	
	//anexo 6 - Detalle de las EP que han realizado la evaluación de los VMA del Anexo 2 del reglamento de VMA
	/*
	 *  N° MUESTRAS INOPINADAS  Se debe indicar el valor colocado en la pregunta “Número total de UND a los que se ha realizado la toma de muestra inopinada. (Parcial)” del formulario para el año seleccionado para la EPS indicada.
		 N° UND QUE SOPREPASAN PARAMETROS DEL ANEXO 2 Se debe indicar el valor colocado en la pregunta “Número de UND que sobrepasan algún(os) parámetro(s) del Anexo N° 2 del Reglamento de VMA.(Parcial)” del formulario para el año seleccionado para la EPS indicada.
		 N° UND CON PLAZO ADICIONAL  Se debe indicar el valor colocado en la pregunta “Número de UND a los que les ha otorgado un plazo adicional (hasta 18 meses) con el fin de implementar las acciones de mejora y acreditar el cumplimiento de los VMA. (Parcial)” del formulario para el año seleccionado para la EPS indicada.
		 N° UND CON ACUERDO SUSCRITO  Se debe indicar el valor colocado en la pregunta “Número de UND que han suscrito un acuerdo en el que se establece un plazo otorgado, por única vez, a fin de ejecutar las acciones de mejora y acreditar el cumplimiento de los VMA.” 
	 */
	public List<AnexoEvaluacionVmaAnexo2DTO> listaDeAnexosEPevaluaronVMAAnexo2(String anhio) {
		  List<RegistroVMA> registrosCompletos = registroVMARepository
				  .findRegistrosCompletos(anhio)
				  .stream()
				  .sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo()))
				  .collect(Collectors.toList());

		  List<AnexoEvaluacionVmaAnexo2DTO> anexos = new ArrayList<>();

		  registrosCompletos.forEach(registroVMA -> {
			  
			  RespuestaVMA muestrasInopinadas = respuestaVMARepository
					  .findRespuestaByPreguntaIdAndRegistro(PREGUNTA_UND_TOMA_MUESTRA_INOPINADA_ID, registroVMA.getIdRegistroVma());
			 
			  RespuestaVMA UNDSobrepasanParametroAnexo2 = respuestaVMARepository
					  .findRespuestaAlternativaPorRegistros(ALTERNATIVA_UND_SOBREPASAN_PARAMETRO_ANEXO2_ID, registroVMA.getIdRegistroVma());
			  
			  RespuestaVMA UNDConPlazoAdicional = respuestaVMARepository
					  .findRespuestaAlternativaPorRegistros(ALTERNATIVA_UND_OTORGADO_PLAZO_ADICIONAL_ID, registroVMA.getIdRegistroVma());
			  
			  RespuestaVMA UNDSuscritoAcuerdo = respuestaVMARepository
					  .findRespuestaAlternativaPorRegistros(ALTERNATIVA_UND_SUSCRITO_PLAZO_OTORGADO_ID, registroVMA.getIdRegistroVma());
		
			  anexos.add(new AnexoEvaluacionVmaAnexo2DTO(
					  registroVMA.getEmpresa().getNombre(),
					  registroVMA.getEmpresa().getTipo(),
					  Integer.parseInt(muestrasInopinadas.getRespuesta()),
					  Integer.parseInt(UNDSobrepasanParametroAnexo2.getRespuesta()),
					  Integer.parseInt(UNDConPlazoAdicional.getRespuesta()),
					  Integer.parseInt(UNDSuscritoAcuerdo.getRespuesta())
					  )
			  );
		  });

		  return anexos;
	  }
	
	//anexo 7 - Detalle de las EP que han realizado la atención de reclamos referidos a VMA
		/*
		 * 	EMPRESA PRESTADORA
			TAMAÑO
			N° UND INSCRITOS EN EL REGISTRO DE UND
			N° RECLAMOS POR VMA
			N° RECLAMOS RESULTOS FUNDADOS

		 */
	
	public List<AnexoReclamosVMADTO> listaDeAnexosEPSAtendieronReclamos(String anhio) {
		  List<RegistroVMA> registrosCompletos = registroVMARepository
				  .findRegistrosCompletos(anhio)
				  .stream()
				  .sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo()))
				  .collect(Collectors.toList());

		  List<AnexoReclamosVMADTO> anexos = new ArrayList<>();

		  registrosCompletos.forEach(registroVMA -> {
			  
			  RespuestaVMA UNDinscritos = respuestaVMARepository
					  .findRespuestaAlternativaPorRegistros(ALTERNATIVA_UND_INSCRITOS_ID, registroVMA.getIdRegistroVma());
			
			  RespuestaVMA reclamosRecibidosVMA = respuestaVMARepository
					  .findRespuestaByPreguntaIdAndRegistro(PREGUNTA_CANTIDAD_RECLAMOS_RECIBIDOS_VMA_ID, registroVMA.getIdRegistroVma());
					  
			  RespuestaVMA reclamosFundadosVMA = respuestaVMARepository
					  .findRespuestaByPreguntaIdAndRegistro(PREGUNTA_CANTIDAD_RECLAMOS_FUNDADOS_VMA_ID, registroVMA.getIdRegistroVma());
			  
			  anexos.add(new AnexoReclamosVMADTO(
					  registroVMA.getEmpresa().getNombre(),
					  registroVMA.getEmpresa().getTipo(),
					  Integer.parseInt(UNDinscritos.getRespuesta()),
					  Integer.parseInt(reclamosRecibidosVMA.getRespuesta()),
					  Integer.parseInt(reclamosFundadosVMA.getRespuesta())
					  )
			  );
		  });

		  return anexos;
	  }
	
	
	//anexo 8 - Detalle de los costos de identificación, inspección y registro de los UND
	
	
	// 
	/*
	 * COSTO EN IDENTIFICACIÓN, INSPECCIÓN Y REGISTRO DE UND (S/)  PREGUNTA_COSTO_TOTAL_ANUAL_UND_ID
	N° UND IDENTIFICADOS (CANT)
		COSTO ANUAL POR UND (S/) Se calcula realizando la siguiente formula por cada EPS COSTO EN IDENTIFICACIÓN,
	//INSPECCIÓN Y REGISTRO DE UND (S/)/ N° UND IDENTIFICADOS (CANT)  por cada EPS 
	 */
	public List<AnexoCostoTotalUNDDTO> anexoDetalleCostosUND(String anhio) {
		  List<RegistroVMA> registrosCompletos = registroVMARepository
				  .findRegistrosCompletos(anhio)
				  .stream()
				  .sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo()))
				  .collect(Collectors.toList());

		  List<AnexoCostoTotalUNDDTO> anexos = new ArrayList<>();

		  registrosCompletos.forEach(registroVMA -> {
			  
			 
			  RespuestaVMA costoTotalAnualUND = respuestaVMARepository
					  .findRespuestaByPreguntaIdAndRegistro(PREGUNTA_COSTO_TOTAL_ANUAL_UND_ID, registroVMA.getIdRegistroVma());
			  
			  RespuestaVMA UNDidentificados = respuestaVMARepository
					  .findRespuestaAlternativaPorRegistros(ALTERNATIVA_UND_IDENTIFICADOS_PARCIAL_ID, registroVMA.getIdRegistroVma());
			  
			 double costoAnual = (double) (Double.parseDouble(costoTotalAnualUND.getRespuesta()) / Double.parseDouble(UNDidentificados.getRespuesta()));
			  
			  anexos.add(new AnexoCostoTotalUNDDTO(
					  registroVMA.getEmpresa().getNombre(),
					  registroVMA.getEmpresa().getTipo(),
					  Double.parseDouble(costoTotalAnualUND.getRespuesta()),
					  Double.parseDouble(UNDidentificados.getRespuesta()),
					  costoAnual
					  )
			  );
		  });

		  return anexos;
	  }
	
	
	
}

package pe.gob.sunass.vma.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import pe.gob.sunass.vma.assembler.FichaAssembler;
import pe.gob.sunass.vma.assembler.RegistroVMAAssembler;
import pe.gob.sunass.vma.dto.*;
import pe.gob.sunass.vma.dto.anexos.AnexoCostoTotalMuestrasInopinadasDTO;
import pe.gob.sunass.vma.dto.anexos.AnexoCostoTotalUNDDTO;
import pe.gob.sunass.vma.dto.anexos.AnexoCostoTotalesIncurridosDTO;
import pe.gob.sunass.vma.dto.anexos.AnexoEvaluacionVmaAnexo1DTO;
import pe.gob.sunass.vma.dto.anexos.AnexoEvaluacionVmaAnexo2DTO;
import pe.gob.sunass.vma.dto.anexos.AnexoIngresosImplVmaDTO;
import pe.gob.sunass.vma.dto.anexos.AnexoPorcentajeMuestraInopinadaDTO;
import pe.gob.sunass.vma.dto.anexos.AnexoReclamosVMADTO;
import pe.gob.sunass.vma.dto.anexos.AnexoRegistroVmaDTO;
import pe.gob.sunass.vma.dto.anexos.AnexoRespuestaSiDTO;
import pe.gob.sunass.vma.dto.anexos.AnexoTresListadoEPDTO;
import pe.gob.sunass.vma.model.*;
import pe.gob.sunass.vma.model.cuestionario.Archivo;
import pe.gob.sunass.vma.model.cuestionario.RegistroVMA;
import pe.gob.sunass.vma.model.cuestionario.RespuestaVMA;
import pe.gob.sunass.vma.repository.*;
import pe.gob.sunass.vma.util.PreguntasAlternativasProperties;
import pe.gob.sunass.vma.util.UserUtil;
import pe.gob.sunass.vma.constants.Constants;

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
	
	@Autowired
	private UserUtil userUtil;

	@Autowired
	private PreguntasAlternativasProperties preguntasAlternativasVMA;
	
	//ya no se usaria, al existir properties de estos valores:
	
//	private final int PREGUNTA_SI_NO_ID = 1;
//	private final int PREGUNTA_NUMERO_TRABAJADORES_EMPRESA_PRESTADORA_ID = 3;
//	private final int ALTERNATIVA_UND_IDENTIFICADOS_PARCIAL_ID = 3;
//	private final int ALTERNATIVA_UND_INSPECCIONADOS_PARCIAL_ID = 5;
//	private final int ALTERNATIVA_UND_INSCRITOS_ID = 11;
//	private final int PREGUNTA_UND_TOMA_MUESTRA_INOPINADA_ID = 11;
//	private final int ALTERNATIVA_UND_FACTURARON_PAGO_ADICIONAL_ID = 17;
//	private final int ALTERNATIVA_UND_REALIZARON_PAGO_ADICIONAL_ID = 19;
//	private final int ALTERNATIVA_UND_SOBREPASAN_PARAMETRO_ANEXO1_ID = 15;
//	private final int ALTERNATIVA_UND_SOBREPASAN_PARAMETRO_ANEXO2_ID = 21;
//	private final int ALTERNATIVA_UND_OTORGADO_PLAZO_ADICIONAL_ID = 23;
//	private final int ALTERNATIVA_UND_SUSCRITO_PLAZO_OTORGADO_ID = 25;
//	private final int PREGUNTA_CANTIDAD_RECLAMOS_RECIBIDOS_VMA_ID = 19;
//	private final int PREGUNTA_CANTIDAD_RECLAMOS_FUNDADOS_VMA_ID = 20;
//	private final int PREGUNTA_COSTO_TOTAL_ANUAL_UND_ID = 23;
//	private final int PREGUNTA_COSTO_TOTAL_ANUAL_MUESTRAS_INOPINADAS_ID = 24;
//	private final int PREGUNTA_OTROS_GASTOS_IMPLEMENTACION_ID = 29;
//	private final int PREGUNTA_INGRESOS_FACTURADOS_VMA_ID = 21;
//	private final int PREGUNTA_REMITIO_INFORME_TECNICO_ID = 31;
	
	
	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public List<RegistroVMADTO> findAllOrderById(String username) throws Exception {
		Usuario usuario = usuarioRepository.findByUserName(username).orElseThrow();
		List<RegistroVMA> listRegistroVMA = null;
		if (usuario.getRole().getIdRol() == Constants.Security.Roles.ID_AdministradorDAP ||
				usuario.getRole().getIdRol() == Constants.Security.Roles.ID_Consultor ) {
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
		if (idRegistroVMA != null) {
			registroVMA = registroVMARepository.findById(idRegistroVMA).orElseThrow();
			registroVMA.setUpdatedAt(new Date());
			registroVMA.setEstado(registroRequest.isRegistroValido() ? Constants.ESTADO_COMPLETO : Constants.ESTADO_INCOMPLETO);
			saveRespuestas(registroRequest.getRespuestas(), registroVMA);
			return registroVMA.getIdRegistroVma();
		} else {
			Usuario usuario = usuarioRepository.findByUserName(username).orElseThrow();
			RegistroVMA nuevoRegistro = new RegistroVMA();
			Empresa empresa = new Empresa();
			empresa.setIdEmpresa(registroRequest.getIdEmpresa());
			nuevoRegistro.setEmpresa(usuario.getEmpresa());
			nuevoRegistro.setUsername(username);
			nuevoRegistro.setEstado(registroRequest.isRegistroValido() ? Constants.ESTADO_COMPLETO : Constants.ESTADO_INCOMPLETO);
			nuevoRegistro.setFichaRegistro(fichaRepository.findFichaRegistroActual());
			nuevoRegistro.setCreatedAt(new Date());
			nuevoRegistro.setIdUsuarioRegistro(userUtil.getCurrentUserId());
			RegistroVMA registroDB = registroVMARepository.save(nuevoRegistro);
			saveRespuestas(registroRequest.getRespuestas(), registroDB);
			return registroDB.getIdRegistroVma();
		}
	}

	@Transactional
	public void saveRespuestaVMAArchivo(MultipartFile file, Integer registroVMAId, Integer preguntaId,
		Integer respuestaId) throws IOException {

		Optional<RegistroVMA> optRegistroVMA = this.registroVMARepository.findByIdRegistroVma(registroVMAId);
		
		//ArchivoDTO archivoDTO = alfrescoService.uploadFile(file);
		ArchivoDTO archivoDTO = alfrescoService.uploadFile(file, optRegistroVMA.get());

		if (Objects.nonNull(respuestaId)) {

			Optional<RespuestaVMA> respuestaOpt = respuestaVMARepository.findById(respuestaId);
			respuestaOpt.ifPresent(respuestaVMA -> {
				Archivo archivoByIdAlfresco = archivoRepository.findArchivoByIdAlfresco(respuestaVMA.getRespuesta());
				if (Objects.nonNull(archivoByIdAlfresco)) {
					archivoRepository.deleteById(archivoByIdAlfresco.getIdArchivo());
				}

				alfrescoService.deleteFile(respuestaVMA.getRespuesta());
			});
		}

		RegistroVMA registroVMA = new RegistroVMA();
		registroVMA.setIdRegistroVma(registroVMAId);
		respuestaVMARepository
				.save(new RespuestaVMA(respuestaId, null, archivoDTO.getIdAlfresco(), registroVMA, preguntaId));  // pendiente  de guardar fechas y/o usuario, x auditoria
	}


	
	public Page<RegistroVMA> searchRegistroVMA(Integer empresaId, String estado, Date startDate, Date endDate,
			String year, String username, int page, int pageSize, String search) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<RegistroVMA> query = cb.createQuery(RegistroVMA.class);
		Root<RegistroVMA> registroVMA = query.from(RegistroVMA.class);
		Usuario usuario = usuarioRepository.findByUserName(username).orElseThrow();
		Pageable pageable = PageRequest.of(page, pageSize);

		if (usuario.getRole().getIdRol() == 2 || usuario.getRole().getIdRol() == 4) {
			List<Predicate> predicates = new ArrayList<>();

			if (empresaId != null) {
				predicates.add(cb.or(
						cb.isNull(registroVMA.get("empresa")),
						cb.equal(registroVMA.get("empresa").get("idEmpresa"), empresaId)
				));
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
			if (search != null) {
				Predicate namePredicate = cb.like(cb.lower(registroVMA.get("empresa").get("nombre")),
						"%" + search.toLowerCase() + "%");
				Predicate typePredicate = cb.like(cb.lower(registroVMA.get("empresa").get("tipo")),
						"%" + search.toLowerCase() + "%");
				Predicate regimenPredicate = cb.like(cb.lower(registroVMA.get("empresa").get("regimen")),
						"%" + search.toLowerCase() + "%");
				Predicate estadoPredicate = cb.like(cb.lower(registroVMA.get("estado")),
						"%" + search.toLowerCase() + "%");
				Predicate anioPredicate = cb.like(cb.lower(registroVMA.get("fichaRegistro").get("anio")),
						"%" + search.toLowerCase() + "%");
				predicates.add(cb.or(namePredicate, typePredicate,regimenPredicate, estadoPredicate, anioPredicate));
			}

			query.where(predicates.toArray(new Predicate[0]));
			query.orderBy(cb.asc(registroVMA.get("idRegistroVma")));
			TypedQuery<RegistroVMA> typedQuery = entityManager.createQuery(query);

			// Calculate pagination
			int firstResult = pageable.getPageNumber() * pageable.getPageSize();
			typedQuery.setFirstResult(firstResult);
			typedQuery.setMaxResults(pageable.getPageSize());

			List<RegistroVMA> resultList = typedQuery.getResultList();

			// Count query
			CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
			Root<RegistroVMA> countRoot = countQuery.from(RegistroVMA.class);
			countQuery.select(cb.count(countRoot));
			countQuery.where(predicates.toArray(new Predicate[0]));
			Long total = entityManager.createQuery(countQuery).getSingleResult();

			return new PageImpl<>(resultList, pageable, total);
		}

     	return this.registroVMARepository.registrosPorIdEmpresa(usuario.getEmpresa().getIdEmpresa(), pageable);
//		return this.registroVMARepository.registrosPorIdEmpresa2(usuario.getEmpresa().getIdEmpresa(), pageable);
	}


//	private void saveRespuestas(List<RespuestaDTO> respuestasRequest, RegistroVMA registro) {
//		respuestaVMARepository.saveAll(respuestasRequest.stream()
//				.map(respuesta -> respuestaDtoToRespuestaVMA(respuesta, registro)).collect(Collectors.toList()));
//	}
	
	private void saveRespuestas(List<RespuestaDTO> respuestasRequest, RegistroVMA registro) {
	    respuestaVMARepository.saveAll(respuestasRequest.stream()
	        .map(respuesta -> {
	            RespuestaVMA respuestaVMA = respuestaDtoToRespuestaVMA(respuesta, registro);
	            
	            //pendiente, falta mas  datos de auditoria , fecha actualizacion e id usuario actualizacion
	            
	            respuestaVMA.setFechaRegistro(new Date());
	            respuestaVMA.setIdUsuarioRegistro(userUtil.getCurrentUserId());
	            // Setear otros campos si es necesario
	            
	            return respuestaVMA;
	        }).collect(Collectors.toList()));
	}


	private RespuestaVMA respuestaDtoToRespuestaVMA(RespuestaDTO respuestaDTO, RegistroVMA registroVMA) {
		return new RespuestaVMA(respuestaDTO.getIdRespuesta(), respuestaDTO.getIdAlternativa(),
				respuestaDTO.getRespuesta(), registroVMA, respuestaDTO.getIdPregunta());
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
	
	// ... Anexos...

	//anexo 1
	public List<AnexoRegistroVmaDTO> listaDeAnexosRegistrosVmaDTO(String anhio) {
		List<Empresa> empresasDB = empresaRepository.findAll();

	

		// Filtrar empresas de tipo "NINGUNO", ordenar por tipo y mapear a DTO
		return empresasDB.stream().filter(empresa -> !Constants.TIPO_EMPRESA_SUNASS.equals(empresa.getTipo()))
				.sorted(Comparator.comparing(Empresa::getTipo)) // Ordenar por tipo, si es necesario
				.map(empresa -> mapToAnexoRegistroVmaDTO(empresa, anhio)) // Mapear a DTO
				.collect(Collectors.toList()); // Colectar en una lista

	}

	//anexo 2
	public List<AnexoRespuestaSiDTO> listaDeAnexosRegistroMarcaronSi(String anhio) {
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());

		List<AnexoRespuestaSiDTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {
			RespuestaVMA respuesta = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(preguntasAlternativasVMA.getId_pregunta_si_no(),
					registroVMA.getIdRegistroVma());
			RespuestaVMA respuestaNroTrabajadores = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					preguntasAlternativasVMA.getId_pregunta_nro_trabajadores_eps(), registroVMA.getIdRegistroVma());

			anexos.add(new AnexoRespuestaSiDTO(registroVMA.getEmpresa().getNombre(), registroVMA.getEmpresa().getTipo(),
					respuesta.getRespuesta(), Integer.parseInt(respuestaNroTrabajadores.getRespuesta())));
		});

		return anexos;
	}

	private AnexoRegistroVmaDTO mapToAnexoRegistroVmaDTO(Empresa empresa, String anio) {
		RegistroVMA registroVmaPorAnhio = registroVMARepository.findRegistroVmaPorAnhio(empresa.getIdEmpresa(), anio);
		boolean registroCompleto = Objects.nonNull(registroVmaPorAnhio)
				&& registroVmaPorAnhio.getEstado().equals(Constants.ESTADO_COMPLETO);
		boolean remitioInforme = false;
		if (Objects.nonNull(registroVmaPorAnhio)) {
			remitioInforme = respuestaVMARepository.isRespuestaArchivoInformacionCompleto(preguntasAlternativasVMA.getId_pregunta_remitio_informe_tecnico(),
					registroVmaPorAnhio.getIdRegistroVma());
		}

		return new AnexoRegistroVmaDTO(empresa.getNombre(), empresa.getTipo(), empresa.getRegimen().equals(Constants.Regimen.RAT),
				registroCompleto, registroCompleto && remitioInforme);
	}
	
	
	// anexo 3 - Relación de las EP que han realizado avances en la inspección y/o
	// inscripción en sus registros de los UND bajo su ámbito de influencia


	public List<AnexoTresListadoEPDTO> listaDeAnexosRelacionEPInspeccionados(String anhio) {
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());

		List<AnexoTresListadoEPDTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {
	  
			RespuestaVMA UNDidentificados = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					preguntasAlternativasVMA.getId_alternativa_nro_und_identificados_parcial(), registroVMA.getIdRegistroVma());

			RespuestaVMA UNDinspeccionados = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					preguntasAlternativasVMA.getId_alternativa_nro_und_inspeccionados_parcial(), registroVMA.getIdRegistroVma());

			RespuestaVMA UNDinscritos = respuestaVMARepository
					.findRespuestaAlternativaPorRegistros(preguntasAlternativasVMA.getId_alternativa_und_inscritos(), registroVMA.getIdRegistroVma());

			anexos.add(new AnexoTresListadoEPDTO(registroVMA.getEmpresa().getNombre(),
					registroVMA.getEmpresa().getTipo(), Integer.parseInt(UNDidentificados.getRespuesta()),
					Integer.parseInt(UNDinspeccionados.getRespuesta()), Integer.parseInt(UNDinscritos.getRespuesta())));
		});

		return anexos;
	}

	// anexo 4 - Detalle de porcentaje de toma de muestra inopinada de las EP
	
	
	public List<AnexoPorcentajeMuestraInopinadaDTO> listaDeAnexosTomaDeMuestrasInopinadas(String anhio) {
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());

		List<AnexoPorcentajeMuestraInopinadaDTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {

			RespuestaVMA UNDinscritos = respuestaVMARepository
					.findRespuestaAlternativaPorRegistros(preguntasAlternativasVMA.getId_alternativa_und_inscritos(), registroVMA.getIdRegistroVma());

			RespuestaVMA muestrasInopinadas = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					preguntasAlternativasVMA.getId_pregunta_und_toma_muestra_inopinada(), registroVMA.getIdRegistroVma());

			int muestrasInopinadasvalor = Integer.parseInt(muestrasInopinadas.getRespuesta());
			int UNDinscritosvalor = Integer.parseInt(UNDinscritos.getRespuesta());
			double porcentaje = ((double) muestrasInopinadasvalor / (double) UNDinscritosvalor) * 100;

			// Redondea a un decimal usando Math.round
			double porcentajeRedondeado = Math.round(porcentaje * 10.0) / 10.0;

			
			anexos.add(new AnexoPorcentajeMuestraInopinadaDTO(registroVMA.getEmpresa().getNombre(),
					registroVMA.getEmpresa().getTipo(), UNDinscritosvalor, muestrasInopinadasvalor,
					porcentajeRedondeado));
		});

		return anexos;
	}

	// anexo 5 - Detalle de las EP que han realizado la evaluación de los VMA del  Anexo 1 del reglamento de VMA
	

	public List<AnexoEvaluacionVmaAnexo1DTO> listaDeAnexosEPevaluaronVMAAnexo1(String anhio) {
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());

		List<AnexoEvaluacionVmaAnexo1DTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {

			RespuestaVMA muestrasInopinadas = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					preguntasAlternativasVMA.getId_pregunta_und_toma_muestra_inopinada(), registroVMA.getIdRegistroVma());
			logger.info(" Integer.parseInt(muestrasInopinadas.getRespuesta()) preg11- "+Integer.parseInt(muestrasInopinadas.getRespuesta()));
			RespuestaVMA UNDSobrepasanParametroAnexo1 = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					preguntasAlternativasVMA.getId_alternativa_und_sobrepasan_parametro_anexo1(), registroVMA.getIdRegistroVma());

			RespuestaVMA UNDFacturadosPagoAdicional = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					preguntasAlternativasVMA.getId_alternativa_und_facturaron_pago_adicional(), registroVMA.getIdRegistroVma());

			RespuestaVMA UNDRealizaronPagoAdicional = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					preguntasAlternativasVMA.getId_alternativa_und_realizaron_pago_adicional(), registroVMA.getIdRegistroVma());

			anexos.add(new AnexoEvaluacionVmaAnexo1DTO(registroVMA.getEmpresa().getNombre(),
					registroVMA.getEmpresa().getTipo(), Integer.parseInt(muestrasInopinadas.getRespuesta()),
					Integer.parseInt(UNDSobrepasanParametroAnexo1.getRespuesta()),
					Integer.parseInt(UNDFacturadosPagoAdicional.getRespuesta()),
					Integer.parseInt(UNDRealizaronPagoAdicional.getRespuesta())));
		});

		return anexos;
	}

	// anexo 6 - Detalle de las EP que han realizado la evaluación de los VMA del  Anexo 2 del reglamento de VMA


	public List<AnexoEvaluacionVmaAnexo2DTO> listaDeAnexosEPevaluaronVMAAnexo2(String anhio) {
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());

		List<AnexoEvaluacionVmaAnexo2DTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {

			RespuestaVMA muestrasInopinadas = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					preguntasAlternativasVMA.getId_pregunta_und_toma_muestra_inopinada(), registroVMA.getIdRegistroVma());

			RespuestaVMA UNDSobrepasanParametroAnexo2 = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					preguntasAlternativasVMA.getId_alternativa_und_sobrepasan_parametro_anexo2(), registroVMA.getIdRegistroVma());

			RespuestaVMA UNDConPlazoAdicional = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					preguntasAlternativasVMA.getId_alternativa_und_otorgado_plazo_adicional(), registroVMA.getIdRegistroVma());

			RespuestaVMA UNDSuscritoAcuerdo = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					preguntasAlternativasVMA.getId_alternativa_und_suscrito_plazo_otorgado(), registroVMA.getIdRegistroVma());

			anexos.add(new AnexoEvaluacionVmaAnexo2DTO(registroVMA.getEmpresa().getNombre(),
					registroVMA.getEmpresa().getTipo(), Integer.parseInt(muestrasInopinadas.getRespuesta()),
					Integer.parseInt(UNDSobrepasanParametroAnexo2.getRespuesta()),
					Integer.parseInt(UNDConPlazoAdicional.getRespuesta()),
					Integer.parseInt(UNDSuscritoAcuerdo.getRespuesta())));
		});

		return anexos;
	}

	// anexo 7 - Detalle de las EP que han realizado la atención de reclamos  referidos a VMA
	

	public List<AnexoReclamosVMADTO> listaDeAnexosEPSAtendieronReclamos(String anhio) {
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());

		List<AnexoReclamosVMADTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {

			RespuestaVMA UNDinscritos = respuestaVMARepository
					.findRespuestaAlternativaPorRegistros(preguntasAlternativasVMA.getId_alternativa_und_inscritos(), registroVMA.getIdRegistroVma());

			RespuestaVMA reclamosRecibidosVMA = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					preguntasAlternativasVMA.getId_pregunta_nro_reclamos_recibidos(), registroVMA.getIdRegistroVma());

			RespuestaVMA reclamosFundadosVMA = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					preguntasAlternativasVMA.getId_pregunta_nro_reclamos_fundados(), registroVMA.getIdRegistroVma());

			anexos.add(new AnexoReclamosVMADTO(registroVMA.getEmpresa().getNombre(), registroVMA.getEmpresa().getTipo(),
					Integer.parseInt(UNDinscritos.getRespuesta()),
					Integer.parseInt(reclamosRecibidosVMA.getRespuesta()),
					Integer.parseInt(reclamosFundadosVMA.getRespuesta())));
		});

		return anexos;
	}

	// anexo 8 - Detalle de los costos de identificación, inspección y registro de los UND

	
	public List<AnexoCostoTotalUNDDTO> anexoDetalleCostosUND(String anhio) {
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());

		List<AnexoCostoTotalUNDDTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {

			RespuestaVMA costoTotalAnualUND = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					preguntasAlternativasVMA.getId_pregunta_costo_total_anual_und(), registroVMA.getIdRegistroVma());

			RespuestaVMA UNDidentificados = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					preguntasAlternativasVMA.getId_alternativa_nro_und_identificados_parcial(), registroVMA.getIdRegistroVma());

			BigDecimal costoAnual = new BigDecimal(costoTotalAnualUND.getRespuesta())
				    .divide(new BigDecimal(UNDidentificados.getRespuesta()), 1, RoundingMode.HALF_UP);
	
			
			anexos.add(new AnexoCostoTotalUNDDTO(registroVMA.getEmpresa().getNombre(),
					registroVMA.getEmpresa().getTipo(), new BigDecimal(costoTotalAnualUND.getRespuesta()),
					Integer.parseInt(UNDidentificados.getRespuesta()), costoAnual));
		});

		return anexos;
	}
	
	// anexo 9 - Detalle de los costos totales por toma de muestras inopinadas
	
	
	public List<AnexoCostoTotalMuestrasInopinadasDTO> listaAnexosCostosMuestrasInopinadas(String anhio) {
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());

		List<AnexoCostoTotalMuestrasInopinadasDTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {

			RespuestaVMA costoTotalAnualMuestras = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					preguntasAlternativasVMA.getId_pregunta_costo_anual_muestras_inopinadas(), registroVMA.getIdRegistroVma());

			RespuestaVMA UNDMuestraInopinada = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					preguntasAlternativasVMA.getId_pregunta_und_toma_muestra_inopinada(), registroVMA.getIdRegistroVma());  

			BigDecimal costoAnual = new BigDecimal(costoTotalAnualMuestras.getRespuesta())
				    .divide(new BigDecimal(UNDMuestraInopinada.getRespuesta()), 2, RoundingMode.HALF_UP);
			
			anexos.add(new AnexoCostoTotalMuestrasInopinadasDTO(registroVMA.getEmpresa().getNombre(),
					registroVMA.getEmpresa().getTipo(), new BigDecimal(costoTotalAnualMuestras.getRespuesta()),
					Integer.parseInt(UNDMuestraInopinada.getRespuesta()), costoAnual));
		});

		return anexos;
	}
	
	//anexo 10 - Detalle de los costos totales incurridos por las Empresas Prestadoras
	
	
	public List<AnexoCostoTotalesIncurridosDTO> listaAnexosCostosTotalesIncurridos(String anhio) {
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());

		List<AnexoCostoTotalesIncurridosDTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {

			RespuestaVMA costoTotalAnualUND = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					preguntasAlternativasVMA.getId_pregunta_costo_total_anual_und(), registroVMA.getIdRegistroVma());

			RespuestaVMA costoTotalAnualMuestrasInopinadas = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					preguntasAlternativasVMA.getId_pregunta_costo_anual_muestras_inopinadas(), registroVMA.getIdRegistroVma());  

			RespuestaVMA costoOtrosGastosImplementacion = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					preguntasAlternativasVMA.getId_pregunta_otros_gastos_implementacion(), registroVMA.getIdRegistroVma());  

			anexos.add(new AnexoCostoTotalesIncurridosDTO(registroVMA.getEmpresa().getNombre(),
					registroVMA.getEmpresa().getTipo(), new BigDecimal(costoTotalAnualUND.getRespuesta()),
					new BigDecimal(costoTotalAnualMuestrasInopinadas.getRespuesta()), 
					new BigDecimal(costoOtrosGastosImplementacion.getRespuesta())));
		});

		return anexos;
	}
	
	//anexo 11 , Detalle de los ingresos facturados durante el año actual, por conceptos de VMA
	
	public List<AnexoIngresosImplVmaDTO> listaDeAnexosIngresosVMA(String anhio) {
		  List<RegistroVMA> registrosCompletos = registroVMARepository
				  .findRegistrosCompletos(anhio)
				  .stream()
				  .sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo()))
				  .collect(Collectors.toList());

		  List<AnexoIngresosImplVmaDTO> anexos = new ArrayList<>();
		  
		  registrosCompletos.forEach(registroVMA -> {
			  RespuestaVMA respuestaIngresos = respuestaVMARepository
					  .findRespuestaByPreguntaIdAndRegistro(preguntasAlternativasVMA.getId_pregunta_ingresos_facturados(), registroVMA.getIdRegistroVma());
			  
			   anexos.add(new AnexoIngresosImplVmaDTO(
					  registroVMA.getEmpresa().getNombre(),
					  registroVMA.getEmpresa().getTipo(),
					  new BigDecimal(respuestaIngresos.getRespuesta())
					  )
			  );
		  });

		  return anexos;
	  }


	public void actualizarEstadoIncompleto(Integer id) {
		Optional<RegistroVMA> registro = registroVMARepository.findById(id);

		if(registro.isPresent()){
			RegistroVMA registroVMA = registro.get();
			registroVMA.setEstado("INCOMPLETO");
			registroVMA.setUpdatedAt(new Date());
			registroVMARepository.save(registroVMA);
		} else {
			throw new RuntimeException("Registro no encontrado");
		}
	}
	
	
	//
	 @Transactional(Transactional.TxType.REQUIRES_NEW)
	  public RegistroVMADTO obtenerEmpresaSinCompletarRegistroVMA()  throws Exception{
		  
		 RegistroVMADTO dto = null;
	    Optional<RegistroVMA> opt = this.registroVMARepository.findEmpresaSinCompletarRegistro(userUtil.getCurrentUserId());
	    
	    if (opt.isPresent()) {
	    	RegistroVMA registrovma = opt.get();
	        dto = RegistroVMAAssembler.buildDtoModel(registrovma);
	        return dto;
	    }else {
	    	return null; 
	    }

	  }
	
}


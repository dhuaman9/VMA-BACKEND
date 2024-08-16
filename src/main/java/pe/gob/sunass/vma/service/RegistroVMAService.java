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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

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

	
	private final int PREGUNTA_SI_NO_ID = 1;
	private final int PREGUNTA_NUMERO_TRABAJADORES_EMPRESA_PRESTADORA_ID = 3;
	private final int ALTERNATIVA_UND_IDENTIFICADOS_PARCIAL_ID = 16;
	private final int ALTERNATIVA_TOTAL_UND_INSPECCIONADOS_PARCIAL_ID = 22;
	private final int ALTERNATIVA_UND_INSCRITOS_ID = 18;
	private final int PREGUNTA_UND_TOMA_MUESTRA_INOPINADA_ID = 11;
	private final int ALTERNATIVA_UND_FACTURARON_PAGO_ADICIONAL_ID = 3;
	private final int ALTERNATIVA_UND_REALIZARON_PAGO_ADICIONAL_ID = 35;
	private final int ALTERNATIVA_UND_SOBREPASAN_PARAMETRO_ANEXO1_ID = 1;
	private final int ALTERNATIVA_UND_SOBREPASAN_PARAMETRO_ANEXO2_ID = 26;
	private final int ALTERNATIVA_UND_OTORGADO_PLAZO_ADICIONAL_ID = 28;
	private final int ALTERNATIVA_UND_SUSCRITO_PLAZO_OTORGADO_ID = 30;
	private final int PREGUNTA_CANTIDAD_RECLAMOS_RECIBIDOS_VMA_ID = 19;
	private final int PREGUNTA_CANTIDAD_RECLAMOS_FUNDADOS_VMA_ID = 20;
	private final int PREGUNTA_COSTO_TOTAL_ANUAL_UND_ID = 23;
	private final int PREGUNTA_COSTO_TOTAL_ANUAL_MUESTRAS_INOPINADAS_ID = 24;
	private final int PREGUNTA_COSTO_OTROS_GASTOS_IMPLEMENTACION_ID = 29;
	private final int PREGUNTA_INGRESOS_FACTURADOS_VMA_ID = 21;
	private final int ID_PREGUNTA_REMITIO_INFORME = 31;
	
	
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
	
	 //paginacion
	  @Transactional(Transactional.TxType.REQUIRES_NEW)
	  public Page<RegistroVMADTO> findAllOrderById(Pageable pageable) throws Exception {
	    Page<RegistroVMA> pageDomain = this.registroVMARepository.findAllByOrderByIdRegistroVma(pageable);
	    Page<RegistroVMADTO> pageDTO = RegistroVMAAssembler.buildDtoModelCollection(pageDomain);

	    return pageDTO;
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

	//anexo 1
	public List<AnexoRegistroVmaDTO> listaDeAnexosRegistrosVmaDTO(String anhio) {
		List<Empresa> empresasDB = empresaRepository.findAll();

		// empresasDB.sort(Comparator.comparing(Empresa::getTipo));
		// return empresasDB.stream().map(empresa -> mapToAnexoRegistroVmaDTO(empresa,
		// anhio)).collect(Collectors.toList());

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
			RespuestaVMA respuesta = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(PREGUNTA_SI_NO_ID,
					registroVMA.getIdRegistroVma());
			RespuestaVMA respuestaNroTrabajadores = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					PREGUNTA_NUMERO_TRABAJADORES_EMPRESA_PRESTADORA_ID, registroVMA.getIdRegistroVma());

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
			remitioInforme = respuestaVMARepository.isRespuestaArchivoInformacionCompleto(ID_PREGUNTA_REMITIO_INFORME,
					registroVmaPorAnhio.getIdRegistroVma());
		}

		return new AnexoRegistroVmaDTO(empresa.getNombre(), empresa.getTipo(), empresa.getRegimen().equals(Constants.Regimen.RAT),
				registroCompleto, registroCompleto && remitioInforme);
	}

	public List<RegistroVMA> searchRegistroVMA(Integer empresaId, String estado, Date startDate, Date endDate,
			String year, String username) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<RegistroVMA> query = cb.createQuery(RegistroVMA.class);
		Root<RegistroVMA> registroVMA = query.from(RegistroVMA.class);
		Usuario usuario = usuarioRepository.findByUserName(username).orElseThrow();

		if (usuario.getRole().getIdRol() == 2 || usuario.getRole().getIdRol() == 4) {
			List<Predicate> predicates = new ArrayList<>();

			if (empresaId != null) {
				predicates.add(cb.or(cb.isNull(registroVMA.get("empresa")),
						cb.equal(registroVMA.get("empresa").get("idEmpresa"), empresaId)));
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
		respuestaVMARepository.saveAll(respuestasRequest.stream()
				.map(respuesta -> respuestaDtoToRespuestaVMA(respuesta, registro)).collect(Collectors.toList()));
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

	// anexo 3 - Relación de las EP que han realizado avances en la inspección y/o
	// inscripción en sus registros de los UND bajo su ámbito de influencia


	public List<AnexoTresListadoEPDTO> listaDeAnexosRelacionEPInspeccionados(String anhio) {
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());

		List<AnexoTresListadoEPDTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {
//			  RespuestaVMA respuesta = respuestaVMARepository
//					  .findRespuestaByPreguntaIdAndRegistro(ALTERNATIVA_UND_IDENTIFICADOS_PARCIAL_ID, registroVMA.getIdRegistroVma());
//			  
			RespuestaVMA UNDidentificados = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					ALTERNATIVA_UND_IDENTIFICADOS_PARCIAL_ID, registroVMA.getIdRegistroVma());

			RespuestaVMA UNDinspeccionados = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					ALTERNATIVA_TOTAL_UND_INSPECCIONADOS_PARCIAL_ID, registroVMA.getIdRegistroVma());

			RespuestaVMA UNDinscritos = respuestaVMARepository
					.findRespuestaAlternativaPorRegistros(ALTERNATIVA_UND_INSCRITOS_ID, registroVMA.getIdRegistroVma());

			anexos.add(new AnexoTresListadoEPDTO(registroVMA.getEmpresa().getNombre(),
					registroVMA.getEmpresa().getTipo(), Integer.parseInt(UNDidentificados.getRespuesta()),
					Integer.parseInt(UNDinspeccionados.getRespuesta()), Integer.parseInt(UNDinscritos.getRespuesta())));
		});

		return anexos;
	}

	// anexo 4 - Detalle de porcentaje de toma de muestra inopinada de las EP
	// PREGUNTA_UND_TOMA_MUESTRA_INOPINADA_ID
	/*
	 * 
	 */
	public List<AnexoPorcentajeMuestraInopinadaDTO> listaDeAnexosTomaDeMuestrasInopinadas(String anhio) {
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());

		List<AnexoPorcentajeMuestraInopinadaDTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {

			RespuestaVMA UNDinscritos = respuestaVMARepository
					.findRespuestaAlternativaPorRegistros(ALTERNATIVA_UND_INSCRITOS_ID, registroVMA.getIdRegistroVma());

			RespuestaVMA muestrasInopinadas = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					PREGUNTA_UND_TOMA_MUESTRA_INOPINADA_ID, registroVMA.getIdRegistroVma());

			int muestrasInopinadasvalor = Integer.parseInt(muestrasInopinadas.getRespuesta());
			int UNDinscritosvalor = Integer.parseInt(UNDinscritos.getRespuesta());
			double porcentaje = ((double) muestrasInopinadasvalor / (double) UNDinscritosvalor) * 100;

			// Redondea a un decimal usando Math.round
			double porcentajeRedondeado = Math.round(porcentaje * 10.0) / 10.0;

			// int muestrasInopinadas = Integer.parseInt(muestrasInopinadas.);
			// int UNDinscritos = Integer.parseInt(UNDinscritosRespuesta);
			// MUESTRA INOPINADA  se debe calcular (N° MUESTRAS INOPINADAS / N° UND
			// INSCRITOS EN EL REGISTRO DE UND) * 100
			anexos.add(new AnexoPorcentajeMuestraInopinadaDTO(registroVMA.getEmpresa().getNombre(),
					registroVMA.getEmpresa().getTipo(), UNDinscritosvalor, muestrasInopinadasvalor,
					porcentajeRedondeado));
		});

		return anexos;
	}

	// anexo 5 - Detalle de las EP que han realizado la evaluación de los VMA del
	// Anexo 1 del reglamento de VMA
	

	public List<AnexoEvaluacionVmaAnexo1DTO> listaDeAnexosEPevaluaronVMAAnexo1(String anhio) {
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());

		List<AnexoEvaluacionVmaAnexo1DTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {

			RespuestaVMA muestrasInopinadas = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					PREGUNTA_UND_TOMA_MUESTRA_INOPINADA_ID, registroVMA.getIdRegistroVma());
			logger.info(" Integer.parseInt(muestrasInopinadas.getRespuesta()) preg11- "+Integer.parseInt(muestrasInopinadas.getRespuesta()));
			RespuestaVMA UNDSobrepasanParametroAnexo1 = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					ALTERNATIVA_UND_SOBREPASAN_PARAMETRO_ANEXO1_ID, registroVMA.getIdRegistroVma());

			RespuestaVMA UNDFacturadosPagoAdicional = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					ALTERNATIVA_UND_FACTURARON_PAGO_ADICIONAL_ID, registroVMA.getIdRegistroVma());

			RespuestaVMA UNDRealizaronPagoAdicional = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					ALTERNATIVA_UND_REALIZARON_PAGO_ADICIONAL_ID, registroVMA.getIdRegistroVma());

			anexos.add(new AnexoEvaluacionVmaAnexo1DTO(registroVMA.getEmpresa().getNombre(),
					registroVMA.getEmpresa().getTipo(), Integer.parseInt(muestrasInopinadas.getRespuesta()),
					Integer.parseInt(UNDSobrepasanParametroAnexo1.getRespuesta()),
					Integer.parseInt(UNDFacturadosPagoAdicional.getRespuesta()),
					Integer.parseInt(UNDRealizaronPagoAdicional.getRespuesta())));
		});

		return anexos;
	}

	// anexo 6 - Detalle de las EP que han realizado la evaluación de los VMA del
	// Anexo 2 del reglamento de VMA
	/*
	
	 */
	public List<AnexoEvaluacionVmaAnexo2DTO> listaDeAnexosEPevaluaronVMAAnexo2(String anhio) {
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());

		List<AnexoEvaluacionVmaAnexo2DTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {

			RespuestaVMA muestrasInopinadas = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					PREGUNTA_UND_TOMA_MUESTRA_INOPINADA_ID, registroVMA.getIdRegistroVma());

			RespuestaVMA UNDSobrepasanParametroAnexo2 = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					ALTERNATIVA_UND_SOBREPASAN_PARAMETRO_ANEXO2_ID, registroVMA.getIdRegistroVma());

			RespuestaVMA UNDConPlazoAdicional = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					ALTERNATIVA_UND_OTORGADO_PLAZO_ADICIONAL_ID, registroVMA.getIdRegistroVma());

			RespuestaVMA UNDSuscritoAcuerdo = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					ALTERNATIVA_UND_SUSCRITO_PLAZO_OTORGADO_ID, registroVMA.getIdRegistroVma());

			anexos.add(new AnexoEvaluacionVmaAnexo2DTO(registroVMA.getEmpresa().getNombre(),
					registroVMA.getEmpresa().getTipo(), Integer.parseInt(muestrasInopinadas.getRespuesta()),
					Integer.parseInt(UNDSobrepasanParametroAnexo2.getRespuesta()),
					Integer.parseInt(UNDConPlazoAdicional.getRespuesta()),
					Integer.parseInt(UNDSuscritoAcuerdo.getRespuesta())));
		});

		return anexos;
	}

	// anexo 7 - Detalle de las EP que han realizado la atención de reclamos
	// referidos a VMA
	/*
	 * EMPRESA PRESTADORA TAMAÑO N° UND INSCRITOS EN EL REGISTRO DE UND N° RECLAMOS
	 * POR VMA N° RECLAMOS RESULTOS FUNDADOS
	 * 
	 */

	public List<AnexoReclamosVMADTO> listaDeAnexosEPSAtendieronReclamos(String anhio) {
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());

		List<AnexoReclamosVMADTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {

			RespuestaVMA UNDinscritos = respuestaVMARepository
					.findRespuestaAlternativaPorRegistros(ALTERNATIVA_UND_INSCRITOS_ID, registroVMA.getIdRegistroVma());

			RespuestaVMA reclamosRecibidosVMA = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					PREGUNTA_CANTIDAD_RECLAMOS_RECIBIDOS_VMA_ID, registroVMA.getIdRegistroVma());

			RespuestaVMA reclamosFundadosVMA = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					PREGUNTA_CANTIDAD_RECLAMOS_FUNDADOS_VMA_ID, registroVMA.getIdRegistroVma());

			anexos.add(new AnexoReclamosVMADTO(registroVMA.getEmpresa().getNombre(), registroVMA.getEmpresa().getTipo(),
					Integer.parseInt(UNDinscritos.getRespuesta()),
					Integer.parseInt(reclamosRecibidosVMA.getRespuesta()),
					Integer.parseInt(reclamosFundadosVMA.getRespuesta())));
		});

		return anexos;
	}

	// anexo 8 - Detalle de los costos de identificación, inspección y registro de
	// los UND

	//
	/*
	 * 
	 */
	public List<AnexoCostoTotalUNDDTO> anexoDetalleCostosUND(String anhio) {
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());

		List<AnexoCostoTotalUNDDTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {

			RespuestaVMA costoTotalAnualUND = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					PREGUNTA_COSTO_TOTAL_ANUAL_UND_ID, registroVMA.getIdRegistroVma());

			RespuestaVMA UNDidentificados = respuestaVMARepository.findRespuestaAlternativaPorRegistros(
					ALTERNATIVA_UND_IDENTIFICADOS_PARCIAL_ID, registroVMA.getIdRegistroVma());

			double costoAnual = (double) (Double.parseDouble(costoTotalAnualUND.getRespuesta())
					/ Double.parseDouble(UNDidentificados.getRespuesta()));
			// Redondea a un decimal usando Math.round
			double costoAnualRedondeado = Math.round(costoAnual * 10.0) / 10.0;
			
			anexos.add(new AnexoCostoTotalUNDDTO(registroVMA.getEmpresa().getNombre(),
					registroVMA.getEmpresa().getTipo(), Double.parseDouble(costoTotalAnualUND.getRespuesta()),
					Double.parseDouble(UNDidentificados.getRespuesta()), costoAnualRedondeado));
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
					PREGUNTA_COSTO_TOTAL_ANUAL_MUESTRAS_INOPINADAS_ID, registroVMA.getIdRegistroVma());

			RespuestaVMA UNDMuestraInopinada = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					PREGUNTA_UND_TOMA_MUESTRA_INOPINADA_ID, registroVMA.getIdRegistroVma());  

			double costoAnual = (double) (Double.parseDouble(costoTotalAnualMuestras.getRespuesta()) / Double.parseDouble(UNDMuestraInopinada.getRespuesta()));
			costoAnual = Math.round(costoAnual * 100.0) / 100.0;
			  
			  
			anexos.add(new AnexoCostoTotalMuestrasInopinadasDTO(registroVMA.getEmpresa().getNombre(),
					registroVMA.getEmpresa().getTipo(), Double.parseDouble(costoTotalAnualMuestras.getRespuesta()),
					Integer.parseInt(UNDMuestraInopinada.getRespuesta()), costoAnual));
		});

		return anexos;
	}
	
	//anexo 10 - Detalle de los costos totales incurridos por las Empresas Prestadoras
	/*
	 * private final int PREGUNTA_COSTO_TOTAL_ANUAL_UND_ID = 23;
	private final int PREGUNTA_COSTO_TOTAL_ANUAL_MUESTRAS_INOPINADAS_ID = 24;
	 *  COSTO EN IDENTIFICACIÓN, INSPECCIÓN Y REGISTRO DE UND (S/)
        COSTO POR TOMAS DE MUESTRAS INOPINADAS (S/)
        COSTO POR OTROS GASTOS DE IMPLEMENTACIÓN (S/)  PREGUNTA_COSTO_OTROS_GASTOS_IMPLEMENTACION_ID
	 */

	
	public List<AnexoCostoTotalesIncurridosDTO> listaAnexosCostosTotalesIncurridos(String anhio) {
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anhio).stream()
				.sorted(Comparator.comparing(registro -> registro.getEmpresa().getTipo())).collect(Collectors.toList());

		List<AnexoCostoTotalesIncurridosDTO> anexos = new ArrayList<>();

		registrosCompletos.forEach(registroVMA -> {

			RespuestaVMA costoTotalAnualUND = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					PREGUNTA_COSTO_TOTAL_ANUAL_UND_ID, registroVMA.getIdRegistroVma());

			RespuestaVMA costoTotalAnualMuestrasInopinadas = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					PREGUNTA_COSTO_TOTAL_ANUAL_MUESTRAS_INOPINADAS_ID, registroVMA.getIdRegistroVma());  

			RespuestaVMA costoOtrosGastosImplementacion = respuestaVMARepository.findRespuestaByPreguntaIdAndRegistro(
					PREGUNTA_COSTO_OTROS_GASTOS_IMPLEMENTACION_ID, registroVMA.getIdRegistroVma());  

			anexos.add(new AnexoCostoTotalesIncurridosDTO(registroVMA.getEmpresa().getNombre(),
					registroVMA.getEmpresa().getTipo(), Double.parseDouble(costoTotalAnualUND.getRespuesta()),
					Double.parseDouble(costoTotalAnualMuestrasInopinadas.getRespuesta()), 
					Double.parseDouble(costoOtrosGastosImplementacion.getRespuesta())));
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
					  .findRespuestaByPreguntaIdAndRegistro(PREGUNTA_INGRESOS_FACTURADOS_VMA_ID, registroVMA.getIdRegistroVma());
			  
			   anexos.add(new AnexoIngresosImplVmaDTO(
					  registroVMA.getEmpresa().getNombre(),
					  registroVMA.getEmpresa().getTipo(),
					  Double.parseDouble(respuestaIngresos.getRespuesta())
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
}

package pe.gob.sunass.vma.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.sunass.vma.constants.Constants;
import pe.gob.sunass.vma.dto.reportes.BarChartBasicoDto;
import pe.gob.sunass.vma.dto.reportes.CostoAnualIncurridoCompletoDTO;
import pe.gob.sunass.vma.dto.reportes.CostoAnualIncurridoDTO;
import pe.gob.sunass.vma.dto.reportes.GraficoComparativoDTO;
import pe.gob.sunass.vma.dto.reportes.PieChartBasicoDto;
import pe.gob.sunass.vma.dto.reportes.RegistroEmpresaChartDto;
import pe.gob.sunass.vma.dto.reportes.RegistroPromedioTrabajadorVMAChartDto;
import pe.gob.sunass.vma.model.Empresa;
import pe.gob.sunass.vma.model.cuestionario.RegistroVMA;
import pe.gob.sunass.vma.model.cuestionario.RespuestaVMA;
import pe.gob.sunass.vma.repository.EmpresaRepository;
import pe.gob.sunass.vma.repository.RegistroVMARepository;
import pe.gob.sunass.vma.repository.RespuestaVMARepository;
import pe.gob.sunass.vma.util.PreguntasAlternativasProperties;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ReporteService {

	private static Logger logger = LoggerFactory.getLogger(ReporteService.class);

	@Autowired
	private RegistroVMARepository registroVMARepository;

	@Autowired
	private EmpresaRepository empresaRepository;

	@Autowired
	private RespuestaVMARepository respuestaVMARepository;

	@Autowired
	private PreguntasAlternativasProperties preguntasAlternativasVMA; // clase properties, donde contiene los valores constantes de preguntas y alternativas

	public List<RegistroEmpresaChartDto> reporteBarraRegistros(String anio) {
		
		List<Empresa> empresas = empresaRepository.findAll();

		Map<String, List<Empresa>> empresasPorTipo = empresas.stream()
				.filter(empresa -> !Constants.TIPO_EMPRESA_NINGUNO.equals(empresa.getTipoEmpresa().getNombre())) // ignora a sunass, es de  tipo NINGUNO
				.collect(Collectors.groupingBy(empresa -> 
		        empresa.getTipoEmpresa() != null ? empresa.getTipoEmpresa().getNombre() : "Sin Tipo"));  

		List<RegistroEmpresaChartDto> dataList = new ArrayList<>();
		empresasPorTipo.forEach((tipo, lista) -> {
			long cantidadRegistro = registroVMARepository.registrosCompletadosPorTipoEmpresa(tipo, anio);
			dataList.add(new RegistroEmpresaChartDto(tipo, (int) cantidadRegistro, lista.size()));
		});

		return dataList;
		
	}

	public List<RegistroEmpresaChartDto> reporteSiNo(String anio) {

		List<RegistroEmpresaChartDto> dataList = new ArrayList<>();
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
		Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos.stream()
				.collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipoEmpresa().getNombre())); 

		registrosPorTipo.forEach((tipo, lista) -> {
			List<RespuestaVMA> respuestas = respuestaVMARepository.findRespuestasByIdPreguntaAndTipoEmpresa(
					preguntasAlternativasVMA.getId_pregunta_si_no(), tipo, anio);
			long respondieronSi = respuestas.stream()
					.filter(respuesta -> respuesta.getRespuesta().equals(Constants.Alternativas.Opcion_SI)).count();
			dataList.add(new RegistroEmpresaChartDto(tipo, (int) respondieronSi, lista.size()));
		});

		return dataList;
	}

	public List<RegistroPromedioTrabajadorVMAChartDto> reporteNumeroPromedioDeTrabajadoresDedicadosVMA(String anio) {
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
		Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos.stream()
				.collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipoEmpresa().getNombre())); 

		List<RegistroPromedioTrabajadorVMAChartDto> registrosPromedio = new ArrayList<>();
		registrosPorTipo.forEach((tipo, lista) -> {
			registrosPromedio.add(mapToRegistroPromedioTrrabajadorVMA(tipo, mapToIdsRegistrosVma(lista)));
		});
		return registrosPromedio;
	}

	// grafico 5
	public List<PieChartBasicoDto> reporteNumeroTotalUND(String anio) {
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
		Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos.stream()
				.collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipoEmpresa().getNombre())); 

		List<PieChartBasicoDto> listaNumeroTotalUND = new ArrayList<>();
		Integer sumaTotalEmpresasUNDIngresadas = respuestaVMARepository.getSumaTotalRespuestaAlternativaPorRegistros(
				mapToIdsRegistrosVma(registrosCompletos),
				preguntasAlternativasVMA.getId_alternativa_nro_und_identificados_parcial());

		registrosPorTipo.forEach((tipo, lista) -> {
			Integer sumaTotalPorTipoEmpresaUNDIngresadas = respuestaVMARepository
					.getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(lista),
							preguntasAlternativasVMA.getId_alternativa_nro_und_identificados_parcial());
			double porcentaje = ((double) sumaTotalPorTipoEmpresaUNDIngresadas / sumaTotalEmpresasUNDIngresadas) * 100;
			listaNumeroTotalUND.add(new PieChartBasicoDto(tipo, porcentaje));
		});
		return listaNumeroTotalUND;
	}

	// grafico 6

	/*
	 * formula porcentaje = Sumatoria de UND inspeccionadas de las EPS por
	 * tamaño/Sumatoria UND identificadas de las EPS por tamaño. promedio =
	 * Sumatoria de UND inspeccionadas de todas las EPS / Sumatoria UND
	 * identificadas de todas las EPS.
	 */

	public List<BarChartBasicoDto> reporteNumeroTotalUNDInspeccionados(String anio) {

		AtomicInteger sumaTotalInspeccionadosAllEPS = new AtomicInteger(0); // para operaciones atomicas dentro de una
																			// expresión lambda en Java, y se mantenga
																			// un estado mutable para subprocesos
		AtomicInteger sumaTotalIdentificadosAllEPS = new AtomicInteger(0);

		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
		Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos.stream()
				.collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipoEmpresa().getNombre()));
		List<BarChartBasicoDto> listaChart = new ArrayList<>();

		registrosPorTipo.forEach((tipo, lista) -> {

			Integer totalInspeccionados = respuestaVMARepository.getSumaTotalRespuestaAlternativaPorRegistros(
					mapToIdsRegistrosVma(lista),
					preguntasAlternativasVMA.getId_alternativa_nro_und_inspeccionados_parcial());

			Integer totalIdentificados = respuestaVMARepository.getSumaTotalRespuestaAlternativaPorRegistros(
					mapToIdsRegistrosVma(lista),
					preguntasAlternativasVMA.getId_alternativa_nro_und_identificados_parcial());

			sumaTotalInspeccionadosAllEPS.addAndGet(totalInspeccionados);// suma total de Inspeccionados de todas las
																			// EPS
			sumaTotalIdentificadosAllEPS.addAndGet(totalIdentificados); // suma total de Identificados de todas las EPS

			double porcentaje = ((double) totalInspeccionados / totalIdentificados) * 100;
			listaChart.add(new BarChartBasicoDto(tipo, porcentaje));
		});

		double promedio = (sumaTotalIdentificadosAllEPS.get() != 0)
				? ((double) sumaTotalInspeccionadosAllEPS.get() / sumaTotalIdentificadosAllEPS.get()) * 100
				: 0.0;
		// listaChart.add(new BarChartBasicoDto(Constants.LABEL_PROMEDIO, sumaPorcentaje
		// / listaChart.size()));
		listaChart.add(new BarChartBasicoDto(Constants.LABEL_PROMEDIO, promedio));

		return listaChart;

	}

	/*
	 * GRAFICO 7 formula = Sumatoria de UND a los que se le ha solicitado la
	 * presentación del diagrama ...DE EPS por tamaño/Sumatoria UND inspeccionadas
	 * de las EPS por tamaño promedio = Sumatoria de UND a los que se le ha
	 * solicitado la presentación ...de todas las EPS /Sumatoria UND inspeccionadas
	 * de todas las EPS
	 */

	public List<BarChartBasicoDto> reporteDiagramaFlujoYBalance(String anio) {

		AtomicInteger sumaTotalDiagramaFlujoAllEPS = new AtomicInteger(0);
		AtomicInteger sumaTotalInspeccionadosAllEPS = new AtomicInteger(0);

		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);

		Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos.stream()
				.collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipoEmpresa().getNombre())); 
		List<BarChartBasicoDto> listaChart = new ArrayList<>();

		registrosPorTipo.forEach((tipo, lista) -> {
			Integer totalDiagramaFlujo = respuestaVMARepository.getSumaTotalRespuestaAlternativaPorRegistros(
					mapToIdsRegistrosVma(lista),
					preguntasAlternativasVMA.getId_alternativa_solicitaron_diagrama_flujo()); // ALTERNATIVA_SOLICITARON_DIAGRAMA_FLUJO_ID
			Integer totalInspeccionados = respuestaVMARepository.getSumaTotalRespuestaAlternativaPorRegistros(
					mapToIdsRegistrosVma(lista),
					preguntasAlternativasVMA.getId_alternativa_nro_und_inspeccionados_parcial());

			sumaTotalDiagramaFlujoAllEPS.addAndGet(totalDiagramaFlujo); // suma total de UND a los que se le ha
																		// solicitado el diagrama de flujo , de todas
																		// las EPS
			sumaTotalInspeccionadosAllEPS.addAndGet(totalInspeccionados);// suma total de UND Inspeccionados, de todas
																			// las EPS

			double porcentaje = ((double) totalDiagramaFlujo / totalInspeccionados) * 100;
			listaChart.add(new BarChartBasicoDto(tipo, porcentaje));

		});

		double promedio = (sumaTotalDiagramaFlujoAllEPS.get() != 0)
				? ((double) sumaTotalDiagramaFlujoAllEPS.get() / sumaTotalInspeccionadosAllEPS.get()) * 100
				: 0.0;
		listaChart.add(new BarChartBasicoDto(Constants.LABEL_PROMEDIO, promedio));

		return listaChart;
	}

	private RegistroPromedioTrabajadorVMAChartDto mapToRegistroPromedioTrrabajadorVMA(String tipo,
			List<Integer> idsRegistrosVma) {

		Integer sumaTrabajadoresDesdicadosRegistroVMA = respuestaVMARepository.getSumaTrabajadoresDesdicadosRegistroVMA(
				idsRegistrosVma, preguntasAlternativasVMA.getId_pregunta_nro_trabajadores_eps());

		double promedio = (double) sumaTrabajadoresDesdicadosRegistroVMA / idsRegistrosVma.size();

		return new RegistroPromedioTrabajadorVMAChartDto(tipo, promedio, sumaTrabajadoresDesdicadosRegistroVMA,
				idsRegistrosVma.size());
	}

	private List<Integer> mapToIdsRegistrosVma(List<RegistroVMA> registrosVma) {
		return registrosVma.stream().map(RegistroVMA::getIdRegistroVma).collect(Collectors.toList());
	}
	
	private List<BigDecimal> mapToIdsRegistrosVmaBigDecimal(List<RegistroVMA> registrosVma) {
	    return registrosVma.stream()
	            .map(registro -> BigDecimal.valueOf(registro.getIdRegistroVma())) // Convierte Integer a BigDecimal
	            .collect(Collectors.toList());
	}

	// grafico 8
	public List<BarChartBasicoDto> reporteDiagramaFlujoYBalancePresentados(String anio) {

		AtomicInteger sumaTotalUNDPresentaronDiagramaAllEPS = new AtomicInteger(0);
		AtomicInteger sumaTotalUNDSolicitaronDiagramaAllEPS = new AtomicInteger(0);

		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);

		Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos.stream()
				.collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipoEmpresa().getNombre())); 
		List<BarChartBasicoDto> listaChart = new ArrayList<>();

		registrosPorTipo.forEach((tipo, lista) -> {
			Integer totalPresentaronDiagrama = respuestaVMARepository.getSumaTotalRespuestaAlternativaPorRegistros(
					mapToIdsRegistrosVma(lista),
					preguntasAlternativasVMA.getId_alternativa_presentaron_diagrama_flujo());
			Integer totalSolicitaronDiagrama = respuestaVMARepository.getSumaTotalRespuestaAlternativaPorRegistros(
					mapToIdsRegistrosVma(lista),
					preguntasAlternativasVMA.getId_alternativa_solicitaron_diagrama_flujo());

			sumaTotalUNDPresentaronDiagramaAllEPS.addAndGet(totalPresentaronDiagrama); // suma total de UND a los que se
																						// le ha solicitado el diagrama
																						// de flujo , de todas las EPS
			sumaTotalUNDSolicitaronDiagramaAllEPS.addAndGet(totalSolicitaronDiagrama);// suma total de UND
																						// Inspeccionados, de todas las
																						// EPS

			double porcentaje = (totalPresentaronDiagrama != 0)
					? ((double) totalPresentaronDiagrama / totalSolicitaronDiagrama) * 100
					: 0.0;

			listaChart.add(new BarChartBasicoDto(tipo, porcentaje));
		});

		double promedio = (sumaTotalUNDPresentaronDiagramaAllEPS.get() != 0)
				? ((double) sumaTotalUNDPresentaronDiagramaAllEPS.get() / sumaTotalUNDSolicitaronDiagramaAllEPS.get())
						* 100
				: 0.0;
		listaChart.add(new BarChartBasicoDto(Constants.LABEL_PROMEDIO, promedio));

		return listaChart;
	}

	// grafico9

	public List<GraficoComparativoDTO> reporteComparativoUND(String anio) {

		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
		Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos.stream()
				.collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipoEmpresa().getNombre()));  
		List<GraficoComparativoDTO> listaTablaComparativa = new ArrayList<>();

		registrosPorTipo.forEach((tipo, lista) -> {
			Integer totalUNDInscritos = respuestaVMARepository.getSumaTotalRespuestaAlternativaPorRegistros(
					mapToIdsRegistrosVma(lista), preguntasAlternativasVMA.getId_alternativa_und_inscritos()); // ALTERNATIVA_UND_INSCRITOS_ID
			Integer totalUNDInspeccionados = respuestaVMARepository.getSumaTotalRespuestaAlternativaPorRegistros(
					mapToIdsRegistrosVma(lista),
					preguntasAlternativasVMA.getId_alternativa_nro_und_inspeccionados_parcial()); // ALTERNATIVA_UND_INSPECCIONADOS_PARCIAL_ID
			Integer totalUNDIdentificados = respuestaVMARepository.getSumaTotalRespuestaAlternativaPorRegistros(
					mapToIdsRegistrosVma(lista),
					preguntasAlternativasVMA.getId_alternativa_nro_und_identificados_parcial());

			double porcentajeUNDinspeccionados = ((double) totalUNDInscritos / totalUNDInspeccionados) * 100;
			double porcentajeUNDinspRedondeado = Math.round(porcentajeUNDinspeccionados * 10.0) / 10.0;

			double porcentajeUNDidentificados = ((double) totalUNDInscritos / totalUNDIdentificados) * 100;
			double porcentajeUNDidentRedondeado = Math.round(porcentajeUNDidentificados * 10.0) / 10.0;

			listaTablaComparativa.add(new GraficoComparativoDTO(tipo, totalUNDInscritos, totalUNDInspeccionados,
					totalUNDIdentificados, porcentajeUNDinspRedondeado, porcentajeUNDidentRedondeado));

		});

		return listaTablaComparativa;
	}

	// graf 10 - Porcentaje de UND que cuentan con caja de registro o dispositivo
	// similar en la parte externa de su predio, según tamaño de la EP

	public List<BarChartBasicoDto> reporteUNDconCajaRegistro(String anio) {

		AtomicInteger sumaTotalUNDCajaRegistroAllEPS = new AtomicInteger(0);
		AtomicInteger sumaTotalUNDInscritosAllEPS = new AtomicInteger(0);

		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
		Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos.stream()
				.collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipoEmpresa().getNombre()));
		List<BarChartBasicoDto> listaChart = new ArrayList<>();

		registrosPorTipo.forEach((tipo, lista) -> {
			Integer totalUNDCajaRegistro = respuestaVMARepository.getSumatotalRespuestaPorRegistros(
					mapToIdsRegistrosVma(lista), preguntasAlternativasVMA.getId_pregunta_nro_und_caja_registro());
			Integer totalUNDInscritos = respuestaVMARepository.getSumaTotalRespuestaAlternativaPorRegistros(
					mapToIdsRegistrosVma(lista), preguntasAlternativasVMA.getId_alternativa_und_inscritos());
			double porcentaje = ((double) totalUNDCajaRegistro / totalUNDInscritos) * 100;
			listaChart.add(new BarChartBasicoDto(tipo, porcentaje));

			sumaTotalUNDCajaRegistroAllEPS.addAndGet(totalUNDCajaRegistro);
			sumaTotalUNDInscritosAllEPS.addAndGet(totalUNDInscritos);

		});

		double promedio = (sumaTotalUNDCajaRegistroAllEPS.get() != 0)
				? ((double) sumaTotalUNDCajaRegistroAllEPS.get() / sumaTotalUNDInscritosAllEPS.get()) * 100
				: 0.0;
		listaChart.add(new BarChartBasicoDto(Constants.LABEL_PROMEDIO, promedio));

		return listaChart;

	}

	// Gráfico 11: Porcentaje de UND a los que se realizó la toma de muestra
	// inopinada, según tamaño de la EP

	public List<BarChartBasicoDto> reportePorcentajeUNDTomaMuestraInopinada(String anio) {

		AtomicInteger sumaTotalUNDTomaMuestraInopinadaAllEPS = new AtomicInteger(0);
		AtomicInteger sumaTotalUNDInscritosAllEPS = new AtomicInteger(0);

		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
		Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos.stream()
				.collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipoEmpresa().getNombre())); 
		List<BarChartBasicoDto> listaChart = new ArrayList<>();

		registrosPorTipo.forEach((tipo, lista) -> {
			Integer totalUNDTomaMuestraInopinada = respuestaVMARepository.getSumatotalRespuestaPorRegistros(
					mapToIdsRegistrosVma(lista), preguntasAlternativasVMA.getId_pregunta_und_toma_muestra_inopinada());
			Integer totalUNDInscritos = respuestaVMARepository.getSumaTotalRespuestaAlternativaPorRegistros(
					mapToIdsRegistrosVma(lista), preguntasAlternativasVMA.getId_alternativa_und_inscritos());
			double porcentaje = ((double) totalUNDTomaMuestraInopinada / totalUNDInscritos) * 100;
			listaChart.add(new BarChartBasicoDto(tipo, porcentaje));

			sumaTotalUNDTomaMuestraInopinadaAllEPS.addAndGet(totalUNDTomaMuestraInopinada);
			sumaTotalUNDInscritosAllEPS.addAndGet(totalUNDInscritos);

		});

		double promedio = (sumaTotalUNDTomaMuestraInopinadaAllEPS.get() != 0)
				? ((double) sumaTotalUNDTomaMuestraInopinadaAllEPS.get() / sumaTotalUNDInscritosAllEPS.get()) * 100
				: 0.0;
		listaChart.add(new BarChartBasicoDto(Constants.LABEL_PROMEDIO, promedio));
		return listaChart;
	}

	// grafico 12 Porcentaje de toma de muestra inopinada, según tamaño de la EP
	/*
	 * Sumatoria del Número total de tomas de muestras inopinadas por tamaño de la
	 * EPS / Sumatoria de Número total de tomas de muestras inopinadas de todas las
	 * EPS
	 * 
	 */

	public List<PieChartBasicoDto> reporteNumeroTotalTomasMuestraInopinadas(String anio) {
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
		Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos.stream()
				.collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipoEmpresa().getNombre())); 

		List<PieChartBasicoDto> listaTotalTomasMuestraInopinadas = new ArrayList<>();
		Integer sumaTotalTomasMuestraInopinadas = respuestaVMARepository.getSumatotalRespuestaPorRegistros(
				mapToIdsRegistrosVma(registrosCompletos),
				preguntasAlternativasVMA.getId_pregunta_total_muestras_inopinadas());

		registrosPorTipo.forEach((tipo, lista) -> {
			Integer sumaTotalPorTipoEPSTomasMuestraInopinadas = respuestaVMARepository
					.getSumatotalRespuestaPorRegistros(mapToIdsRegistrosVma(lista),
							preguntasAlternativasVMA.getId_pregunta_total_muestras_inopinadas());
			double porcentaje = (sumaTotalPorTipoEPSTomasMuestraInopinadas != 0)
					? ((double) sumaTotalPorTipoEPSTomasMuestraInopinadas / sumaTotalTomasMuestraInopinadas) * 100
					: 0.0;
			listaTotalTomasMuestraInopinadas.add(new PieChartBasicoDto(tipo, porcentaje));
		});
		return listaTotalTomasMuestraInopinadas;
	}

	// Gráfico 13: Porcentaje de UND que sobrepasan algún(os) parámetro(s) del Anexo
	// N° 1 del REGLAMENTO VMA, según tamaño de la EP

	public List<BarChartBasicoDto> reportePorcentajeUNDSobrepasanParametroAnexo1(String anio) {

		AtomicInteger sumaTotalUNDTomaMuestraInopinadaAllEPS = new AtomicInteger(0);
		AtomicInteger sumaTotalUNDParametroAnexo1AllEPS = new AtomicInteger(0);

		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
		Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos.stream()
				.collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipoEmpresa().getNombre()));
		List<BarChartBasicoDto> listaChart = new ArrayList<>();

		registrosPorTipo.forEach((tipo, lista) -> {
			Integer totalUNDTomaMuestraInopinada = respuestaVMARepository.getSumatotalRespuestaPorRegistros(
					mapToIdsRegistrosVma(lista), preguntasAlternativasVMA.getId_pregunta_und_toma_muestra_inopinada());
			Integer totalUNDParametroAnexo1 = respuestaVMARepository.getSumaTotalRespuestaAlternativaPorRegistros(
					mapToIdsRegistrosVma(lista),
					preguntasAlternativasVMA.getId_alternativa_und_sobrepasan_parametro_anexo1());
			double porcentaje = (totalUNDParametroAnexo1 != 0)
					? ((double) totalUNDParametroAnexo1 / totalUNDTomaMuestraInopinada) * 100
					: 0.0;

			sumaTotalUNDTomaMuestraInopinadaAllEPS.addAndGet(totalUNDTomaMuestraInopinada);
			sumaTotalUNDParametroAnexo1AllEPS.addAndGet(totalUNDParametroAnexo1);

			listaChart.add(new BarChartBasicoDto(tipo, porcentaje));
		});

		double promedio = (sumaTotalUNDTomaMuestraInopinadaAllEPS.get() != 0)
				? ((double) sumaTotalUNDTomaMuestraInopinadaAllEPS.get() / sumaTotalUNDParametroAnexo1AllEPS.get())
						* 100
				: 0.0;

		listaChart.add(new BarChartBasicoDto(Constants.LABEL_PROMEDIO, promedio));
		return listaChart;

	}

	// Gráfico 14: Porcentaje de UND a los que se ha facturado por concepto de Pago
	// adicional por exceso de concentración, según tamaño de la EP

	public List<BarChartBasicoDto> reportePorcentajeUNDFacturadoPorConceptoAdicional(String anio) {

		AtomicInteger sumaTotalUNDFacturaronPagoAdicionalAllEPS = new AtomicInteger(0);
		AtomicInteger sumaTotalUNDParametroAnexo1AllEPS = new AtomicInteger(0);

		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
		Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos.stream()
				.collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipoEmpresa().getNombre())); 
		List<BarChartBasicoDto> listaChart = new ArrayList<>();

		registrosPorTipo.forEach((tipo, lista) -> {
			Integer totalUNDFacturaronPagoAdicional = respuestaVMARepository
					.getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(lista),
							preguntasAlternativasVMA.getId_alternativa_und_facturaron_pago_adicional());
			Integer totalUNDParametroAnexo1 = respuestaVMARepository.getSumaTotalRespuestaAlternativaPorRegistros(
					mapToIdsRegistrosVma(lista),
					preguntasAlternativasVMA.getId_alternativa_und_sobrepasan_parametro_anexo1());
			double porcentaje = (totalUNDFacturaronPagoAdicional != 0)
					? ((double) totalUNDFacturaronPagoAdicional / totalUNDParametroAnexo1) * 100
					: 0.0;
			sumaTotalUNDFacturaronPagoAdicionalAllEPS.addAndGet(totalUNDFacturaronPagoAdicional);
			sumaTotalUNDParametroAnexo1AllEPS.addAndGet(totalUNDParametroAnexo1);

			listaChart.add(new BarChartBasicoDto(tipo, porcentaje));
		});

		double promedio = (sumaTotalUNDFacturaronPagoAdicionalAllEPS.get() != 0)
				? ((double) sumaTotalUNDFacturaronPagoAdicionalAllEPS.get() / sumaTotalUNDParametroAnexo1AllEPS.get())
						* 100
				: 0.0;
		listaChart.add(new BarChartBasicoDto(Constants.LABEL_PROMEDIO, promedio));

		return listaChart;

	}

	// grafico 15
	/*
	 * formula Sumatoria de UND que realizaron el Pago adicional por exceso de
	 * concentración de las EPS por tamaño / Sumatoria de las UND a los que se ha
	 * facturado por concepto de Pago adicional por exceso de concentración de las
	 * EPS por tamaño promedio - Sumatoria de UND que realizaron el Pago adicional
	 * por exceso de concentración todas las EPS / Sumatoria UND a los que se ha
	 * facturado por concepto de Pago adicional por exceso de concentración de todas
	 * las EPS
	 * 
	 */

	public List<BarChartBasicoDto> reportePorcentajeUNDPagoAdicional(String anio) {

		AtomicInteger sumaTotalUNDRealizaronPagoAdicionalAllEPS = new AtomicInteger(0);
		AtomicInteger sumaTotalUNDFacturaronPagoAdicionalAllEPS = new AtomicInteger(0);

		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
		Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos.stream()
				.collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipoEmpresa().getNombre())); 
		List<BarChartBasicoDto> listaChart = new ArrayList<>();

		registrosPorTipo.forEach((tipo, lista) -> {
			Integer totalUNDRealizaronPagoAdicional = respuestaVMARepository
					.getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(lista),
							preguntasAlternativasVMA.getId_alternativa_und_realizaron_pago_adicional());
			Integer totalUNDFacturaronPagoAdicional = respuestaVMARepository
					.getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(lista),
							preguntasAlternativasVMA.getId_alternativa_und_facturaron_pago_adicional());

			double porcentaje = (totalUNDRealizaronPagoAdicional != 0)
					? ((double) totalUNDRealizaronPagoAdicional / totalUNDFacturaronPagoAdicional) * 100
					: 0.0;
			sumaTotalUNDRealizaronPagoAdicionalAllEPS.addAndGet(totalUNDRealizaronPagoAdicional);
			sumaTotalUNDFacturaronPagoAdicionalAllEPS.addAndGet(totalUNDFacturaronPagoAdicional);

			listaChart.add(new BarChartBasicoDto(tipo, porcentaje));
		});

		double promedio = (sumaTotalUNDRealizaronPagoAdicionalAllEPS.get() != 0)
				? ((double) sumaTotalUNDRealizaronPagoAdicionalAllEPS.get()
						/ sumaTotalUNDFacturaronPagoAdicionalAllEPS.get()) * 100
				: 0.0;

		listaChart.add(new BarChartBasicoDto(Constants.LABEL_PROMEDIO, promedio));
		return listaChart;

	}

	// grafico 16 - Porcentaje de UND que sobrepasan algún(os) parámetro(s) del
	// Anexo N° 2 del Reglamento de VMA, según tamaño de la EP
	

	public List<BarChartBasicoDto> reportePorcentajeUNDParametrosAnexo2(String anio) {

		AtomicInteger sumaTotalUNDSobrepasanParametroAnexo2AllEPS = new AtomicInteger(0);
		AtomicInteger sumaTotalUNDTomaMuestraInopinadaAllEPS = new AtomicInteger(0);

		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
		Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos.stream()
				.collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipoEmpresa().getNombre()));
		List<BarChartBasicoDto> listaChart = new ArrayList<>();

		registrosPorTipo.forEach((tipo, lista) -> {
			Integer totalUNDSobrepasanParametroAnexo2 = respuestaVMARepository
					.getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(lista),
							preguntasAlternativasVMA.getId_alternativa_und_sobrepasan_parametro_anexo2());
			Integer totalUNDTomaMuestraInopinada = respuestaVMARepository.getSumatotalRespuestaPorRegistros(
					mapToIdsRegistrosVma(lista), preguntasAlternativasVMA.getId_pregunta_und_toma_muestra_inopinada());
			double porcentaje = (totalUNDSobrepasanParametroAnexo2 != 0)
					? ((double) totalUNDSobrepasanParametroAnexo2 / totalUNDTomaMuestraInopinada) * 100
					: 0.0;
			sumaTotalUNDSobrepasanParametroAnexo2AllEPS.addAndGet(totalUNDSobrepasanParametroAnexo2);
			sumaTotalUNDTomaMuestraInopinadaAllEPS.addAndGet(totalUNDTomaMuestraInopinada);

			listaChart.add(new BarChartBasicoDto(tipo, porcentaje));
		});

		double promedio = (sumaTotalUNDSobrepasanParametroAnexo2AllEPS.get() != 0)
				? ((double) sumaTotalUNDSobrepasanParametroAnexo2AllEPS.get()
						/ sumaTotalUNDTomaMuestraInopinadaAllEPS.get()) * 100
				: 0.0;
		listaChart.add(new BarChartBasicoDto(Constants.LABEL_PROMEDIO, promedio));

		return listaChart;

	}

	// grafico 17 - Porcentaje de UND a los que les ha otorgado un plazo adicional
	// (hasta 18 meses), según tamaño de la EP
	/*
	 * formula = Sumatoria de Número de UND a los que les ha otorgado un plazo
	 * adicional (hasta 18 meses) con..... de EPS por tamaño/ Sumatoria de las
	 * Número de UND que sobrepasan algún(os) parámetro(s) del Anexo N° 2 de las EPS
	 * por tamaño
	 * 
	 * promedio = Sumatoria del Número de UND a los que les ha otorgado un plazo
	 * adicional (hasta 18 meses) con el .... de todas las EPS / Sumatoria del
	 * Número de UND que sobrepasan algún(os) parámetro(s) del Anexo N° 2 de todas
	 * las EPS.
	 */

	public List<BarChartBasicoDto> reportePorcentajeUNDOtorgadoPlazoAdicinal(String anio) {

		AtomicInteger sumaTotalUNDPlazoAdicionalAllEPS = new AtomicInteger(0);
		AtomicInteger sumaTotalUNDSobrepasanParametroAnexo2AllEPS = new AtomicInteger(0);

		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
		Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos.stream()
				.collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipoEmpresa().getNombre()));
		List<BarChartBasicoDto> listaChart = new ArrayList<>();

		registrosPorTipo.forEach((tipo, lista) -> {
			Integer totalUNDPlazoAdicional = respuestaVMARepository.getSumaTotalRespuestaAlternativaPorRegistros(
					mapToIdsRegistrosVma(lista),
					preguntasAlternativasVMA.getId_alternativa_und_otorgado_plazo_adicional());
			Integer totalUNDSobrepasanParametroAnexo2 = respuestaVMARepository
					.getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(lista),
							preguntasAlternativasVMA.getId_alternativa_und_sobrepasan_parametro_anexo2());

			double porcentaje = (totalUNDPlazoAdicional != 0)
					? ((double) totalUNDPlazoAdicional / totalUNDSobrepasanParametroAnexo2) * 100
					: 0.0;
			sumaTotalUNDPlazoAdicionalAllEPS.addAndGet(totalUNDPlazoAdicional);
			sumaTotalUNDSobrepasanParametroAnexo2AllEPS.addAndGet(totalUNDSobrepasanParametroAnexo2);

			listaChart.add(new BarChartBasicoDto(tipo, porcentaje));
		});

		double promedio = (sumaTotalUNDPlazoAdicionalAllEPS.get() != 0)
				? ((double) sumaTotalUNDPlazoAdicionalAllEPS.get() / sumaTotalUNDSobrepasanParametroAnexo2AllEPS.get())
						* 100
				: 0.0;
		listaChart.add(new BarChartBasicoDto(Constants.LABEL_PROMEDIO, promedio));
		return listaChart;

	}

	// Gráfico 18: Porcentaje de UND que han suscrito un acuerdo en el que se establece un plazo otorgado, por única vez,

	public List<BarChartBasicoDto> reportePorcentajeUNDSuscritoAcuerdo(String anio) {

		AtomicInteger sumaTotalUNDSuscritoPlazoAllEPS = new AtomicInteger(0);
		AtomicInteger sumaTotalUNDSobrepasanParametroAnexo2AllEPS = new AtomicInteger(0);

		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
		Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos.stream()
				.collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipoEmpresa().getNombre())); 
		List<BarChartBasicoDto> listaChart = new ArrayList<>();

		registrosPorTipo.forEach((tipo, lista) -> {
			Integer totalUNDSuscritoPlazo = respuestaVMARepository.getSumaTotalRespuestaAlternativaPorRegistros(
					mapToIdsRegistrosVma(lista),
					preguntasAlternativasVMA.getId_alternativa_und_suscrito_plazo_otorgado());
			Integer totalUNDSobrepasanParametroAnexo2 = respuestaVMARepository
					.getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(lista),
							preguntasAlternativasVMA.getId_alternativa_und_sobrepasan_parametro_anexo2());

			double porcentaje = (totalUNDSuscritoPlazo != 0)
					? ((double) totalUNDSuscritoPlazo / totalUNDSobrepasanParametroAnexo2) * 100
					: 0.0;
			sumaTotalUNDSuscritoPlazoAllEPS.addAndGet(totalUNDSuscritoPlazo);
			sumaTotalUNDSobrepasanParametroAnexo2AllEPS.addAndGet(totalUNDSobrepasanParametroAnexo2);

			listaChart.add(new BarChartBasicoDto(tipo, porcentaje));
		});

		double promedio = (sumaTotalUNDSuscritoPlazoAllEPS.get() != 0)
				? ((double) sumaTotalUNDSuscritoPlazoAllEPS.get() / sumaTotalUNDSobrepasanParametroAnexo2AllEPS.get())
						* 100
				: 0.0;
		listaChart.add(new BarChartBasicoDto(Constants.LABEL_PROMEDIO, promedio));

		return listaChart;

	}

	// grafico 19- Gráfico 19: Porcentaje de recibidos por VMA, según tamaño de la  EP

	public List<BarChartBasicoDto> reportePorcentajeReclamosRecibidosVMA(String anio) {

		AtomicInteger sumaTotalReclamosRecibidosVMAAllEPS = new AtomicInteger(0);
		AtomicInteger sumaTotalUNDInscritosAllEPS = new AtomicInteger(0);

		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
		Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos.stream()
				.collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipoEmpresa().getNombre())); 
		List<BarChartBasicoDto> listaChart = new ArrayList<>();

		registrosPorTipo.forEach((tipo, lista) -> {
			Integer totalReclamosRecibidosVMA = respuestaVMARepository.getSumatotalRespuestaPorRegistros(
					mapToIdsRegistrosVma(lista), preguntasAlternativasVMA.getId_pregunta_nro_reclamos_recibidos());
			Integer totalUNDInscritos = respuestaVMARepository.getSumaTotalRespuestaAlternativaPorRegistros(
					mapToIdsRegistrosVma(lista), preguntasAlternativasVMA.getId_alternativa_und_inscritos());
			double porcentaje = (totalReclamosRecibidosVMA != 0)
					? ((double) totalReclamosRecibidosVMA / totalUNDInscritos) * 100
					: 0.0;
			sumaTotalReclamosRecibidosVMAAllEPS.addAndGet(totalReclamosRecibidosVMA);
			sumaTotalUNDInscritosAllEPS.addAndGet(totalUNDInscritos);

			listaChart.add(new BarChartBasicoDto(tipo, porcentaje));
		});

		double promedio = (sumaTotalReclamosRecibidosVMAAllEPS.get() != 0)
				? ((double) sumaTotalReclamosRecibidosVMAAllEPS.get() / sumaTotalUNDInscritosAllEPS.get()) * 100
				: 0.0;
		listaChart.add(new BarChartBasicoDto(Constants.LABEL_PROMEDIO, promedio));
		return listaChart;

	}

	// grafico 20- Porcentaje de reclamos por VMA resueltos fundados, según tamaño
	// de la EP

	public List<BarChartBasicoDto> reporteReclamosFundadosVMA(String anio) {

		AtomicInteger sumaTotalReclamosFundadosVMAAllEPS = new AtomicInteger(0);
		AtomicInteger sumaTotalReclamosRecibidosVMAAllEPS = new AtomicInteger(0);

		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
		Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos.stream()
				.collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipoEmpresa().getNombre())); 
		List<BarChartBasicoDto> listaChart = new ArrayList<>();

		registrosPorTipo.forEach((tipo, lista) -> {
			Integer totalReclamosFundadosVMA = respuestaVMARepository.getSumatotalRespuestaPorRegistros(
					mapToIdsRegistrosVma(lista), preguntasAlternativasVMA.getId_pregunta_nro_reclamos_fundados());
			Integer totalReclamosRecibidosVMA = respuestaVMARepository.getSumatotalRespuestaPorRegistros(
					mapToIdsRegistrosVma(lista), preguntasAlternativasVMA.getId_pregunta_nro_reclamos_recibidos());
			double porcentaje = (totalReclamosFundadosVMA != 0)
					? ((double) totalReclamosFundadosVMA / totalReclamosRecibidosVMA) * 100
					: 0.0;
			sumaTotalReclamosFundadosVMAAllEPS.addAndGet(totalReclamosFundadosVMA);
			sumaTotalReclamosRecibidosVMAAllEPS.addAndGet(totalReclamosRecibidosVMA);

			listaChart.add(new BarChartBasicoDto(tipo, porcentaje));
		});

		double promedio = (sumaTotalReclamosFundadosVMAAllEPS.get() != 0)
				? ((double) sumaTotalReclamosFundadosVMAAllEPS.get() / sumaTotalReclamosRecibidosVMAAllEPS.get()) * 100
				: 0.0;
		listaChart.add(new BarChartBasicoDto(Constants.LABEL_PROMEDIO, promedio));
		return listaChart;

	}

	// grafico 21 y 22

	public CostoAnualIncurridoCompletoDTO reporteCostoTotalIncurrido(String anio) {
		List<RegistroVMA> registros = registroVMARepository.findRegistros(anio);
		Map<String, List<RegistroVMA>> registrosPorTipo = registros.stream()
				.collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipoEmpresa().getNombre())); 

		List<BarChartBasicoDto> listaChart = new ArrayList<>();

		registros.forEach(registro -> {
			if (!registro.getEmpresa().getNombre().equals(Constants.EMPRESA_SUNASS)
					&& !registro.getEmpresa().getNombre().equals(Constants.EPS_SEDAPAL)
					&& registro.getEstado().equals(Constants.ESTADO_COMPLETO)) {
				BigDecimal costoAnual = respuestaVMARepository.getCostoAnualIncurridoPorRegistro(
						registro.getIdRegistroVma(), preguntasAlternativasVMA.getId_pregunta_costo_total_anual_und());
				listaChart.add(new BarChartBasicoDto(registro.getEmpresa().getNombre(), costoAnual.doubleValue()));
			}
		});

		List<CostoAnualIncurridoDTO> lista = new ArrayList<>();

		registrosPorTipo.forEach((tipoEmpresa, listaRegistros) -> {
			List<RegistroVMA> registrosCompletos = listaRegistros.stream()
					.filter(registro -> registro.getEstado().equals(Constants.ESTADO_COMPLETO))
					.collect(Collectors.toList());

			List<BigDecimal> idsVmaCompleto = mapToIdsRegistrosVmaBigDecimal(registrosCompletos);

			if (!idsVmaCompleto.isEmpty()) {
				BigDecimal cantidadVmaCompletos = BigDecimal.valueOf(idsVmaCompleto.size());
				BigDecimal sumaCostoTotalPorTipoEmpresaVmaCompleto = respuestaVMARepository
						.getSumaCostoTotalAnualIncurridoVmasCompleto(idsVmaCompleto,
								preguntasAlternativasVMA.getId_pregunta_costo_total_anual_und());

				BigDecimal promedio = sumaCostoTotalPorTipoEmpresaVmaCompleto.divide(cantidadVmaCompletos, 2,
						RoundingMode.HALF_UP);
				lista.add(new CostoAnualIncurridoDTO(tipoEmpresa, listaRegistros.size(), registrosCompletos.size(),
						sumaCostoTotalPorTipoEmpresaVmaCompleto, promedio));
			} else {
				lista.add(new CostoAnualIncurridoDTO(tipoEmpresa, listaRegistros.size(), registrosCompletos.size(),
						BigDecimal.ZERO, BigDecimal.ZERO));
			}
		});
		return new CostoAnualIncurridoCompletoDTO(listaChart, lista);
	}

	// grafico 23 Costo anual por conexión incurrido en la identificación,
	// inspección e inscripción de los UND

	public List<BarChartBasicoDto> reporteCostoAnualConexionIncurrido(String anio) {
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
		List<BarChartBasicoDto> listaChart = new ArrayList<>();

		registrosCompletos.forEach(registro -> {
			// if(!registro.getEmpresa().getNombre().equals("SUNASS") ) { //&&
			// registro.getEstado().equals("COMPLETO")
			BigDecimal costoAnual = respuestaVMARepository.getCostoAnualIncurridoPorRegistro(
					registro.getIdRegistroVma(), preguntasAlternativasVMA.getId_pregunta_costo_total_anual_und());

			listaChart.add(new BarChartBasicoDto(registro.getEmpresa().getNombre(), costoAnual.doubleValue()));
			// }
		});

		return listaChart;

	}

	// Gráficos 24 y 25: Costo anual incurrido por realizar las tomas de muestras inopinadas, según tamaño de la EP

	public CostoAnualIncurridoCompletoDTO reporteCostoTotalIncurridoMuestrasInopinadas(String anio) {
		List<RegistroVMA> registros = registroVMARepository.findRegistros(anio);
		Map<String, List<RegistroVMA>> registrosPorTipo = registros.stream()
				.collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipoEmpresa().getNombre()));

		List<BarChartBasicoDto> listaChart = new ArrayList<>();

		registros.forEach(registro -> {
			if (!registro.getEmpresa().getNombre().equals(Constants.EMPRESA_SUNASS)
					&& !registro.getEmpresa().getNombre().equals(Constants.EPS_SEDAPAL)
					&& registro.getEstado().equals(Constants.ESTADO_COMPLETO)) {
				BigDecimal costoAnual = respuestaVMARepository.getCostoAnualIncurridoPorRegistro(
						registro.getIdRegistroVma(),
						preguntasAlternativasVMA.getId_pregunta_costo_anual_muestras_inopinadas());
				listaChart.add(new BarChartBasicoDto(registro.getEmpresa().getNombre(), costoAnual.doubleValue()));
			}
		});

		List<CostoAnualIncurridoDTO> lista = new ArrayList<>();

		registrosPorTipo.forEach((tipoEmpresa, listaRegistros) -> {
			List<RegistroVMA> registrosCompletos = listaRegistros.stream()
					.filter(registro -> registro.getEstado().equals(Constants.ESTADO_COMPLETO))
					.collect(Collectors.toList());

			List<BigDecimal> idsVmaCompleto = mapToIdsRegistrosVmaBigDecimal(registrosCompletos);

			if (!idsVmaCompleto.isEmpty()) {
				BigDecimal cantidadVmaCompletos = BigDecimal.valueOf(idsVmaCompleto.size());
				BigDecimal sumaCostoTotalPorTipoEmpresaVmaCompleto = respuestaVMARepository
						.getSumaCostoTotalAnualIncurridoVmasCompleto(idsVmaCompleto,
								preguntasAlternativasVMA.getId_pregunta_costo_anual_muestras_inopinadas());

				BigDecimal promedio = sumaCostoTotalPorTipoEmpresaVmaCompleto.divide(cantidadVmaCompletos, 2,
						RoundingMode.HALF_UP);
				lista.add(new CostoAnualIncurridoDTO(tipoEmpresa, listaRegistros.size(), registrosCompletos.size(),
						sumaCostoTotalPorTipoEmpresaVmaCompleto, promedio));
			} else {
				lista.add(new CostoAnualIncurridoDTO(tipoEmpresa, listaRegistros.size(), registrosCompletos.size(),
						BigDecimal.ZERO, BigDecimal.ZERO));
			}
		});
		return new CostoAnualIncurridoCompletoDTO(listaChart, lista);
	}

	// Gráficos 26: Costo anual por conexión incurrido por realizar las tomas de
	// muestras inopinadas

	public List<BarChartBasicoDto> reporteGraficoCostoAnualIncurridoMuestrasInopinadas(String anio) {
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
		List<BarChartBasicoDto> listaChart = new ArrayList<>();

		registrosCompletos.forEach(registro -> {
			
			BigDecimal costoAnual = respuestaVMARepository.getCostoAnualIncurridoPorRegistro(
					registro.getIdRegistroVma(),
					preguntasAlternativasVMA.getId_pregunta_costo_anual_muestras_inopinadas());

			listaChart.add(new BarChartBasicoDto(registro.getEmpresa().getNombre(), costoAnual.doubleValue()));
			// }
		});
		return listaChart;

	}

	// grafico 27
	public List<BarChartBasicoDto> reporteCostoAnualPorOtrosGastos(String anio) {
		
		List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
		Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos.stream()
				.collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipoEmpresa().getNombre())); 
		List<BarChartBasicoDto> listaChart = new ArrayList<>();

		registrosPorTipo.forEach((tipo, lista) -> {
			BigDecimal CostoTotalAnualIncurridoVmas = respuestaVMARepository
					.getSumaCostoTotalAnualIncurridoVmasCompleto(mapToIdsRegistrosVmaBigDecimal(lista),
							preguntasAlternativasVMA.getId_pregunta_otros_gastos_implementacion());
			listaChart.add(new BarChartBasicoDto(tipo, CostoTotalAnualIncurridoVmas.doubleValue()));
		});

		double total = listaChart.stream().mapToDouble(BarChartBasicoDto::getValue).sum();

		listaChart.add(new BarChartBasicoDto(Constants.LABEL_TOTAL, total));

		return listaChart;
	}

}
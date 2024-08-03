package pe.gob.sunass.vma.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.sunass.vma.dto.BarChartBasicoDto;
import pe.gob.sunass.vma.dto.PieChartBasicoDto;
import pe.gob.sunass.vma.dto.RegistroEmpresaChartDto;
import pe.gob.sunass.vma.dto.RegistroPromedioTrabajadorVMAChartDto;
import pe.gob.sunass.vma.model.Empresa;
import pe.gob.sunass.vma.model.RegistroVMA;
import pe.gob.sunass.vma.model.RespuestaVMA;
import pe.gob.sunass.vma.repository.EmpresaRepository;
import pe.gob.sunass.vma.repository.RegistroVMARepository;
import pe.gob.sunass.vma.repository.RespuestaVMARepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    //TODO: Esto se debe de manejar como una configuración en la properties
    private final int PREGUNTA_SI_NO_ID = 1;
    private final int ALTERNATIVA_PREGUNTA_SI_ID = 12;
    private final int ALTERNATIVA_PREGUNTA_NO_ID = 13;
    private final int PREGUNTA_NUMERO_TRABAJADORES_EMPRESA_PRESTADORA_ID = 3;
    private final int ALTERNATIVA_UND_IDENTIFICADOS_PARCIAL_ID = 16;
    private final int ALTERNATIVA_UND_INSPECCIONADOS_PARCIAL_ID = 22;
    private final int ALTERNATIVA_TOTAL_INSPECCIONADOS_ID = 22;
    private final int ALTERNATIVA_SOLICITARON_DIAGRAMA_FLUJO_ID = 24;
    private final int ALTERNATIVA_PRESENTARON_DIAGRAMA_FLUJO_ID = 14;
    private final int PREGUNTA_UND_CAJA_REGISTRO_ID= 10;
    private final int ALTERNATIVA_UND_INSCRITOS_ID= 18;
    private final int PREGUNTA_UND_TOMA_MUESTRA_INOPINADA_ID= 11;
    private final int PREGUNTA_TOTAL_MUESTRAS_INOPINADAS_ID= 12;
    private final int ALTERNATIVA_UND_SOBREPASAN_PARAMETRO_ANEXO1_ID= 13;
    private final int ALTERNATIVA_UND_FACTURADO_CONCEPTO_ADICIONAL_ID= 13;
    
    

    public List<RegistroEmpresaChartDto> reporteBarraRegistros(String anio) {
        List<Empresa> empresas = empresaRepository.findAll();

        Map<String, List<Empresa>> empresasPorTipo =
                empresas.stream().filter(empresa -> !"NINGUNO".equals(empresa.getTipo()))
                .collect(Collectors.groupingBy(Empresa::getTipo));

        List<RegistroEmpresaChartDto> dataList = new ArrayList<>();
        empresasPorTipo.forEach((tipo, lista) -> {
            long cantidadRegistro = registroVMARepository.registrosCompletadosPorTipoEmpresa(tipo, anio);
            dataList.add(new RegistroEmpresaChartDto(tipo, (int) cantidadRegistro, lista.size()));
        });

        return dataList;
    }

    //graf3,  division empresas respondieronSI x tamaño / completaronInfo x tamaño
    public List<RegistroEmpresaChartDto> reporteSiNo(String anio) {
    	
    	List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
    	
    	logger.info("registrosCompletos -"+ registrosCompletos.size());
        Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos
                .stream()
                .collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipo()));
        List<RegistroEmpresaChartDto> dataList = new ArrayList<>();
        
//        registrosPorTipo.forEach((tipo, lista) -> { 
//            List<RespuestaVMA> respuestas = respuestaVMARepository.findRespuestasByIdPreguntaAndTipoEmpresa(PREGUNTA_SI_NO_ID, tipo, anio);
//            long respondieronSi = respuestas.stream().filter(respuesta -> respuesta.getRespuesta().equals("Sí")).count();
//            long respondieronNo = respuestas.stream().filter(respuesta -> respuesta.getRespuesta().equals("No")).count();
//            logger.info("TIPO -"+ tipo);
//            logger.info("respondieronSi - "+respondieronSi);
//            logger.info("respondieronNo -"+respondieronNo);
//            logger.info("lista.size() -"+lista.size());
//          //  dataList.add(new RegistroEmpresaChartDto(tipo, (int) respondieronSi, lista.size()));
//           
//        });
        
        registrosPorTipo.forEach((tipo, lista) -> {
            Integer respondieronSi = respuestaVMARepository
                    .getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(lista), ALTERNATIVA_PREGUNTA_SI_ID);  //null
            Integer respondieronNO = respuestaVMARepository
                    .getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(lista), ALTERNATIVA_PREGUNTA_NO_ID);  //null

            dataList.add(new RegistroEmpresaChartDto(tipo,  respondieronSi, (respondieronSi+respondieronNO)));

        });
        
        return dataList;
    }

    public List<RegistroPromedioTrabajadorVMAChartDto> reporteNumeroPromedioDeTrabajadoresDedicadosVMA(String anio) {
        List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
        Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos
                .stream()
                .collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipo()));

        List<RegistroPromedioTrabajadorVMAChartDto> registrosPromedio = new ArrayList<>();
        registrosPorTipo.forEach((tipo, lista) -> {
            registrosPromedio.add(mapToRegistroPromedioTrrabajadorVMA(tipo,mapToIdsRegistrosVma(lista)));
        });
        return registrosPromedio;
    }

    //grafico 5
    public List<PieChartBasicoDto> reporteNumeroTotalUND(String anio) {
        List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
        Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos
                .stream()
                .collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipo()));

        List<PieChartBasicoDto> listaNumeroTotalUND = new ArrayList<>();
        Integer sumaTotalEmpresasUNDIngresadas = respuestaVMARepository.getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(registrosCompletos), ALTERNATIVA_UND_IDENTIFICADOS_PARCIAL_ID);

        registrosPorTipo.forEach((tipo, lista) -> {
            Integer sumaTotalPorTipoEmpresaUNDIngresadas = respuestaVMARepository.getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(lista), ALTERNATIVA_UND_IDENTIFICADOS_PARCIAL_ID);
            double porcentaje = ((double) sumaTotalPorTipoEmpresaUNDIngresadas / sumaTotalEmpresasUNDIngresadas) * 100;
            listaNumeroTotalUND.add(
                    new PieChartBasicoDto(tipo, porcentaje)
            );
        });
        return listaNumeroTotalUND;
    }

  //dhr graf 6
    //observar formula -
    
    //formula - Sumatoria de UND inspeccionadas de las EPS por tamaño/Sumatoria UND identificadas de las EPS por tamaño.
    //Para la barra del promedio la formula   Sumatoria de UND inspeccionadas de todas las EPS /Sumatoria UND identificadas de todas las EPS
    
    public List<BarChartBasicoDto> reporteNumeroTotalUNDInspeccionados(String anio) {
        List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
        Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos
                .stream()
                .collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipo()));
        List<BarChartBasicoDto> listaChart = new ArrayList<>();

        registrosPorTipo.forEach((tipo, lista) -> {
            Integer totalInspeccionados = respuestaVMARepository
                    .getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(lista), ALTERNATIVA_UND_INSPECCIONADOS_PARCIAL_ID);
            Integer totalIdentificados = respuestaVMARepository
                    .getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(lista), ALTERNATIVA_UND_IDENTIFICADOS_PARCIAL_ID);

            double porcentaje = ((double) totalInspeccionados / totalIdentificados) * 100;
            listaChart.add(new BarChartBasicoDto(tipo, porcentaje));
        });
        double sumaPorcentaje = listaChart
                .stream()
                .mapToDouble(BarChartBasicoDto::getValue).sum();
        listaChart.add(new BarChartBasicoDto("Promedio", sumaPorcentaje / listaChart.size()));

        return listaChart;
        
    }
    
    //GRAF 7
    public List<BarChartBasicoDto> reporteDiagramaFlujoYBalance(String anio) {
        List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
        registrosCompletos.get(0);
        Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos
                .stream()
                .collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipo()));
        List<BarChartBasicoDto> listaChart = new ArrayList<>();

        registrosPorTipo.forEach((tipo, lista) -> {
            Integer totalDiagrama = respuestaVMARepository
                    .getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(lista), ALTERNATIVA_SOLICITARON_DIAGRAMA_FLUJO_ID);
            Integer totalInspeccionados = respuestaVMARepository
                    .getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(lista), ALTERNATIVA_TOTAL_INSPECCIONADOS_ID);

            double porcentaje = ((double) totalDiagrama / totalInspeccionados) * 100;
            listaChart.add(new BarChartBasicoDto(tipo, porcentaje));

        });

        double sumaPorcentaje = listaChart
                .stream()
                .mapToDouble(BarChartBasicoDto::getValue).sum();

        listaChart.add(new BarChartBasicoDto("Promedio", sumaPorcentaje / listaChart.size()));

        return listaChart;
    }

    private RegistroPromedioTrabajadorVMAChartDto mapToRegistroPromedioTrrabajadorVMA(String tipo, List<Integer> idsRegistrosVma) {
        Integer sumaTrabajadoresDesdicadosRegistroVMA = respuestaVMARepository.getSumaTrabajadoresDesdicadosRegistroVMA(idsRegistrosVma, PREGUNTA_NUMERO_TRABAJADORES_EMPRESA_PRESTADORA_ID);
        double promedio = (double) sumaTrabajadoresDesdicadosRegistroVMA / idsRegistrosVma.size();
        return new RegistroPromedioTrabajadorVMAChartDto(tipo, promedio, sumaTrabajadoresDesdicadosRegistroVMA, idsRegistrosVma.size());
    }

    private List<Integer> mapToIdsRegistrosVma(List<RegistroVMA> registrosVma) {
        return registrosVma
                .stream()
                .map(RegistroVMA::getIdRegistroVma)
                .collect(Collectors.toList());
    }

    private Map<String, List<RegistroVMA>> getRegistrosPorTipoEmpresa(String anio) {
        List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
        return registrosCompletos
                .stream()
                .collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipo()));
    }
    
    //dhr grafic 8
    public List<BarChartBasicoDto> reporteDiagramaFlujoYBalancePresentados(String anio) {
        List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
        Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos
                .stream()
                .collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipo()));
        List<BarChartBasicoDto> listaChart = new ArrayList<>();

        registrosPorTipo.forEach((tipo, lista) -> {
            Integer totalPresentaronDiagrama = respuestaVMARepository
                    .getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(lista), ALTERNATIVA_PRESENTARON_DIAGRAMA_FLUJO_ID);
            Integer totalSolicitaronDiagrama = respuestaVMARepository
                    .getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(lista), ALTERNATIVA_SOLICITARON_DIAGRAMA_FLUJO_ID);

            double porcentaje = ((double) totalPresentaronDiagrama / totalSolicitaronDiagrama) * 100;
            listaChart.add(new BarChartBasicoDto(tipo, porcentaje));
        });

        double sumaPorcentaje = listaChart
                .stream()
                .mapToDouble(BarChartBasicoDto::getValue).sum();

        listaChart.add(new BarChartBasicoDto("Promedio", sumaPorcentaje / listaChart.size()));

        return listaChart;
    }

    
    
  // graf 10  -  Porcentaje de UND que cuentan con caja de registro o dispositivo similar en la parte externa de su predio, según tamaño de la EP

    public List<BarChartBasicoDto> reporteUNDconCajaRegistro(String anio) {
        List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
        Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos
                .stream()
                .collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipo()));
        List<BarChartBasicoDto> listaChart = new ArrayList<>();

        registrosPorTipo.forEach((tipo, lista) -> {
            Integer totalUNDCajaRegistro = respuestaVMARepository
                    .getSumatotalUNDInscritosRegistroVMA(mapToIdsRegistrosVma(lista), PREGUNTA_UND_CAJA_REGISTRO_ID);
            Integer totalUNDInscritos = respuestaVMARepository
                    .getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(lista), ALTERNATIVA_UND_INSCRITOS_ID);
            double porcentaje = ((double) totalUNDCajaRegistro / totalUNDInscritos) * 100;
            listaChart.add(new BarChartBasicoDto(tipo, porcentaje));
        });
        double sumaPorcentaje = listaChart
                .stream()
                .mapToDouble(BarChartBasicoDto::getValue).sum();
        listaChart.add(new BarChartBasicoDto("Promedio", sumaPorcentaje / listaChart.size()));
        return listaChart;
        
    }
    
  // Gráfico 11: Porcentaje de UND a los que se realizó la toma de muestra inopinada, según tamaño de la EP
    
    public List<BarChartBasicoDto> reportePorcentajeUNDTomaMuestraInopinada(String anio) {
        List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
        Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos
                .stream()
                .collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipo()));
        List<BarChartBasicoDto> listaChart = new ArrayList<>();

        registrosPorTipo.forEach((tipo, lista) -> {
            Integer totalUNDTomaMuestraInopinada = respuestaVMARepository
                    .getSumatotalRespuestaPorRegistros(mapToIdsRegistrosVma(lista), PREGUNTA_UND_TOMA_MUESTRA_INOPINADA_ID);
            Integer totalUNDInscritos = respuestaVMARepository
                    .getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(lista), ALTERNATIVA_UND_INSCRITOS_ID);
            double porcentaje = ((double) totalUNDTomaMuestraInopinada / totalUNDInscritos) * 100;
            listaChart.add(new BarChartBasicoDto(tipo, porcentaje));
        });
        double sumaPorcentaje = listaChart
                .stream()
                .mapToDouble(BarChartBasicoDto::getValue).sum();
        listaChart.add(new BarChartBasicoDto("Promedio", sumaPorcentaje / listaChart.size()));
        return listaChart;
    }
    
    // grafico 12 Porcentaje de toma de muestra inopinada, según tamaño de la EP  
    /*
     * Sumatoria del Número total de tomas de muestras inopinadas por tamaño de la EPS /
     *  Sumatoria de Número total de tomas de muestras inopinadas de todas las EPS
     *  
     * */
  
    public List<PieChartBasicoDto> reporteNumeroTotalTomasMuestraInopinadas(String anio) {
        List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
        Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos
                .stream()
                .collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipo()));

        List<PieChartBasicoDto> listaTotalTomasMuestraInopinadas = new ArrayList<>();
        Integer sumaTotalTomasMuestraInopinadas = respuestaVMARepository.getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(registrosCompletos), PREGUNTA_TOTAL_MUESTRAS_INOPINADAS_ID);

        registrosPorTipo.forEach((tipo, lista) -> {
            Integer sumaTotalPorTipoEPSTomasMuestraInopinadas = respuestaVMARepository.getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(lista), PREGUNTA_UND_TOMA_MUESTRA_INOPINADA_ID);
            double porcentaje = ((double) sumaTotalPorTipoEPSTomasMuestraInopinadas / sumaTotalTomasMuestraInopinadas) * 100;
            listaTotalTomasMuestraInopinadas.add(
                    new PieChartBasicoDto(tipo, porcentaje)
            );
        });
        return listaTotalTomasMuestraInopinadas;
    }
    
    //Gráfico 13: Porcentaje de UND que sobrepasan algún(os) parámetro(s) del Anexo N° 1 del REGLAMENTO VMA, según tamaño de la EP
    
    
   
    public List<BarChartBasicoDto> reportePorcentajeUNDSobrepasanParametroAnexo1(String anio) {
        List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
        Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos
                .stream()
                .collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipo()));
        List<BarChartBasicoDto> listaChart = new ArrayList<>();

        registrosPorTipo.forEach((tipo, lista) -> {
            Integer totalUNDTomaMuestraInopinada = respuestaVMARepository
                    .getSumatotalRespuestaPorRegistros(mapToIdsRegistrosVma(lista), PREGUNTA_UND_TOMA_MUESTRA_INOPINADA_ID);
            Integer totalUNDParametroAnexo1 = respuestaVMARepository
                    .getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(lista), ALTERNATIVA_UND_SOBREPASAN_PARAMETRO_ANEXO1_ID);
            double porcentaje = ((double) totalUNDParametroAnexo1 / totalUNDTomaMuestraInopinada) * 100;
            listaChart.add(new BarChartBasicoDto(tipo, porcentaje));
        });
        double sumaPorcentaje = listaChart
                .stream()
                .mapToDouble(BarChartBasicoDto::getValue).sum();
        listaChart.add(new BarChartBasicoDto("Promedio", sumaPorcentaje / listaChart.size()));
        return listaChart;
    
    }
    
    
    //Gráfico 14: Porcentaje de UND a los que se ha facturado por concepto de Pago adicional por exceso de concentración, según tamaño de la EP
    
    /*
     * Sumatoria de UND a los que se ha facturado por concepto de Pago adicional por exceso de concentración de las EPS por tamaño/
     * Sumatoria UND que sobrepasan algún(os) parámetro(s) del Anexo N° 1 del Reglamento de VMA de las EPS por tamaño.
		Para la barra del promedio la formula:
		  Sumatoria de UND a los que se ha facturado por concepto de Pago adicional por exceso de concentración de todas las EPS /
		  Sumatoria UND que sobrepasan algún(os) parámetro(s) del Anexo N° 1 del Reglamento de VMA de todas las EPS 
     * 
     * 
     */
    public List<BarChartBasicoDto> reportePorcentajeUNDFacturadoPorConceptoAdicional(String anio) {
        List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
        Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos
                .stream()
                .collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipo()));
        List<BarChartBasicoDto> listaChart = new ArrayList<>();

        registrosPorTipo.forEach((tipo, lista) -> {
            Integer totalUNDTomaMuestraInopinada = respuestaVMARepository
                    .getSumatotalRespuestaPorRegistros(mapToIdsRegistrosVma(lista), PREGUNTA_UND_TOMA_MUESTRA_INOPINADA_ID);
            Integer totalUNDParametroAnexo1 = respuestaVMARepository
                    .getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(lista), ALTERNATIVA_UND_SOBREPASAN_PARAMETRO_ANEXO1_ID);
            double porcentaje = ((double) totalUNDParametroAnexo1 / totalUNDTomaMuestraInopinada) * 100;
            listaChart.add(new BarChartBasicoDto(tipo, porcentaje));
        });
        double sumaPorcentaje = listaChart
                .stream()
                .mapToDouble(BarChartBasicoDto::getValue).sum();
        listaChart.add(new BarChartBasicoDto("Promedio", sumaPorcentaje / listaChart.size()));
        return listaChart;
    
    }
    
    //grafico 15
    /*
     * Se debe contabilizar las EP que completaron el registro de información y se contabilizará el valor del campo Número de UND que realizaron 
     * el Pago adicional por exceso de concentración y Número de UND a los que se ha facturado por concepto de Pago adicional por exceso de concentración. 
     *formula
     *Sumatoria de UND que realizaron el Pago adicional por exceso de concentración de las EPS por tamaño /
     *Sumatoria de las UND a los que se ha facturado por concepto de Pago adicional por exceso de concentración de las EPS por tamaño
     * 
     * */
    
    
    
    
    
    
    
}

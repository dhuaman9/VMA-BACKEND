package pe.gob.sunass.vma.service;

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

    @Autowired
    private RegistroVMARepository registroVMARepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private RespuestaVMARepository respuestaVMARepository;

    //TODO: Esto se debe de manejar como una configuración en la properties
    private final int PREGUNTA_SI_NO_ID = 1;
    private final int PREGUNTA_NUMERO_TRABAJADORES_EMPRESA_PRESTADORA_ID = 3;
    private final int ALTERNATIVA_PARCIAL_ID = 16;
    private final int ALTERNATIVA_TOTAL_INSPECCIONADOS_ID = 22;
    private final int ALTERNATIVA_PRESENTACION_DIAGRAMA_FLUJO_ID = 24;


    public List<RegistroEmpresaChartDto> reporteBarraRegistros(String anio) {
        List<Empresa> empresas = empresaRepository.findAll();

        Map<String, List<Empresa>> empresasPorTipo =
                empresas.stream().filter(empresa -> !"NINGUNO".equals(empresa.getTipo()))
                .collect(Collectors.groupingBy(Empresa::getTipo));

        List<RegistroEmpresaChartDto> dataList = new ArrayList<>();
        empresasPorTipo.forEach((tipo, lista) -> {
            long cantidadRegistro = registroVMARepository.registrosPorTipoEmpresa(tipo, anio);
            dataList.add(new RegistroEmpresaChartDto(tipo, (int) cantidadRegistro, lista.size()));
        });

        return dataList;
    }

    public List<RegistroEmpresaChartDto> reporteSiNo(String anio) {
        List<Empresa> empresas = empresaRepository.findAll();
        List<RegistroEmpresaChartDto> dataList = new ArrayList<>();
        
        Map<String, List<Empresa>> empresasPorTipo =
                empresas.stream().filter(empresa -> !"NINGUNO".equals(empresa.getTipo()))
                .collect(Collectors.groupingBy(Empresa::getTipo));


        empresasPorTipo.forEach((tipo, lista) -> {
            List<RespuestaVMA> respuestas = respuestaVMARepository.findRespuestasByIdPreguntaAndTipoEmpresa(PREGUNTA_SI_NO_ID, tipo, anio);
            long respondieronSi = respuestas.stream().filter(respuesta -> respuesta.getRespuesta().equals("Sí")).count();
            dataList.add(new RegistroEmpresaChartDto(tipo, (int) respondieronSi, lista.size()));
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

    public List<PieChartBasicoDto> reporteNumeroTotalUND(String anio) {
        List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
        Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos
                .stream()
                .collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipo()));

        List<PieChartBasicoDto> listaNumeroTotalUND = new ArrayList<>();
        Integer sumaTotalEmpresasUNDIngresadas = respuestaVMARepository.getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(registrosCompletos), ALTERNATIVA_PARCIAL_ID);

        registrosPorTipo.forEach((tipo, lista) -> {
            Integer sumaTotalPorTipoEmpresaUNDIngresadas = respuestaVMARepository.getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(lista), ALTERNATIVA_PARCIAL_ID);
            double porcentaje = ((double) sumaTotalPorTipoEmpresaUNDIngresadas / sumaTotalEmpresasUNDIngresadas) * 100;
            listaNumeroTotalUND.add(
                    new PieChartBasicoDto(tipo, porcentaje)
            );
        });
        return listaNumeroTotalUND;
    }

    public List<BarChartBasicoDto> reporteDiagramaFlujoYBalance(String anio) {
        List<RegistroVMA> registrosCompletos = registroVMARepository.findRegistrosCompletos(anio);
        Map<String, List<RegistroVMA>> registrosPorTipo = registrosCompletos
                .stream()
                .collect(Collectors.groupingBy(reg -> reg.getEmpresa().getTipo()));
        List<BarChartBasicoDto> listaChart = new ArrayList<>();

        registrosPorTipo.forEach((tipo, lista) -> {
            Integer totalDiagrama = respuestaVMARepository
                    .getSumaTotalRespuestaAlternativaPorRegistros(mapToIdsRegistrosVma(lista), ALTERNATIVA_PRESENTACION_DIAGRAMA_FLUJO_ID);
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
}

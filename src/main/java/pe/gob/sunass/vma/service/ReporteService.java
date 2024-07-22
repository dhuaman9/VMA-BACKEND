package pe.gob.sunass.vma.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.sunass.vma.dto.BarraDto;
import pe.gob.sunass.vma.model.Empresa;
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

    public List<BarraDto> reporteBarraRegistros(Integer anio) {
        List<Empresa> empresas = empresaRepository.findAll();

        Map<String, List<Empresa>> empresasPorTipo =
                empresas.stream().filter(empresa -> !"NINGUNO".equals(empresa.getTipo()))
                .collect(Collectors.groupingBy(Empresa::getTipo));

        List<BarraDto> dataList = new ArrayList<>();
        empresasPorTipo.forEach((tipo, lista) -> {
            long cantidadRegistro = registroVMARepository.registrosPorTipoEmpresa(tipo, anio);
            dataList.add(new BarraDto(tipo, (int) cantidadRegistro, lista.size()));
        });

        return dataList;
    }

    public List<BarraDto> reporteSiNo(int anio) {
        List<Empresa> empresas = empresaRepository.findAll();
        List<BarraDto> dataList = new ArrayList<>();
        
        Map<String, List<Empresa>> empresasPorTipo =
                empresas.stream().filter(empresa -> !"NINGUNO".equals(empresa.getTipo()))
                .collect(Collectors.groupingBy(Empresa::getTipo));


        empresasPorTipo.forEach((tipo, lista) -> {
            List<RespuestaVMA> respuestas = respuestaVMARepository.findRespuestasByIdPreguntaAndTipoEmpresa(PREGUNTA_SI_NO_ID, tipo, anio);
            long respondieronSi = respuestas.stream().filter(respuesta -> respuesta.getRespuesta().equals("Sí")).count();
            dataList.add(new BarraDto(tipo, (int) respondieronSi, lista.size()));
        });

        return dataList;
    }
}

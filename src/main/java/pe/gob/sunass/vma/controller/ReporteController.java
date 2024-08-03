package pe.gob.sunass.vma.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.sunass.vma.service.ReporteService;
import pe.gob.sunass.vma.util.ResponseEntity;

@RestController
@RequestMapping("/api/reporte")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/registros")
    public ResponseEntity<?> generarReporteRegistro(@RequestParam("anio") String anio) {
        return ResponseEntity.ok(reporteService.reporteBarraRegistros(anio));
    }

    @GetMapping("/respuesta-si-no")
    public ResponseEntity<?> generarReporteRespuestaSiNo(@RequestParam("anio") String anio) {
        return ResponseEntity.ok(reporteService.reporteSiNo(anio));
    }

    @GetMapping("/trabajadores-dedicados-registro")
    public ResponseEntity<?> generarReporteTrabajadoresDedicadosRegistro(@RequestParam("anio") String anio) {
        return ResponseEntity.ok(reporteService.reporteNumeroPromedioDeTrabajadoresDedicadosVMA(anio));
    }

    @GetMapping("/numero-total-und")
    public ResponseEntity<?> generarReporteNumeroTotalUND(@RequestParam("anio") String anio) {
        return ResponseEntity.ok(reporteService.reporteNumeroTotalUND(anio));
    }

    //UND inspeccionados grafico 6
    @GetMapping("/numero-und-inspeccionados")
    public ResponseEntity<?> generarReporteUNDInspecionados(@RequestParam("anio") String anio) {
        return ResponseEntity.ok(reporteService.reporteNumeroTotalUNDInspeccionados(anio));
    }
     
    
    @GetMapping("/diagrama-flujo-balance")
    public ResponseEntity<?> generarReporteDiagramaFlujoYBalance(@RequestParam("anio") String anio) {
        return ResponseEntity.ok(reporteService.reporteDiagramaFlujoYBalance(anio));
    }
    
    //grafico 8
    @GetMapping("/diagrama-flujo-balance-presentado")
    public ResponseEntity<?> generarReporteDiagramaFlujoYBalancePresentado(@RequestParam("anio") String anio) {
        return ResponseEntity.ok(reporteService.reporteDiagramaFlujoYBalancePresentados(anio));
    }
    
    //grafico 10 , Porcentaje de UND que cuentan con caja de registro o dispositivo similar en la parte externa de su predio, según tamaño de la EP
    @GetMapping("/porcentaje-und-caja-registro")
    public ResponseEntity<?> generarReportePorcentajesUNDCajaRegistro(@RequestParam("anio") String anio) {
        return ResponseEntity.ok(reporteService.reporteUNDconCajaRegistro(anio));
    }
    
   // grafico 11  Porcentaje de UND a los que se realizó la toma de muestra inopinada, según tamaño de la EP
    @GetMapping("/porcentaje-und-muestra-inopinada")
    public ResponseEntity<?> generarReportePorcentajesUNDTomaMuestraInopinada(@RequestParam("anio") String anio) {
        return ResponseEntity.ok(reporteService.reportePorcentajeUNDTomaMuestraInopinada(anio));
    }
    
 // grafico 12  Porcentaje de UND a los que se realizó la toma de muestra inopinada, según tamaño de la EP
    @GetMapping("/porcentaje-total-muestras-inopinadas")
    public ResponseEntity<?> generarReportePorcentajesTotalMuestrasInopinadas(@RequestParam("anio") String anio) {
        return ResponseEntity.ok(reporteService.reporteNumeroTotalTomasMuestraInopinadas(anio));
    }
    
}

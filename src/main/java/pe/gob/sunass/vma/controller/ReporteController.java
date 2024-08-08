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
    
    //grafico 9  , Comparativo de los UND registrados, inspeccionados e identificados
    @GetMapping("/comparativo-UND")
    public ResponseEntity<?> generarReporteComparativoUND(@RequestParam("anio") String anio) {
        return ResponseEntity.ok(reporteService.reporteComparativoUND(anio));
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
    
 // grafico 12  Porcentaje de toma de muestra inopinada, según tamaño de la EP  
    @GetMapping("/porcentaje-total-muestras-inopinadas")
    public ResponseEntity<?> generarReportePorcentajesTotalMuestrasInopinadas(@RequestParam("anio") String anio) {
        return ResponseEntity.ok(reporteService.reporteNumeroTotalTomasMuestraInopinadas(anio));
    }
    
 // grafico 13  Porcentaje de UND que sobrepasan algún(os) parámetro(s) del Anexo N° 1 del REGLAMENTO VMA, según tamaño de la EP
    @GetMapping("/porcentaje-und-sobrepasan-parametro-anexo1")
    public ResponseEntity<?> generarReportePorcentajesUNDSobrepasanParametrosAnexoUno(@RequestParam("anio") String anio) {
        return ResponseEntity.ok(reporteService.reportePorcentajeUNDSobrepasanParametroAnexo1(anio));
    }
    
 // grafico 14  Porcentaje de UND a los que se ha facturado por concepto de Pago adicional por exceso de concentración, según tamaño de la EP
    @GetMapping("/porcentaje-und-facturado-pago-adicional")
    public ResponseEntity<?> generarReportePorcentajesUNDfacturadoPagoAdicional(@RequestParam("anio") String anio) {
        return ResponseEntity.ok(reporteService.reportePorcentajeUNDFacturadoPorConceptoAdicional(anio));
    }
    
 // grafico 15  Porcentaje de UND que realizaron el Pago adicional por exceso de concentración, según tamaño de la EP
    @GetMapping("/porcentaje-und-pago-adicional")
    public ResponseEntity<?> generarReporteUNDPagoAdicional(@RequestParam("anio") String anio) {
        return ResponseEntity.ok(reporteService.reportePorcentajeUNDPagoAdicional(anio));
    }
    
    /*
 // grafico 16  Porcentaje de UND que sobrepasan algún(os) parámetro(s) del Anexo N° 2 del Reglamento de VMA, según tamaño de la EP 
    @GetMapping("/porcentaje-total-muestras-inopinadas")
    public ResponseEntity<?> generarReportePorcentajesTUNDParametroAnexo2(@RequestParam("anio") String anio) {
        return ResponseEntity.ok(reporteService.reporteNumeroTotalTomasMuestraInopinadas(anio));
    }
    
 // grafico 17  Porcentaje de UND a los que les ha otorgado un plazo adicional (hasta 18 meses) con el fin de implementar las acciones de mejora y acreditar el cumplimiento de los VMA, según tamaño de la EP
    @GetMapping("/porcentaje-total-muestras-inopinadas")
    public ResponseEntity<?> generarReporteUNDPlazoAdicional(@RequestParam("anio") String anio) {
        return ResponseEntity.ok(reporteService.reporteNumeroTotalTomasMuestraInopinadas(anio));
    }*/
    
}

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

    @GetMapping("/diagrama-flujo-balance")
    public ResponseEntity<?> generarReporteDiagramaFlujoYBalance(@RequestParam("anio") String anio) {
        return ResponseEntity.ok(reporteService.reporteDiagramaFlujoYBalance(anio));
    }
}

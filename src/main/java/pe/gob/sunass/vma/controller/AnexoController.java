package pe.gob.sunass.vma.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.sunass.vma.service.RegistroVMAService;
import pe.gob.sunass.vma.util.ResponseEntity;

@RestController
@RequestMapping("/anexos")
public class AnexoController {

    @Autowired
    private RegistroVMAService registroVMAService;

    //anexo1
    @GetMapping("/registros-vma")
    public ResponseEntity<?> getAnexos(@RequestParam(name = "anio") String anio) {
        return ResponseEntity.ok(registroVMAService.listaDeAnexosRegistrosVmaDTO(anio));
    }

    //anexo 2
    @GetMapping("/respuestas-si")
    public ResponseEntity<?> getRespuestasMarcaronSi(@RequestParam(name = "anio") String anio) {
        return ResponseEntity.ok(registroVMAService.listaDeAnexosRegistroMarcaronSi(anio));
    }
    
    //anexo3
    
    @GetMapping("/registros-und")
    public ResponseEntity<?> getRegistrosUND(@RequestParam(name = "anio") String anio) {
        return ResponseEntity.ok(registroVMAService.listaDeAnexosRelacionEPInspeccionados(anio));
    }
    
    //anexo4  de toma de muestra inopinada 
    @GetMapping("/registros-tomas-muestras-inopinadas")
    public ResponseEntity<?> getTomaMuestrasInopinadas(@RequestParam(name = "anio") String anio) {
        return ResponseEntity.ok(registroVMAService.listaDeAnexosTomaDeMuestrasInopinadas(anio));
    }
    
    //anexo5  Detalle de las EP que han realizado la evaluación de los VMA del Anexo 1 del reglamento de VMA
    @GetMapping("/registros-ep-evaluaron-vma-anexo1")
    public ResponseEntity<?> getEPRealizaronEvaluacionVMAAnexo1(@RequestParam(name = "anio") String anio) {
        return ResponseEntity.ok(registroVMAService.listaDeAnexosEPevaluaronVMAAnexo1(anio));
    }
    
    //anexo6  Detalle de las EP que han realizado la evaluación de los VMA del Anexo 2 del reglamento de VMA
    @GetMapping("/registros-ep-evaluaron-vma-anexo2")
    public ResponseEntity<?> getEPRealizaronEvaluacionVMAAnexo2(@RequestParam(name = "anio") String anio) {
        return ResponseEntity.ok(registroVMAService.listaDeAnexosEPevaluaronVMAAnexo2(anio));
    }
    
    //anexo7  Detalle de las EP que han realizado la atención de reclamos referidos a VMA
    @GetMapping("/registros-ep-reclamos-vma")
    public ResponseEntity<?> getEPAtencionReclamosVMA(@RequestParam(name = "anio") String anio) {
        return ResponseEntity.ok(registroVMAService.listaDeAnexosEPSAtendieronReclamos(anio));
    }
    
  //anexo8  . Detalle de los costos de identificación, inspección y registro de los UND
    @GetMapping("/registros-costos-und")
    public ResponseEntity<?> getCostosIdentificacionUND(@RequestParam(name = "anio") String anio) {
        return ResponseEntity.ok(registroVMAService.listaDeAnexosEPSAtendieronReclamos(anio));
    }
    
    
  //anexo9  Detalle de los costos totales por toma de muestras inopinadas
    @GetMapping("/registros-costos-totales-muestras-inopinadas")
    public ResponseEntity<?> getDetalleCostosTotalesxMuestrasInopinadas(@RequestParam(name = "anio") String anio) {
        return ResponseEntity.ok(registroVMAService.listaDeAnexosEPSAtendieronReclamos(anio));
    }
    
    
    
    
}

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

    @GetMapping("/registros-vma")
    public ResponseEntity<?> getAnexos(@RequestParam(name = "anio") String anio) {
        return ResponseEntity.ok(registroVMAService.listaDeAnexosRegistrosVmaDTO(anio));
    }
}

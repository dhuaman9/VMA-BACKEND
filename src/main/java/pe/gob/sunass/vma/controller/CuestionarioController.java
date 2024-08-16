package pe.gob.sunass.vma.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pe.gob.sunass.vma.dto.CuestionarioDTO;
import pe.gob.sunass.vma.model.cuestionario.Cuestionario;
import pe.gob.sunass.vma.service.CuestionarioService;
import pe.gob.sunass.vma.util.ResponseEntity;

import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@CrossOrigin
@RequestMapping("/cuestionarios")
public class CuestionarioController {

	@Autowired
    private  CuestionarioService cuestionarioService;

    @GetMapping(path = "/{idCuestionario}", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCuestionarioById(@PathVariable Integer idCuestionario) {
        Optional<Cuestionario> cuestionario = cuestionarioService.findById(idCuestionario);

        if(cuestionario.isPresent()) {
            return ResponseEntity.ok(cuestionario.get());
        } else {
            return new ResponseEntity<>(NOT_FOUND);
        }
    }
    
    @GetMapping(path = "/lastId", produces= MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<?> getCuestionarioByIdMax() {
       
        Optional<Cuestionario> cuestionario = cuestionarioService.getLastCuestionario();

        if(cuestionario.isPresent()) {
            return ResponseEntity.ok(cuestionario.get());
        } else {
            return new ResponseEntity<>(NOT_FOUND);
        }
    }

    @GetMapping(path = "/respuestas/{idRegistro}", produces= MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<?> getCuestionarioConRespuestas(@PathVariable Integer idRegistro) {

        CuestionarioDTO cuestionario = cuestionarioService.getCuestionarioConRespuestas(idRegistro);

        if(cuestionario != null) {
            return ResponseEntity.ok(cuestionario);
        } else {
            return new ResponseEntity<>(NOT_FOUND);
        }
    }
    
    
}

package pe.gob.sunass.vma.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.gob.sunass.vma.model.Cuestionario;
import pe.gob.sunass.vma.repository.CuestionarioRepository;
import pe.gob.sunass.vma.repository.SeccionRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CuestionarioService {
    private final CuestionarioRepository cuestionarioRepository;
    private final SeccionRepository seccionRepository;

    public Optional<Cuestionario> findById(Integer idCuestionario) {
        Optional<Cuestionario> cuestionario = cuestionarioRepository.findById(idCuestionario);

        cuestionario.ifPresent(cuestionarioEncontrado -> {
            cuestionarioEncontrado.setSecciones(seccionRepository.findAllByCuestionarioId(idCuestionario));
        });

        return cuestionario;
    }
    
    //obtiene el cuestionario segun el ultimo ID
    public Optional<Cuestionario>  getLastCuestionario() {
    	 Optional<Cuestionario> cuestionario  = cuestionarioRepository.getLastCuestionario();

    	 
    	 cuestionario.ifPresent(cuestionarioEncontrado -> {
             cuestionarioEncontrado.setSecciones(seccionRepository.findAllByCuestionarioId(cuestionario.get().getIdCuestionario()));
         });

         return cuestionario;
    }
    
}

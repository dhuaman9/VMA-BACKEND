package pe.gob.sunass.vma.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pe.gob.sunass.vma.model.cuestionario.Archivo;

@Repository
public interface ArchivoRepository extends CrudRepository<Archivo, Integer> {

	public Optional<Archivo> findByIdArchivo(Integer id);

	boolean existsByNombreArchivoAndIdArchivoNot(String nombreArchivo, Integer idArchivo);

	public Archivo findArchivoByIdAlfresco(String idAlfresco);

}

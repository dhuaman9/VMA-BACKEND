package pe.gob.sunass.vma.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pe.gob.sunass.vma.model.Archivo;
import pe.gob.sunass.vma.model.Empresa;


@Repository
public interface ArchivoRepository extends CrudRepository<Archivo, Integer> {
	
	//para evitar duplicidad de archivos en un registro VMA
	//public List<Archivo> findByNombreArchivo(RegistroVMA registroVMA, String nombreArchivo); //para evitar duplicidad de archivos en un registro VMA
	
	@Query("SELECT a FROM Archivo a WHERE a.nombreArchivo = :nombreArchivo AND a.registroVma.idRegistroVma= :idRegistroVMA")
	public List<Archivo> findByNombre(@Param("nombreArchivo") String nombreArchivo, @Param("idRegistroVMA") Integer idRegistroVMA);  //pendiente para validar duplicidad al actualizar o registrar

	public  Optional<Archivo>  findByIdArchivo(Integer id);

	boolean existsByNombreArchivoAndIdArchivoNot(String nombreArchivo, Integer idArchivo);

	Archivo findArchivoByIdAlfresco(String idAlfresco);
	
}

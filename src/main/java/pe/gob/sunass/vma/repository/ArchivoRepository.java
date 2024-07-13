package pe.gob.sunass.vma.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pe.gob.sunass.vma.model.Archivo;
import pe.gob.sunass.vma.model.Empresa;
import pe.gob.sunass.vma.model.RegistroVMA;

@Repository
public interface ArchivoRepository extends JpaRepository<Archivo, Integer> {
	
	//para evitar duplicidad de archivos en un registro VMA
	//public List<Archivo> findByNombreArchivo(RegistroVMA registroVMA, String nombreArchivo); //para evitar duplicidad de archivos en un registro VMA
	
	@Query("SELECT a FROM Archivo a WHERE a.nombreArchivo = :nombreArchivo AND a.registroVma.idRegistroVma= :idRegistroVMA")
	  public List<Archivo> findByNombre(@Param("nombreArchivo") String nombreArchivo, @Param("idRegistroVMA") Integer idRegistroVMA);  //pendiente para validar duplicidad al actualizar o registrar

}

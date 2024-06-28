package pe.gob.sunass.vma.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.gob.sunass.vma.model.Cuestionario;




@Repository
public interface CuestionarioRepository extends JpaRepository<Cuestionario, Integer> {
	
	/*@Query("SELECT  MAX(c.idCuestionario) FROM Cuestionario c ")
	public Integer getLastCuestionario();*/
	
	@Query("SELECT c FROM Cuestionario c WHERE c.idCuestionario = (SELECT MAX(subC.idCuestionario) FROM Cuestionario subC)  ")
	public Optional<Cuestionario> getLastCuestionario();
	

	
	
}

package pe.gob.sunass.vma.repository;

import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.gob.sunass.vma.model.cuestionario.Seccion;

@Repository
public interface SeccionRepository extends JpaRepository<Seccion, Integer> {

	public List<Seccion> findAllByOrderByIdSeccion();

	@Query("FROM Seccion s WHERE s.cuestionario.idCuestionario = ?1 ORDER BY s.orden")
	List<Seccion> findAllByCuestionarioId(Integer idCuestionario);

	public Optional<Seccion> findById(Integer id);


}

package pe.gob.sunass.vma.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pe.gob.sunass.vma.model.Empresa;
import pe.gob.sunass.vma.model.Seccion;


@Repository
public interface SeccionRepository extends JpaRepository<Seccion, Integer>{
	
	
	  public List<Seccion> findAllByOrderByIdSeccion();

	  @Query("FROM Seccion s WHERE s.cuestionario.idCuestionario = ?1 ORDER BY s.orden")
	  List<Seccion> findAllByCuestionarioId(Integer idCuestionario);

	  //public Page<Seccion> findAllByOrderByIdEmpresa(Pageable pageable);

	  public Optional<Seccion> findById(Integer id);

	 /* @Query("SELECT e FROM Empresa e WHERE e.nombre = :nombre")
	  public List<Seccion> findByEps(@Param("nombre") String nombre);*/
	

}

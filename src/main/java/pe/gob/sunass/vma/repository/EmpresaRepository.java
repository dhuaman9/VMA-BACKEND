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


@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {

	  public List<Empresa> findAllByOrderByIdEmpresa();

	  public Page<Empresa> findAllByOrderByIdEmpresa(Pageable pageable);

	  public Optional<Empresa> findByIdEmpresa(Integer id);

	  @Query("SELECT e FROM Empresa e WHERE e.nombre = :nombre")
	  public List<Empresa> findByEps(@Param("nombre") String nombre);

	  @Query("SELECT e FROM Empresa e WHERE e.nombre = :nombre")
	  public Optional<Empresa> findEmpresaByName(@Param("nombre") String nombre);

	  @Query("SELECT e FROM Empresa e WHERE " +
			  "LOWER(e.nombre) LIKE LOWER(CONCAT('%', :filter, '%')) OR " +
			  "LOWER(e.regimen) LIKE LOWER(CONCAT('%', :filter, '%')) OR " +
			  "LOWER(e.tipo) LIKE LOWER(CONCAT('%', :filter, '%'))")
			  Page<Empresa> findByFilter(@Param("filter") String filter, Pageable pageable);

//	  public List<ProyectoDomain> findByEmpresaAndIdNotAndEstado(String proyecto, Integer id, Boolean estado);

}

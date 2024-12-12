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

	//en los metodos, se excluye al campo estado, por mencion de DF.
	
	// Método que excluye a la empresa sunass
	@Query("SELECT e FROM Empresa e WHERE  e.idEmpresa <> 1  ORDER BY e.idEmpresa")
	public List<Empresa> findByExcludingSunassOrderByIdEmpresa();

	@Query("SELECT e  FROM Empresa e  WHERE e.nombre <> 'SUNASS'  order by idEmpresa")
	public Page<Empresa> findAllEmpresa(Pageable pageable);

	public Optional<Empresa> findByIdEmpresa(Integer id);

	@Query("SELECT e FROM Empresa e WHERE e.nombre = :nombre")
	public List<Empresa> findByEps(@Param("nombre") String nombre);

	@Query("SELECT e FROM Empresa e WHERE e.nombre = :nombre")
	public Optional<Empresa> findEmpresaByName(@Param("nombre") String nombre);

	@Query("SELECT e FROM Empresa e WHERE " + "LOWER(e.nombre) LIKE LOWER(CONCAT('%', :filter, '%')) OR "
			+ "LOWER(e.regimen) LIKE LOWER(CONCAT('%', :filter, '%')) OR "
			+ "LOWER(e.tipo) LIKE LOWER(CONCAT('%', :filter, '%')) " + "AND e.nombre <> 'SUNASS'  ")
	Page<Empresa> findByFilter(@Param("filter") String filter, Pageable pageable);

	@Query("SELECT e " + "FROM Empresa e " + "WHERE e.idEmpresa NOT IN ( " + "    SELECT r.empresa.idEmpresa "
			+ "    FROM RegistroVMA r " + ") " + "AND e.nombre <> 'SUNASS' ")
	public List<Empresa> findByRegistroVmaIsNull();

}

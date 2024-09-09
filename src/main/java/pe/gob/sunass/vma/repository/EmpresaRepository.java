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

	  @Query("SELECT e "
		  		+ "FROM Empresa e "
		  		+ "WHERE e.idEmpresa NOT IN ( "
		  		+ "    SELECT r.empresa.idEmpresa "
		  		+ "    FROM RegistroVMA r "
		  		+ ") "
		  		+ "AND e.nombre <> 'SUNASS' ")
	  public List<Empresa> findByRegistroVmaIsNull();
	  
	  //para listar empresas que no tienen registro en vma, de acuerdo a cada anio
	  @Query(value = "SELECT e.nombre, e.tipo, fr.anio , fr.fecha_inicio, fr.fecha_fin " +
				"FROM vma.empresa e " +
				"CROSS JOIN vma.ficha_registro fr " +
				"LEFT JOIN vma.registro_vma rv ON rv.id_empresa = e.id_empresa " +
				"AND rv.id_ficha_registro = fr.id_ficha_registro " +
				"WHERE rv.id_ficha_registro IS NULL AND e.nombre <> 'SUNASS' " +
				"ORDER BY e.id_empresa, fr.anio", nativeQuery = true)
		List<Object[]> findMissingFichaRegistros();
		

}

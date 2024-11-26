package pe.gob.sunass.vma.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pe.gob.sunass.vma.model.FichaRegistro;

@Repository
public interface FichaRepository extends JpaRepository<FichaRegistro, Integer>{
	
	  public List<FichaRegistro> findAllByOrderByIdFichaRegistroDesc();

	  //public Page<FichaRegistro> findAllByOrderByIdFichaRegistroDesc(Pageable pageable);//orden descendente de los IDs
	  
	  public Optional<FichaRegistro> findById(Integer id);
	  
	  @Query("SELECT f FROM FichaRegistro f WHERE f.fechaInicio = :fechaInicio OR f.fechaFin = :fechaFin")
	  public List<FichaRegistro> validarFechaInicioFin(@Param("fechaInicio") LocalDate fechaInicio,@Param("fechaFin") LocalDate fechaFin);

	 
	  @Query(value = "SELECT COUNT(*) FROM vma.ficha_registro f WHERE  f.anio = :anio AND EXTRACT(YEAR FROM CAST(:fechaInicio AS DATE)) = EXTRACT(YEAR FROM CURRENT_DATE)", nativeQuery = true)  //r.id_ficha_registro <> :idFichaRegistro and 
	  public int nroRegistroPorAnio(@Param("anio") String anio,@Param("fechaInicio") LocalDate fechaInicio  );  //devuelve el nro de Periodos de registro VMA , segun el anio
	  
	  @Query(value = "SELECT COUNT(*) FROM vma.ficha_registro f WHERE f.id_ficha_registro  <> :id_ficha_registro and  f.anio = :anio AND EXTRACT(YEAR FROM CAST(:fechaInicio AS DATE)) = EXTRACT(YEAR FROM CURRENT_DATE)", nativeQuery = true)
	  public int nroRegistroPorAnioUpdate(@Param("anio") String anio,@Param("fechaInicio") LocalDate fechaInicio, @Param("id_ficha_registro") Integer idFichaRegistro );  
	  
	  
	  @Query("SELECT MAX(f.fechaInicio) FROM FichaRegistro f")
	  LocalDate findMaxFechaInicio();  //para obtener fecha maxima
	  
	  @Query("SELECT COUNT(r) " +
	           "FROM FichaRegistro r " +
	           "WHERE :fechaInicio BETWEEN r.fechaInicio AND r.fechaFin " +
	           "   OR :fechaFin BETWEEN r.fechaInicio AND r.fechaFin " +
	           "   OR r.fechaInicio BETWEEN :fechaInicio AND :fechaFin " +
	           "   OR r.fechaFin BETWEEN :fechaInicio AND :fechaFin")
	  long validarFechas(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);//para el registro de nuevas fechas

	  @Query("SELECT COUNT(r) " +
	           "FROM FichaRegistro r " +
	           "WHERE  r.idFichaRegistro <> :idFichaRegistro AND ( :fechaInicio BETWEEN r.fechaInicio AND r.fechaFin " +
	           "   OR :fechaFin BETWEEN r.fechaInicio AND r.fechaFin " +
	           "   OR r.fechaInicio BETWEEN :fechaInicio AND :fechaFin " +
	           "   OR r.fechaFin BETWEEN :fechaInicio AND :fechaFin  )")
	  long validarFechasForUpdate(@Param("idFichaRegistro") Integer idFichaRegistro, @Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin); //se utiliza para actualizar fechas
	  
	  
	  @Query("SELECT r " +
		       "FROM FichaRegistro r " +
		       "WHERE :fecha BETWEEN r.fechaInicio AND r.fechaFin")
		Optional<FichaRegistro> validarRangoConFecha(@Param("fecha") LocalDate fecha);
	 
	  @Query("SELECT r FROM FichaRegistro r WHERE CURRENT_DATE >= r.fechaInicio AND CURRENT_DATE <= r.fechaFin")
	  public FichaRegistro findFichaRegistroActual();
	  
	  
	  @Query("SELECT CASE WHEN r.fechaFin >= CURRENT_DATE THEN (r.fechaFin - CURRENT_DATE) ELSE -1 END " +
		       "FROM FichaRegistro r " +
		       "WHERE CURRENT_DATE BETWEEN r.fechaInicio AND r.fechaFin")
	  public List<Integer>  findDiasRestantes();  //el resultado no puede   ser unico ,como no se usa el LIMIT 1 en JPQL,  se usa el List<>.
	  
	  @Query("SELECT r FROM FichaRegistro r WHERE CURRENT_DATE >= r.fechaInicio AND CURRENT_DATE <= r.fechaFin")
	  public Optional<FichaRegistro> findOptionalPeriodosActivos();  // obtiene el periodo actual segun la fecha actual
	  
	  @Query("SELECT r.anio FROM FichaRegistro r WHERE r.anio = :anio ")
	  public String findAnioFichaRegistro(@Param("anio") String anio);  // obtiene el anio de la fichaRegistro
	  
	  
}

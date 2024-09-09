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
	  public List<FichaRegistro> existsByFecha(@Param("fechaInicio") LocalDate fechaInicio,@Param("fechaFin") LocalDate fechaFin);

	  @Query("SELECT COUNT(f) FROM FichaRegistro f WHERE f.anio = :anio")
	  public int countByYear(@Param("anio") String anio ); 
	  
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
	  
	  
	  @Query("SELECT " +
		       "CASE " +
		       "WHEN CURRENT_DATE >= r.fechaInicio AND CURRENT_DATE <= r.fechaFin THEN " +
		       "CASE " +
		       "WHEN r.fechaFin >= CURRENT_DATE THEN FUNCTION('DATEDIFF', r.fechaFin, CURRENT_DATE) " +
		       "ELSE -1 END " +
		       "ELSE -1 " +
		       "END " +
		       "FROM FichaRegistro r")
	  public Integer findDaysRemaining();  //el resultado debe ser unico ,si sale mas de un resultado, es debido a que hay un cruce con otro periodo registrado
	  
	  
	  
	  
//	  @Query(value = "SELECT " +
//              "COALESCE((SELECT " +
//              "CASE " +
//              "WHEN r.fecha_fin >= CURRENT_DATE THEN " +
//              "  CASE " +
//              "  WHEN EXTRACT(DAY FROM AGE(r.fecha_fin, CURRENT_DATE)) >= 0 THEN " +
//              "    EXTRACT(DAY FROM AGE(r.fecha_fin, CURRENT_DATE)) " +
//              "  ELSE -1 END " +
//              "ELSE -1 END " +
//              "FROM vma.ficha_registro r " +
//              "WHERE CURRENT_DATE >= r.fecha_inicio " +
//              "AND CURRENT_DATE <= r.fecha_fin " +
//              "LIMIT 1), -1) AS dias_restantes", 
//      nativeQuery = true)
//	  public Integer findDaysRemaining();  // otra opcion, es usar este query nativo
	  
	  
}

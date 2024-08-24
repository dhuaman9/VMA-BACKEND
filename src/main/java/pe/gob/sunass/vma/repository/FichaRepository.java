package pe.gob.sunass.vma.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pe.gob.sunass.vma.model.FichaRegistro;

@Repository
public interface FichaRepository extends JpaRepository<FichaRegistro, Integer>{
	
	  public List<FichaRegistro> findAllByOrderByIdFichaRegistroDesc();

	  // public Page<FichaRegistro> findAll(Pageable pageable);
	  
	  //public Page<FichaRegistro> findAllByOrderByIdFichaRegistroDesc(Pageable pageable);//orden descendente de los IDs
	  
	  public Optional<FichaRegistro> findById(Integer id);
	  
	  @Query("SELECT f FROM FichaRegistro f WHERE f.fechaInicio = :fechaInicio OR f.fechaFin = :fechaFin")
	  public List<FichaRegistro> existsByFecha(@Param("fechaInicio") LocalDate fechaInicio,@Param("fechaFin") LocalDate fechaFin);

	  @Query("SELECT COUNT(f) FROM FichaRegistro f WHERE f.anio = :anio")
	  public int countByYear(@Param("anio") String anio );  // para que no registre mas de N cantidad de años en la tabla
	  
	  @Query("SELECT MAX(f.fechaInicio) FROM FichaRegistro f")
	  LocalDate findMaxFechaInicio();  //para obtener fecha max
	  
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
	  long validarFechasForUpdate(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin); //se utiliza para actualizar fechas
	  
	  
	  @Query("SELECT r " +
		       "FROM FichaRegistro r " +
		       "WHERE :fecha BETWEEN r.fechaInicio AND r.fechaFin")
		Optional<FichaRegistro> validarRangoConFecha(@Param("fecha") LocalDate fecha);
	 
	  @Query("SELECT r FROM FichaRegistro r WHERE CURRENT_DATE >= r.fechaInicio AND CURRENT_DATE <= r.fechaFin")
	  public FichaRegistro findFichaRegistroActual();
	  
}

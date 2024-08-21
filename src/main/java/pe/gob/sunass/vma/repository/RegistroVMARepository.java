package pe.gob.sunass.vma.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pe.gob.sunass.vma.model.Usuario;
import pe.gob.sunass.vma.model.cuestionario.RegistroVMA;

@Repository
public interface RegistroVMARepository  extends JpaRepository<RegistroVMA, Integer>{

	//si es usuario tipo SUNASS, deberia ver todos los registros VMA de todas las EPS . Se usara esto por mientras.

	public List<RegistroVMA> findAllByOrderByIdRegistroVma();
	
	//paginacion
	@Query("SELECT r FROM RegistroVMA r WHERE r.empresa.idEmpresa = :idEmpresa")
	public Page<RegistroVMA> registrosPorIdEmpresa(Integer idEmpresa, Pageable pageable);


	@Query("SELECT r FROM RegistroVMA r WHERE r.empresa.idEmpresa = :idEmpresa")
	public List<RegistroVMA> registrosPorIdEmpresa(Integer idEmpresa);

	@Query("SELECT COUNT(r) FROM RegistroVMA r WHERE r.empresa.tipo = :tipoEmpresa AND r.fichaRegistro.anio = :anio AND r.estado='COMPLETO'")
	long registrosCompletadosPorTipoEmpresa(String tipoEmpresa, String anio);
	
	@Query("SELECT COUNT(r) FROM RegistroVMA r WHERE  r.fichaRegistro.anio = :anio AND r.estado='COMPLETO'")
	long registrosCompletadosEmpresas( String anio);

	@Query("FROM RegistroVMA r WHERE r.empresa.idEmpresa = :idEmpresa AND r.fichaRegistro.anio = :anio")
	RegistroVMA findRegistroVmaPorAnhio(Integer idEmpresa, String anio);

	public Optional<RegistroVMA> findById(Integer id);
	
	@Query("FROM RegistroVMA r WHERE r.idRegistroVma = :id")
	public Optional<RegistroVMA> findByIdRegistroVma(@Param("id") Integer id);
	
	// ** query para obtener el listado de registros VMA, segun el usuario en sesion.
	/*@Query("SELECT r FROM RegistroVMA r WHERE r.username = ( SELECT u.idEmpresa FROM Usuario u WHERE u.username = :username)")
	public List<RegistroVMA> findAllByOrderByIdRegistroVmaAndUsername(@Param("username") String username);*/
	
	//query para obtener el listado de registros VMA, segun la EPS , a la que pertenece el usuario en sesion.
	@Query("SELECT r FROM RegistroVMA r WHERE r.empresa.idEmpresa = ( SELECT e.idEmpresa FROM Empresa e WHERE e.nombre = :nombre)")
	public List<RegistroVMA> findAllByOrderByIdRegistroVmaAndEPS(@Param("nombre") String nombre);


	@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
		       "FROM RegistroVMA e " +
		       "INNER JOIN e.fichaRegistro fr " +
			   "WHERE e.fichaRegistro.idFichaRegistro IN ( " +
			   "    SELECT fr.idFichaRegistro " +
			   "    FROM FichaRegistro fr " +
			   "    WHERE CURRENT_DATE >= fr.fechaInicio AND CURRENT_DATE <= fr.fechaFin " +
			   ") " +
		       "AND e.empresa.idEmpresa = :idEmpresa " +
		       "AND (e.createdAt BETWEEN fr.fechaInicio AND fr.fechaFin) ")
	boolean isRegistroCompletado(@Param("idEmpresa") Integer idEmpresa);  //para deshabilitar o habilitar el boton de Registrar para VMA

	
	@Query("SELECT r FROM RegistroVMA r WHERE r.estado= 'COMPLETO' AND r.fichaRegistro.anio = :anio")
	public List<RegistroVMA> findRegistrosCompletos(@Param("anio") String anio);
	

	@Query("SELECT r FROM RegistroVMA r WHERE r.fichaRegistro.anio = :anio")
	List<RegistroVMA> findRegistros(@Param("anio") String anio);
	
	@Query("FROM RegistroVMA r WHERE r.idRegistroVma in :ids")
	List<RegistroVMA> findRegistrosVmasPorIds(List<Integer> ids);
	
}

package pe.gob.sunass.vma.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pe.gob.sunass.vma.model.RegistroVMA;

@Repository
public interface RegistroVMARepository  extends JpaRepository<RegistroVMA, Integer>{

	//si es usuario tipo SUNASS, deberia ver todos los registros VMA de todas las EPS . Se usara esto por mientras.

	public List<RegistroVMA> findAllByOrderByIdRegistroVma();

	@Query("SELECT r FROM RegistroVMA r WHERE r.empresa.idEmpresa = :idEmpresa")
	public List<RegistroVMA> registrosPorIdEmpresa(Integer idEmpresa);

	
	public Optional<RegistroVMA> findById(Integer id);
	
	// ** query para obtener el listado de registros VMA, segun el usuario en sesion.
	/*@Query("SELECT r FROM RegistroVMA r WHERE r.username = ( SELECT u.idEmpresa FROM Usuario u WHERE u.username = :username)")
	public List<RegistroVMA> findAllByOrderByIdRegistroVmaAndUsername(@Param("username") String username);*/
	
	//query para obtener el listado de registros VMA, segun la EPS , a la que pertenece el usuario en sesion.
	@Query("SELECT r FROM RegistroVMA r WHERE r.empresa.idEmpresa = ( SELECT e.idEmpresa FROM Empresa e WHERE e.nombre = :nombre)")
	public List<RegistroVMA> findAllByOrderByIdRegistroVmaAndEPS(@Param("nombre") String nombre);


	@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
			"FROM RegistroVMA e " +
			"WHERE e.empresa.idEmpresa = ?1")
    boolean isRegistroCompletado(Integer idEmpresa);
}

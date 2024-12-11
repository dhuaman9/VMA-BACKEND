package pe.gob.sunass.vma.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pe.gob.sunass.vma.model.cuestionario.RegistroVMA;

@Repository
public interface RegistroVMARepository extends JpaRepository<RegistroVMA, Integer> {

	public List<RegistroVMA> findAllByOrderByIdRegistroVma();

	public Optional<RegistroVMA> findById(Integer id);

	// paginacion
	@Query("SELECT r FROM RegistroVMA r WHERE r.empresa.idEmpresa = :idEmpresa")
	public Page<RegistroVMA> registrosPorIdEmpresa(Integer idEmpresa, Pageable pageable);

	@Query("SELECT r FROM RegistroVMA r WHERE r.empresa.idEmpresa = :idEmpresa")
	public List<RegistroVMA> registrosPorIdEmpresa(Integer idEmpresa);

	@Query("SELECT COUNT(r) FROM RegistroVMA r WHERE r.empresa.tipo = :tipoEmpresa AND r.fichaRegistro.anio = :anio AND r.estado='COMPLETO'")
	long registrosCompletadosPorTipoEmpresa(String tipoEmpresa, String anio);

	@Query("SELECT COUNT(r) FROM RegistroVMA r WHERE  r.fichaRegistro.anio = :anio AND r.estado='COMPLETO'")
	long registrosCompletadosEmpresas(String anio);

	@Query("FROM RegistroVMA r WHERE r.empresa.idEmpresa = :idEmpresa AND r.fichaRegistro.anio = :anio")
	RegistroVMA findRegistroVmaPorAnhio(Integer idEmpresa, String anio);

	@Query("FROM RegistroVMA r WHERE r.idRegistroVma = :id")
	public Optional<RegistroVMA> findByIdRegistroVma(@Param("id") Integer id);

	@Query("FROM RegistroVMA r WHERE r.empresa.idEmpresa = :idEmpresa AND r.idRegistroVma = :idRegistroVma")
	public Optional<RegistroVMA> findByIdItem(Integer idRegistroVma, Integer idEmpresa); // es un metodo similar al
																							// anterior, para tener los
																							// registros segun el
																							// idEmpresa, del usuario en
																							// sesion.

	// query para obtener el listado de registros VMA, segun la EPS , a la que
	// pertenece el usuario en sesion.
	@Query("SELECT r FROM RegistroVMA r WHERE r.empresa.idEmpresa = ( SELECT e.idEmpresa FROM Empresa e WHERE e.nombre = :nombre)")
	public List<RegistroVMA> findAllByOrderByIdRegistroVmaAndEPS(@Param("nombre") String nombre);

	@Query("SELECT r FROM RegistroVMA r WHERE r.estado= 'COMPLETO' AND r.fichaRegistro.anio = :anio")
	public List<RegistroVMA> findRegistrosCompletos(@Param("anio") String anio);

	@Query("SELECT r FROM RegistroVMA r WHERE r.fichaRegistro.anio = :anio")
	List<RegistroVMA> findRegistros(@Param("anio") String anio);

	@Query("SELECT r FROM RegistroVMA r "
			+ "WHERE r.empresa.idEmpresa = (SELECT u.empresa.idEmpresa FROM Usuario u WHERE u.id = :id) "
			+ "AND r.estado = 'COMPLETO' " + " AND r.fichaRegistro.idFichaRegistro IN ("
			+ "SELECT f.idFichaRegistro FROM FichaRegistro f "
			+ "WHERE CURRENT_DATE >= f.fechaInicio AND CURRENT_DATE <= f.fechaFin)")
	public Optional<RegistroVMA> findEmpresasCompletaronRegistro(@Param("id") Integer userId);

	@Query("FROM RegistroVMA r WHERE r.idRegistroVma in :ids order by r.idRegistroVma desc")
	List<RegistroVMA> findRegistrosVmasPorIds(List<Integer> ids); // se usa cuando se descarga excel, segun los ids
																	// seleccionados en la tabla.

	@Query(value = "SELECT CASE " + "   WHEN EXISTS (" + "       SELECT 1 " + "       FROM vma.empresa e "
			+ "       WHERE e.id_empresa = :idEmpresa " + "         AND e.estado = false" + "   ) THEN true "
			+ "   WHEN NOT EXISTS (" + "       SELECT 1 " + "       FROM vma.ficha_registro fr "
			+ "       WHERE CURRENT_DATE >= fr.fecha_inicio " + "         AND CURRENT_DATE <= fr.fecha_fin" + "   ) "
			+ "   OR EXISTS (" + "       SELECT 1 " + "       FROM vma.registro_vma rv "
			+ "       INNER JOIN vma.ficha_registro fr " + "           ON rv.id_ficha_registro = fr.id_ficha_registro "
			+ "       WHERE rv.id_empresa = :idEmpresa "
			+ "         AND rv.fecha_creacion BETWEEN fr.fecha_inicio AND fr.fecha_fin" + "   ) THEN true "
			+ "   ELSE false " + "END AS resultado", nativeQuery = true)
	public boolean isRegistroCompletado(@Param("idEmpresa") Integer idEmpresa); // para deshabilitar o habilitar el
																				// boton de Registrar para VMA

}

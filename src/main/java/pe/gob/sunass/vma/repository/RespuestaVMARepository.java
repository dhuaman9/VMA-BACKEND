package pe.gob.sunass.vma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.gob.sunass.vma.model.RespuestaVMA;

import java.util.List;

@Repository
public interface RespuestaVMARepository extends JpaRepository<RespuestaVMA, Integer> {
	
	@Query("FROM RespuestaVMA r WHERE r.registroVMA.idRegistroVma = :idRegistroVMA")
	List<RespuestaVMA> findByRegistroVMAId(Integer idRegistroVMA);

	@Query("FROM RespuestaVMA r WHERE r.idPregunta = ?1")
	List<RespuestaVMA> findRespuestasByIdPregunta(Integer preguntaId);

	@Query("FROM RespuestaVMA r WHERE r.idPregunta = :preguntaId and r.registroVMA.idRegistroVma = :registroVmaId")
	RespuestaVMA findRespuestasByIdPreguntaAndRegistroVma(Integer preguntaId, Integer registroVmaId);

//	@Query("FROM RespuestaVMA r WHERE r.idPregunta = :preguntaId and r.registroVMA.empresa.tipo = :tipoEmpresa AND r.registroVMA.fichaRegistro.anio = :anio")
//	List<RespuestaVMA> findRespuestasByIdPreguntaAndTipoEmpresa(Integer preguntaId, String tipoEmpresa, String anio);
	
	@Query("SELECT r FROM RespuestaVMA r WHERE r.idPregunta = :preguntaId AND r.registroVMA.empresa.tipo = :tipoEmpresa AND r.registroVMA.fichaRegistro.anio = :anio")
	List<RespuestaVMA> findRespuestasByIdPreguntaAndTipoEmpresa(@Param("preguntaId") Integer preguntaId, @Param("tipoEmpresa") String tipoEmpresa, @Param("anio") String anio);


	@Query("FROM RespuestaVMA r WHERE r.idPregunta = :preguntaId and r.registroVMA.idRegistroVma = :registroId")
	RespuestaVMA findRespuestaByPreguntaIdAndRegistro(Integer preguntaId, Integer registroId);

	@Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
			"FROM RespuestaVMA r WHERE r.idPregunta = :idPregunta AND r.registroVMA.idRegistroVma = :idRegistroVMA")
	boolean isRespuestaArchivoInformacionCompleto(Integer idPregunta, Integer idRegistroVMA);

	@Query(value="SELECT SUM(CAST(r.respuesta AS INTEGER)) FROM vma.respuesta_vma r WHERE r.id_pregunta = :preguntaId AND r.id_registro_vma IN :registroVMAIds", nativeQuery = true)
	Integer getSumaTrabajadoresDesdicadosRegistroVMA(@Param("registroVMAIds") List<Integer> registroVMAIds, @Param("preguntaId") Integer preguntaId);

	
	@Query(value = "SELECT SUM(CAST(r.respuesta AS INTEGER)) FROM vma.respuesta_vma r WHERE r.id_alternativa = :alternativaId AND r.id_registro_vma IN :registroVMAIds", nativeQuery = true)
	Integer getSumaTotalRespuestaAlternativaPorRegistros(List<Integer> registroVMAIds, Integer alternativaId);
	
	@Query(value="SELECT SUM(CAST(r.respuesta AS INTEGER)) FROM vma.respuesta_vma r WHERE r.id_pregunta = :preguntaId AND r.id_registro_vma IN :registroVMAIds", nativeQuery = true)
	Integer getSumatotalUNDInscritosRegistroVMA(@Param("registroVMAIds") List<Integer> registroVMAIds, @Param("preguntaId") Integer preguntaId);

	
	//generico
	@Query(value="SELECT SUM(CAST(r.respuesta AS INTEGER)) FROM vma.respuesta_vma r WHERE r.id_pregunta = :preguntaId AND r.id_registro_vma IN :registroVMAIds", nativeQuery = true)
	Integer getSumatotalRespuestaPorRegistros(@Param("registroVMAIds") List<Integer> registroVMAIds, @Param("preguntaId") Integer preguntaId);
	
}

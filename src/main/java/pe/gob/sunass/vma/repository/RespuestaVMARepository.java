package pe.gob.sunass.vma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pe.gob.sunass.vma.model.cuestionario.RespuestaVMA;

import java.math.BigDecimal;
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
	
	
	@Query("FROM RespuestaVMA r WHERE r.idPregunta = :preguntaId and r.registroVMA.estado = 'COMPLETO' and r.registroVMA.empresa.tipo = :tipoEmpresa AND r.registroVMA.fichaRegistro.anio = :anio")
	List<RespuestaVMA> findRespuestasByIdPreguntaAndTipoEmpresa(Integer preguntaId, String tipoEmpresa, String anio);

	@Query("FROM RespuestaVMA r WHERE r.idPregunta = :preguntaId and r.registroVMA.idRegistroVma = :registroId")
	RespuestaVMA findRespuestaByPreguntaIdAndRegistro(Integer preguntaId, Integer registroId);
	
	//para anexos
	
	//@Query(value = "SELECT SUM(CAST(r.respuesta AS INTEGER)) FROM vma.respuesta_vma r WHERE r.id_alternativa = :alternativaId AND r.id_registro_vma IN :registroVMAIds", nativeQuery = true)
	@Query("FROM RespuestaVMA r WHERE r.idAlternativa = :alternativaId and r.registroVMA.idRegistroVma = :registroId")
	RespuestaVMA findRespuestaAlternativaPorRegistros(Integer alternativaId, Integer registroId);

	@Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
			"FROM RespuestaVMA r WHERE r.idPregunta = :idPregunta AND r.registroVMA.idRegistroVma = :idRegistroVMA")
	boolean isRespuestaArchivoInformacionCompleto(Integer idPregunta, Integer idRegistroVMA);

	// @Query("SELECT SUM(CAST(r.respuesta AS integer)) FROM RespuestaVMA r WHERE r.idPregunta = :preguntaId AND r.idRegistroVma IN :registroVMAIds")
	@Query(value="SELECT COALESCE(SUM(CAST(r.respuesta AS INTEGER)), 0) FROM vma.respuesta_vma r WHERE r.id_pregunta = :preguntaId AND r.id_registro_vma IN :registroVMAIds", nativeQuery = true)
	Integer getSumaTrabajadoresDesdicadosRegistroVMA(@Param("registroVMAIds") List<Integer> registroVMAIds, @Param("preguntaId") Integer preguntaId);

	
	@Query(value = "SELECT COALESCE(SUM(CAST(r.respuesta AS INTEGER)), 0) FROM vma.respuesta_vma r WHERE r.id_alternativa = :alternativaId AND r.id_registro_vma IN :registroVMAIds", nativeQuery = true)
	Integer getSumaTotalRespuestaAlternativaPorRegistros(List<Integer> registroVMAIds, Integer alternativaId);
	
	@Query(value="SELECT COALESCE(SUM(CAST(r.respuesta AS INTEGER)), 0) FROM vma.respuesta_vma r WHERE r.id_pregunta = :preguntaId AND r.id_registro_vma IN :registroVMAIds", nativeQuery = true)
	Integer getSumatotalUNDInscritosRegistroVMA(@Param("registroVMAIds") List<Integer> registroVMAIds, @Param("preguntaId") Integer preguntaId);

	
	//generico
	@Query(value="SELECT COALESCE(SUM(CAST(r.respuesta AS INTEGER)), 0) FROM vma.respuesta_vma r WHERE r.id_pregunta = :preguntaId AND r.id_registro_vma IN :registroVMAIds", nativeQuery = true)
	Integer getSumatotalRespuestaPorRegistros(@Param("registroVMAIds") List<Integer> registroVMAIds, @Param("preguntaId") Integer preguntaId);
	
	@Query(value = "SELECT COALESCE(SUM(CAST(r.respuesta AS INTEGER)), 0) FROM vma.respuesta_vma r WHERE r.id_pregunta = :preguntaId AND r.id_registro_vma IN :registroVmaIds", nativeQuery = true)
	BigDecimal getSumaCostoTotalAnualIncurridoVmasCompleto(List<Integer> registroVmaIds, Integer preguntaId);

	@Query("SELECT r.respuesta FROM RespuestaVMA r WHERE r.registroVMA.idRegistroVma = :registroVmaId AND r.idPregunta = :preguntaId")
	BigDecimal getCostoAnualIncurridoPorRegistro(Integer registroVmaId, Integer preguntaId);
	
}

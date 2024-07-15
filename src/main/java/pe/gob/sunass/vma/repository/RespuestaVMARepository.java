package pe.gob.sunass.vma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.gob.sunass.vma.model.RespuestaVMA;

import java.util.List;

@Repository
public interface RespuestaVMARepository extends JpaRepository<RespuestaVMA, Integer> {
	
	@Query("FROM RespuestaVMA r WHERE r.registroVMA.idRegistroVma = ?1")
	List<RespuestaVMA> findByRegistroVMAId(Integer idRegistroVMA);

	@Query("FROM RespuestaVMA r WHERE r.idPregunta = ?1")
	List<RespuestaVMA> findRespuestasByIdPregunta(Integer preguntaId);

	@Query("FROM RespuestaVMA r WHERE r.idPregunta = ?1 and r.registroVMA.idRegistroVma = ?2")
	RespuestaVMA findRespuestasByIdPreguntaAndRegistroVma(Integer preguntaId, Integer registroVmaId);

	@Query("FROM RespuestaVMA r WHERE r.idPregunta = ?1 and r.registroVMA.empresa.tipo = ?2 AND YEAR(r.registroVMA.createdAt) = ?3")
	List<RespuestaVMA> findRespuestasByIdPreguntaAndTipoEmpresa(Integer preguntaId, String tipoEmpresa, int anio);
}

package pe.gob.sunass.vma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.gob.sunass.vma.model.RespuestaVMA;

import java.util.List;

@Repository
public interface RespuestaVMARepository extends JpaRepository<RespuestaVMA, Integer> {
	
	@Query("FROM RespuestaVMA r WHERE r.registroVMA = ?1")
	List<RespuestaVMA> findByRegistroVMAId(Integer idRegistroVMA);
}

package pe.gob.sunass.vma.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.sunass.vma.model.TipoEmpresa;

public interface TipoEmpresaRepository extends JpaRepository<TipoEmpresa, Integer> {
	
	@Query("SELECT t FROM TipoEmpresa t WHERE t.nombre <> 'NINGUNO'  ORDER BY t.idTipoEmpresa")
	public List<TipoEmpresa> findAll();
	
}
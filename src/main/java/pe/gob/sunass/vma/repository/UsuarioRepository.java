package pe.gob.sunass.vma.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.gob.sunass.vma.model.Usuario;
import pe.gob.sunass.vma.model.UsuarioLdap;

@Repository
public interface UsuarioRepository  extends JpaRepository<Usuario, Integer> {

	
	  public List<Usuario> findAllByOrderById();

	  public Page<Usuario> findAllByOrderById(Pageable pageable);

	  public Optional<Usuario> findById(Integer id);

	  public List<Usuario> findByUserNameAndEstado(String userName, Boolean estado);

	  public List<Usuario> findByUserNameAndIdNotAndEstado(String userName, Integer id, Boolean estado);
	  
	  public Usuario findByCorreo(String name);

	  public Optional<Usuario> findByUserName(String username);
	  
	
}

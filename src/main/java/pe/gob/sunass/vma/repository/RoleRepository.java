package pe.gob.sunass.vma.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.gob.sunass.vma.model.Role;

@Repository
public interface RoleRepository  extends JpaRepository<Role, Integer> {
	  public List<Role> findAll();

	  public Page<Role> findAll(Pageable pageable);

	  public Optional<Role> findById(Integer id);
	}


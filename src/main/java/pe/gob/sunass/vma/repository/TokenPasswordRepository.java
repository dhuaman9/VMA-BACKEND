package pe.gob.sunass.vma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.gob.sunass.vma.model.TokenPassword;

import java.util.Optional;

public interface TokenPasswordRepository extends JpaRepository<TokenPassword, Integer> {

    @Query("FROM TokenPassword t WHERE t.token = :token")
    Optional<TokenPassword> findByToken(String token);
}

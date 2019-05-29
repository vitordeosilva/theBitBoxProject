package server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

	@Query("SELECT t FROM Usuario t where t.nome = :nome")
    public Optional<Usuario> findByName(@Param("nome") String nome);
	
	@Query("ALTER TABLE usuario ALTER COLUMN pin SET DATA TYPE text")
	public Optional<Usuario> debug();
}
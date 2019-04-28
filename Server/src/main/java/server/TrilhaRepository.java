package server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface TrilhaRepository extends JpaRepository<Trilha, Long>{
	
	@Query(value = "SELECT t.posicao FROM Trilha t WHERE t.maquinaID = :mID AND t.produtoID = :pID AND t.qtde_produtos > 0", nativeQuery = true)
    public Optional<Integer> getPosition(@Param("mID") Long maquinaID, @Param("pID") Long produtoID);

}


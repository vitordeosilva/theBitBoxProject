package server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface TrilhaRepository extends JpaRepository<Trilha, Long>{
	
	@Query(value = "SELECT * FROM Trilha t WHERE t.maquinaID = :mID AND t.produtoID = :pID AND t.qtde_produtos > 0", nativeQuery = true)
    public Optional<Trilha> getTrilha(@Param("mID") Long maquinaID, @Param("pID") Long produtoID);
	
	@Query(value = "SELECT * FROM Trilha t WHERE t.maquinaID = :mID AND t.produtoID = :pID AND t.posicaoLinha = :linha AND t.posicaoColuna = :coluna AND t.qtde_produtos > 0", nativeQuery = true)
	public Optional<Trilha> getTrilhaFromPos(@Param("mID") Long maquinaID, @Param("pID") Long produtoID, @Param("linha") int linha, @Param("coluna") int coluna);

}


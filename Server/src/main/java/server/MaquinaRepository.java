package server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface MaquinaRepository extends JpaRepository<Maquina, Long>{

	@Query("SELECT new server.MaquinaItem(t.maquinaID, t.produtoID, p.nome, p.precoUnitario) from Produto p join Trilha t on t.produtoID = p.id where t.maquinaID = :mID and t.qtdeProdutos > 0")
    public List<MaquinaItem> getAvailableItems(@Param("mID") Long id);
}


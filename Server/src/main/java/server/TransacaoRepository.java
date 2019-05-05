package server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long>{

	@Query("SELECT t FROM Transacao t where t.maquinaID = :maquinaID AND t.estado > 2 AND t.estado < 5")
    public List<Transacao> findTransactionsWaitingToDispense(@Param("maquinaID") Long id);
	
	@Query("SELECT t FROM Transacao t where t.maquinaID = :maquinaID AND t.estado = 4")
    public List<Transacao> findTransactionsWaitingToConfirmDispensed(@Param("maquinaID") Long id);
	
	@Query("SELECT t FROM Transacao t where t.usuarioID = :usuarioID AND t.estado != 5")
	public List<Transacao> findUnfinishedTransactionsFromUID(@Param("usuarioID") Long id);
	
	@Query("SELECT t FROM Transacao t where t.maquinaID = :maquinaID AND t.estado != 5")
	public List<Transacao> findUnfinishedTransactionsFromMID(@Param("maquinaID") Long id);

}


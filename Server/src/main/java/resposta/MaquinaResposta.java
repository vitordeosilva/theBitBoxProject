package resposta;

import java.util.ArrayList;
import java.util.List;

public class MaquinaResposta extends Resposta {
	
	private Long id_maquina;
	private	List itens_disponiveis;

	public MaquinaResposta (String mensagem, int erro, Long id_maquina, List itens_disponiveis) {
		super(mensagem, erro);
		this.id_maquina = id_maquina;
		this.itens_disponiveis = itens_disponiveis;
	}

	public Long getIdMaquina(){
		return id_maquina;
	}
	public void setIdMaquina(){
		this.id_maquina=id_maquina;
	}
	public List getItensDisponiveis(){
		return itens_disponiveis;
	}
	public void setItensDisponiveis(){
		this.itens_disponiveis=itens_disponiveis;
	}
}

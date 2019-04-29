package resposta;

import java.util.ArrayList;
import java.util.List;

public class LoginResposta extends Resposta {
	
	Long id_usuario;
	float saldo;
	float saldo_pendente;
	List transacoes_pendentes;
	
	public LoginResposta (String mensagem, int erro, Long id_usuario, float saldo, float saldo_pendente, List transacoes_pendentes) {
		super(mensagem, erro);
		this.id_usuario = id_usuario;
		this.saldo = saldo;
		this.saldo_pendente = saldo_pendente;
		this.transacoes_pendentes = transacoes_pendentes;
	}
}
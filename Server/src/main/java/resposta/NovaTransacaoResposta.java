package resposta;

public class NovaTransacaoResposta extends Resposta {
	private Long id_transacao;
	
	public NovaTransacaoResposta(String mensagem, int erro, Long id_transacao) {
		super(mensagem, erro);
		this.id_transacao = id_transacao;
	}
	
	public Long getIdTransacao() {
		return id_transacao;
	}
}
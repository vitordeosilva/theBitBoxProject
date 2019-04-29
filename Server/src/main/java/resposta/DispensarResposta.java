package resposta;

public class DispensarResposta extends Resposta {
	
	int posicao_triha;
	
	public DispensarResposta (String mensagem, int erro, int posicao_triha) {
		super(mensagem, erro);
		this.posicao_triha = posicao_triha;
	}
}
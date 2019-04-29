package resposta;

public class Resposta {
	
	private int erro;
	private String mensagem;
	
	Resposta (String mensagem, int erro) {
		this.erro = erro;
		this.mensagem = mensagem;
	}
}
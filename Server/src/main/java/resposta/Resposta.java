package resposta;

public class Resposta {
	
	private int erro;
	private String mensagem;
	
	public Resposta (String mensagem, int erro) {
		this.erro = erro;
		this.mensagem = mensagem;
	}
	
	public int getErro() {
		return erro;
	}
	
	public String getMensagem() {
		return mensagem;
	}
}
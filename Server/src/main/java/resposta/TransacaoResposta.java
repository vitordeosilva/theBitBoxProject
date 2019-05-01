package resposta;

import server.Transacao;

public class TransacaoResposta extends Resposta {
	private Long id;
	private int estado;
    private Long usuarioID;
    private Long maquinaID;
    private Long produtoID;
	
	public TransacaoResposta(String mensagem, int erro, Transacao transacao) {
		super(mensagem, erro);
		this.id = transacao.getId();
		this.estado = transacao.getEstado();
		this.usuarioID = transacao.getUsuarioID();
		this.maquinaID = transacao.getMaquinaID();
		this.produtoID = transacao.getProdutoID();
	}
	
	public Long getId() {
		return id;
	}
	
	public int getEstado() {
		return estado;
	}
	
	public Long getUsuarioID() {
		return usuarioID;
	}
	
	public Long getMaquinaID() {
		return maquinaID;
	}
	
	public Long getProdutoID() {
		return produtoID;
	}
}
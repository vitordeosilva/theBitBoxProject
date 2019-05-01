package resposta;

import server.Transacao;

public class TransacaoResposta extends Resposta {
	private Long id;
	private int estado;
    private Long usuarioID;
    private Long maquinaID;
    private Long produtoID;
	
	public NovaTransacaoResposta(String mensagem, int erro, Transacao transacao) {
		super(mensagem, erro);
		this.id = transacao.getId();
		this.estado = transacao.getEstado();
		this.usuarioID = transacao.getUsuarioId();
		this.maquinaID = transacao.getMaquinaId();
		this.produtoID = transacao.getProdutoId();
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
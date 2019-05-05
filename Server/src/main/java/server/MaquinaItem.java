package server;

public class MaquinaItem {

	private Long maquina_id;
	private Long produto_id;
	private String produto_nome;
	private float produto_preco;
	private boolean produto_disponivel;
	private String produto_imagem;

	public MaquinaItem(Long maquina_id, Long produto_id, String produto_nome, float produto_preco, String produto_imagem, boolean produto_disponivel){
		this.maquina_id = maquina_id;
		this.produto_id = produto_id;
		this.produto_nome = produto_nome;
		this.produto_preco = produto_preco;
		this.produto_disponivel = produto_disponivel;
		this.produto_imagem = produto_imagem;
	}

	public Long getMaquinaId() {
		return maquina_id;
	}
	public void setMaquinaId(Long maquina_id) {
		this.maquina_id = maquina_id;
	}

	public Long getProdutoId() {
		return produto_id;
	}
	public void setProdutoId(Long produto_id) {
		this.produto_id = produto_id;
	}

	public String getProdutoNome() {
		return produto_nome;
	}
	public void setProdutoNome(String produto_nome) {
		this.produto_nome = produto_nome;
	}

	public float getProdutoPreco() {
		return produto_preco;
	}
	public void setProdutoPreco(float produto_preco) {
		this.produto_preco = produto_preco;
	}

	public String getProdutoImagem() {
		return produto_imagem;
	}
	public void setProdutoImagem(String produto_imagem) {
		this.produto_imagem = produto_imagem;
	}

	public boolean getProdutoDisponivel() {
		return produto_disponivel;
	}
	public void setProdutoDisponivel(boolean produto_disponivel) {
		this.produto_disponivel = produto_disponivel;
	}

}

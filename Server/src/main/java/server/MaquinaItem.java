package server;

public class MaquinaItem {

	private Long maquina_id;
	private Long produto_id;
	private String produto_nome;
	private float produto_preco;
	private boolean produto_disponivel;

	public MaquinaItem(Long maquina_id, Long produto_id, String produto_nome, float produto_preco, boolean produto_disponivel){
		this.maquina_id = maquina_id;
		this.produto_id = produto_id;
		this.produto_nome = produto_nome;
		this.produto_preco = produto_preco;
		this.produto_disponivel = produto_disponivel;
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

	public boolean getProdutoDisponivel() {
		return produto_disponivel;
	}
	public void setProdutoDisponivel(boolean produto_disponivel) {
		this.produto_disponivel = produto_disponivel;
	}

}

package server;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.CascadeType;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
public class Produto {
	@Id
	private Long id;
	private String nome;
	private float precoUnitario;
	private String imagemURL;

    @OneToMany(mappedBy="produtoID", cascade = CascadeType.ALL)
    private Set<Transacao> transacoes = new HashSet();

    @OneToMany(mappedBy="produtoID", cascade = CascadeType.ALL)
    private Set<Trilha> trilhas = new HashSet();

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

	public float getPrecoUnitario() {
		return precoUnitario;
	}
	public void setPrecoUnitario(float precoUnitario) {
		this.precoUnitario = precoUnitario;
	}

	public String getImagemURL() {
		return imagemURL;
	}
	public void setImagemURL(String imagemURL) {
		this.imagemURL = imagemURL;
	}

	public Set getTransacoes(){
		return transacoes;
	}
	public void setTransacoes(Set transacoes){
		this.transacoes = transacoes;
	}

	public Set getTrilhas(){
		return trilhas;
	}
	public void setTrilhas(Set trilhas){
		this.trilhas = trilhas;
	}

}

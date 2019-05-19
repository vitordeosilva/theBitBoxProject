package server;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
public class Usuario {
	@Id
	private Long id;
	private String nome;
	private String senha;
	private String idCarteira;
	private int pin;

    @OneToMany(mappedBy="usuarioID", cascade = CascadeType.ALL)
    private Set<Transacao> transacoes = new HashSet();

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

	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getIdCarteira() {
		return idCarteira;
	}
	public void setIdCarteira(String idCarteira) {
		this.idCarteira = idCarteira;
	}

	public Set getTransacoes(){
		return transacoes;
	}
	public void setTransacoes(Set transacoes){
		this.transacoes = transacoes;
	}
	
	public int getPin(){
		return pin;
	}
	
	public void setPin(){
		this.pin = pin;
	}
}

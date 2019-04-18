package server;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
public class Maquina {
	@Id
	@GeneratedValue
	private Long id;
	private String idCarteira;

    @OneToMany(mappedBy="maquinaID", cascade = CascadeType.ALL)
    private Set<Transacao> transacoes = new HashSet();

    @OneToMany(mappedBy="maquinaID", cascade = CascadeType.ALL)
    private Set<Trilha> trilhas = new HashSet();

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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

	public Set getTrilhas(){
		return trilhas;
	}
	public void setTrilhas(Set trilhas){
		this.trilhas = trilhas;
	}

}

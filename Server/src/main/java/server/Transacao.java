package server;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
public class Transacao {
	@Id
	@GeneratedValue(strategy="increment")
	private Long id;
	private int estado;
    private Long usuarioID;
    private Long maquinaID;
    private Long produtoID;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Long getUsuarioID(){
		return usuarioID;
	}
	public void setUsuarioID(Long usuarioID){
		this.usuarioID = usuarioID;
	}

	public Long getMaquinaID(){
		return maquinaID;
	}
	public void setMaquinaID(Long maquinaID){
		this.maquinaID = maquinaID;
	}

	public Long getProdutoID(){
		return produtoID;
	}
	public void setProdutoID(Long produtoID){
		this.produtoID = produtoID;
	}

	public int getEstado(){
		return estado;
	}
	public void setEstado(int estado){
		this.estado = estado;
	}
}

package server;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.ID;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;

@Entity
public class Transacao {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	private int estado;
    private Long usuarioID;
    private Long maquinaID;
    private Long produtoID;

	public Long getID() {
		return id;
	}
	public void setID(Long id) {
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

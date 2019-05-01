package server;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
    private Long usuarioId;
    private Long maquinaId;
    private Long produtoId;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Long getUsuarioId(){
		return usuarioId;
	}
	public void setUsuarioId(Long usuarioId){
		this.usuarioId = usuarioId;
	}

	public Long getMaquinaId(){
		return maquinaId;
	}
	public void setMaquinaId(Long maquinaId){
		this.maquinaId = maquinaId;
	}

	public Long getProdutoId(){
		return produtoId;
	}
	public void setProdutoId(Long produtoId){
		this.produtoId = produtoId;
	}

	public int getEstado(){
		return estado;
	}
	public void setEstado(int estado){
		this.estado = estado;
	}
}

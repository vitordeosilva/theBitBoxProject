package server;

import javax.persistence.*;

@Entity
public class Comentario {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Long usuarioID;
	private String texto;

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
	
	public String getTexto(){
		return texto;
	}
	public void setTexto(String texto){
		this.texto = texto;
	}
}

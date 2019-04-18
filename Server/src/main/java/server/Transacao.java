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
	@GeneratedValue
	private Long id;

    private Long usuarioID;

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
}

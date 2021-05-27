package mx.uam.ayd.proyecto.negocio.modelo;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class RefreshToken {
	
	@Id
	@GeneratedValue
	private UUID id;
	
	private Long issuedAt;
	
	private Long expireAt;	
	
	@ManyToOne
	@JoinColumn(name = "usuario")
	private Usuario usuario;

}

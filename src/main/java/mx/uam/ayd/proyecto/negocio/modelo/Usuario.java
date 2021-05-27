package mx.uam.ayd.proyecto.negocio.modelo;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad de negocio Usuario
 * 
 * @author humbertocervantes
 *
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {
	@Id
	@GeneratedValue
	private UUID id;

	private String nombre;
	
	private String apellido;
	
	private int edad;

	@ManyToOne
	private Grupo grupo;
	
	@Builder.Default
	@OneToMany(targetEntity = RefreshToken.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "usuario")
	@Fetch(value = FetchMode.SUBSELECT)
	private final List<RefreshToken> refreshTokens = new ArrayList<>();

	public boolean tieneElRefreshToken(RefreshToken refreshToken) {
		return refreshTokens.contains(refreshToken);
	}
}

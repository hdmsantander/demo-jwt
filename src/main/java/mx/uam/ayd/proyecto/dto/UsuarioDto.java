package mx.uam.ayd.proyecto.dto;

import java.util.UUID;

import lombok.Data;
import mx.uam.ayd.proyecto.negocio.modelo.Usuario;

/**
 * DTO de usuarios
 * 
 */
@Data
public class UsuarioDto {
	
	private String id;

	private String nombre;
	
	private String apellido;
	
	private int edad;
	
	private long grupo;
		
	/**
	 * Este método permite generar un DTO a partir de la entidad
	 * 
	 * @param usuario
	 * @return
	 */
	public static UsuarioDto creaDto(Usuario usuario) {
		UsuarioDto dto = new UsuarioDto();

		dto.setId(usuario.getId().toString());
		dto.setNombre(usuario.getNombre());
		dto.setApellido(usuario.getApellido());
		dto.setEdad(usuario.getEdad());
		dto.setGrupo(usuario.getGrupo().getIdGrupo());

		return dto;
	}
}

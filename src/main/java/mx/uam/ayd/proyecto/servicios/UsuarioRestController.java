package mx.uam.ayd.proyecto.servicios;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.http.ResponseEntity;

import lombok.extern.slf4j.Slf4j;
import mx.uam.ayd.proyecto.dto.UsuarioDto;
import mx.uam.ayd.proyecto.negocio.ServicioUsuario;

import mx.uam.ayd.proyecto.negocio.modelo.Usuario;
import mx.uam.ayd.proyecto.seguridad.ServicioSeguridad;

@RestController
@RequestMapping("/v1") // Versionamiento
public class UsuarioRestController {

	@Autowired
	private ServicioUsuario servicioUsuarios;
	
	@Autowired
	private ServicioSeguridad servicioSeguridad;
	
	/**
	 * Permite recuperar todos los usuarios
	 * 
	 * @return
	 */
	@GetMapping(path = "/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity <List<UsuarioDto>> retrieveAll() {
		
		List <UsuarioDto> usuarios =  servicioUsuarios.recuperaUsuarios();
		
		return ResponseEntity.status(HttpStatus.OK).body(usuarios);
		
	}

	/**
	 * Método que permite agregar un usuario
	 * 
	 * @param nuevoUsuario
	 * @return
	 */
	@ApiOperation(
			value = "Crear usuario",
			notes = "Permite crear un nuevo usuario"
			) // Documentacion del api
	@PostMapping(path = "/usuarios", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UsuarioDto> create(@RequestBody @Valid UsuarioDto nuevoUsuario) {
		
		try {
		
			UsuarioDto usuarioDto = servicioUsuarios.agregaUsuario(nuevoUsuario);

			return ResponseEntity.status(HttpStatus.CREATED).body(usuarioDto);

		} catch(Exception ex) {
			
			HttpStatus status;
			
			if(ex instanceof IllegalArgumentException) {
				status = HttpStatus.BAD_REQUEST;
			} else {
				status = HttpStatus.INTERNAL_SERVER_ERROR;
			}
			
			throw new ResponseStatusException(status, ex.getMessage());
		}
		
	}
	
	/**
	 * Permite recuperar un usuario a partir de us ID
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping(path = "/usuarios/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity <?> retrieve(@PathVariable("id") UUID id,
			@ApiParam(name = "Authorization", value = "Bearer token", example = ServicioSeguridad.EJEMPLO_HEADER_AUTORIZACION, required = true) @RequestHeader(value = "Authorization", name = "Authorization", required = true) String authorization) {
		
		try {
			
			// Revisamos si es un JWT válido para esta petición, quitamos la parte de bearer del header para tener solo el JWT
			if (servicioSeguridad.jwtEsValido(authorization.replace("Bearer ", ""))) {
				
				// Obtenemos el UUID que viene en el JWT, quitamos la parte de bearer del header para tener solo el JWT
				UUID uuid = servicioSeguridad.obtenUuidDeJwt(authorization.replace("Bearer ", ""));
				
				// Comparamos el UUID solicitado al controlador con el que viene en el token
				// solo aceptamos peticiones para el usuario del token, si esta UUID
				// es la misma y el usuario existe, regresamos el usuario
				if (id.equals(uuid) && servicioUsuarios.recuperaUsuario(uuid).isPresent()) {
					return ResponseEntity.status(HttpStatus.CREATED).body(servicioUsuarios.recuperaUsuario(uuid).get());
				}
				
			}
			
			// Cualquier otro caso, regresamos un no autorizado
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
		}
		
	}

}

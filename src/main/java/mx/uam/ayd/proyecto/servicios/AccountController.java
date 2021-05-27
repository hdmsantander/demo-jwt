package mx.uam.ayd.proyecto.servicios;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import mx.uam.ayd.proyecto.seguridad.ServicioSeguridad;

@RestController
@RequestMapping("/v1")
@Api(value = "account")
public class AccountController {
	
	@Autowired
	private ServicioSeguridad servicioSeguridad;
		
	@PostMapping(path = "/account/login", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, String>> login(
			@ApiParam(value = "X-Uuid", required = true) @RequestHeader(name = "X-Uuid", required = true) UUID uuid) {
		
		// Generamos el JWT del usuario con ese UUID
		String jwt = servicioSeguridad.generaTokenUsuario(uuid);
		String rt = servicioSeguridad.generaRefreshTokenUsuario(uuid);

		// Si el JWT es nulo, regresamos un bad request
		if (jwt == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		
		// Construimos el JSON que se regresará
		HashMap<String, String> json = new HashMap<>();

		// Indicamos qué tipo de token es
		json.put("type", "Bearer Token");
		
		// Ponemos el refresh token
		json.put("refresh-token", rt);
		
		// Ponemos el JWT y lo regresamos
		json.put("token", jwt);
		
		return ResponseEntity.status(HttpStatus.OK).body(json);
		
	}
	
	@PostMapping(path = "/account/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, String>> refresh(
			@ApiParam(name = "Authorization", value = "Bearer token", example = ServicioSeguridad.EJEMPLO_HEADER_AUTORIZACION, required = true) @RequestHeader(value = "Authorization", name = "Authorization", required = true) String authorization,
			@ApiParam(value = "X-Refresh-Token", required = true) @RequestHeader(name = "X-Refresh-Token", required = true) UUID refreshToken) {
		
		// Generamos el JWT del usuario con ese UUID
		String jwt = servicioSeguridad.refrescaJwt(authorization.replace("Bearer ", ""), refreshToken);
		
		// Revisamos que tenga el 
		
		// Si el JWT es nulo, regresamos un bad request
		if (jwt == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		
		// Construimos el JSON que se regresará
		HashMap<String, String> json = new HashMap<>();

		// Indicamos qué tipo de token es
		json.put("type", "Bearer Token");
		
		// Ponemos el token y lo regresamos
		json.put("token", jwt);
		
		return ResponseEntity.status(HttpStatus.OK).body(json);
		
	}

}

package mx.uam.ayd.proyecto.seguridad;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;

import mx.uam.ayd.proyecto.datos.RefreshTokenRepository;
import mx.uam.ayd.proyecto.datos.UsuarioRepository;
import mx.uam.ayd.proyecto.negocio.modelo.RefreshToken;
import mx.uam.ayd.proyecto.negocio.modelo.Usuario;

@Service
public class ServicioSeguridad {
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private ServicioAlgoritmo servicioAlgoritmo;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	public static final String EJEMPLO_HEADER_AUTORIZACION = "Bearer aaaaaaa.bbbbbbb.ccccccc";
	
	public String generaTokenUsuario(UUID idUsuario) {
		
		// Buscamos el usuario con ese UUID en la base de datos
		Optional<Usuario> usuarioABuscar = usuarioRepository.findById(idUsuario);
		
		// Si no encontramos el usuario con ese UUID, de una vez regresamos null
		if (usuarioABuscar.isEmpty()) {
			return null;
		}
		
		// Tenemos el usuario al cual le vamos a generar el JWT
		Usuario usuario = usuarioABuscar.get();
				
		// Creamos el JWT y lo regresamos
		return JWT.create()
				
				// Esto representa el issuer o emisor que está emitiendo este JWT
				.withIssuer(applicationContext.getId())
				
				// Esto es el claim sub, el subject o sujeto al cual representa este JWT
				.withSubject(usuario.getId().toString())
				
				// Estas son propiedades que queremos que tenga el token, son atributos de la entidad en cuestión
				.withClaim("nombre", usuario.getNombre())
				.withClaim("apellido", usuario.getApellido())
				.withClaim("grupo", usuario.getGrupo().getNombre())
				.withClaim("edad", usuario.getEdad())
				
				// La fecha de emisión del JWT
				.withIssuedAt(new Date(System.currentTimeMillis()))
				
				// La fecha de caducidad del JWT
				.withExpiresAt(new Date(System.currentTimeMillis() + 120000))
				
				// Al final lo firmamos con el algoritmo que nos provee el servicio de algoritmos
				// notar que usar un servicio de algoritmos nos permite en un futuro hacer cambios
				// sobre el algoritmo (o reemplazarlo en pruebas)
				.sign(servicioAlgoritmo.getAlgoritmo());

	}
	
	@Transactional
	public String generaRefreshTokenUsuario(UUID idUsuario) {
		
		// Buscamos el usuario con ese UUID en la base de datos
		Optional<Usuario> usuarioABuscar = usuarioRepository.findById(idUsuario);
		
		// Si no encontramos el usuario con ese UUID, de una vez regresamos null
		if (usuarioABuscar.isEmpty()) {
			return null;
		}
		
		// Tenemos el usuario al cual le vamos a generar el JWT
		Usuario usuario = usuarioABuscar.get();
		
		// Le generamos su refresh token
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setIssuedAt(System.currentTimeMillis());
		refreshToken.setExpireAt(System.currentTimeMillis() + 600000);
		refreshToken = refreshTokenRepository.save(refreshToken);
		
		// Le agregamos el refresh token al usuario y lo guardamos
		usuario.getRefreshTokens().add(refreshToken);		
		usuarioRepository.save(usuario);
		
		return refreshToken.getId().toString();
		
	}
	
	public String refrescaJwt(String headerAutorizacion, UUID refreshToken) {
		
		// Buscamos el usuario con el UUID que viene en el token en la base de datos
		Optional<Usuario> usuarioABuscar = usuarioRepository.findById(obtenUuidDeJwt(headerAutorizacion));
		
		// Si no encontramos el usuario con ese UUID, de una vez regresamos null
		if (usuarioABuscar.isEmpty()) {
			return null;
		}
		
		// Tenemos el usuario al cual le vamos a refrescar el JWT
		Usuario usuario = usuarioABuscar.get();
		
		// Buscamos el refresh token que tiene ese UUID en la base de datos
		Optional<RefreshToken> tokenABuscar = refreshTokenRepository.findById(refreshToken);
		
		// Si no encontramos el refresh token con ese UUID, de una vez regresamos null
		if (tokenABuscar.isEmpty()) {
			return null;
		}
		
		// Revisamos si el usuario tiene ese refresh token y no ha caducado, sino, igual regresamos null
		if (!usuario.tieneElRefreshToken(tokenABuscar.get()) || tokenABuscar.get().getExpireAt() < System.currentTimeMillis()) {
			return null;
		}
		
		// Si todo está bien, generamos un nuevo token lo regresamos
		return JWT.create()
				
				// Esto representa el issuer o emisor que está emitiendo este JWT
				.withIssuer(applicationContext.getId())
				
				// Esto es el claim sub, el subject o sujeto al cual representa este JWT
				.withSubject(usuario.getId().toString())
				
				// Estas son propiedades que queremos que tenga el token, son atributos de la entidad en cuestión
				.withClaim("nombre", usuario.getNombre())
				.withClaim("apellido", usuario.getApellido())
				.withClaim("grupo", usuario.getGrupo().getNombre())
				.withClaim("edad", usuario.getEdad())
				
				// La fecha de emisión del JWT
				.withIssuedAt(new Date(System.currentTimeMillis()))
				
				// La fecha de caducidad del JWT
				.withExpiresAt(new Date(System.currentTimeMillis() + 120000))
				
				// Al final lo firmamos con el algoritmo que nos provee el servicio de algoritmos
				// notar que usar un servicio de algoritmos nos permite en un futuro hacer cambios
				// sobre el algoritmo (o reemplazarlo en pruebas)
				.sign(servicioAlgoritmo.getAlgoritmo());
		
	}
	
	public Boolean jwtEsValido(String headerAutorizacion) {
		
		// Creamos un verificador de JWT con el algoritmo que usamos para crearlos
		JWTVerifier verifier = JWT.require(servicioAlgoritmo.getAlgoritmo()).build();
		
		// Si no se arrojó una excepción el objeto será diferente de nulo
		return verifier.verify(headerAutorizacion) != null;
	}

	public UUID obtenUuidDeJwt(String headerAutorizacion) {
		
		// Creamos un verificador de JWT con el algoritmo que usamos para crearlos
		JWTVerifier verifier = JWT.require(servicioAlgoritmo.getAlgoritmo()).build();
		
		// Si no se arrojó una excepción el objeto será diferente de nulo y podremos obtener
		// el claim subject que tiene el UUID del usuario
		return UUID.fromString(verifier.verify(headerAutorizacion).getSubject());
		
	}
}

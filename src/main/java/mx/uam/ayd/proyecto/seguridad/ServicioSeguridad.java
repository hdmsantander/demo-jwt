package mx.uam.ayd.proyecto.seguridad;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;

import mx.uam.ayd.proyecto.datos.UsuarioRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Usuario;

@Service
public class ServicioSeguridad {
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private ServicioAlgoritmo servicioAlgoritmo;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
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

}

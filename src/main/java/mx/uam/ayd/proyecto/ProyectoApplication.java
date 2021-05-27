package mx.uam.ayd.proyecto;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import mx.uam.ayd.proyecto.datos.GrupoRepository;
import mx.uam.ayd.proyecto.datos.RefreshTokenRepository;
import mx.uam.ayd.proyecto.datos.UsuarioRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Grupo;
import mx.uam.ayd.proyecto.negocio.modelo.RefreshToken;
import mx.uam.ayd.proyecto.negocio.modelo.Usuario;
import mx.uam.ayd.proyecto.seguridad.ServicioSeguridad;

/**
 * 
 * Clase principal que arranca la aplicación 
 * construida usando el principio de 
 * inversión de control
 * 
 * Ejemplo de cambio en Rama
 * 
 * @author humbertocervantes
 *
 */
@SpringBootApplication
public class ProyectoApplication {

	
	@Autowired
	GrupoRepository grupoRepository;
	
	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	ServicioSeguridad servicioSeguridad;
	
	@Autowired
	RefreshTokenRepository refreshTokenRepository;
	
	/**
	 * 
	 * Método principal
	 * 
	 */
	public static void main(String[] args) {
		
		SpringApplicationBuilder builder = new SpringApplicationBuilder(ProyectoApplication.class);

		builder.headless(false);

		builder.run(args);
	}

	/**
	 * Metodo que arranca la aplicacion
	 * inicializa la bd y arranca el controlador
	 * otro comentario
	 */
	@PostConstruct
	public void inicia() {
		
		inicializaBD();
		
		//controlPrincipal.inicia();
	}
	
	
	/**
	 * 
	 * Inicializa la BD con datos
	 * 
	 * 
	 */
	public void inicializaBD() {
		
		// Vamos a crear los dos grupos de usuarios
		
		Grupo grupoAdmin = new Grupo();
		grupoAdmin.setNombre("Administradores");
		grupoRepository.save(grupoAdmin);
		
		Grupo grupoOps = new Grupo();
		grupoOps.setNombre("Operadores");
		grupoRepository.save(grupoOps);
		
		Usuario usuario = new Usuario();
		usuario.setGrupo(grupoAdmin);
		usuario.setNombre("fabian");
		usuario.setApellido("santander");
		usuario.setEdad(27);
		usuario = usuarioRepository.save(usuario);
		
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setIssuedAt(System.currentTimeMillis());
		refreshToken.setExpireAt(System.currentTimeMillis() + 600000);
		refreshToken = refreshTokenRepository.save(refreshToken);
		
		// Le agregamos el refresh token al usuario y lo guardamos
		usuario.getRefreshTokens().add(refreshToken);		
		usuarioRepository.save(usuario);
								
	}
}

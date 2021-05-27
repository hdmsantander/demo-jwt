package mx.uam.ayd.proyecto.seguridad;

import org.springframework.stereotype.Service;

import com.auth0.jwt.algorithms.Algorithm;

import lombok.Getter;

@Service
@Getter
public class ServicioAlgoritmo {
	
	private Algorithm algoritmo;
	
	private static final String SECRETO = "4E635266556A586E327234753778214125442A472D4B6150645367566B597033";

	public ServicioAlgoritmo() {
		this.algoritmo = Algorithm.HMAC256(SECRETO);
	}

}

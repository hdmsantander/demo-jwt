package mx.uam.ayd.proyecto.datos;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import mx.uam.ayd.proyecto.negocio.modelo.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository <RefreshToken, UUID>  {

}

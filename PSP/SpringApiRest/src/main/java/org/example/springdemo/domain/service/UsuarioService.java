package org.example.springdemo.domain.service;

import org.example.springdemo.data.entity.UsuarioEntity;
import org.example.springdemo.data.UsuarioRepository;
import org.example.springdemo.domain.mapper.UsuarioMapper;
import org.example.springdemo.domain.model.Rol;
import org.example.springdemo.domain.model.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.time.LocalDateTime;


@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;
    private final CryptoService cryptoService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, 
                          UsuarioMapper usuarioMapper, CryptoService cryptoService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
        this.cryptoService = cryptoService;
    }

    public Usuario login(String username, String password) {
        UsuarioEntity entity = usuarioRepository.getByUsername(username);
        if (entity != null && passwordEncoder.matches(password, entity.getPassword())) {
            return usuarioMapper.toDomain(entity);
        }
        return null;
    }

    /**
     * Register a new user with RSA key pair generation
     */
    public Usuario registrarUsuario(String username, String password, String email, String nombre, Rol rol) {
        try {
            // Check if username already exists
            if (usuarioRepository.getByUsername(username) != null) {
                throw new RuntimeException("El nombre de usuario ya existe");
            }
            
            // Generate RSA key pair
            KeyPair keyPair = cryptoService.generarParClaves();
            
            // Encrypt private key with user's password
            byte[] clavePrivadaCifrada = cryptoService.cifrarClavePrivada(keyPair.getPrivate(), password);
            
            // Get public key bytes
            byte[] clavePublica = keyPair.getPublic().getEncoded();
            
            // Create user entity
            UsuarioEntity entity = new UsuarioEntity();
            entity.setUsername(username);
            entity.setPassword(passwordEncoder.encode(password));
            entity.setEmail(email);
            entity.setNombre(nombre);
            entity.setRol(rol);
            entity.setClavePublica(clavePublica);
            entity.setClavePrivadaCifrada(clavePrivadaCifrada);
            entity.setFechaRegistro(LocalDateTime.now());
            
            UsuarioEntity savedEntity = usuarioRepository.save(entity);
            return usuarioMapper.toDomain(savedEntity);
            
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar usuario: " + e.getMessage(), e);
        }
    }
}

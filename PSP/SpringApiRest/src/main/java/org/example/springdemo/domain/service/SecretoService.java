package org.example.springdemo.domain.service;

import org.example.springdemo.data.SecretoCompartidoRepository;
import org.example.springdemo.data.SecretoRepository;
import org.example.springdemo.data.UsuarioRepository;
import org.example.springdemo.data.entity.SecretoCompartidoEntity;
import org.example.springdemo.data.entity.SecretoEntity;
import org.example.springdemo.data.entity.UsuarioEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SecretoService {
    
    @Autowired
    private SecretoRepository secretoRepository;
    
    @Autowired
    private SecretoCompartidoRepository secretoCompartidoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private CryptoService cryptoService;
    
    /**
     * Save a new secret
     */
    @Transactional
    public SecretoEntity guardarSecreto(Long autorId, String titulo, String contenido, String passwordAutor) {
        try {
            // Get author
            UsuarioEntity autor = usuarioRepository.findById(autorId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Generate random AES key
            SecretKey aesKey = cryptoService.generarClaveAES();
            
            // Encrypt content with AES
            byte[] contenidoCifrado = cryptoService.cifrarConAES(contenido, aesKey);
            
            // Decrypt author's private key
            PrivateKey privateKey = cryptoService.descifrarClavePrivada(autor.getClavePrivadaCifrada(), passwordAutor);
            
            // Get author's public key
            PublicKey publicKey = cryptoService.byteArrayToPublicKey(autor.getClavePublica());
            
            // Encrypt AES key with author's RSA public key
            byte[] claveAESCifrada = cryptoService.cifrarClaveAESConRSA(aesKey, publicKey);
            
            // Sign encrypted content with author's private key
            byte[] firma = cryptoService.firmar(contenidoCifrado, privateKey);
            
            // Create secret entity
            SecretoEntity secreto = new SecretoEntity();
            secreto.setAutor(autor);
            secreto.setTitulo(titulo);
            secreto.setContenidoCifrado(contenidoCifrado);
            secreto.setClaveSimétricaCifrada(claveAESCifrada);
            secreto.setFirma(firma);
            secreto.setFechaCreacion(LocalDateTime.now());
            
            return secretoRepository.save(secreto);
            
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar secreto: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get decrypted secret (for owner or shared user)
     */
    @Transactional(readOnly = true)
    public String verSecreto(Long secretoId, Long usuarioId, String passwordUsuario) {
        try {
            // Get secret
            SecretoEntity secreto = secretoRepository.findById(secretoId)
                    .orElseThrow(() -> new RuntimeException("Secreto no encontrado"));
            
            // Get user
            UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Verify signature first
            PublicKey authorPublicKey = cryptoService.byteArrayToPublicKey(secreto.getAutor().getClavePublica());
            boolean firmaValida = cryptoService.verificarFirma(
                    secreto.getContenidoCifrado(), 
                    secreto.getFirma(), 
                    authorPublicKey
            );
            
            if (!firmaValida) {
                throw new RuntimeException("Firma digital inválida - el contenido ha sido modificado");
            }
            
            // Decrypt user's private key
            PrivateKey privateKey = cryptoService.descifrarClavePrivada(usuario.getClavePrivadaCifrada(), passwordUsuario);
            
            // Determine which encrypted AES key to use
            byte[] claveAESCifrada;
            if (secreto.getAutor().getId().equals(usuarioId)) {
                // User is the owner
                claveAESCifrada = secreto.getClaveSimétricaCifrada();
            } else {
                // User is a recipient - find shared secret
                SecretoCompartidoEntity compartido = secretoCompartidoRepository
                        .findBySecretoIdAndDestinatarioId(secretoId, usuarioId)
                        .orElseThrow(() -> new RuntimeException("No tiene permisos para ver este secreto"));
                claveAESCifrada = compartido.getClaveSimétricaCifradaDestinatario();
            }
            
            // Decrypt AES key
            SecretKey aesKey = cryptoService.descifrarClaveAESConRSA(claveAESCifrada, privateKey);
            
            // Decrypt content
            return cryptoService.descifrarConAES(secreto.getContenidoCifrado(), aesKey);
            
        } catch (Exception e) {
            throw new RuntimeException("Error al ver secreto: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get all secrets (own and shared) - metadata only
     */
    @Transactional(readOnly = true)
    public List<SecretoEntity> listarSecretos(Long usuarioId) {
        List<SecretoEntity> secretos = new ArrayList<>();
        
        // Get own secrets
        secretos.addAll(secretoRepository.findByAutorId(usuarioId));
        
        // Get shared secrets
        List<SecretoCompartidoEntity> compartidos = secretoCompartidoRepository
                .findByDestinatarioIdWithSecreto(usuarioId);
        for (SecretoCompartidoEntity compartido : compartidos) {
            secretos.add(compartido.getSecreto());
        }
        
        return secretos;
    }
    
    /**
     * Share a secret with another user
     */
    @Transactional
    public SecretoCompartidoEntity compartirSecreto(Long secretoId, Long autorId, Long destinatarioId, String passwordAutor) {
        try {
            // Get secret
            SecretoEntity secreto = secretoRepository.findById(secretoId)
                    .orElseThrow(() -> new RuntimeException("Secreto no encontrado"));
            
            // Verify ownership
            if (!secreto.getAutor().getId().equals(autorId)) {
                throw new RuntimeException("Solo el autor puede compartir el secreto");
            }
            
            // Get author
            UsuarioEntity autor = usuarioRepository.findById(autorId)
                    .orElseThrow(() -> new RuntimeException("Autor no encontrado"));
            
            // Get recipient
            UsuarioEntity destinatario = usuarioRepository.findById(destinatarioId)
                    .orElseThrow(() -> new RuntimeException("Destinatario no encontrado"));
            
            // Check if already shared
            if (secretoCompartidoRepository.findBySecretoIdAndDestinatarioId(secretoId, destinatarioId).isPresent()) {
                throw new RuntimeException("El secreto ya está compartido con este usuario");
            }
            
            // Decrypt author's private key
            PrivateKey authorPrivateKey = cryptoService.descifrarClavePrivada(autor.getClavePrivadaCifrada(), passwordAutor);
            
            // Decrypt AES key using author's private key
            SecretKey aesKey = cryptoService.descifrarClaveAESConRSA(secreto.getClaveSimétricaCifrada(), authorPrivateKey);
            
            // Get recipient's public key
            PublicKey recipientPublicKey = cryptoService.byteArrayToPublicKey(destinatario.getClavePublica());
            
            // Encrypt AES key with recipient's public key
            byte[] claveAESCifradaDestinatario = cryptoService.cifrarClaveAESConRSA(aesKey, recipientPublicKey);
            
            // Create shared secret entity
            SecretoCompartidoEntity compartido = new SecretoCompartidoEntity();
            compartido.setSecreto(secreto);
            compartido.setDestinatario(destinatario);
            compartido.setClaveSimétricaCifradaDestinatario(claveAESCifradaDestinatario);
            compartido.setFechaCompartido(LocalDateTime.now());
            
            return secretoCompartidoRepository.save(compartido);
            
        } catch (Exception e) {
            throw new RuntimeException("Error al compartir secreto: " + e.getMessage(), e);
        }
    }
    
    /**
     * Stop sharing a secret with a user
     */
    @Transactional
    public void dejarDeCompartirSecreto(Long secretoId, Long autorId, Long destinatarioId) {
        // Get secret
        SecretoEntity secreto = secretoRepository.findById(secretoId)
                .orElseThrow(() -> new RuntimeException("Secreto no encontrado"));
        
        // Verify ownership
        if (!secreto.getAutor().getId().equals(autorId)) {
            throw new RuntimeException("Solo el autor puede dejar de compartir el secreto");
        }
        
        // Delete shared secret
        secretoCompartidoRepository.deleteBySecretoIdAndDestinatarioId(secretoId, destinatarioId);
    }
}

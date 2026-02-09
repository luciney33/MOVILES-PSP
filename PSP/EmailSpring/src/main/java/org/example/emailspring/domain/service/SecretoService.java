package org.example.emailspring.domain.service;

import org.example.emailspring.common.Constantes;
import org.example.emailspring.data.SecretoCompartidoRepository;
import org.example.emailspring.data.SecretoRepository;
import org.example.emailspring.data.UsuarioRepository;
import org.example.emailspring.data.entity.SecretoCompartidoEntity;
import org.example.emailspring.data.entity.SecretoEntity;
import org.example.emailspring.data.entity.UsuarioEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;


@Service
public class SecretoService {
    private final SecretoRepository secretoRepository;
    private final UsuarioRepository usuarioRepository;
    private final SymmetricEncryptionService symmetricService;
    private final AsymmetricEncryptionService asymmetricService;
    private final SecretoCompartidoRepository compartidoRepository;
    private final UsuarioService usuarioService;

    public SecretoService(SecretoRepository secretoRepository, UsuarioRepository usuarioRepository, SymmetricEncryptionService symmetricService, AsymmetricEncryptionService asymmetricService, SecretoCompartidoRepository compartidoRepository, UsuarioService usuarioService) {
        this.secretoRepository = secretoRepository;
        this.usuarioRepository = usuarioRepository;
        this.symmetricService = symmetricService;
        this.asymmetricService = asymmetricService;
        this.compartidoRepository = compartidoRepository;
        this.usuarioService = usuarioService;
    }

    public String verPorUsername(String username, String password, Long secretoId) throws Exception {
        UsuarioEntity user = usuarioRepository.findByUsername(username);
        return this.ver(user.getId(), password, secretoId);
    }

    @Transactional
    public void compartirPorUsername(String username, String password, Long secretoId, Long receptorId) throws Exception {
        UsuarioEntity entity = usuarioRepository.findByUsername(username);
        this.compartir(entity.getId(), password, secretoId, receptorId);
    }

    @Transactional
    public void revocarPorUsername(String username, Long secretoId, Long receptorId) {
        UsuarioEntity entity = usuarioRepository.findByUsername(username);
        this.dejarDeCompartir(entity.getId(), secretoId, receptorId);
    }

    @Transactional
    public Long guardar(Long autorId, String password, String texto) throws Exception {
        UsuarioEntity autor = usuarioRepository.findById(autorId).orElseThrow();

        SecretKey aesKey = symmetricService.generateKey();
        byte[] iv = symmetricService.generateIV();
        byte[] contenidoCifrado = symmetricService.encrypt(texto.getBytes(StandardCharsets.UTF_8), aesKey, iv);

        byte[] aes = asymmetricService.encryptKey(aesKey, asymmetricService.bytesToPublicKey(autor.getClavePublica()));
        PrivateKey priv = usuarioService.obtenerClavePrivadaDescifrada(autor, password);
        byte[] firma = asymmetricService.firma(texto.getBytes(StandardCharsets.UTF_8), priv);

        SecretoEntity s = new SecretoEntity(null, autor, autor.getSalt(), iv,contenidoCifrado, aes, firma, null);
        SecretoEntity guardado = secretoRepository.save(s);

        return guardado.getId();
    }

    public String ver(Long userId, String password, Long secretoId) throws Exception {
        SecretoEntity s = secretoRepository.findById(secretoId)
                .orElseThrow(() -> new RuntimeException(Constantes.SECRETO_NO_ENCONTRADO));
        UsuarioEntity u = usuarioRepository.findById(userId).orElseThrow();
        boolean esCompartido = !s.getAutor().getId().equals(userId);
        byte[] keyCifrada = !esCompartido ? s.getClaveSimetricaCifrada() :
                compartidoRepository.findBySecretoIdAndDestinatarioId(secretoId, userId)
                        .orElseThrow(() -> new RuntimeException(Constantes.ACCESO_DENEGADO))
                        .getClaveSimetricaCifradaDestinatario();

        PrivateKey priv = usuarioService.obtenerClavePrivadaDescifrada(u, password);
        SecretKey aesKey = symmetricService.bytesToKey(asymmetricService.decryptKey(keyCifrada, priv));

        byte[] textoBytes = symmetricService.decrypt(s.getContenidoCifrado(), aesKey, s.getIv());
        String texto = new String(textoBytes, StandardCharsets.UTF_8);

        if(!asymmetricService.verify(textoBytes, s.getFirma(), asymmetricService.bytesToPublicKey(s.getAutor().getClavePublica()))) {
            throw new SecurityException(Constantes.LA_FIRMA_ES_INVÁLIDA);
        }

        return texto;
    }

    @Transactional
    public void compartir(Long ownerId, String password, Long secretoId, Long receptorId) throws Exception {
        SecretoEntity s = secretoRepository.findById(secretoId).orElseThrow();

        PrivateKey privAutor = usuarioService.obtenerClavePrivadaDescifrada(s.getAutor(), password);
        byte[] aesKeyBytes = asymmetricService.decryptKey(s.getClaveSimetricaCifrada(), privAutor);
        SecretKey aesKey = symmetricService.bytesToKey(aesKeyBytes);

        UsuarioEntity dest = usuarioRepository.findById(receptorId).orElseThrow();
        byte[] aesDes = asymmetricService.encryptKey(aesKey, asymmetricService.bytesToPublicKey(dest.getClavePublica()));

        SecretoCompartidoEntity sc = new SecretoCompartidoEntity(null, s, dest, aesDes);
        compartidoRepository.save(sc);
    }

    @Transactional
    public void dejarDeCompartir(Long ownerId, Long secretoId, Long destinoId) {
        compartidoRepository.deleteBySecretoIdAndSecretoAutorIdAndDestinatarioId(secretoId, ownerId, destinoId);
    }

}

package org.example.emailspring.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.emailspring.common.Constantes;
import org.example.emailspring.domain.model.Rol;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = Constantes.TABLE_USUARIOS)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String username;
    @Column
    private String password;
    @Column
    private String email;
    @Column
    private String nombre;
    @Column
    private boolean activo;
    @Column
    private String codigoActivacion;
    @Column
    private LocalDateTime expiracionCodigo;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @Column
    private Boolean twoFactorEnabled;

    @Column
    private String twoFactorSecret;

    @Lob
    private byte[] salt;

    @Lob
    private byte[] iv;

    @Lob
    @Column(name = Constantes.CLAVE_PUBLICA, nullable = false)
    private byte[] clavePublica;

    @Lob
    @Column(name = Constantes.CLAVE_PRIVADA_CIFRADA, nullable = false)
    private byte[] clavePrivada;

    @OneToMany(mappedBy = Constantes.AUTOR, cascade = CascadeType.ALL)
    private List<SecretoEntity> secretos = new ArrayList<>();
}

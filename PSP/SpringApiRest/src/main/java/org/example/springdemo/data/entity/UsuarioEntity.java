package org.example.springdemo.data.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.springdemo.common.Constantes;
import org.example.springdemo.domain.model.Rol;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = Constantes.TABLE_USUARIOS)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"secretosCreados", "secretosRecibidos", "clavePrivadaCifrada"})
@EqualsAndHashCode(exclude = {"secretosCreados", "secretosRecibidos"})
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;
    private String nombre;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @Lob
    @Column(name = "clave_publica", columnDefinition = "BLOB")
    private byte[] clavePublica;

    @Lob
    @Column(name = "clave_privada_cifrada", columnDefinition = "BLOB")
    private byte[] clavePrivadaCifrada;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SecretoEntity> secretosCreados = new ArrayList<>();

    @OneToMany(mappedBy = "destinatario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SecretoCompartidoEntity> secretosRecibidos = new ArrayList<>();

    public UsuarioEntity(Long id, String username, String password, String email, String nombre, Rol rol) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.nombre = nombre;
        this.rol = rol;
    }
}

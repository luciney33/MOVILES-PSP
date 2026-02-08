package org.example.springdemo.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "secretos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"compartidos"})
@EqualsAndHashCode(exclude = {"compartidos"})
public class SecretoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private UsuarioEntity autor;

    @Column(nullable = false)
    private String titulo;

    @Lob
    @Column(name = "contenido_cifrado", nullable = false, columnDefinition = "BLOB")
    private byte[] contenidoCifrado;

    @Lob
    @Column(name = "clave_simetrica_cifrada", nullable = false, columnDefinition = "BLOB")
    private byte[] claveSimétricaCifrada;

    @Lob
    @Column(nullable = false, columnDefinition = "BLOB")
    private byte[] firma;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "secreto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SecretoCompartidoEntity> compartidos = new ArrayList<>();
}

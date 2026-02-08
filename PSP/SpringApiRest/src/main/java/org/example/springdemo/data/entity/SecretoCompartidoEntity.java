package org.example.springdemo.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "secretos_compartidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class SecretoCompartidoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secreto_id", nullable = false)
    private SecretoEntity secreto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinatario_id", nullable = false)
    private UsuarioEntity destinatario;

    @Lob
    @Column(name = "clave_simetrica_cifrada_destinatario", nullable = false, columnDefinition = "BLOB")
    private byte[] claveSimétricaCifradaDestinatario;

    @Column(name = "fecha_compartido", nullable = false)
    private LocalDateTime fechaCompartido;
}

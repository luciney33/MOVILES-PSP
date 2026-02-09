package org.example.emailspring.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.emailspring.common.Constantes;


@Entity
@Table(name = Constantes.TABLE_SECRETOS_COMPARTIDOS)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecretoCompartidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constantes.SECRETO_ID, nullable = false)
    private SecretoEntity secreto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constantes.DESTINATARIO_ID, nullable = false)
    private UsuarioEntity destinatario;

    @Lob
    @Column(name = Constantes.CLAVE_SIMETRICA_CIFRADA_DESTINATARIO, nullable = false)
    private byte[] claveSimetricaCifradaDestinatario;
}


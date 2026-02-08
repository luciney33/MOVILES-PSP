package org.example.emailspring.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.emailspring.common.Constantes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = Constantes.TABLE_SECRETOS)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecretoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constantes.AUTOR_ID, nullable = false)
    private UsuarioEntity autor;

    @Lob
    private byte[] salt;

    @Lob
    private byte[] iv;

    @Lob
    @Column(name = Constantes.CONTENIDO_CIFRADO,nullable = false)
    private byte[] contenidoCifrado;

    @Lob
    @Column(name = Constantes.CLAVE_SIMETRICA_CIFRADA,nullable = false)
    private byte[] claveSimetricaCifrada;

    @Lob
    @Column(nullable = false)
    private byte[] firma;


    @OneToMany(mappedBy = Constantes.SECRETO, cascade = CascadeType.ALL)
    private List<SecretoCompartidoEntity> compartidos = new ArrayList<>();

}


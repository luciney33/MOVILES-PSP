package org.example.emailspring.data.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.emailspring.common.Constantes;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = Constantes.TABLE_ENTRENAMIENTO)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "ejercicios")
@ToString(exclude = "ejercicios")
public class EntrenamientoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = Constantes.SESSION_USUARIO_ID)
    private Long usuarioId;
    @Column
    private String nombre;
    @Column
    private String descripcion;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "entrenamiento_ejercicios",
            joinColumns = @JoinColumn(name = "entrenamiento_id"),
            inverseJoinColumns = @JoinColumn(name = "ejercicio_id")
    )
    private Set<EjercicioEntity> ejercicios = new HashSet<>();
}

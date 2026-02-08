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
@EqualsAndHashCode(exclude = Constantes.EJERCICIOS)
@ToString(exclude = Constantes.EJERCICIOS)
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
            name = Constantes.TABLE_ENTRENAMIENTO_EJERCICIOS,
            joinColumns = @JoinColumn(name = Constantes.ENTRENAMIENTO_ID),
            inverseJoinColumns = @JoinColumn(name = Constantes.EJERCICIO_ID)
    )
    private Set<EjercicioEntity> ejercicios = new HashSet<>();
}

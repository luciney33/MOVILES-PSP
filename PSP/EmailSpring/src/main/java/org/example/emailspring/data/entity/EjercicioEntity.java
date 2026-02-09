package org.example.emailspring.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.emailspring.common.Constantes;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = Constantes.TABLE_EJERCICIOS)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "entrenamientos") //lo dejo hardcodeado porque sino me da fallo
@ToString(exclude = "entrenamientos")
public class EjercicioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = Constantes.TIPO_ENTRENAMIENTO, nullable = false)
    private String tipoEntrenamiento;

    @Column(name = Constantes.IMAGEN_URL)
    private String imagenUrl;

    @Column
    private String descripcion;

    @ManyToMany(mappedBy = Constantes.EJERCICIOS)
    @JsonIgnore
    private Set<EntrenamientoEntity> entrenamientos = new HashSet<>();
}


package org.example.springdemo.data.repository.implementacion;

import org.example.springdemo.data.entity.EjercicioEntity;
import org.example.springdemo.data.mapper.EjercicioRowMap;
import org.example.springdemo.data.repository.EjercicioRepository;
import org.example.springdemo.data.utilities.Queries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public class SpringEjercicioRepositoy implements EjercicioRepository {
    private final EjercicioRowMap rowMapper;
    private final JdbcClient jdbcClient;

    @Autowired
    public SpringEjercicioRepositoy(EjercicioRowMap rowMapper, JdbcClient jdbcClient) {
        this.rowMapper = rowMapper;
        this.jdbcClient = jdbcClient;
    }


    @Override
    public List<EjercicioEntity> getAll() {
        return jdbcClient.sql(Queries.SELECT_FROM_EJERCICIO)
                .query(rowMapper)
                .list();
    }

    @Override
    public EjercicioEntity getById(int id) {
        return jdbcClient.sql(Queries.SELECT_EJERCICIO_BY_ID)
                .param(1, id)
                .query(rowMapper)
                .optional()
                .orElse(null);
    }

    @Override
    public List<EjercicioEntity> getByEntrenamientoId(int entrenamientoId) {
        return jdbcClient.sql(Queries.SELECT_EJERCICIO_BY_ENTRENAMIENTO_ID)
                .param(1, entrenamientoId)
                .query(rowMapper)
                .list();
    }

    @Override
    public int save(EjercicioEntity ejercicio) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcClient.sql(Queries.INSERT_EJERCICIO)
                .param(1, ejercicio.getEntrenamientoId())
                .param(2, ejercicio.getNombre())
                .param(3, ejercicio.getRepeticiones())
                .param(4, ejercicio.getSeries())
                .update(keyHolder);

        return Objects.requireNonNull(keyHolder.getKey(), "Key was not generated").intValue();
    }

    @Override
    public void update(EjercicioEntity ejercicio) {
        jdbcClient.sql(Queries.UPDATE_EJERCICIO)
                .param(1, ejercicio.getEntrenamientoId())
                .param(2, ejercicio.getNombre())
                .param(3, ejercicio.getRepeticiones())
                .param(4, ejercicio.getSeries())
                .param(5, ejercicio.getId())
                .update();
    }

    @Override
    public void deleteByEntrenamientoId(int entrenamientoId) {
        jdbcClient.sql(Queries.DELETE_EJERCICIO_BY_ENTRENAMIENTO_ID)
                .param(1, entrenamientoId)
                .update();
    }


    @Override
    public boolean delete(int id) {
        int result = jdbcClient.sql(Queries.DELETE_EJERCICIO_BY_ID)
                .param(1, id)
                .update();

        return result == 1;
    }
}

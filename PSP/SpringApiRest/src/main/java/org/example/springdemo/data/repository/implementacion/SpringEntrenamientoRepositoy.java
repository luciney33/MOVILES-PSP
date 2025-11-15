package org.example.springdemo.data.repository.implementacion;

import org.example.springdemo.data.entity.EntrenamientoEntity;
import org.example.springdemo.data.mapper.EntrenamientoRowMap;
import org.example.springdemo.data.repository.EntrenamientoRepository;
import org.example.springdemo.data.utilities.Queries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Objects;

@Profile("spring")
@Repository
public class SpringEntrenamientoRepositoy implements EntrenamientoRepository {
    private final EntrenamientoRowMap rowMapper;
    private final JdbcClient jdbcClient;
    @Autowired
    public SpringEntrenamientoRepositoy(EntrenamientoRowMap rowMapper, JdbcClient jdbcClient) {
        this.rowMapper = rowMapper;
        this.jdbcClient = jdbcClient;
    }



    @Override
    public List<EntrenamientoEntity> getAll() {
        return jdbcClient.sql(Queries.SELECT_FROM_ENTRENAMIENTO)
                .query(rowMapper)
                .list();
    }

    @Override
    public EntrenamientoEntity getById(int id) {
        return jdbcClient.sql(Queries.SELECT_ENTRENAMIENTO_BY_ID)
                .param(1, id)
                .query(rowMapper)
                .optional()
                .orElse(null);
    }


    @Override
    public List<EntrenamientoEntity> getByUsuarioId(int usuarioId) {
        return jdbcClient.sql(Queries.SELECT_ENTRENAMIENTO_BY_USUARIO_ID)
                .param(1, usuarioId)
                .query(rowMapper)
                .list();
    }

    @Override
    public int save(EntrenamientoEntity entrenamiento) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcClient.sql(Queries.INSERT_ENTRENAMIENTO)
                .param(1, entrenamiento.getUsuarioId())
                .param(2, entrenamiento.getNombre())
                .param(3, entrenamiento.getDescripcion())
                .update(keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }

    @Override
    public void update(EntrenamientoEntity entrenamiento) {
        jdbcClient.sql(Queries.UPDATE_ENTRENAMIENTO)
                .param(1, entrenamiento.getUsuarioId())
                .param(2, entrenamiento.getNombre())
                .param(3, entrenamiento.getDescripcion())
                .param(4, entrenamiento.getId())
                .update();
    }

    @Override
    public boolean delete(int id) {
        try {
            int result = jdbcClient.sql(Queries.DELETE_ENTRENAMIENTO_BY_ID)
                    .param(1, id)
                    .update();
            return result == 1;

        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }
}
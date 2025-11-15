package org.example.springdemo.data.repository.implementacion;

import org.example.springdemo.data.entity.EntrenamientoEntity;
import org.example.springdemo.data.mapper.EntrenamientoRowMap;
import org.example.springdemo.data.repository.EjercicioRepository;
import org.example.springdemo.data.repository.EntrenamientoRepository;
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
    private final EjercicioRepository ejercicioRepository;
    public SpringEntrenamientoRepositoy(EntrenamientoRowMap rowMapper, EjercicioRepository ejercicioRepository) {
        this.rowMapper = rowMapper;
        this.ejercicioRepository = ejercicioRepository;
    }

    @Autowired
    private JdbcClient jdbcClient;



    @Override
    public List<EntrenamientoEntity> getAll() {
        String sql = "SELECT * FROM entrenamiento";
        return jdbcClient.sql(sql)
                .query(rowMapper)
                .list();
    }

    @Override
    public EntrenamientoEntity getById(int id) {
        String sql = "SELECT * FROM entrenamiento WHERE id = ?";
        return jdbcClient.sql(sql)
                .param(1, id)
                .query(rowMapper)
                .optional()
                .orElse(null);
    }


    @Override
    public List<EntrenamientoEntity> getByUsuarioId(int usuarioId) {
        String sql = "SELECT * FROM entrenamiento WHERE usuarioId = ?";
        return jdbcClient.sql(sql)
                .param(1, usuarioId)
                .query(rowMapper)
                .list();
    }

    @Override
    public int save(EntrenamientoEntity entrenamiento) {
        String sql = "INSERT INTO entrenamiento (usuarioId, nombre, descripcion) VALUES (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcClient.sql(sql)
                .param(1, entrenamiento.getUsuarioId())
                .param(2, entrenamiento.getNombre())
                .param(3, entrenamiento.getDescripcion())
                .update(keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }

    @Override
    public void update(EntrenamientoEntity entrenamiento) {
        String sql = "UPDATE entrenamiento SET usuarioId = ?, nombre = ?, descripcion = ? WHERE id = ?";

        jdbcClient.sql(sql)
                .param(1, entrenamiento.getUsuarioId())
                .param(2, entrenamiento.getNombre())
                .param(3, entrenamiento.getDescripcion())
                .param(4, entrenamiento.getId())
                .update();
    }

    @Override
    public void deleteByUsuarioId(int usuarioId) {
        String sql = "DELETE FROM entrenamiento WHERE usuarioId = ?";
        jdbcClient.sql(sql)
                .param(1, usuarioId)
                .update();
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM entrenamiento WHERE id = ?";

        try {
            int result = jdbcClient.sql(sql)
                    .param(1, id)
                    .update();
            return result == 1;

        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }
}
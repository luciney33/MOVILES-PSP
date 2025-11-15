package org.example.springdemo.data.repository.implementacion;

import org.example.springdemo.data.entity.EjercicioEntity;
import org.example.springdemo.data.mapper.EjercicioRowMap;
import org.example.springdemo.data.repository.EjercicioRepository;
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

    @Autowired
    private JdbcClient jdbcClient;

    public SpringEjercicioRepositoy(EjercicioRowMap rowMapper) {
        this.rowMapper = rowMapper;
    }


    @Override
    public List<EjercicioEntity> getAll() {
        String sql = "SELECT * FROM ejercicio";
        return jdbcClient.sql(sql)
                .query(rowMapper)
                .list();
    }

    @Override
    public EjercicioEntity getById(int id) {
        String sql = "SELECT * FROM ejercicio WHERE id = ?";
        return jdbcClient.sql(sql)
                .param(1, id)
                .query(rowMapper)
                .optional()
                .orElse(null);
    }

    @Override
    public List<EjercicioEntity> getByEntrenamientoId(int entrenamientoId) {
        String sql = "SELECT * FROM ejercicio WHERE entrenamientoId = ?";
        return jdbcClient.sql(sql)
                .param(1, entrenamientoId)
                .query(rowMapper)
                .list();
    }

    @Override
    public int save(EjercicioEntity ejercicio) {
        String sql = "INSERT INTO ejercicio(entrenamientoId, nombre, repeticiones, series) VALUES (?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcClient.sql(sql)
                .param(1, ejercicio.getEntrenamientoId())
                .param(2, ejercicio.getNombre())
                .param(3, ejercicio.getRepeticiones())
                .param(4, ejercicio.getSeries())
                .update(keyHolder);

        return Objects.requireNonNull(keyHolder.getKey(), "Key was not generated").intValue();
    }

    @Override
    public void update(EjercicioEntity ejercicio) {
        String sql = "UPDATE ejercicio SET entrenamientoId=?, nombre=?, repeticiones=?, series=? WHERE id=? ";

        jdbcClient.sql(sql)
                .param(1, ejercicio.getEntrenamientoId())
                .param(2, ejercicio.getNombre())
                .param(3, ejercicio.getRepeticiones())
                .param(4, ejercicio.getSeries())
                .param(5, ejercicio.getId())
                .update();
    }

    @Override
    public void deleteByEntrenamientoId(int entrenamientoId) {
        String sql = "DELETE FROM ejercicio WHERE entrenamientoId=?";
        jdbcClient.sql(sql)
                .param(1, entrenamientoId)
                .update();
    }


    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM ejercicio WHERE id=?";

        int result = jdbcClient.sql(sql)
                .param(1, id)
                .update();

        return result == 1;
    }
}

package org.example.springdemo.data.mapper;

import org.example.springdemo.data.entity.EjercicioEntity;
import org.example.springdemo.data.entity.EntrenamientoEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EjercicioRowMap implements RowMapper<EjercicioEntity> {
    @Override
    public EjercicioEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new EjercicioEntity(
               rs.getInt("id"),
                rs.getInt("entrenamientoId"),
               rs.getString("nombre"),
              rs.getInt("repeticiones"),
              rs.getInt("series")
        );
    }
}

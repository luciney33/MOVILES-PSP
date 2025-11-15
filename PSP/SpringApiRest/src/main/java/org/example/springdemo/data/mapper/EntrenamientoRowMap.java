package org.example.springdemo.data.mapper;

import org.example.springdemo.data.entity.EntrenamientoEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EntrenamientoRowMap implements RowMapper<EntrenamientoEntity> {
    @Override
    public EntrenamientoEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new EntrenamientoEntity(
                rs.getInt("id"),
                rs.getInt("usuarioId"),
                rs.getString("nombre"),
                rs.getString("descripcion"));
    }
}

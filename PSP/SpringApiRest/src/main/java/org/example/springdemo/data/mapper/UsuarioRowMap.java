package org.example.springdemo.data.mapper;

import org.example.springdemo.data.entity.UsuarioEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UsuarioRowMap implements RowMapper<UsuarioEntity> {
    @Override
    public UsuarioEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(rs.getLong("id"));
        usuario.setUsername(rs.getString("username"));
        usuario.setPassword(rs.getString("password"));
        usuario.setEmail(rs.getString("email"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setRol(rs.getString("rol"));
        return usuario;
    }
}

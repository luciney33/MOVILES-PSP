package org.example.springdemo.data.repository.implementacion;

import org.example.springdemo.data.entity.UsuarioEntity;
import org.example.springdemo.data.mapper.UsuarioRowMap;
import org.example.springdemo.data.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;

@Profile("spring")
@Repository
public class SpringUsuarioRepository implements UsuarioRepository {
    private final UsuarioRowMap usuarioRowMap;

    public SpringUsuarioRepository(UsuarioRowMap usuarioRowMap) {
        this.usuarioRowMap = usuarioRowMap;
    }

    @Autowired
    private JdbcClient jdbcClient;


    @Override
    public List<UsuarioEntity> getAll() {
        String sql = "SELECT * FROM usuario";
        return jdbcClient.sql(sql)
                .query(usuarioRowMap)
                .list();
    }

    @Override
    public UsuarioEntity getById(Long id) {
        String sql = "SELECT * FROM usuario WHERE id = ?";
        return jdbcClient.sql(sql)
                .param(1, id)
                .query(usuarioRowMap)
                .optional()
                .orElse(null);
    }

    @Override
    public UsuarioEntity getByUsername(String username) {
        String sql = "SELECT * FROM usuario WHERE username = ?";
        return jdbcClient.sql(sql)
                .param(1, username)
                .query(usuarioRowMap)
                .optional()
                .orElse(null);
    }

    @Override
    @Transactional
    public Long save(UsuarioEntity usuario) {
        String sql = "INSERT INTO usuario(username,password,email,nombre,rol) VALUES(?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(sql)
                .param(1, usuario.getUsername())
                .param(2, usuario.getPassword())
                .param(3, usuario.getEmail())
                .param(4, usuario.getNombre())
                .param(5, usuario.getRol())
                .update(keyHolder);
        return Objects.requireNonNull(keyHolder.getKey(), "Key was not generated").longValue();
    }

    @Override
    public void update(UsuarioEntity usuario) {
        String sql = "UPDATE usuario SET username=?, password=?, email=?, nombre=?, rol=? WHERE id=?";
        jdbcClient.sql(sql)
                .param(1, usuario.getUsername())
                .param(2, usuario.getPassword())
                .param(3, usuario.getEmail())
                .param(4, usuario.getNombre())
                .param(5, usuario.getRol())
                .update();
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        try {
            // Primero eliminar objetos asociados
            jdbcClient.sql("DELETE FROM ejercicio WHERE entrenamiento_id IN (SELECT id FROM entrenamiento WHERE user_id = ?)")
                    .param(1, id)
                    .update();

            jdbcClient.sql("DELETE FROM entrenamiento WHERE user_id = ?")
                    .param(1, id)
                    .update();

            // Finalmente eliminar el usuario
            int result = jdbcClient.sql("DELETE FROM usuario WHERE id=?")
                    .param(1, id)
                    .update();

            return result == 1;
        } catch (DataIntegrityViolationException e) {
            System.out.println("No se puede eliminar el usuario con id " + id + " porque tiene referencias existentes.");
            return false;
        }
    }
}

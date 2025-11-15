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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Objects;

@Profile("spring")
@Repository
public class SpringUsuarioRepository implements UsuarioRepository {
    private final UsuarioRowMap usuarioRowMap;
    private final PasswordEncoder passwordEncoder;
    private final JdbcClient jdbcClient;



    public SpringUsuarioRepository(UsuarioRowMap usuarioRowMap, PasswordEncoder passwordEncoder, JdbcClient jdbcClient) {
        this.usuarioRowMap = usuarioRowMap;
        this.passwordEncoder = passwordEncoder;
        this.jdbcClient = jdbcClient;

        saveWithPlainPassword("admin", "admin123", "admin@gmail.com", "Admin", "ADMIN");
        saveWithPlainPassword("user", "user123", "user@gmail.com", "Usuario Normal", "USER");
    }

    private void saveWithPlainPassword(String username, String plainPassword, String email, String nombre, String rol) {
        String hashedPassword = passwordEncoder.encode(plainPassword);

        UsuarioEntity entity = new UsuarioEntity();
        entity.setUsername(username);
        entity.setPassword(hashedPassword);
        entity.setEmail(email);
        entity.setNombre(nombre);
        entity.setRol(rol);

        save(entity);
    }




    @Override
    public List<UsuarioEntity> getAll() {
        String sql = "SELECT * FROM usuario";
        return jdbcClient.sql(sql)
                .query(usuarioRowMap)
                .list();
    }

    @Override
    public UsuarioEntity getById(int id) {
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
    public int save(UsuarioEntity usuario) {
        String sql = "INSERT INTO usuario(username,password,email,nombre,rol) VALUES(?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(sql)
                .param(1, usuario.getUsername())
                .param(2, usuario.getPassword())
                .param(3, usuario.getEmail())
                .param(4, usuario.getNombre())
                .param(5, usuario.getRol())
                .update(keyHolder);
        return Objects.requireNonNull(keyHolder.getKey(), "Key was not generated").intValue();
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
                .param(6, usuario.getId())
                .update();
    }

    @Override
    public boolean delete(int id) {
        try {
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

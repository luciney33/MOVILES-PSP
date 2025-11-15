package org.example.springdemo.data.repository.implementacion;

import org.example.springdemo.common.constantes;
import org.example.springdemo.data.entity.UsuarioEntity;
import org.example.springdemo.data.mapper.UsuarioRowMap;
import org.example.springdemo.data.repository.UsuarioRepository;
import org.example.springdemo.data.utilities.Queries;
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


    @Autowired
    public SpringUsuarioRepository(UsuarioRowMap usuarioRowMap, PasswordEncoder passwordEncoder, JdbcClient jdbcClient) {
        this.usuarioRowMap = usuarioRowMap;
        this.passwordEncoder = passwordEncoder;
        this.jdbcClient = jdbcClient;

        if (getByUsername("admin") == null) {
            saveWithPlainPassword("admin", "admin123", "admin@gmail.com", "Admin", "ADMIN");
        }
        if (getByUsername("user") == null) {
            saveWithPlainPassword("user", "user123", "user@gmail.com", "user@gmail.com", "USER");
        }
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
        return jdbcClient.sql(Queries.SELECT_FROM_USUARIO)
                .query(usuarioRowMap)
                .list();
    }

    @Override
    public UsuarioEntity getById(int id) {
        return jdbcClient.sql(Queries.SELECT_USUARIO_BY_ID)
                .param(1, id)
                .query(usuarioRowMap)
                .optional()
                .orElse(null);
    }

    @Override
    public UsuarioEntity getByUsername(String username) {
        return jdbcClient.sql(Queries.SELECT_USUARIO_BY_USERNAME)
                .param(1, username)
                .query(usuarioRowMap)
                .optional()
                .orElse(null);
    }

    @Override
    public int save(UsuarioEntity usuario) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(Queries.INSERT_USUARIO)
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
        jdbcClient.sql(Queries.UPDATE_USUARIO)
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
            int result = jdbcClient.sql(Queries.DELETE_USUARIO_BY_ID)
                    .param(1, id)
                    .update();

            return result == 1;
        } catch (DataIntegrityViolationException e) {
            System.out.println(String.format(constantes.MSG_CANNOT_DELETE_USER, id));
            return false;
        }
    }
}

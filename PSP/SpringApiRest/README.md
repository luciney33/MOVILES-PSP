# SpringApiRest — README

Resumen rápido
- Proyecto Spring Boot con H2(extraa) en memoria que expone APIs REST para `Usuario`, `Entrenamiento` y `Ejercicio`.
- Login por sesión HTTP (HttpSession). Roles: `ADMIN` (acceso total) y `USER` (ver/borrar sus recursos).
- SQLs en `src/main/java/org/example/springdemo/data/utilities/Queries.java`.
- Rutas y mensajes centralizados en `src/main/java/org/example/springdemo/common/constantes.java`.
- Pruebas unitarias en `src/test/java/org/example/springdemo/pruebas/`. En cada prueba se explica qué se testea (extraa).

Acceso y implementacion de la bd H2 (con la app corriendo)
- URL consola H2: http://localhost:8080/h2-console
- JDBC URL (configuración en `application.properties`): `jdbc:h2:mem:gymdb`
- Usuario/Pass: (`root`/`root`).
- La estrategia sigue este orden al iniciar la aplicación:
- 1.  **Esquema (`schema.sql`):**
   *   El archivo `src/main/resources/schema.sql` solo contiene las sentencias `CREATE TABLE` para definir la base de datos (usuarios, entrenamientos, ejercicios). **No** contiene sentencias `INSERT`.
- 2.  **Precarga de Usuarios:**
     *   El `SpringUsuarioRepository` utiliza un **constructor** para verificar e insertar los usuarios iniciales (`admin` y `user`) si no existen.
     *   Esto permite utilizar el `PasswordEncoder` inyectado para hashear las contraseñas de manera segura antes de guardarlas.
     *   Gracias a esto, se garantiza que los usuarios con `ID=1` (admin) y `ID=2` (user) existan antes de que se carguen los datos dependientes.
- 3.  **Carga de Datos de Prueba (`data.sql`):**
    *   El archivo `src/main/resources/data.sql` contiene las sentencias `INSERT INTO` para los entrenamientos y ejercicios de prueba.
    *   Estos `INSERT` utilizan los IDs literales `1` y `2` para los `usuarioId`, confiando en que los usuarios ya han sido creados en el paso anterior.
- Me ha ayudado este tutorial:
  - https://www.baeldung.com/spring-boot-h2-database


Endpoints principales
- Auth
  - POST /api/auth/login  -> { "username","password" }
  - POST /api/auth/logout
  - GET  /api/auth/session
- Entrenamientos
  - GET  /api/entrenamientos
  - GET  /api/entrenamientos/{id}
  - POST /api/entrenamientos (ADMIN)
  - PUT  /api/entrenamientos/{id} (ADMIN)
  - DELETE /api/entrenamientos/{id} (ADMIN o propietario)
- Ejercicios (similares a Entrenamientos)

Qué ficheros importantes y dónde tocar
- `src/main/resources/application.properties` — configuración de datasource, H2
- Repositorios implementados (con `JdbcClient`), ya que he aprendido hacerlo así en acceso a datos:
  - `src/main/java/org/example/springdemo/data/repository/implementacion/SpringUsuarioRepository.java`
  - `src/main/java/org/example/springdemo/data/repository/implementacion/SpringEntrenamientoRepositoy.java`
  - `src/main/java/org/example/springdemo/data/repository/implementacion/SpringEjercicioRepositoy.java`
- Servicios y controladores: `ui/service` y `ui/controller` — lógica de negocio y control de permisos

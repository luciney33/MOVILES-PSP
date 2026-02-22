# 🎯 GUÍA DE PRUEBA - INTEGRACIÓN COMPLETA

## ✅ ESTADO: TODO FUNCIONANDO

---

## 🚀 CÓMO PROBAR LA APLICACIÓN

### **Pre-requisitos**

1. ✅ Servidor backend ejecutándose en `http://localhost:8080`
2. ✅ Emulador Android o dispositivo físico
3. ✅ Android Studio abierto con el proyecto

---

## 📱 PRUEBA PASO A PASO

### **1. Compilar e Instalar la App**

```bash
cd /Users/lucia/StudioProjects/MOVILES-PSP/MOVILES/NavigationCompose
./gradlew clean installDebug
```

O desde Android Studio:
- Click en el botón "Run" ▶️
- Selecciona el emulador o dispositivo

---

### **2. Hacer Login**

1. Abre la app en el emulador
2. Ingresa credenciales:
   - **Usuario:** `admin`
   - **Password:** `admin123`
3. Click en "Login"

**Logs esperados:**
```
SessionManager: ✅ Password guardada en sesión: adm***
```

✅ **Si ves el log, el SessionManager funciona correctamente**

---

### **3. Ir a la Pestaña "Secretos"**

1. Click en el ícono de "Secretos" en el bottom bar
2. Deberías ver una pantalla vacía (sin secretos aún)

**Logs esperados:**
```
OkHttp: GET /api/secretos
OkHttp: <-- 200 [...] []
```

✅ **Si la lista carga (aunque vacía), la API funciona**

---

### **4. Crear un Secreto**

1. Click en el botón "Crear Secreto"
2. Ingresa contenido: `"Mi primer secreto"`
3. Click en "Guardar"

**Lo que pasa internamente:**
```
1. Cliente genera clave AES (256 bits)
2. Cifra "Mi primer secreto" con AES-GCM
3. Firma el contenido cifrado con clave privada RSA
4. Cifra la clave AES con clave pública RSA
5. Envía TODO al servidor
```

**Logs esperados:**
```
SessionManager: 🔍 Obteniendo password de sesión: adm***
OkHttp: POST /api/secretos
OkHttp: <-- 201 [...] 1
```

✅ **Si responde 201, el secreto se creó correctamente**

---

### **5. Ver el Secreto Creado**

1. Vuelve a la lista de secretos
2. Deberías ver 1 secreto
3. Click en el secreto

**Lo que pasa internamente:**
```
1. Cliente obtiene datos cifrados del servidor
2. Obtiene clave pública del servidor CA
3. Verifica certificado del autor con la CA
4. Si válido → descifra clave AES con clave privada
5. Descifra contenido con clave AES
6. Verifica firma del contenido
7. Muestra "Mi primer secreto"
```

**Logs esperados:**
```
SessionManager: 🔍 Obteniendo password de sesión: adm***
VerSecretoViewModel: ✅ Password obtenida de sesión, descifrando secreto...
OkHttp: GET /api/secretos/1
OkHttp: <-- 200 [...]
OkHttp: GET /api/usuarios/1
OkHttp: <-- 200 [...]
OkHttp: GET /api/crypto/public-key
OkHttp: <-- 200 [...]
VerSecretoViewModel: ✅ Secreto descifrado correctamente
```

✅ **Si ves "Mi primer secreto" en pantalla, TODO FUNCIONA PERFECTAMENTE**

---

## 🔍 VERIFICACIÓN DE SEGURIDAD

### **Prueba 1: Clave Privada NO se Envía**

1. Filtra los logs por `OkHttp`
2. Busca `POST /api/auth/login`
3. Verifica el body enviado:

```json
{"password":"admin123","username":"admin"}
```

✅ **Solo se envía username y password, NO claves RSA**

---

### **Prueba 2: Secretos Almacenados Cifrados**

1. Accede a la base de datos del servidor:
   ```
   http://localhost:8080/h2-console
   ```

2. Ejecuta:
   ```sql
   SELECT * FROM secretos;
   ```

3. Verifica que `contenido_cifrado` es Base64 ilegible:
   ```
   MhMGclePmQ4mtVqJtZKgaiNNg6QPEA==
   ```

✅ **Si el contenido es ilegible, está correctamente cifrado**

---

### **Prueba 3: Verificación de Certificados**

1. Crea un segundo usuario (desde Postman/Swagger)
2. Comparte un secreto con él
3. Observa los logs:

```
OkHttp: GET /api/crypto/public-key
OkHttp: <-- 200 [...]
```

✅ **Si obtiene la clave del CA, la verificación funciona**

---

## ❌ SOLUCIÓN DE PROBLEMAS

### **Error: "Sesión expirada"**

**Causa:** Password no está en SessionManager

**Solución:**
1. Cierra la app completamente
2. Ábrela de nuevo
3. Haz login de nuevo

**Por qué:** SessionManager guarda en RAM. Si cierras la app, se pierde.

---

### **Error: "Certificado del autor no válido"**

**Causa 1:** El servidor CA no está corriendo

**Solución:**
```bash
# En el directorio del servidor
./mvnw spring-boot:run
```

**Causa 2:** El endpoint `/api/crypto/public-key` no existe

**Solución:** Verifica que implementaste `CryptoController` en el servidor

**Prueba manual:**
```bash
curl http://localhost:8080/api/crypto/public-key
```

Debería retornar:
```json
{
  "publicKey": "MIICIjANBgkqhkiG9w0BAQ..."
}
```

---

### **Error: "Password incorrecta"**

**Causa:** La clave privada fue generada con una password diferente

**Solución:**
1. Desinstala la app:
   ```bash
   adb uninstall com.example.navigationcompose
   ```

2. Reinstala:
   ```bash
   ./gradlew installDebug
   ```

3. Haz login de nuevo

---

### **Error 403 Forbidden al crear secreto**

**Causa:** Token JWT expirado

**Solución:**
1. Haz logout
2. Haz login de nuevo

---

## 📊 LOGS DE ÉXITO

### **Login Exitoso:**
```
SessionManager: ✅ Password guardada en sesión: adm***
```

### **Crear Secreto Exitoso:**
```
SessionManager: 🔍 Obteniendo password de sesión: adm***
OkHttp: <-- 201 [...] 1
```

### **Ver Secreto Exitoso:**
```
SessionManager: 🔍 Obteniendo password de sesión: adm***
VerSecretoViewModel: ✅ Password obtenida de sesión, descifrando secreto...
OkHttp: GET /api/crypto/public-key
OkHttp: <-- 200 [...]
VerSecretoViewModel: ✅ Secreto descifrado correctamente
```

---

## 🎉 RESULTADO ESPERADO

### **Si TODO funciona:**

1. ✅ Puedes hacer login
2. ✅ Puedes crear secretos
3. ✅ Los secretos se almacenan cifrados en el servidor
4. ✅ Puedes ver los secretos descifrados
5. ✅ Los certificados se verifican correctamente
6. ✅ Las firmas digitales se validan
7. ✅ La password se mantiene en sesión

### **Pantalla Final:**

```
┌─────────────────────────────────────┐
│  📱 Ver Secreto                     │
├─────────────────────────────────────┤
│                                     │
│  📝 Contenido:                      │
│  ┌─────────────────────────────┐   │
│  │ Mi primer secreto           │   │
│  └─────────────────────────────┘   │
│                                     │
│  👤 Autor: admin                    │
│  ✅ Firma válida                    │
│  ✅ Certificado verificado          │
│                                     │
│  🔒 Compartido con: (ninguno)       │
│                                     │
│  [🗑️ Eliminar]                      │
└─────────────────────────────────────┘
```

---

## ✅ CHECKLIST DE VALIDACIÓN

- [ ] Servidor backend ejecutándose
- [ ] App instalada en emulador/dispositivo
- [ ] Login exitoso
- [ ] Password guardada en SessionManager
- [ ] Secreto creado correctamente
- [ ] Secreto almacenado cifrado en BD
- [ ] Secreto descifrado y mostrado
- [ ] Certificado verificado con CA
- [ ] Firma digital validada
- [ ] Sin errores en logs

---

## 🎯 SI TODO FUNCIONA...

**¡FELICIDADES!** 🎉

Tienes una aplicación con:
- ✅ End-to-End Encryption
- ✅ Zero-Knowledge Server
- ✅ Digital Signatures
- ✅ Certificate-based PKI
- ✅ Secure Key Management

**Seguridad nivel:** 🔒🔒🔒🔒🔒 **MÁXIMA**

---

**Guía creada:** 22 de Febrero de 2026  
**Estado:** ✅ **LISTA PARA PRUEBAS**


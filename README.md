# Rutinas Saludables – README

Backend **Spring Boot + GraphQL** para gestionar hábitos, rutinas, progreso, guías, recordatorios y usuarios con **JWT**, permisos por **módulo**, rol de **coach** y **auditor** (solo lectura).

---

## 🚀 Características principales

- **GraphQL** (Queries/Mutations con resolvers)
- **Autenticación JWT** (stateless) y **autorización** por módulo:
  - Módulos: `HABITS`, `ROUTINES`, `PROGRESS`, `GUIDES`, `REMINDERS`, `USERS`
  - Niveles: `CONSULT` (R) / `MUTATE` (RW)
- **Self-service**:
  - Usuario normal puede **gestionar lo suyo** (p. ej. completar hábitos, marcar favoritos) sin permisos extra.
  - Para tocar datos de **otros** o **catálogo** ⇒ se exige permiso del módulo.
- **Roles especiales**:
  - **Auditor**: **solo lectura** (bloqueo global de mutaciones).
- **Estadísticas mensuales** por categoría (SQL nativo + DTOs)
- **Recuperación de contraseña** con token temporal
- **Rate limiting** en login, **sanitización de input**

---

## 🧱 Arquitectura por capas

- **models (Entities)**: mapeo JPA de tablas (`User`, `Habitactivity`, `Completedactivity`, `Routine`, etc.).
- **repositories**: acceso a BD (Spring Data JPA / consultas nativas cuando aplica).
- **dtos**: objetos limpios de entrada/salida (evitan exponer entidades crudas).
- **services**: lógica de negocio + seguridad (`SecurityUtils`) + mapeos **Entity ⇆ DTO**.
- **resolvers**: “controladores” GraphQL; reciben args, llaman al service y devuelven DTOs.



---

## 🔐 Seguridad

- **JWT** se genera en login y lleva:
  - `sub` (userId), `authorities` (ej. `MOD:HABITS:RW`), banderas (`isAuditor`, `isCoach`), `exp`
- **Auditor**: `forbidAuditorWrites()` en services ⇒ **no puede mutar nada**, ni siquiera self-service
- **Self-service**:
  - `CompletedActivity`: el usuario puede crear/borrar **sus** completados; para otros ⇒ `PROGRESS:MUTATE`
  - `FavoriteHabit`: igual lógica (lo suyo sí; terceros ⇒ `HABITS:MUTATE`)
  - Queries “My…” no exigen `CONSULT` (solo estar logueado)
- **Catálogo** (crear hábitos, guías, etc.): requiere `…:MUTATE`
- **Coach**: `isCoach=true`, vistas de coachees y asignación `assignedCoachId`

---

## 🗃️ Base de datos (resumen tablas)

- `user`: cuentas, flags `is_auditor`, `is_coach`, `assigned_coach_id`
- `user_module_permission`: **intermedia** usuario ↔ módulo ↔ permiso (CONSULT/MUTATE)
- `habitactivity`: catálogo de hábitos
- `routine` + `routine_habit` (N:M): rutinas y sus hábitos (con orden y hora objetivo)
- `favorite_habit`: hábitos marcados como favoritos por usuario (único por user+habit)
- `reminder`: recordatorios (hora/frecuencia) por usuario/hábito
- `completedactivity`: progreso (fecha/hora/notas) por usuario/hábito/(rutina opcional)
- `guide` + `guide_habit` (N:M): guías y hábitos recomendados
- `password_reset_token`: tokens temporales para reset de contraseña

> Índices clave para rendimiento:  
> `completedactivity (user_id, date)`, `(date)`, `(habit_id)`, `habitactivity (category)`, `routine_habit (routine_id, order_in_routine, habit_id)`.

---

## ⚙️ Configuración

**`src/main/resources/application.properties`**
```properties
spring.application.name=rutinas-saludables

# DB
spring.datasource.url=jdbc:mariadb://localhost:3306/rutinasdb
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect

# JWT
jwt.secret=super-ultra-mega-secretooooo-1234567890-ABCDEF
jwt.expiration=3600000   # 1 hora (ms)

# GraphQL / GraphiQL
spring.graphql.path=/graphql
spring.graphql.graphiql.enabled=true
spring.graphql.graphiql.path=/graphiql
spring.graphql.websocket.path=/graphql
spring.graphql.cors.allowed-origins=*
spring.graphql.cors.allowed-methods=GET,POST
```

> La expiración real del token la fija `jwt.expiration` y se aplica en `JwtService#setExpiration(...)`.

---

## ▶️ Cómo ejecutar

```bash
# Windows
.\mvnd clean spring-boot:run

# Linux/Mac
./mvnw -U clean spring-boot:run
```

Abre **GraphiQL**: `http://localhost:8080/graphiql`

---

## 🔑 Primeros pasos (GraphQL, copy-paste)

### 1) Registrar y loguear
```graphql
mutation {
  register(name:"Admin", email:"admin@example.com", password:"Admin123!") { id name email }
}
mutation {
  login(email:"admin@example.com", password:"Admin123!")
}
```
Copia el JWT devuelto en **HTTP Headers**:
```json
{ "Authorization": "Bearer <TU_TOKEN>" }
```


## 🧪 Tests

```bash
# Ejecutar pruebas
./mvnw -U clean test
```

Incluyen services/resolvers de hábitos, rutinas, progreso, favoritos, recordatorios, guías, usuarios y estadísticas.

---



---

## 🛠️ Solución de problemas

- **FORBIDDEN al crear hábito**: necesitas `HABITS:MUTATE` o usar token de admin.
- **No veo datos “globales”**: para ver de otros/global, necesitas `…:CONSULT`.
- **Auditor no puede escribir**: es correcto; está bloqueado aunque tenga permisos.
- **Token no funciona**: revisa expiración (`jwt.expiration`) o vuelve a hacer login.

---

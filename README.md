# API de Mesa de Ayuda (Helpdesk) con SLA

API REST para gestión de tickets de soporte técnico con autenticación JWT, control de acceso basado en roles y cálculo automático de SLA.

---

## Descripción

Sistema backend que permite a usuarios reportar incidencias técnicas y al equipo de soporte gestionarlas. Cada ticket tiene una prioridad que define un SLA (tiempo máximo de respuesta) calculado automáticamente por el servidor. La autenticación se maneja con JWT y refresh token persistido en base de datos.

---

## Tecnologías

- Java 17
- Spring Boot 3.3.2
- Spring Security 6
- Spring Data JPA
- JJWT 0.12.6
- H2 Database
- BCrypt
- Maven
- Lombok

---

## Requisitos previos

- JDK 17
- Maven 3.6+
- Cliente HTTP (Postman, Thunder Client, Insomnia, curl)

---

## Instalación y ejecución

```bash
# Clonar el repositorio
git clone https://github.com/dzaplopez-dot/API-de-Mesa-de-Ayuda-Helpdesk-con-SLA---Spring-Boot-JWT.git
cd API-de-Mesa-de-Ayuda-Helpdesk-con-SLA---Spring-Boot-JWT

# Compilar
mvn clean compile

# Ejecutar
mvn spring-boot:run
La aplicación estará disponible en: http://localhost:8080

Consola H2

URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:helpdesk
Usuario: sa
Contraseña: (vacío)
Estructura del proyecto

text
src/main/java/com/example/helpdesk/
├── config/
│   ├── JwtConfig.java
│   └── SecurityConfig.java
├── controller/
│   ├── AdminController.java
│   ├── AuthController.java
│   └── TicketController.java
├── dto/
│   ├── AuthRequest.java
│   ├── AuthResponse.java
│   ├── CambiarEstadoRequest.java
│   ├── RefreshTokenRequest.java
│   ├── RegisterRequest.java
│   ├── TicketRequest.java
│   └── TicketResponse.java
├── model/
│   ├── entity/
│   │   ├── RefreshToken.java
│   │   ├── Ticket.java
│   │   └── Usuario.java
│   └── enums/
│       ├── Estado.java
│       ├── Prioridad.java
│       └── Rol.java
├── repository/
│   ├── RefreshTokenRepository.java
│   ├── TicketRepository.java
│   └── UsuarioRepository.java
├── security/
│   ├── CustomUserDetailsService.java
│   ├── JwtAuthenticationFilter.java
│   └── JwtTokenProvider.java
├── service/
│   ├── AuthService.java
│   └── TicketService.java
└── HelpdeskApplication.java
Endpoints

Autenticación (públicos)

Método	Ruta	Descripción
POST	/api/auth/registro	Registrar usuario
POST	/api/auth/login	Iniciar sesión
POST	/api/auth/refresh	Renovar access token
GET	/api/ping	Verificar estado de la API
Tickets (autenticado)

Método	Ruta	Descripción	Rol
POST	/api/tickets	Crear ticket	Cualquier autenticado
GET	/api/tickets/mios	Listar tickets del usuario	Cualquier autenticado
GET	/api/tickets/{id}	Obtener ticket por ID	Dueño / SOPORTE / ADMIN
Tickets (SOPORTE / ADMIN)

Método	Ruta	Descripción
GET	/api/tickets	Listar todos los tickets
PATCH	/api/tickets/{id}/estado	Cambiar estado de un ticket
GET	/api/tickets/vencidos	Listar tickets vencidos
Administración (ADMIN)

Método	Ruta	Descripción
POST	/api/admin/soporte?email={email}	Ascender usuario a SOPORTE
Ejemplos de peticiones

Registrar usuario

http
POST /api/auth/registro
Content-Type: application/json

{
    "nombre": "Juan Pérez",
    "email": "juan@mail.com",
    "password": "password123"
}
Respuesta (201 Created):

json
{
    "mensaje": "Usuario registrado exitosamente",
    "email": "juan@mail.com"
}
Iniciar sesión

http
POST /api/auth/login
Content-Type: application/json

{
    "email": "juan@mail.com",
    "password": "password123"
}
Respuesta (200 OK):

json
{
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tipo": "Bearer",
    "expiraEn": 900
}
Crear ticket

http
POST /api/tickets
Content-Type: application/json
Authorization: Bearer <accessToken>

{
    "titulo": "Problema con la impresora",
    "descripcion": "La impresora no imprime documentos",
    "prioridad": "MEDIA"
}
Respuesta (201 Created):

json
{
    "id": 1,
    "titulo": "Problema con la impresora",
    "descripcion": "La impresora no imprime documentos",
    "prioridad": "MEDIA",
    "estado": "ABIERTO",
    "creadoEn": "2026-08-19T16:49:01.716306",
    "slaVenceEn": "2026-08-20T16:49:01.716306",
    "creadoPor": "juan@mail.com",
    "vencido": false
}
Cambiar estado de ticket (SOPORTE / ADMIN)

http
PATCH /api/tickets/1/estado
Content-Type: application/json
Authorization: Bearer <accessToken>

{
    "estado": "EN_PROCESO"
}
Respuesta (200 OK):

json
{
    "id": 1,
    "titulo": "Problema con la impresora",
    "estado": "EN_PROCESO",
    ...
}
Ascender usuario a SOPORTE (ADMIN)

http
POST /api/admin/soporte?email=usuario@mail.com
Authorization: Bearer <accessToken>
Respuesta (200 OK):

json
{
    "mensaje": "Usuario usuario@mail.com ascendido a SOPORTE exitosamente",
    "email": "usuario@mail.com",
    "nuevoRol": "SOPORTE"
}
Reglas de negocio

SLA (Service Level Agreement)

Al crear un ticket, el servidor calcula automáticamente slaVenceEn sumando horas a la fecha de creación según la prioridad:

Prioridad	SLA
ALTA	4 horas
MEDIA	24 horas
BAJA	72 horas
Estado inicial

El estado del ticket siempre inicia como ABIERTO, independientemente de lo que envíe el cliente.

Tickets vencidos

Un ticket se considera vencido cuando su estado no es RESUELTO y la fecha actual supera slaVenceEn. El campo vencido se calcula automáticamente en la respuesta.

Roles y permisos

Rol	Descripción
USUARIO	Crear tickets, ver solo sus tickets
SOPORTE	Ver todos los tickets, cambiar estados, ver vencidos
ADMIN	Todos los permisos, ascender usuarios a SOPORTE
Matriz de permisos


Opción elegida: A - Persistido en base de datos

La entidad RefreshToken almacena cada token con su usuario, fecha de expiración (7 días) y estado revocado.

Motivación:

Permite revocar tokens en logout
Control total sobre tokens activos
Mayor seguridad ante filtraciones
Opción más didáctica para el taller
Flujo:

Login → Se genera y guarda refresh token en BD
Logout → Token marcado como revocado = true
Refresh con token revocado → 401 Unauthorized
Manejo de errores

La API maneja excepciones globalmente y devuelve respuestas consistentes.

Formato de error:

json
{
    "error": "Descripción del error"
}
Códigos HTTP:

Código	Descripción
200	OK
201	Creado
400	Datos inválidos
401	No autenticado
403	Rol insuficiente
404	No encontrado
Ejemplos:

Situación	Código	Mensaje
Credenciales incorrectas	401	Credenciales inválidas
Email ya registrado	400	El email ya está registrado
Token expirado	401	Refresh Token expirado
Token revocado	401	Refresh Token revocado
Rol insuficiente	403	No tienes permiso para ver este ticket
Ticket no encontrado	404	Ticket no encontrado
Colección de Postman

El repositorio incluye postman_collection.json con todos los endpoints.

Importar:

Abrir Postman
Import → Seleccionar postman_collection.json
Ejecutar las pruebas
Pruebas incluidas:

Registro y login
Renovación de token
Logout y revocación
Creación de tickets con SLA
Listado de tickets (propios, todos, vencidos)
Cambio de estado
Administración de roles
Estado del proyecto

Completado. Todas las funcionalidades requeridas están implementadas y probadas.

Implementado

Modelo de datos completo
Autenticación JWT con refresh token
Control de acceso por roles
CRUD de tickets con SLA
Endpoints de administración
Mejoras futuras

Paginación en listado de tickets
Historial de cambios de estado
Estadísticas de SLA

Autor
Daniela Zapata Lopez



Licencia

Proyecto desarrollado con fines académicos como parte del curso de Backend del SENA.
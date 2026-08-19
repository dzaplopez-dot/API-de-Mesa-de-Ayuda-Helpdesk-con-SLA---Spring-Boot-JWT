API de Mesa de Ayuda (Helpdesk) con SLA

API REST para gestión de tickets de soporte técnico con autenticación JWT, control de acceso por roles y cálculo automático de SLA.

Descripción

Sistema backend que permite a usuarios reportar incidencias técnicas y al equipo de soporte gestionarlas. Cada ticket tiene una prioridad que define un SLA (tiempo máximo de respuesta) calculado automáticamente por el servidor.

Características

Autenticación JWT con refresh token persistido en base de datos
Control de acceso por roles (USUARIO, SOPORTE, ADMIN)
Creación de tickets con cálculo automático de SLA según prioridad
Listado de tickets propios y general
Cambio de estado de tickets (SOPORTE/ADMIN)
Tickets vencidos identificados automáticamente
Administración de roles (ascender a SOPORTE solo ADMIN)
Tecnologías

Java 17
Spring Boot 3.3.2
Spring Security 6
Spring Data JPA
JJWT 0.12.6
H2 Database (en memoria)
BCrypt
Maven
Lombok

Estructura del Proyecto

text
src/main/java/com/example/helpdesk/
├── config/
│   ├── JwtConfig.java          # Configuración JWT
│   └── SecurityConfig.java     # Configuración Spring Security
├── controller/
│   ├── AdminController.java    # Endpoints de administración
│   ├── AuthController.java     # Endpoints de autenticación
│   └── TicketController.java   # Endpoints de tickets
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

Requisitos Previos

Java 17
Maven 3.6+
Instalación y Ejecución

bash
# Clonar repositorio
git clone <repository-url>
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
API Endpoints

Autenticación (Públicos)

Método	Ruta	Descripción
POST	/api/auth/registro	Registrar usuario
POST	/api/auth/login	Iniciar sesión
POST	/api/auth/refresh	Renovar access token
GET	/api/ping	Verificar estado
Tickets (Autenticado)

Método	Ruta	Descripción	Rol
POST	/api/tickets	Crear ticket	Cualquier autenticado
GET	/api/tickets/mios	Listar tickets propios	Cualquier autenticado
GET	/api/tickets/{id}	Obtener ticket por ID	Dueño/SOPORTE/ADMIN
Tickets (SOPORTE/ADMIN)

Método	Ruta	Descripción
GET	/api/tickets	Listar todos los tickets
PATCH	/api/tickets/{id}/estado	Cambiar estado
GET	/api/tickets/vencidos	Listar tickets vencidos
Administración (ADMIN)

Método	Ruta	Descripción
POST	/api/admin/soporte?email=	Ascender usuario a SOPORTE
Regla de Negocio: SLA

Al crear un ticket, el servidor calcula slaVenceEn automáticamente:

Prioridad	SLA
ALTA	4 horas
MEDIA	24 horas
BAJA	72 horas
El estado siempre inicia como ABIERTO.

Roles

Rol	Descripción
USUARIO	Crear tickets, ver sus tickets
SOPORTE	Ver todos los tickets, cambiar estados, ver vencidos
ADMIN	Todos los permisos, ascender usuarios a SOPORTE
Estado del Proyecto

Completo. Todas las funcionalidades requeridas están implementadas y funcionando.

Implementado

Modelo de datos completo
Autenticación JWT con refresh token
Control de acceso por roles
CRUD de tickets con SLA
Endpoints de administración
Pendiente

Paginación en listado de tickets
Historial de cambios de estado
Estadísticas de SLA
Licencia

Proyecto educativo. Sin licencia de uso comercial.
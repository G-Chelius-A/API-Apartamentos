# API Apartamentos (Alquiler de Apartamentos y Habitaciones)

API REST y frontend Thymeleaf para la administración y alquiler de apartamentos, casas y habitaciones: gestión de unidades rentables, casas, habitaciones, disponibilidad, reservaciones, pagos, reembolsos, reseñas, mensajes, personas y organizaciones. Desarrollada con Spring Boot 4 y MySQL.

## Autor

**Gerardo Yeray Chelius Altamirano |**  
7°B de Ingeniería en Sistemas Computacionales

---

## Tecnologías utilizadas

| Tecnología | Versión | Rol |
|---|---|---|
| Java | 21 | Lenguaje principal |
| Spring Boot | 4.1.1 | Framework base |
| Spring Data JPA | — | Capa de persistencia (ORM) con Hibernate 7 |
| Spring Web MVC | — | API REST |
| Bean Validation | — | Validación de modelos y datos |
| MySQL | 8+ | Base de datos relacional |
| MySQL Connector/J | — | Driver JDBC |
| Maven | 3.9+ | Gestión de dependencias y build |

---

## Requisitos previos

- **Java 21** instalado y en el `PATH`.
- **MySQL 8+** en ejecución en el puerto `3306` con la base de datos `apartamentos_blue` creada.
- **Maven 3.9+** (o usar el wrapper `mvnw.cmd` / `./mvnw` incluido).

---

## Configuración de variables de entorno

Las credenciales de acceso a la base de datos se gestionan mediante un archivo **`.env`** en la raíz del proyecto. Este archivo **nunca** se sube al repositorio (está excluido por `.gitignore`).

### 1. Crear el archivo `.env`

Copia la plantilla incluida y edítala con tus credenciales:

```bash
# Windows
copy .env.example .env

# macOS / Linux
cp .env.example .env
```

### 2. Editar `.env`

```env
# Base de datos MySQL
DB_URL=jdbc:mysql://127.0.0.1:3306/apartamentos_blue?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=tu_usuario
DB_PASSWORD=tu_contraseña
```

> Las variables `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` son leídas directamente por `application.properties` usando la sintaxis `${VARIABLE}`.

### `application.properties` (referencia)

```properties
spring.application.name=apartamentos

# Database Configuration — valores cargados desde .env
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=true

# Puerto del servidor
server.port=1501
```

---

## Instalación y ejecución

### 1. Clonar el repositorio y situarse en la carpeta

```bash
git clone https://github.com/G-Chelius-A/API-Apartamentos
cd API-Apartamentos
```

### 2. Crear la base de datos en MySQL

```sql
CREATE DATABASE apartamentos_blue CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configurar el archivo `.env`

Sigue los pasos de la sección **Configuración de variables de entorno** indicados arriba.

### 4. Ejecutar la aplicación

**Con Maven Wrapper (recomendado):**

```bash
# Windows
mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

**Con Maven instalado:**

```bash
mvn spring-boot:run
```

La aplicación se levanta en: **`http://localhost:1501`**

> Al iniciar, Hibernate ejecuta `ddl-auto=update`, por lo que las tablas se crean y sincronizan automáticamente.

---


## Interfaz web (Frontend Thymeleaf & Bootstrap)

La aplicación integra una interfaz gráfica completa desarrollada con **Thymeleaf y Bootstrap 5**, compartiendo el mismo proceso y puerto (`1501`) que el backend REST:

Acceso principal desde el navegador: **`http://localhost:1501/vista`**

| Ruta | Descripción | Operaciones funcionales |
|---|---|---|
| `/vista` | Panel principal (Dashboard) | Métricas en tiempo real, ingresos, ocupación y accesos rápidos |
| `/vista/unidades` | Gestión de unidades rentables | Listado general, formulario de alta/edición y eliminación |
| `/vista/casas` | Gestión de casas | Consulta de datos arquitectónicos, registro y edición |
| `/vista/habitaciones` | Distribución de habitaciones | Listado por casa, alta de habitaciones según tipo |
| `/vista/disponibilidades` | Calendario de disponibilidad | Consulta de periodos, precios especiales y alta de fechas |
| `/vista/imagenes` | Galería multimedia | Visualización y vinculación de imágenes a unidades |
| `/vista/reservaciones` | Administración de reservaciones | Registro con cálculo automático de precio, cambio de estado y borrado |
| `/vista/pagos` | Control de pagos | Listado de pagos, registro contra saldo adeudado y eliminación |
| `/vista/reembolsos` | Gestión de reembolsos | Solicitudes para reservas canceladas y avance de estado |
| `/vista/usuarios` | Cuentas de usuario | Listado, alta y actualización de cuentas |
| `/vista/personas` | Perfiles de persona física | Registro y consulta de datos personales vinculados 1:1 |
| `/vista/organizaciones` | Perfiles fiscales y corporativos | Registro y consulta de empresas y personas morales |
| `/vista/resenas` | Calificaciones y reseñas | Opiniones para reservaciones completadas con desglose 1-10 |
| `/vista/mensajes` | Mensajería interna | Envío y consulta de comunicaciones entre usuarios |

---

## Endpoints de la API REST

### Unidades Rentables — `/api/unidades`

| Método | Ruta | Descripción | Código de éxito |
|---|---|---|---|
| `GET` | `/api/unidades` | Lista todas las unidades rentables | `200 OK` |
| `POST` | `/api/unidades` | Registra una nueva unidad rentable | `201 Created` |
| `GET` | `/api/unidades/{id}` | Obtiene una unidad por ID | `200 OK` |
| `PUT` | `/api/unidades/{id}` | Actualiza datos de la unidad | `200 OK` |
| `DELETE` | `/api/unidades/{id}` | Elimina una unidad por ID | `204 No Content` |

---

### Casas — `/api/casas`

| Método | Ruta | Descripción | Código de éxito |
|---|---|---|---|
| `GET` | `/api/casas` | Lista todas las casas | `200 OK` |
| `POST` | `/api/casas` | Registra una casa asociada a una unidad | `201 Created` |
| `GET` | `/api/casas/{id}` | Obtiene casa por ID de unidad | `200 OK` |
| `PUT` | `/api/casas/{id}` | Actualiza características de la casa | `200 OK` |
| `DELETE` | `/api/casas/{id}` | Elimina una casa | `204 No Content` |

---

### Habitaciones — `/api/habitaciones`

| Método | Ruta | Descripción | Código de éxito |
|---|---|---|---|
| `GET` | `/api/habitaciones` | Lista todas las habitaciones | `200 OK` |
| `POST` | `/api/habitaciones` | Registra una habitación en una casa | `201 Created` |
| `GET` | `/api/habitaciones/{id}` | Obtiene habitación por ID | `200 OK` |
| `PUT` | `/api/habitaciones/{id}` | Actualiza datos de la habitación | `200 OK` |
| `DELETE` | `/api/habitaciones/{id}` | Elimina una habitación | `204 No Content` |

---

### Disponibilidades — `/api/disponibilidades`

| Método | Ruta | Descripción | Código de éxito |
|---|---|---|---|
| `GET` | `/api/disponibilidades` | Lista todos los periodos de disponibilidad | `200 OK` |
| `POST` | `/api/disponibilidades` | Registra un periodo de disponibilidad | `201 Created` |
| `GET` | `/api/disponibilidades/{id}` | Obtiene disponibilidad por ID | `200 OK` |
| `PUT` | `/api/disponibilidades/{id}` | Actualiza un periodo de disponibilidad | `200 OK` |
| `DELETE` | `/api/disponibilidades/{id}` | Elimina un periodo de disponibilidad | `204 No Content` |

---

### Reservaciones — `/api/reservaciones`

| Método | Ruta | Descripción | Código de éxito |
|---|---|---|---|
| `GET` | `/api/reservaciones` | Lista todas las reservaciones | `200 OK` |
| `POST` | `/api/reservaciones` | Registra una nueva reservación con validación | `201 Created` |
| `GET` | `/api/reservaciones/{id}` | Obtiene una reservación por ID | `200 OK` |
| `PUT` | `/api/reservaciones/{id}` | Actualiza datos de la reservación | `200 OK` |
| `PATCH` | `/api/reservaciones/{id}/estado` | Cambia el estado de la reservación | `200 OK` |
| `DELETE` | `/api/reservaciones/{id}` | Elimina una reservación | `204 No Content` |

**Valores válidos para `estado`:** `pendiente`, `confirmada`, `en_curso`, `completada`, `cancelada`, `no_presento`

---

### Pagos — `/api/pagos`

| Método | Ruta | Descripción | Código de éxito |
|---|---|---|---|
| `GET` | `/api/pagos` | Lista todos los pagos | `200 OK` |
| `POST` | `/api/pagos` | Registra un pago vinculado a una reservación | `201 Created` |
| `GET` | `/api/pagos/{id}` | Obtiene un pago por ID | `200 OK` |
| `PUT` | `/api/pagos/{id}` | Actualiza datos del pago | `200 OK` |
| `DELETE` | `/api/pagos/{id}` | Elimina un pago | `204 No Content` |
| `GET` | `/api/pagos/reservacion/{idReservacion}` | Lista pagos de una reservación | `200 OK` |
| `GET` | `/api/pagos/reservacion/{idReservacion}/pagada` | Verifica si la reservación está saldada | `200 OK` |

**Valores válidos para `metodo`:** `credito`, `debito`, `paypal`, `transferencia`  
**Valores válidos para `estado`:** `pendiente`, `aceptado`, `rechazado`, `cancelado`, `reembolso`

---

### Reembolsos — `/api/reembolsos`

| Método | Ruta | Descripción | Código de éxito |
|---|---|---|---|
| `GET` | `/api/reembolsos` | Lista todos los reembolsos | `200 OK` |
| `POST` | `/api/reembolsos` | Registra un reembolso directo | `201 Created` |
| `POST` | `/api/reservaciones/{id}/reembolso` | Registra reembolso de una reservación cancelada | `201 Created` |
| `GET` | `/api/reembolsos/{id}` | Obtiene un reembolso por ID | `200 OK` |
| `PUT` | `/api/reembolsos/{id}` | Actualiza monto de reembolso | `200 OK` |
| `PATCH` | `/api/reembolsos/{id}/estado` | Actualiza estado (`aprobado`, `procesado`, etc.) | `200 OK` |
| `DELETE` | `/api/reembolsos/{id}` | Elimina un reembolso | `204 No Content` |
| `GET` | `/api/pagos/{idPago}/monto-reembolsable` | Consulta el saldo reembolsable de un pago | `200 OK` |

---

### Reseñas — `/api/resenas`

| Método | Ruta | Descripción | Código de éxito |
|---|---|---|---|
| `GET` | `/api/resenas` | Lista todas las reseñas | `200 OK` |
| `POST` | `/api/resenas` | Registra una reseña de reservación completada | `201 Created` |
| `GET` | `/api/resenas/{id}` | Obtiene una reseña por ID | `200 OK` |
| `PUT` | `/api/resenas/{id}` | Actualiza calificaciones o comentarios | `200 OK` |
| `DELETE` | `/api/resenas/{id}` | Elimina una reseña | `204 No Content` |
| `GET` | `/api/unidades/{id}/resenas` | Lista reseñas de una unidad rentable | `200 OK` |

---

### Mensajes — `/api/mensajes`

| Método | Ruta | Descripción | Código de éxito |
|---|---|---|---|
| `GET` | `/api/mensajes` | Lista todos los mensajes | `200 OK` |
| `POST` | `/api/mensajes` | Registra y envía un mensaje | `201 Created` |
| `GET` | `/api/mensajes/{id}` | Obtiene un mensaje por ID | `200 OK` |
| `PUT` | `/api/mensajes/{id}` | Actualiza un mensaje | `200 OK` |
| `DELETE` | `/api/mensajes/{id}` | Elimina un mensaje | `204 No Content` |
| `GET` | `/api/mensajes/remitente/{idUsuario}` | Filtra mensajes por remitente | `200 OK` |
| `GET` | `/api/mensajes/destinatario/{idUsuario}` | Filtra mensajes por destinatario | `200 OK` |
| `GET` | `/api/mensajes/reservacion/{idReservacion}` | Filtra mensajes por reservación | `200 OK` |

---

### Usuarios, Personas y Organizaciones

* **Usuarios (`/api/usuarios`)**: `GET`, `POST`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`
* **Personas (`/api/personas`)**: `GET`, `POST`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`
* **Organizaciones (`/api/organizaciones`)**: `GET`, `POST`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`

---

### Reportes — `/api/reportes`

| Método | Ruta | Descripción | Código de éxito |
|---|---|---|---|
| `GET` | `/api/reportes/ocupacion` | Total de reservaciones y desglose por estado | `200 OK` |
| `GET` | `/api/reportes/ingresos` | Monto acumulado y número de pagos aceptados | `200 OK` |
| `GET` | `/api/reportes/morosidad` | Deuda total acumulada y reservaciones morosas | `200 OK` |

---

## Ejemplos de peticiones y respuestas

### Registrar una unidad rentable

**`POST /api/unidades`**

```json
// Petición
{
  "propietario": { "idUsuario": 1 },
  "titulo": "Departamento céntrico con vista al parque",
  "descripcion": "Acogedor departamento de 2 habitaciones totalmente equipado.",
  "direccion": "Av. Reforma 123, Depto 4B",
  "ciudad": "Ciudad de México",
  "entidad": "CDMX",
  "codigoPostal": "06600",
  "pais": "México",
  "capacidadPersonas": 4,
  "comodidades": "Wifi, Cocina, Aire Acondicionado, Elevador",
  "normasUnidad": "No fumar, No fiestas",
  "petFriendly": true,
  "accesibilidad": true,
  "precioNoche": 1250.00
}
```

```json
// Respuesta — 201 Created
{
  "idUnidad": 1,
  "titulo": "Departamento céntrico con vista al parque",
  "descripcion": "Acogedor departamento de 2 habitaciones totalmente equipado.",
  "direccion": "Av. Reforma 123, Depto 4B",
  "ciudad": "Ciudad de México",
  "entidad": "CDMX",
  "codigoPostal": "06600",
  "pais": "México",
  "capacidadPersonas": 4,
  "comodidades": "Wifi, Cocina, Aire Acondicionado, Elevador",
  "normasUnidad": "No fumar, No fiestas",
  "petFriendly": true,
  "accesibilidad": true,
  "precioNoche": 1250.00,
  "fechaCreacion": "2026-10-13T02:00:00",
  "fechaActualizacion": "2026-10-13T02:00:00"
}
```

---

### Registrar una reservación

**`POST /api/reservaciones`**

```json
// Petición
{
  "propiedad": { "idUnidad": 1 },
  "cliente": { "idUsuario": 2 },
  "fechaEntrada": "2026-11-10",
  "fechaSalida": "2026-11-15",
  "numeroHuespedes": 2,
  "notas": "Llegada estimada a las 15:00 hrs"
}
```

```json
// Respuesta — 201 Created
{
  "idReservacion": 1,
  "propiedad": { "idUnidad": 1, "titulo": "Departamento céntrico con vista al parque" },
  "cliente": { "idUsuario": 2, "username": "juan_perez" },
  "fechaEntrada": "2026-11-10",
  "fechaSalida": "2026-11-15",
  "numeroHuespedes": 2,
  "precioTotal": 6250.00,
  "importeTotal": 6250.00,
  "codigoReserva": "COD-1791880000000",
  "estado": "pendiente",
  "notas": "Llegada estimada a las 15:00 hrs"
}
```

---

### Registrar un pago

**`POST /api/pagos`**

```json
// Petición
{
  "reservacion": { "idReservacion": 1 },
  "monto": 6250.00,
  "referenciaPago": "TXN-998877",
  "datosPago": "Pago con tarjeta débito terminación 4321",
  "metodo": "debito",
  "estado": "aceptado"
}
```

```json
// Respuesta — 201 Created
{
  "idPago": 1,
  "monto": 6250.00,
  "referenciaPago": "TXN-998877",
  "datosPago": "Pago con tarjeta débito terminación 4321",
  "metodo": "debito",
  "estado": "aceptado",
  "fechaPago": "2026-10-13T02:15:00"
}
```

---

## Manejo de errores

| Código HTTP | Situación |
|---|---|
| `400 Bad Request` | Datos inválidos, campos obligatorios faltantes o parámetros inconsistentes |
| `404 Not Found` | Recurso no encontrado para el identificador proporcionado |
| `409 Conflict` | Conflicto de reglas de negocio (traslapes de fechas, reservación duplicada o cambio de estado prohibido) |

---

## Estructura del proyecto

```
src/main/java/com/blue/apartamentos/
├── controllers/
│   ├── ApiExceptionHandler.java         # Manejador global de excepciones HTTP
│   ├── CasaController.java              # API REST — casas
│   ├── DisponibilidadController.java    # API REST — disponibilidad
│   ├── HabitacionController.java        # API REST — habitaciones
│   ├── MensajeController.java           # API REST — mensajes
│   ├── OrganizacionController.java      # API REST — organizaciones
│   ├── PagoController.java              # API REST — pagos
│   ├── PersonaController.java           # API REST — personas
│   ├── PropiedadImagenController.java   # API REST — imágenes de unidades
│   ├── ReembolsoController.java         # API REST — reembolsos
│   ├── ReporteController.java           # API REST — reportes
│   ├── ResenaController.java            # API REST — reseñas
│   ├── ReservacionController.java       # API REST — reservaciones
│   ├── UnidadController.java            # API REST — unidades rentables
│   └── UsuarioController.java           # API REST — usuarios
├── models/
│   ├── CasaModel.java
│   ├── DisponibilidadModel.java
│   ├── HabitacionModel.java
│   ├── MensajeModel.java
│   ├── OrganizacionModel.java
│   ├── PagoModel.java
│   ├── PersonaModel.java
│   ├── PropiedadImagenModel.java
│   ├── ReembolsoModel.java
│   ├── ResenaModel.java
│   ├── ReservacionModel.java
│   ├── UnidadRentableModel.java
│   └── UsuarioModel.java
├── repositories/
│   ├── ICasaRepository.java
│   ├── IDisponibilidadRepository.java
│   ├── IHabitacionRepository.java
│   ├── IMensajeRepository.java
│   ├── IOrganizacionRepository.java
│   ├── IPagoRepository.java
│   ├── IPersonaRepository.java
│   ├── IPropiedadImagenRepository.java
│   ├── IReembolsoRepository.java
│   ├── IResenaRepository.java
│   ├── IReservacionRepository.java
│   ├── IUnidadRentableRepository.java
│   └── IUsuarioRepository.java
└── services/
    ├── CasaService.java
    ├── DisponibilidadService.java
    ├── HabitacionService.java
    ├── MensajeService.java
    ├── OrganizacionService.java
    ├── PagoService.java
    ├── PersonaService.java
    ├── PropiedadImagenService.java
    ├── ReembolsoService.java
    ├── ReporteService.java
    ├── ResenaService.java
    ├── ReservacionService.java
    ├── UnidadRentableService.java
    └── UsuarioService.java
```

---

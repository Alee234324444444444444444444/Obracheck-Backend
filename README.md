
# 🏗️ ObraCheck – Aplicación para registro de obras

**ObraCheck** es una aplicación desarrollada para facilitar el control del seguimiento de actividades dentro de una obra de construcción. Permite gestionar trabajadores, registrar avances diarios, tomar asistencia, adjuntar fotos como evidencias y generar reportes automáticamente.

Este backend está construido con **Kotlin + Spring Boot** y sigue una **arquitectura por capas** (Controller, Service, Repository), lo que mejora la mantenibilidad del código y la separación de responsabilidades.

---

## 🧱 Arquitectura del backend

### 📂 Estructura del Proyecto

El backend sigue una arquitectura por capas con la siguiente estructura de paquetes:

```plaintext
com/
└── cconstruct/
    └── construction/
        ├── constants/          # Constantes del sistema (rutas, etc.)
        ├── controllers/        # Controladores REST (manejan los endpoints)
        ├── exceptions/         # Clases de excepciones personalizadas
        ├── mappers/            # Conversores entre entidades y DTOs
        ├── models/             # Modelos del dominio (datos)
        │   ├── entities/       # Entidades JPA mapeadas a la base de datos
        │   ├── requests/       # Clases para datos de entrada (POST, PUT)
        │   └── responses/      # Clases para datos de salida (GET)
        ├── repositories/       # Interfaces de acceso a base de datos
        ├── services/           # Lógica de negocio de la aplicación
        └── ConstructionApplication.kt  # Clase principal de arranque
```

---

## 🗃️ Estructura de la base de datos

**ObraCheck** utiliza una base de datos PostgreSQL llamada `constructionsdb`, que almacena toda la información relacionada con los usuarios, obras, trabajadores, avances y evidencias. Esta base de datos es esencial para el funcionamiento del backend y debe estar activa al momento de correr la aplicación.

El sistema maneja las siguientes tablas principales:

- **users**: Usuarios del sistema (administradores o encargados).
- **sites**: Obras o proyectos registrados.
- **workers**: Información de los trabajadores asignados a obras.
- **progress**: Registro de avances diarios de cada obra.
- **evidences**: Archivos o imágenes que sirven como evidencia del progreso.

**Relaciones principales:**
- Un `site` puede tener muchos `workers`.
- Un `worker` está asignado a un `site`.
- Un `progress` pertenece a un `site` y puede estar relacionado con un `worker`.
- Una `evidence` está asociada a un `progress`.

---

## 🔌 APIs utilizadas en el proyecto

- 🌐 **Spring Boot Starter Web** – para la API REST
- 🗃️ **Spring Data JPA** – para acceso a datos con PostgreSQL
- 🧾 **Jackson** – para manejo de JSON
- 🐳 **Docker Compose** – para contenerización de la base de datos
- 🐘 **PostgreSQL** – como motor de base de datos relacional
- 🧠 **DBeaver** – cliente GUI para administración de la base de datos

---

## 🔗 Endpoints principales – ObraCheck

| ⚙️ Método | 📍 Ruta             | 📄 Descripción                            |
|----------|---------------------|------------------------------------------|
| `POST`   | `/users`            | Crear nuevo usuario                      |
| `POST`   | `/sites`            | Crear nueva obra (site)                  |
| `POST`   | `/workers`          | Registrar nuevo trabajador               |
| `POST`   | `/progress`         | Registrar avance de obra                 |
| `POST`   | `/evidences`        | Subir evidencia (imagen o archivo)       |
| `GET`    | `/progress/{id}`    | Obtener avance por ID                    |
| `GET`    | `/evidences/{id}`   | Obtener evidencia por ID                 |
| `PUT`    | `/evidences/{id}`   | Actualizar evidencia existente           |
| `DELETE` | `/evidences/{id}`   | Eliminar evidencia registrada            |

---

##  ⚙️ Cómo ejecutar el proyecto

- 🐳 Tener **Docker Desktop** instalado y en ejecución
- 🐘 Tener **DBeaver** abierto con una conexión configurada a PostgreSQL
- 💻 Tener **Git** instalado

---

## 🛠️ Configuración en DBeaver

1. Abre **DBeaver**.
2. Crea una nueva conexión PostgreSQL.
3. Configura con los siguientes parámetros:

```
Host: localhost
Puerto: 6969
Base de datos: constructionsdb
Usuario: admin
Contraseña: admin
```

---

## 📦 Clonar el proyecto

Desde la terminal, ejecuta:

```bash
git clone https://github.com/Alee234324444444444444444444/integrador-m-vil.git
cd integrador-m-vil
```

---

## 🚀 Levantar el proyecto

1. Asegúrate de tener **Docker Desktop** activo.
2. En la raíz del proyecto, ejecuta:

```bash
docker-compose up
```

🧱 Esto levantará el contenedor de PostgreSQL necesario para el backend.

3. Abre otra terminal o IntelliJ IDEA y corre el backend con:

```bash
./gradlew bootRun
```

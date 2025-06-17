# ObraCheck – Aplicación para registro de obras

**ObraCheck** es una aplicación desarrollada para facilitar el control de seguridad y el seguimiento de actividades dentro de una obra de construcción. Permite gestionar trabajadores, registrar avances diarios, tomar asistencia, adjuntar fotos como evidencias y generar reportes automáticamente.

Este backend está construido con **Kotlin + Spring Boot** y sigue una **arquitectura por capas** (Controller, Service, Repository), lo que mejora la mantenibilidad del código y la separación de responsabilidades.

---

## Requisitos previos

- Tener **Docker Desktop** instalado y en ejecución
- Tener **DBeaver** abierto con una conexión configurada a PostgreSQL
- Tener **Git** instalado

---

## Configuración en DBeaver

1. Abre DBeaver.
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

## Clonar el proyecto

Desde la terminal, ejecuta:

```bash
git clone https://github.com/Alee234324444444444444444444/integrador-m-vil.git
cd integrador-m-vil
```

---

## Levantar el backend

Una vez dentro del proyecto:

1. Asegúrate de tener Docker Desktop abierto.
2. Luego en la raíz del proyecto, ejecuta:

```bash
docker-compose up
```

3. Cuando el contenedor esté corriendo, en IntelliJ IDEA buscar el archivo:

```
src/main/kotlin/com/cconstruct/construction/ConstructionApplication.kt
```

4. Presiona el botón ▶ de "Run" en la parte superior para arrancar manualmente la aplicación.

---

## Arquitectura del backend

Este backend utiliza una **arquitectura en capas**:

- `controller`: expone los endpoints
- `service`: contiene la lógica de negocio
- `repository`: se comunica con la base de datos
- `models`: define entidades, requests y responses
- `exceptions`: manejo centralizado de errores

---

## APIs utilizadas en el proyecto

- **Spring Boot Starter Web** – para la API REST
- **Spring Data JPA** – para acceso a datos con PostgreSQL
- **Jackson** – para manejo de JSON
- **Docker Compose** – para contenerización de la base de datos
- **PostgreSQL** – como motor de base de datos relacional
- **DBeaver** – cliente GUI para administración de la base de datos

---

---

## Endpoints principales – ObraCheck

| Método | Ruta               | Descripción                              |
|--------|--------------------|------------------------------------------|
| POST   | /users             | Crear nuevo usuario                      |
| POST   | /sites             | Crear nueva obra (site)                  |
| POST   | /workers           | Registrar nuevo trabajador               |
| POST   | /progress          | Registrar avance de obra                 |
| POST   | /evidences         | Subir evidencia (imagen o archivo)       |
| GET    | /progress/{id}     | Obtener avance por ID                    |
| GET    | /evidences/{id}    | Obtener evidencia por ID                 |
| PUT    | /evidences/{id}    | Actualizar evidencia existente           |
| DELETE | /evidences/{id}    | Eliminar evidencia registrada            |

---



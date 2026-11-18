# Sistema Academico UNIAJC

Proyecto de Sistema Academico desarrollado en Java con arquitectura MVC + Servicio + DAO y base de datos PostgreSQL en Supabase.

## Estructura del Proyecto
- `com.sistema.config`: Conexion a base de datos (`ConexionBD`) con `config.properties` y encriptacion de contrasenas (`SeguridadUtil`).
- `com.sistema.modelo`: Clases de las entidades y sus DAOs (`Estudiante`, `Docente`, `Materia`, `Grupo`, `InscripcionCurso`, `Usuario`).
- `com.sistema.servicio`: Validaciones y logica de negocio de cada entidad.
- `com.sistema.controlador`: Controladores que conectan las vistas con los servicios.
- `com.sistema.vista`: Interfaces graficas en Swing y menu principal.

## Usuarios de Prueba
- Admin: `admin` / `admin123`
- Docente: `cgomez` / `docente123`
- Estudiante: `johhang` / `estudiante123`

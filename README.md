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

## Asociaciones de cuentas y permisos

En Control de Usuarios, selecciona el rol y el registro academico correspondiente.
Una cuenta ESTUDIANTE requiere un estudiante existente; una cuenta DOCENTE requiere
un docente existente. ADMIN no debe tener ninguno de estos vinculos. Un registro
academico no puede estar vinculado a dos cuentas. Al editar el rol, selecciona el
nuevo vinculo antes de guardar.

Las cuentas antiguas sin asociacion deben corregirse desde la pantalla de usuarios
con una cuenta administradora. La aplicacion ya no deduce la identidad por el nombre
de usuario ni asigna el primer estudiante registrado. No se realiza ninguna migracion
automatica de las cuentas ni de los datos existentes.

Los docentes consultan y califican solo las inscripciones de sus propios grupos.
El boletin que consulta un docente muestra exclusivamente las materias que imparte.
Los estudiantes consultan sus propias inscripciones y no pueden asignarse notas.
La comprobacion del docente se incluye en el UPDATE de la calificacion.

## Verificacion

Ejecuta `mvn test` para las pruebas. Incluyen DAO simulados, consultas JDBC sobre
H2 temporal en modo PostgreSQL y pruebas de formularios Swing. No leen credenciales
ni se conectan a Supabase. Las pruebas Swing requieren un escritorio disponible.

Se comprueban el alta y edicion de cuentas, la conservacion de contrasena al editar,
el cambio de rol, la limpieza del formulario, los vinculos duplicados o inexistentes,
las cuentas sin vinculo, los listados por docente y el rechazo de notas ajenas.
El boletin tambien se limpia al refrescar si el docente deja de tener grupos.
H2 comprueba la logica SQL local; no sustituye una prueba de integracion con
PostgreSQL real en una base de desarrollo.

La clase de pruebas `com.sistema.soporte.VistaPreviaUsuarios` abre una vista previa
interactiva con datos ficticios en memoria. Se ejecuta con el classpath de pruebas,
despues de `mvn test-compile`; H2 es una dependencia exclusiva de pruebas y no se
incluye como base de datos de la aplicacion. Cerrar la vista descarta esos datos.

Antes del commit, revisa los cambios en VS Code. No incluyas `config.properties`:
contiene la configuracion privada de este equipo y ya estaba versionado en el
repositorio. Agregarlo a .gitignore por si solo no evita subir un archivo versionado.

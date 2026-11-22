# Sistema Académico UNIAJC

El Sistema Académico UNIAJC es un programa que permite organizar la información de estudiantes, docentes y asignaturas. Desde un mismo lugar se pueden registrar personas, crear grupos de clase, realizar matrículas y consultar las calificaciones.

Su propósito es facilitar el seguimiento de la actividad académica y permitir que cada persona acceda a las funciones que le corresponden: el administrador organiza la información, el docente registra las notas de sus grupos y el estudiante consulta su avance y matricula asignaturas.

## ¿Qué se puede hacer en el programa?

- Registrar y actualizar los datos de los estudiantes.
- Registrar docentes y su especialidad.
- Crear materias e indicar sus créditos.
- Organizar grupos con una materia, un docente, un aula y un horario.
- Matricular estudiantes en los grupos disponibles.
- Registrar calificaciones y consultar el boletín de notas.
- Cancelar matrículas conservando su historial.
- Crear cuentas de acceso y asignarles un perfil.

La información se guarda en una base de datos. Esto permite cerrar el programa y volver a consultar los registros al abrirlo nuevamente.

## ¿Quiénes pueden utilizarlo?

### Administrador

Es la persona encargada de organizar el sistema. Puede registrar estudiantes y docentes, crear materias y grupos, administrar las cuentas de acceso y revisar las matrículas y calificaciones. También puede cancelar matrículas y corregir los datos registrados.

### Docente

Puede consultar los estudiantes matriculados en sus grupos y registrar sus calificaciones. También puede consultar sus notas en el boletín. Su acceso se limita a los grupos y asignaturas que tiene a cargo.

### Estudiante

Puede matricular asignaturas y consultar sus propias notas. Su cuenta está relacionada con su registro de estudiante, por lo que no puede consultar la información de otros estudiantes ni modificar calificaciones.

## ¿Cómo funciona?

### 1. Iniciar sesión

Al abrir el programa aparece la ventana de ingreso. La persona escribe su usuario y contraseña y pulsa **Ingresar al Portal**.

Si los datos son correctos, se abre el menú principal. Las opciones cambian según el perfil de la cuenta: administrador, docente o estudiante.

Las cuentas las administra el administrador del sistema. Registrar a una persona como estudiante o docente no crea automáticamente su cuenta de acceso; ese paso se realiza en **Control de Usuarios y Roles**.

### 2. Organizar la información académica

Antes de realizar una matrícula, el administrador registra la información necesaria:

1. **Estudiante:** nombre, apellido y correo.
2. **Docente:** nombre y especialidad.
3. **Materia:** nombre y cantidad de créditos.
4. **Grupo:** materia que se va a dictar, docente encargado, aula y horario.

Una materia y un grupo tienen funciones diferentes. Por ejemplo, **Matemáticas** es la materia, mientras que un grupo indica quién la dicta, en qué aula y en qué horario.

### 3. Crear las cuentas de acceso

En **Control de Usuarios y Roles**, el administrador crea el usuario, establece una contraseña y elige su perfil.

Si la cuenta es de un estudiante o docente, también selecciona a qué persona pertenece. Así, cuando esa persona ingresa, el programa puede mostrarle la información que le corresponde.

### 4. Matricular una asignatura

El estudiante entra en **Matricular Asignaturas**, selecciona un grupo disponible y confirma la matrícula. El administrador también puede realizar este proceso desde **Matrículas y Calificaciones**, seleccionando al estudiante y al grupo.

La matrícula relaciona a un estudiante con un grupo de clase. No se permite matricular a la misma persona dos veces en el mismo grupo.

### 5. Registrar las calificaciones

El docente entra en **Registrar Calificaciones de mis Grupos**, selecciona una matrícula, escribe la nota y guarda el cambio.

Las notas van de **0,0 a 5,0** y se guardan con un decimal. El programa determina el resultado a partir de la nota guardada:

| Situación | Estado |
| --- | --- |
| Todavía no tiene nota | Matriculado |
| Nota igual o superior a 3,0 | Aprobado |
| Nota inferior a 3,0 | Reprobado |
| Matrícula cancelada por el administrador | Cancelado |

### 6. Consultar el boletín

En la opción de consulta de notas se muestran las asignaturas, las calificaciones y el estado de cada matrícula. También se presenta el total de créditos de las matrículas no canceladas y un promedio de las asignaturas calificadas.

El estudiante consulta su propia información. El docente consulta la información correspondiente a las asignaturas que imparte y el administrador puede revisar la de los estudiantes registrados.

### 7. Cancelar una matrícula

El administrador puede cancelar una matrícula desde **Matrículas y Calificaciones**. La matrícula permanece registrada con el estado **Cancelado**, de modo que se conserva el historial.

Una matrícula cancelada no se puede seguir calificando ni volver a crear para el mismo estudiante y grupo. Las cancelaciones no se incluyen en el cálculo del promedio ni en el total de créditos del boletín.

## Ejemplo de uso

Supongamos que Ana es una estudiante y Carlos es un docente:

1. El administrador registra a Ana y a Carlos.
2. Crea la materia **Programación**, con tres créditos.
3. Abre un grupo de Programación, asigna a Carlos y define el aula y el horario.
4. Crea las cuentas de Ana y Carlos, cada una vinculada con su registro.
5. Ana ingresa con su cuenta y se matricula en ese grupo.
6. Carlos ingresa con su cuenta y registra una nota de **4,2**.
7. Ana consulta su boletín y ve Programación con la nota **4,2** y el estado **Aprobado**.

## Uso de los formularios

En las pantallas de administración se encuentran campos para escribir los datos y una tabla con los registros guardados. Los nombres de los botones pueden variar según la pantalla, pero las acciones principales son:

- **Registrar o guardar:** agrega la información escrita en el formulario.
- **Actualizar:** guarda los cambios del registro seleccionado en la tabla.
- **Eliminar o retirar:** elimina un registro cuando no tiene información relacionada que deba conservarse.
- **Limpiar campos:** deja el formulario vacío para escribir otros datos.
- **Recargar o refrescar:** vuelve a consultar la información guardada.
- **Volver al menú principal:** permite elegir otro módulo.
- **Cerrar sesión:** regresa al ingreso para utilizar otra cuenta.

En el caso de las matrículas, la cancelación conserva el registro. Además, el programa bloquea el borrado de estudiantes, docentes, materias o grupos que tengan registros relacionados.

## Reglas importantes

- Los campos obligatorios deben estar completos.
- El correo debe tener un formato válido y no estar registrado para otro estudiante.
- Las materias deben tener entre uno y diez créditos.
- Las contraseñas nuevas deben tener al menos ocho caracteres.
- Cada persona debe utilizar su propia cuenta.
- Si el programa informa un problema al guardar, se deben revisar los datos y el mensaje antes de repetir la operación.

## Formas de utilizar el programa

La forma principal es mediante ventanas, botones y tablas. También existe una versión de consola, en la que se eligen opciones escribiendo números. Ambas trabajan con la misma información y respetan los permisos de cada perfil.

## ¿Cómo abrir el proyecto?

Con el proyecto y la conexión a la base de datos ya preparados:

1. Abre la carpeta del proyecto en Visual Studio Code.
2. Busca el archivo `Main.java`.
3. Pulsa **Run** para abrir la ventana de ingreso.
4. Inicia sesión con una cuenta registrada.

Para instalarlo por primera vez, configurar la base de datos o ejecutar las pruebas, consulta la [guía de instalación](docs/INSTALACION.md). Esa guía reúne los pasos técnicos para quien prepara el equipo.

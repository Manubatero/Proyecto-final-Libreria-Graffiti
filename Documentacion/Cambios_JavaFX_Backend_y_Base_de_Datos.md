# Cambios realizados para la entrega

## Introducción

Para esta entrega se avanzó en la aplicación de escritorio del sistema de gestión de stock de **Graffiti**. Se incorporó **JavaFX** para construir la interfaz gráfica, se organizó la lógica que comunica las pantallas con la base de datos y se dejó un diagrama actualizado como referencia del modelo de datos.

El objetivo fue pasar de una idea centrada principalmente en la estructura de la base de datos a una aplicación que pueda abrirse en una computadora y permita trabajar con información del inventario desde una pantalla.

---

## 1. Incorporación de JavaFX para la interfaz

### Cambio realizado

Se incorporó **JavaFX** al proyecto Java mediante su configuración en Gradle. La aplicación cuenta con una ventana principal y una pestaña de gestión de inventario, donde se muestra una tabla de productos y un formulario para operar sobre ellos.

También se preparó una pantalla de punto de cobro y un encabezado visual como partes iniciales de la interfaz. En esta versión, la ventana principal carga la pestaña de inventario; las otras vistas todavía no están conectadas como pantallas activas del recorrido principal.

### Motivo del cambio

Se eligió JavaFX porque permite desarrollar una aplicación gráfica de escritorio usando Java, el mismo lenguaje utilizado para la lógica del sistema. De esta forma, la persona usuaria puede trabajar desde una ventana propia del programa, sin depender de abrir un navegador.

Además, JavaFX ofrece componentes listos para mostrar tablas, formularios, botones, pestañas y mensajes. Esto resulta adecuado para las tareas previstas en un sistema de stock y permite mantener el desarrollo dentro del proyecto Java existente.

### Ventaja respecto a lo anterior

Antes no se contaba con una pantalla de escritorio conectada a las funciones del sistema. Con JavaFX ya se dispone de una interfaz visual inicial desde la que se puede consultar y administrar el inventario.

La aplicación puede ejecutarse como programa de escritorio. La preparación de un instalador o paquete de distribución para otras computadoras no forma parte de lo que queda implementado en esta entrega.

---

## 2. Gestión de inventario desde la pantalla

### Cambio realizado

Se implementó en la pestaña de inventario una tabla con datos principales del producto, como código de barras, nombre, precio de venta, stock actual y stock mínimo. Desde el formulario se puede:

- Dar de alta un producto.
- Seleccionar un producto de la tabla para modificarlo.
- Darlo de baja de manera lógica, conservando el registro en la base de datos.
- Actualizar el listado y limpiar el formulario.
- Seleccionar una imagen del equipo y mostrar una vista previa.
- Ver resaltados los productos cuyo stock actual llegó al mínimo o está por debajo.

Al guardar, modificar o dar de baja un producto, la pantalla informa si la operación se realizó correctamente o si los datos necesitan revisión.

### Motivo del cambio

La gestión de stock es una de las funciones centrales del sistema. Tener las operaciones principales en una misma pantalla facilita el trabajo y permite interactuar con los productos sin escribir consultas manualmente.

### Ventaja respecto a lo anterior

La tabla facilita la consulta rápida de productos y existencias. El aviso visual de stock bajo ayuda a identificar qué artículos requieren atención, mientras que la baja lógica evita borrar definitivamente información que podría ser útil para conservar el historial.

---

## 3. Organización de la lógica de negocio y acceso a datos

### Cambio realizado

Se organizó el código Java en partes con responsabilidades diferenciadas:

- **Modelos:** representan la información del sistema, como productos, categorías, proveedores, usuarios, ventas y detalles de venta.
- **DAO:** reúnen las operaciones de consulta y modificación de datos para cada grupo de información.
- **Implementaciones DAO:** contienen las consultas SQL que se ejecutan contra la base.
- **Vistas:** presentan la información y reciben las acciones de la persona usuaria.
- **Conexión:** centraliza la apertura y el cierre de la conexión con MySQL.

Para productos se encuentran implementadas operaciones para insertar, actualizar, buscar por identificador o código de barras, listar los productos activos y realizar una baja lógica. También hay operaciones preparadas para categorías, proveedores, usuarios, ventas y detalles de venta.

La pantalla de inventario ya utiliza el acceso a datos de productos. Las clases de punto de cobro y encabezado son avances de interfaz; todavía no completan un circuito de venta conectado desde la pantalla.

### Motivo del cambio

Separar la presentación de las operaciones con la base facilita entender dónde se resuelve cada tarea. También permite ampliar las funciones sin concentrar toda la lógica en una sola clase.

### Ventaja respecto a lo anterior

La aplicación cuenta con una base de organización para mantener y ampliar el sistema. Por ejemplo, la vista de inventario solicita las operaciones al componente de productos, mientras que las consultas SQL quedan agrupadas en su implementación correspondiente.

---

## 4. Conexión con la base de datos

### Cambio realizado

Se configuró el conector de MySQL en el proyecto y se agregó una clase central para obtener la conexión JDBC. Los datos de conexión se leen desde el recurso `db.properties`, separado de las clases de interfaz.

Las operaciones de acceso a datos utilizan esa conexión y consultas preparadas para enviar valores a MySQL. Esto permite reutilizar la configuración desde distintas partes de la aplicación.

### Motivo del cambio

La interfaz necesita consultar y guardar información persistente. Centralizar la conexión permite que las vistas y las operaciones de datos trabajen sobre la misma base de datos con una configuración común.

### Ventaja respecto a lo anterior

La aplicación ya tiene implementado el mecanismo para comunicarse con MySQL y la pantalla de inventario lo utiliza para consultar y guardar productos. La conexión depende de que MySQL esté disponible y de que `db.properties` tenga valores válidos para el entorno donde se ejecute el programa.

---

## 5. Modelo de base de datos actualizado

### Cambio realizado

Se cuenta con un diagrama actualizado de la base `graffiti`, que incorpora la tabla `proveedores`, su relación con los productos y la separación entre precio de costo y precio de venta. El archivo de imagen está disponible en:

**[Ver diagrama de bases de datos](../DirectorioDeBDD/DiagramaDeBasesDeDatos.png)**

El modelo contempla las siguientes tablas y relaciones principales:

| Tabla | Información que representa |
|---|---|
| `categorias` | Clasificación de los productos. |
| `productos` | Código, nombre, descripción, precios, existencias, imagen y estado del producto. |
| `proveedores` | Datos de contacto y estado de los proveedores. |
| `usuarios` | Datos de acceso, nombre, rol y estado de las personas usuarias. |
| `ventas` | Fecha, total y usuario asociado a cada operación. |
| `detalle_venta` | Productos, cantidades, precio unitario y subtotal de cada venta. |

Relaciones generales del modelo:

```text
CATEGORÍAS  1 ───── N PRODUCTOS
PROVEEDORES 1 ───── N PRODUCTOS
USUARIOS    1 ───── N VENTAS
VENTAS      1 ───── N DETALLE_VENTA
PRODUCTOS   1 ───── N DETALLE_VENTA
```

En la estructura actual, la asociación del producto con un proveedor admite inicialmente un valor vacío. El detalle de venta conserva el precio unitario utilizado en esa operación, aunque el precio del producto cambie después.

### Motivo del cambio

El diagrama sirve como guía visual para comprender qué información guarda el sistema y cómo se conectan sus partes. También permite tomar como referencia el modelo al continuar con nuevas pantallas u operaciones.

### Ventaja respecto a lo anterior

El modelo ahora identifica a los proveedores y permite asociarlos con sus productos. Además, diferencia el costo de adquisición del precio de venta y conserva en cada detalle de venta el precio aplicado en su momento.

---

## Comparación general

| Aspecto | Antes | Después |
|---|---|---|
| Interfaz | No había una pantalla de escritorio conectada al inventario. | Hay una ventana JavaFX con gestión de inventario. |
| Operaciones sobre productos | Se dependía de trabajar directamente con datos o consultas. | Se pueden dar de alta, modificar, consultar y dar de baja desde la pantalla. |
| Aviso de stock | No se destacaban visualmente los productos con pocas unidades. | Se resaltan los productos que alcanzaron o están por debajo del stock mínimo. |
| Organización del programa | Las funciones no estaban presentadas en capas diferenciadas. | Se separan modelos, vistas, conexión y acceso a datos mediante DAO. |
| Conexión | No estaba integrada a la pantalla de inventario. | La aplicación usa JDBC y MySQL para leer y guardar productos. |
| Proveedores y precios | El modelo anterior no vinculaba productos con proveedores y utilizaba un precio único. | Se representa a los proveedores y se distinguen costo y precio de venta. |
| Referencia del modelo | La estructura era menos clara para consultar en conjunto. | Se dispone de un diagrama actualizado de las tablas y sus relaciones. |

---

## Conclusión

En esta entrega se avanzó en una primera versión funcional de escritorio para el sistema de gestión de stock de Graffiti. JavaFX permitió construir una pantalla para trabajar con productos, y la conexión con MySQL hace posible que las operaciones del inventario se reflejen en la base de datos.

También se ordenó el código para separar las pantallas de los datos y sus operaciones, y se dejó el diagrama actualizado como referencia para continuar el desarrollo. El punto de cobro y algunas vistas están iniciados, pero todavía requieren conectarse con la lógica de venta para completar ese circuito.

En conjunto, estos cambios dejan una base más clara para seguir incorporando funciones y para presentar el sistema como una aplicación de escritorio.

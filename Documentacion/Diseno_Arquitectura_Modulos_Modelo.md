# Diseño del sistema: arquitectura, módulos y modelo de datos

## Propósito y estado

Este documento reúne la arquitectura del sistema de gestión de stock, ventas y facturación interna de la librería y regalería **Graffiti**, el listado de módulos funcionales y la descripción del modelo relacional. Distingue las funciones previstas de las que ya tienen una pantalla o acceso a datos implementados.

El sistema se orienta a la operación del local en una red local. Se excluyen ventas en línea, gestión avanzada de personal y sincronización entre sucursales. La facturación fiscal electrónica queda como trabajo futuro; el comprobante previsto es un ticket interno no fiscal.

## Arquitectura

### Estilo

Se propone una arquitectura de escritorio organizada por capas:

1. **Presentación (JavaFX):** ventanas, pestañas, tablas, formularios y mensajes.
2. **Acceso a datos (DAO):** contratos e implementaciones para leer y modificar cada entidad.
3. **Modelos (Java):** objetos que representan productos, categorías, proveedores, usuarios, ventas y detalles de venta.
4. **Persistencia (MySQL):** almacenamiento relacional, restricciones e índices.
5. **Conexión (JDBC):** comunicación entre Java y MySQL mediante MySQL Connector/J.

La interfaz solicita operaciones a los DAO. Estos ejecutan consultas preparadas mediante JDBC y convierten los resultados en modelos. MySQL aplica las claves y restricciones de integridad.

```text
Personal del comercio
        │
        ▼
Presentación JavaFX (AppLauncher y vistas)
        │ solicita operaciones
        ▼
DAO e implementaciones (consultas preparadas/JDBC)
        │                       ▲
        ▼                       │
Modelos Java ─────────────── Resultados SQL
        │
        ▼
MySQL (graffiti)
```

### Tecnologías y justificación

| Tecnología | Uso | Justificación |
|---|---|---|
| Java | Aplicación y modelos | El equipo tiene experiencia con el lenguaje; facilita organizar el dominio con objetos. |
| JavaFX | Interfaz de escritorio | Proporciona controles para tablas, formularios y navegación sin requerir navegador. |
| MySQL | Base relacional | Permite relacionar las entidades y aplicar integridad referencial. |
| JDBC y MySQL Connector/J | Comunicación Java-MySQL | Permiten ejecutar consultas parametrizadas desde los DAO. |
| Gradle | Dependencias y ejecución | Centraliza la configuración de JavaFX, el conector y tareas del proyecto. |
| MySQL Workbench | Diseño y revisión del esquema | Se utiliza para representar el modelo entidad-relación. |

### Estructura del repositorio

| Ruta | Responsabilidad |
|---|---|
| `graffiti-app/src/main/java/com/graffiti/` | Inicio y lanzamiento de la aplicación JavaFX. |
| `graffiti-app/src/main/java/com/graffiti/view/` | Vistas JavaFX de inventario, punto de venta y encabezado. |
| `graffiti-app/src/main/java/com/graffiti/model/` | Modelos de las entidades del negocio. |
| `graffiti-app/src/main/java/com/graffiti/dao/` | Contratos de acceso a datos. |
| `graffiti-app/src/main/java/com/graffiti/dao/impl/` | Implementaciones SQL/JDBC de los DAO. |
| `graffiti-app/src/main/java/com/graffiti/config/` | Apertura y cierre de conexión JDBC. |
| `graffiti-app/src/main/resources/` | Recursos de configuración de la aplicación. |
| `DirectorioDeBDD/` | Scripts SQL y diagrama de la base. |
| `Documentacion/` | Informe, consignas y documentación del diseño. |

## Módulos funcionales

Prioridad **Alta**: funciones centrales de inventario y ventas. Prioridad **Media**: administración y consultas complementarias. El estado refleja lo que se observa en el repositorio.

| Módulo | Descripción | Prioridad | Estado actual |
|---|---|---:|---|
| Productos e inventario | Alta, consulta, modificación y baja lógica; código de barras, precios, stock, mínimo e imagen. | Alta | La vista JavaFX está conectada al DAO de productos. La pantalla aún no completa todos los campos del modelo, como costo, categoría y proveedor. |
| Alertas de reposición | Señalar productos con stock actual menor o igual al mínimo. | Alta | La tabla de inventario resalta visualmente esos productos. |
| Punto de venta | Leer el código de barras, formar el detalle, calcular total y registrar venta y detalle. | Alta | Existe una vista inicial, todavía no conectada al circuito de venta ni a sus DAO. |
| Comprobante interno | Mostrar o imprimir el detalle de una venta no fiscal. | Alta | Está contemplado en el alcance, pero el circuito no está conectado. |
| Categorías | Mantener categorías y asociarlas con productos. | Media | Hay modelo y operaciones DAO; no se observa una pantalla integrada al flujo principal. |
| Proveedores | Mantener datos de contacto y asociarlos con productos. | Media | Hay modelo, operaciones DAO y estructura en el diagrama; no se observa una pantalla integrada al flujo principal. |
| Autenticación y usuarios | Validar acceso y administrar usuarios, estado y rol. | Media | El DAO incluye búsqueda de acceso y administración de usuarios; no se observa pantalla de inicio de sesión integrada al flujo principal. El script actualizado valida el rol contra un catálogo. |
| Ventas e historial | Registrar ventas y consultar sus detalles, conservando el precio aplicado. | Media | Hay modelos y operaciones DAO; no se observa una vista de historial conectada. |

## Modelo de datos relacional

El diagrama entidad-relación está en [`DirectorioDeBDD/DiagramaDeBasesDeDatos.png`](../DirectorioDeBDD/DiagramaDeBasesDeDatos.png); el archivo editable está en [`DirectorioDeBDD/DiagramaDeBasesDeDatos.mwb`](../DirectorioDeBDD/DiagramaDeBasesDeDatos.mwb).

| Tabla | Campos y tipos principales | Claves, restricciones e índices |
|---|---|---|
| `categorias` | `id_categoria INT`, `nombre VARCHAR(100)`, `descripcion VARCHAR(255)` | PK `id_categoria`; índice único recomendado para `nombre`. |
| `proveedores` | `id_proveedor INT`, `nombre VARCHAR(150)`, `telefono VARCHAR(30)`, `email VARCHAR(150)`, `direccion VARCHAR(255)`, `activo BOOLEAN` | PK `id_proveedor`; índice recomendado por nombre. |
| `productos` | `id_producto INT`, `codigo_barra VARCHAR(50)`, `nombre VARCHAR(150)`, `descripcion VARCHAR(255)`, `precio_venta DECIMAL(10,2)`, `precio_costo DECIMAL(10,2)`, `stock_actual INT`, `stock_minimo INT`, `imagen_path VARCHAR(500)`, `id_categoria INT`, `id_proveedor INT NULL`, `activo BOOLEAN` | PK `id_producto`; UNIQUE `codigo_barra`; FK `id_categoria` → `categorias`; FK `id_proveedor` → `proveedores`; índices sobre FK y búsquedas frecuentes. |
| `roles` | `nombre_rol VARCHAR(50)`, `descripcion VARCHAR(255)` | PK/UNIQUE sobre `nombre_rol`; catálogo controlado. |
| `usuarios` | `id_usuario INT`, `nombre_usuario VARCHAR(50)`, `nombre VARCHAR(100)`, `apellido VARCHAR(100)`, `password VARCHAR(255)`, `rol VARCHAR(50)`, `activo BOOLEAN` | PK `id_usuario`; UNIQUE `nombre_usuario`; FK `rol` → `roles.nombre_rol`. |
| `ventas` | `id_venta INT`, `fecha_hora DATETIME`, `total DECIMAL(10,2)`, `id_usuario INT` | PK `id_venta`; FK `id_usuario` → `usuarios.id_usuario`; índice recomendado por fecha. |
| `detalle_venta` | `id_detalle INT`, `id_venta INT`, `id_producto INT`, `cantidad INT`, `precio_unitario DECIMAL(10,2)`, `subtotal DECIMAL(10,2)` | PK `id_detalle`; FK `id_venta` → `ventas.id_venta`; FK `id_producto` → `productos.id_producto`; índices sobre ambas FK. `precio_unitario` conserva el valor histórico. |

Las claves primarias son autoincrementales. Las FK deben estar indexadas; MySQL crea los índices requeridos para hacer cumplir las relaciones.

```text
CATEGORIAS  1 ───── N PRODUCTOS
PROVEEDORES 1 ───── N PRODUCTOS (proveedor opcional)
ROLES       1 ───── N USUARIOS
USUARIOS    1 ───── N VENTAS
VENTAS      1 ───── N DETALLE_VENTA
PRODUCTOS   1 ───── N DETALLE_VENTA
```

### Reglas e integridad

- El código de barras y el nombre de usuario son únicos.
- Los importes se almacenan con `DECIMAL`.
- Existencias y cantidades son enteros no negativos; la cantidad del detalle debe ser mayor que cero. Estas reglas deben validarse en la aplicación o mediante `CHECK` según la versión de MySQL.
- Las claves foráneas impiden asociar ventas, detalles o productos con registros inexistentes.
- La baja lógica utiliza `activo` para conservar productos, proveedores y usuarios relacionados con el historial.
- El rol se respalda con un catálogo y una FK que restringen los valores válidos, conservando el campo de texto `rol` que utiliza el DAO Java actual.

### Scripts SQL y sincronización

El archivo `DirectorioDeBDD/create_query.sql` reúne la creación de la base, las tablas, las relaciones y las restricciones del modelo actualizado. Para crear la base en una instalación nueva, se ejecuta ese único script. Incluye el catálogo inicial de roles y sus valores permitidos.

La pantalla de inventario ya utiliza el acceso a datos de productos. El punto de venta tiene una vista inicial y los DAO de otras entidades están preparados, pero esos flujos aún no están conectados desde la interfaz.
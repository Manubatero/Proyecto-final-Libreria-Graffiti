# Cambios realizados en el punto de cobro y la gestión de vendedores

## Introducción

En los últimos cambios se reorganizó el punto de cobro de la aplicación de escritorio de **Graffiti** y se completó el registro de ventas con sus productos, cantidades, precios y vendedor responsable. También se incorporó la tabla de vendedores a la base de datos.

El objetivo fue completar el circuito de cobro desde la selección de productos hasta el guardado de la venta, con controles de stock y una interfaz más clara para la persona usuaria.

---

## 1. Punto de cobro modular

### Cambio realizado

Se reorganizó la pantalla de punto de cobro en componentes con tareas específicas: búsqueda y selección de productos, edición de cantidades, carrito y resumen de cobro. Desde la pantalla se puede:

- Buscar productos y agregarlos al carrito.
- Elegir la cantidad disponible y modificarla luego.
- Quitar productos del carrito y consultar subtotales y total.
- Ver resaltados los productos cuyo stock está en el mínimo o por debajo.
- Seleccionar al vendedor responsable antes de iniciar el cobro.
- Revisar la venta, indicar un método de pago y confirmar manualmente que el pago fue aprobado.

La pantalla muestra los estados de venta en curso, pendiente de pago y pagada. Tras guardar correctamente, informa el número de venta y limpia el carrito.

### Motivo del cambio

Separar las partes de la pantalla facilita el uso del punto de cobro y permite mantener cada tarea en un componente propio. El flujo guiado ayuda a revisar los productos, cantidades y total antes de registrar la operación.

### Ventaja respecto a lo anterior

El punto de cobro cuenta con una secuencia de trabajo más completa. La persona usuaria puede administrar el carrito y revisar el resumen dentro de una sola pantalla, con indicaciones visibles sobre el estado de la operación.

---

## 2. Registro transaccional de ventas y control de stock

### Cambio realizado

El registro de una venta ahora guarda la cabecera y sus detalles dentro de una transacción SQL. Antes de guardar, se comprueba que los productos estén activos y tengan stock suficiente; se toma el precio vigente de cada producto, se calculan los subtotales y el total, se descuenta el stock y se insertan los detalles de venta.

Si alguna validación o escritura falla, se revierte la transacción para evitar dejar guardada una venta incompleta o un descuento parcial de stock. Al terminar, se actualiza el catálogo visible.

### Motivo del cambio

Una venta afecta a varios datos relacionados: el total, los productos vendidos, sus precios y las existencias. La transacción permite guardar esos cambios como una única operación consistente.

### Ventaja respecto a lo anterior

La venta conserva el precio aplicado en cada detalle y descuenta las unidades vendidas. Los controles en la base también contemplan cambios concurrentes de stock durante el cobro.

---

## 3. Incorporación de vendedores

### Cambio realizado

Se agregó el modelo `Vendedor`, sus operaciones de acceso a datos y una tabla `vendedores` con nombre y apellido. El resumen de cobro carga los vendedores disponibles y permite elegir quién realiza la venta. También se puede registrar un vendedor nuevo desde el menú de acciones; luego queda disponible para seleccionarlo.

Las ventas guardan la referencia al vendedor y las consultas de ventas recuperan su nombre. La asociación anterior con el usuario de la aplicación se mantiene como un dato separado y puede quedar vacía.

### Motivo del cambio

El vendedor que atiende una operación representa una responsabilidad comercial distinta de la cuenta de usuario que opera el sistema. Separar ambas referencias permite identificar quién realizó cada venta.

### Ventaja respecto a lo anterior

Las ventas nuevas quedan asociadas a una persona vendedora identificable, y se puede agregar esa persona desde la interfaz sin tener que cargarla directamente mediante consultas SQL.

---

## 4. Actualización del esquema de base de datos

### Cambio realizado

El script consolidado `DirectorioDeBDD/create_query.sql` incorpora la tabla de vendedores y la relación entre vendedores y ventas. Para bases existentes se agregó la migración `modifcaciones/BDD/migracion_vendedores.sql`, que crea la tabla, agrega `id_vendedor` a `ventas` y permite que las ventas históricas no tengan vendedor asignado.

La migración define una clave foránea que impide eliminar vendedores asociados a ventas y permite actualizar sus identificadores. Las nuevas ventas de la aplicación requieren seleccionar un vendedor.

### Motivo del cambio

El esquema debe representar la nueva información y conservar la compatibilidad con las ventas creadas antes de incorporar vendedores.

### Ventaja respecto a lo anterior

La base mantiene la relación entre ventas y vendedores y protege su integridad. Las ventas históricas se conservan aunque no cuenten con esa asociación.

---

## Comparación general

| Aspecto | Antes | Después |
|---|---|---|
| Punto de cobro | La pantalla tenía menos componentes y un flujo de cobro menos desarrollado. | Hay búsqueda de productos, carrito editable, resumen y estados de venta. |
| Registro de una venta | La operación no guardaba conjuntamente todos los cambios de una venta y sus detalles. | La cabecera, los detalles y el descuento de stock se procesan en una transacción. |
| Control de existencias | El stock podía no reflejar una venta completa de forma coordinada. | Se valida y descuenta el stock; ante un error se revierte la operación. |
| Responsable de venta | Se utilizaba el usuario del sistema como referencia del vendedor. | La venta se asocia con un registro de vendedor independiente. |
| Base de datos | No existía una tabla propia de vendedores vinculada a las ventas. | El esquema incluye vendedores y una migración para bases existentes. |

---

## Conclusión

Estos cambios completan una parte central del sistema: la preparación de una venta, la confirmación del cobro y su persistencia junto con los detalles y la actualización de existencias. La incorporación de vendedores permite identificar a la persona responsable de cada operación y mantiene esa información separada de las cuentas de acceso.

La selección del método de pago y la confirmación se realizan manualmente desde la interfaz; el código no se conecta con terminales ni con Mercado Pago para verificar pagos automáticamente. Para actualizar una base ya creada, se debe ejecutar la migración de vendedores; las instalaciones nuevas pueden utilizar el script consolidado.

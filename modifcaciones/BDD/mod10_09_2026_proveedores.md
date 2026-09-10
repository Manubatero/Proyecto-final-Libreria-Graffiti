# Cambios realizados en la base de datos

## Introducción

Se realizaron modificaciones en la estructura de la base de datos del sistema de gestión de stock de **Graffiti** con el objetivo de mejorar la administración de los productos y permitir una gestión más completa de los proveedores y los costos de adquisición.

Anteriormente, la tabla `productos` solamente almacenaba un único campo `precio`, utilizado para representar el precio del producto. Además, no existía una relación entre los productos y sus proveedores.

A partir de los cambios realizados, cada producto puede estar asociado a un proveedor y se pueden diferenciar el **precio de costo** y el **precio de venta**.

---

## 1. Incorporación de la tabla `proveedores`

### Cambio realizado

Se agregó una nueva tabla denominada `proveedores`, que contiene la información básica de cada proveedor:

- `id_proveedor`: identificador único del proveedor.
- `nombre`: nombre del proveedor.
- `telefono`: número de contacto.
- `email`: correo electrónico.
- `direccion`: dirección del proveedor.
- `activo`: permite indicar si el proveedor se encuentra activo.

### Motivo del cambio

La estructura anterior no contemplaba una forma de registrar quién provee cada producto. Esto dificultaba mantener organizada la información relacionada con las compras y el abastecimiento del negocio.

La incorporación de esta tabla permite separar la información de los proveedores de la información propia de los productos y mantener una estructura más ordenada.

### Ventaja respecto a lo anterior

La principal ventaja es que ahora el sistema puede identificar de manera directa qué proveedor está asociado a cada producto.

Por ejemplo, un mismo proveedor puede estar asociado a diferentes productos, como lapiceras, lápices, cuadernos y otros artículos. De esta manera, no es necesario repetir los datos del proveedor dentro de cada producto.

---

## 2. Relación entre `productos` y `proveedores`

### Cambio realizado

Se agregó el campo `id_proveedor` a la tabla `productos` y se estableció una **clave foránea** que referencia a `proveedores(id_proveedor)`.

La relación establecida es:

**PROVEEDORES 1 ─── N PRODUCTOS**

Esto significa que un proveedor puede estar asociado a varios productos, mientras que cada producto puede tener asociado un proveedor.

### Motivo del cambio

La relación permite vincular la información de los productos con su proveedor correspondiente sin duplicar los datos del proveedor.

### Ventaja respecto a lo anterior

Anteriormente no existía ninguna relación entre ambas entidades. Con esta modificación, la base de datos puede responder consultas como:

- Qué proveedor corresponde a un determinado producto.
- Qué productos proporciona un determinado proveedor.
- Qué proveedor está asociado a los productos que necesitan reposición.

Además, el uso de una clave foránea ayuda a mantener la **integridad referencial** de la información.

---

## 3. Separación entre precio de costo y precio de venta

### Cambio realizado

El campo anterior `precio` de la tabla `productos` fue reemplazado por `precio_venta`.

Además, se incorporó el campo `precio_costo`.

De esta manera, un producto ahora puede almacenar dos valores diferentes:

- `precio_costo`: valor al que el negocio adquiere el producto.
- `precio_venta`: valor al que el producto se vende al cliente.

### Motivo del cambio

El campo `precio` anterior no permitía diferenciar entre el costo de adquisición y el precio final de venta.

Esta diferenciación es importante para un sistema de gestión de stock, ya que ambos valores cumplen funciones diferentes.

### Ventaja respecto a lo anterior

La nueva estructura permite conocer cuánto cuesta adquirir un producto y cuánto se obtiene por su venta.

Por ejemplo, si una lapicera tiene:

**Precio de costo:** $500  
**Precio de venta:** $800

el sistema puede diferenciar ambos valores y, a partir de ellos, obtener información relacionada con el margen de venta.

Esto proporciona una visión más completa de la situación económica de los productos.

---

## 4. Mantenimiento del `precio_unitario` en `detalle_venta`

No fue necesario modificar el campo `precio_unitario` de la tabla `detalle_venta`.

Este campo continúa almacenando el precio utilizado en una venta determinada.

Esto es importante porque el `precio_venta` de un producto puede modificarse posteriormente, mientras que una venta ya realizada debe conservar el precio con el que fue registrada.

Por ejemplo, si un producto se vendió a $800 y posteriormente su precio de venta aumenta a $900, la venta anterior debe continuar registrando $800.

De esta manera, se conserva correctamente el historial de las operaciones realizadas.

---

## 5. Compatibilidad con los datos existentes

Debido a que la base de datos ya había sido creada y podía contener productos registrados, los nuevos campos `id_proveedor` y `precio_costo` se incorporaron inicialmente permitiendo valores `NULL`.

Esto permite realizar la modificación de la estructura sin que los registros existentes provoquen un error.

Una vez que todos los productos existentes tengan asignado un proveedor y un precio de costo, estos campos pueden establecerse como obligatorios (`NOT NULL`).

---

## Comparación general

| Aspecto | Antes | Después |
|---|---|---|
| Proveedores | No estaban registrados | Se incorporó la tabla `proveedores` |
| Relación producto-proveedor | No existía | Se agregó mediante `id_proveedor` |
| Precio del producto | Un único campo `precio` | `precio_costo` y `precio_venta` |
| Información del proveedor | No disponible | Nombre, teléfono, email, dirección y estado |
| Análisis del margen | Limitado | Se pueden comparar costo y venta |
| Integridad entre productos y proveedores | No aplicaba | Se utiliza una clave foránea |
| Historial de ventas | Conservado mediante `precio_unitario` | Se mantiene el precio de cada venta independientemente de cambios posteriores |

---

## Conclusión

Los cambios realizados permiten que la base de datos represente de manera más completa el funcionamiento del sistema de gestión de stock de Graffiti.

La incorporación de `proveedores` permite organizar y relacionar los productos con sus respectivos proveedores, mientras que la separación entre `precio_costo` y `precio_venta` permite diferenciar el costo de adquisición del valor de venta.

En conjunto, estas modificaciones ofrecen una estructura más organizada, permiten obtener información que anteriormente no estaba disponible y sientan una mejor base para futuras funcionalidades relacionadas con el abastecimiento, los costos y la rentabilidad de los productos.


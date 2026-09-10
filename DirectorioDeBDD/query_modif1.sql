-- =============================================
-- Fecha: 2026-09-10
-- Descripción: Query para modificación de base de datos e incorporación de tabla proveedores
-- Base de datos: graffiti
-- =============================================

USE graffiti;


-- =============================================
-- 1. Crear tabla de proveedores
-- =============================================

CREATE TABLE proveedores (
    id_proveedor INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    telefono VARCHAR(30),
    email VARCHAR(150),
    direccion VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT TRUE
);


-- =============================================
-- 2. Agregar proveedor a productos
-- =============================================

ALTER TABLE productos
ADD COLUMN id_proveedor INT NULL;


-- =============================================
-- 3. Crear relación entre productos y proveedores
-- =============================================

ALTER TABLE productos
ADD CONSTRAINT fk_producto_proveedor
    FOREIGN KEY (id_proveedor)
    REFERENCES proveedores(id_proveedor);


-- =============================================
-- 4. Renombrar precio a precio_venta
-- =============================================

ALTER TABLE productos
CHANGE COLUMN precio precio_venta DECIMAL(10,2) NOT NULL;


-- =============================================
-- 5. Agregar precio de costo
-- =============================================

ALTER TABLE productos
ADD COLUMN precio_costo DECIMAL(10,2) NULL;
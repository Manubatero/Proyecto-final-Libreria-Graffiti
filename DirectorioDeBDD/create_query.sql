-- ============================================================
-- Sistema de gestion de stock, ventas y facturacion interna
-- Base de datos: graffiti
-- Script consolidado para una instalacion nueva.
-- Fecha de actualizacion: 2026-10-05
-- ============================================================

CREATE DATABASE IF NOT EXISTS graffiti
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE graffiti;

CREATE TABLE categorias (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    CONSTRAINT uq_categoria_nombre UNIQUE (nombre)
) ENGINE=InnoDB;

CREATE TABLE proveedores (
    id_proveedor INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    telefono VARCHAR(30),
    email VARCHAR(150),
    direccion VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_proveedor_nombre (nombre)
) ENGINE=InnoDB;

CREATE TABLE roles (
    nombre_rol VARCHAR(50) NOT NULL PRIMARY KEY,
    descripcion VARCHAR(255)
) ENGINE=InnoDB;

-- Roles iniciales controlados. usuarios.rol referencia este catalogo.
INSERT INTO roles (nombre_rol, descripcion) VALUES
    ('ADMINISTRADOR', 'Acceso a tareas de administracion del sistema'),
    ('VENDEDOR', 'Acceso a tareas de venta y consulta de productos');

CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(50) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_usuario_nombre_usuario UNIQUE (nombre_usuario),
    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (rol) REFERENCES roles(nombre_rol)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE productos (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    codigo_barra VARCHAR(50) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(255),
    precio_venta DECIMAL(10,2) NOT NULL,
    precio_costo DECIMAL(10,2),
    stock_actual INT NOT NULL DEFAULT 0,
    stock_minimo INT NOT NULL DEFAULT 0,
    imagen_path VARCHAR(500),
    id_categoria INT NOT NULL,
    id_proveedor INT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_producto_codigo_barra UNIQUE (codigo_barra),
    CONSTRAINT chk_producto_precio_venta CHECK (precio_venta >= 0),
    CONSTRAINT chk_producto_precio_costo CHECK (precio_costo IS NULL OR precio_costo >= 0),
    CONSTRAINT chk_producto_stock_actual CHECK (stock_actual >= 0),
    CONSTRAINT chk_producto_stock_minimo CHECK (stock_minimo >= 0),
    INDEX idx_producto_categoria (id_categoria),
    INDEX idx_producto_proveedor (id_proveedor),
    INDEX idx_producto_nombre (nombre),
    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_producto_proveedor
        FOREIGN KEY (id_proveedor) REFERENCES proveedores(id_proveedor)
        ON UPDATE CASCADE
        ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE ventas (
    id_venta INT AUTO_INCREMENT PRIMARY KEY,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    id_usuario INT NOT NULL,
    CONSTRAINT chk_venta_total CHECK (total >= 0),
    INDEX idx_venta_fecha_hora (fecha_hora),
    INDEX idx_venta_usuario (id_usuario),
    CONSTRAINT fk_venta_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE detalle_venta (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_venta INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    CONSTRAINT chk_detalle_cantidad CHECK (cantidad > 0),
    CONSTRAINT chk_detalle_precio CHECK (precio_unitario >= 0),
    CONSTRAINT chk_detalle_subtotal CHECK (subtotal >= 0),
    INDEX idx_detalle_venta (id_venta),
    INDEX idx_detalle_producto (id_producto),
    CONSTRAINT fk_detalle_venta
        FOREIGN KEY (id_venta) REFERENCES ventas(id_venta)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_detalle_producto
        FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Orden de entidades: categorias, proveedores, roles, usuarios,
-- productos, ventas y detalle_venta.
-- En instalaciones nuevas, ejecutar este archivo completo una sola vez.

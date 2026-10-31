-- Migración para una base graffiti creada con la versión anterior del esquema.
-- Las ventas existentes quedan con id_vendedor NULL; las nuevas requieren uno.
USE graffiti;

CREATE TABLE IF NOT EXISTS vendedores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    INDEX idx_vendedor_apellido_nombre (apellido, nombre)
) ENGINE=InnoDB;

ALTER TABLE ventas
    MODIFY COLUMN id_usuario INT NULL,
    ADD COLUMN id_vendedor INT NULL AFTER id_usuario,
    ADD INDEX idx_venta_vendedor (id_vendedor),
    ADD CONSTRAINT fk_venta_vendedor
        FOREIGN KEY (id_vendedor) REFERENCES vendedores(id)
        ON UPDATE CASCADE ON DELETE RESTRICT;

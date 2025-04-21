/*Se verifica y se elimina la base de datos si existe antes de crearla nuevamente */
DROP SCHEMA IF EXISTS bruma;
DROP USER IF EXISTS usuario_prueba;
DROP USER IF EXISTS usuario_reportes;

/*Se crea la base de datos */
CREATE SCHEMA bruma;

/*Se crea un usuario para la base de datos llamado "usuario_prueba" y tiene la contraseña "Usuario_Clave."*/
CREATE USER 'usuario_prueba'@'%' IDENTIFIED BY 'Usuar1o_Clave.';
CREATE USER 'usuario_reportes'@'%' IDENTIFIED BY 'Usuar1o_Reportes.';

/*Se asignan los privilegios sobre la base de datos Bruma al usuario creado */
GRANT ALL PRIVILEGES ON bruma.* TO 'usuario_prueba'@'%';
GRANT SELECT ON bruma.* TO 'usuario_reportes'@'%';
FLUSH PRIVILEGES;

USE bruma;

/* la tabla de categoria contiene categorias de productos de joyería*/
CREATE TABLE categoria (
  id_categoria INT NOT NULL AUTO_INCREMENT,
  descripcion VARCHAR(30) NOT NULL,
  ruta_imagen VARCHAR(1024),
  activo BOOL,
  PRIMARY KEY (id_categoria))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE producto (
  id_producto INT NOT NULL AUTO_INCREMENT,
  id_categoria INT NOT NULL,
  nombre VARCHAR(50) NOT NULL,  
  descripcion VARCHAR(1600) NOT NULL, 
  material VARCHAR(50),
  precio DOUBLE,
  existencias INT,  
  ruta_imagen VARCHAR(1024),
  activo BOOL,
  PRIMARY KEY (id_producto),
  FOREIGN KEY fk_producto_categoria (id_categoria) REFERENCES categoria(id_categoria)  
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

/*Se crea la tabla de clientes llamada usuario */
CREATE TABLE usuario (
  id_usuario INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(20) NOT NULL,
  password VARCHAR(512) NOT NULL,
  nombre VARCHAR(20) NOT NULL,
  apellidos VARCHAR(30) NOT NULL,
  correo VARCHAR(75) NULL,
  telefono VARCHAR(15) NULL,
  direccion VARCHAR(200) NULL,
  ruta_imagen VARCHAR(1024),
  activo BOOLEAN,
  PRIMARY KEY (id_usuario))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

-- Tabla para almacenar múltiples direcciones de usuarios
CREATE TABLE direccion (
  id_direccion INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  nombre VARCHAR(100) NOT NULL, -- Etiqueta para la dirección (ej: "Casa", "Trabajo")
  calle VARCHAR(200) NOT NULL,
  ciudad VARCHAR(100) NOT NULL,
  provincia VARCHAR(100) NOT NULL,
  codigo_postal VARCHAR(20) NOT NULL,
  pais VARCHAR(100) DEFAULT 'Costa Rica',
  telefono VARCHAR(20),
  es_principal BOOLEAN DEFAULT FALSE, -- Para marcar la dirección predeterminada
  activo BOOLEAN DEFAULT TRUE,
  PRIMARY KEY (id_direccion),
  FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4;

-- Tabla para almacenar métodos de pago (tarjetas)
CREATE TABLE metodo_pago (
  id_metodo_pago INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  tipo VARCHAR(50) NOT NULL, -- (ej: "VISA", "MasterCard", "AMEX")
  nombre_titular VARCHAR(100) NOT NULL,
  numero_tarjeta VARCHAR(100) NOT NULL, -- Se almacenará encriptado en un caso real
  mes_expiracion INT NOT NULL,
  anio_expiracion INT NOT NULL,
  es_principal BOOLEAN DEFAULT FALSE, -- Para marcar el método predeterminado
  activo BOOLEAN DEFAULT TRUE,
  PRIMARY KEY (id_metodo_pago),
  FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4;

-- Insertar algunas direcciones de ejemplo para los usuarios existentes
INSERT INTO direccion (id_usuario, nombre, calle, ciudad, provincia, codigo_postal, pais, telefono, es_principal) VALUES
(1, 'Casa', 'Calle 123', 'San José', 'San José', '10101', 'Costa Rica', '8765-4321', TRUE),
(1, 'Trabajo', 'Avenida Central', 'San José', 'San José', '10102', 'Costa Rica', '8765-4322', FALSE),
(2, 'Casa', 'Calle Principal 45', 'Heredia', 'Heredia', '20201', 'Costa Rica', '8123-9876', TRUE),
(3, 'Casa', 'Avenida 2', 'Cartago', 'Cartago', '30301', 'Costa Rica', '7890-1234', TRUE);

-- Insertar algunos métodos de pago de ejemplo para los usuarios existentes
INSERT INTO metodo_pago (id_usuario, tipo, nombre_titular, numero_tarjeta, mes_expiracion, anio_expiracion, es_principal) VALUES
(1, 'VISA', 'María López Mora', '4111XXXXXXXXXXXX', 5, 2026, TRUE),
(1, 'MasterCard', 'María López Mora', '5500XXXXXXXXXXXX', 8, 2027, FALSE),
(2, 'VISA', 'Carlos Ramírez Torres', '4222XXXXXXXXXXXX', 3, 2025, TRUE),
(3, 'AMEX', 'Laura Vega Castro', '3700XXXXXXXXXXXX', 12, 2026, TRUE);

CREATE TABLE factura (
  id_factura INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  fecha DATE,  
  total DOUBLE,
  estado INT, /* 1=En Proceso, 2=Pagada, 3=Anulada */
  PRIMARY KEY (id_factura),
  FOREIGN KEY fk_factura_usuario (id_usuario) REFERENCES usuario(id_usuario)  
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE venta (
  id_venta INT NOT NULL AUTO_INCREMENT,
  id_factura INT NOT NULL,
  id_producto INT NOT NULL,
  precio DOUBLE, 
  cantidad INT,
  PRIMARY KEY (id_venta),
  FOREIGN KEY fk_ventas_factura (id_factura) REFERENCES factura(id_factura),
  FOREIGN KEY fk_ventas_producto (id_producto) REFERENCES producto(id_producto) 
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

/*Se insertan 3 registros en la tabla usuario como ejemplo */
INSERT INTO usuario (id_usuario, username, password, nombre, apellidos, correo, telefono, direccion, ruta_imagen, activo) VALUES 
(1, 'maria', '$2a$10$P1.w58XvnaYQUQgZUCk4aO/RTRl8EValluCqB3S2VMLTbRt.tlre.', 'María', 'López Mora', 'mlopez@gmail.com', '8765-4321', 'San José, Costa Rica', 'https://randomuser.me/api/portraits/women/45.jpg', true),
(2, 'carlos', '$2a$10$GkEj.ZzmQa/aEfDmtLIh3udIH5fMphx/35d0EYeqZL5uzgCJ0lQRi', 'Carlos', 'Ramírez Torres', 'cramirez@gmail.com', '8123-9876', 'Heredia, Costa Rica', 'https://randomuser.me/api/portraits/men/32.jpg', true),
(3, 'laura', '$2a$10$koGR7eS22Pv5KdaVJKDcge04ZB53iMiw76.UjHPY.XyVYlYqXnPbO', 'Laura', 'Vega Castro', 'lvega@gmail.com', '7890-1234', 'Cartago, Costa Rica', 'https://randomuser.me/api/portraits/women/65.jpg', true);

/*Se insertan categorías de joyería como ejemplo */
INSERT INTO categoria (id_categoria, descripcion, ruta_imagen, activo) VALUES 
('1', 'Anillos', 'https://images.unsplash.com/photo-1605100804763-247f67b3557e', true), 
('2', 'Pulseras', 'https://images.unsplash.com/photo-1611591437281-460bfbe1220a', true),
('3', 'Collares', 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f', true),
('4', 'Aretes', 'https://images.unsplash.com/photo-1625741737936-ba10df029b09', true);

/*Se insertan productos de joyería */
INSERT INTO producto (id_producto, id_categoria, nombre, descripcion, material, precio, existencias, ruta_imagen, activo) VALUES
(1, 1, 'Anillo Solitario Diamante', 'Elegante anillo solitario con diamante central de corte brillante de 0.5 quilates. Montado en oro blanco de 18k, este anillo atemporal es perfecto para ocasiones especiales o como anillo de compromiso.', 'Oro blanco 18k, Diamante', 450000, 5, 'https://images.unsplash.com/photo-1605100804763-247f67b3557e', true),
(2, 1, 'Anillo Vintage Flora', 'Anillo de estilo vintage con diseño floral, elaborado en oro rosa de 14k con detalles de zafiros pequeños que acentúan los pétalos. Una pieza única con carácter y elegancia.', 'Oro rosa 14k, Zafiros', 320000, 3, 'https://images.unsplash.com/photo-1543294001-f7cd5d7fb516', true),
(3, 1, 'Anillo Trinity', 'Anillo triple entrelazado que simboliza amor, fidelidad y amistad. Elaborado con tres tipos de oro: amarillo, blanco y rosa de 18k, creando un contraste hermoso y significativo.', 'Oro 18k tricolor', 275000, 7, 'https://images.unsplash.com/photo-1603561591411-c61c8bcf3383', true),
(4, 1, 'Anillo Eternity', 'Anillo de eternidad con pequeños diamantes que recorren todo el contorno. Montado en platino, este anillo representa el amor eterno con su círculo ininterrumpido de brillantes.', 'Platino, Diamantes', 520000, 2, 'https://images.unsplash.com/photo-1589272709208-19d590936f13', true),
(5, 2, 'Pulsera Eslabones Oro', 'Pulsera de eslabones gruesos elaborada en oro amarillo de 18k. Diseño contemporáneo con cierre de seguridad, perfecta para cualquier ocasión.', 'Oro amarillo 18k', 380000, 4, 'https://images.unsplash.com/photo-1611591437281-460bfbe1220a', true),
(6, 2, 'Pulsera Tennis Diamantes', 'Clásica pulsera tennis con 25 diamantes pequeños de corte brillante (total 2 quilates) engarzados en monturas de plata 925. Elegante y versátil.', 'Plata 925, Diamantes', 560000, 2, 'https://images.unsplash.com/photo-1612036861513-20824eic56b42', true),
(7, 2, 'Pulsera Charm Personalizable', 'Pulsera de cadena con sistema para añadir charms. Viene con tres dijes iniciales. Elaborada en plata 925 con baño de oro de 24k.', 'Plata 925, Baño de oro', 120000, 10, 'https://images.unsplash.com/photo-1650316555793-2d522ae5a81f', true),
(8, 2, 'Pulsera Rígida Perlas', 'Pulsera rígida con diseño moderno que incorpora perlas cultivadas intercaladas en estructura de oro blanco de 14k. Cierre invisible de presión.', 'Oro blanco 14k, Perlas', 290000, 3, 'https://images.unsplash.com/photo-1636126927073-24c94e384250', true),
(9, 3, 'Collar Gargantilla Zafiro', 'Elegante gargantilla con zafiro central de 1 quilate rodeado de pequeños diamantes. Cadena de oro blanco de 18k ajustable.', 'Oro blanco 18k, Zafiro, Diamantes', 680000, 2, 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f', true),
(10, 3, 'Collar Largo Perlas', 'Collar largo de perlas cultivadas de 8mm con broche de plata 925. Longitud de 70cm, puede usarse simple o en doble vuelta.', 'Plata 925, Perlas', 240000, 5, 'https://images.unsplash.com/photo-1615655532939-1859f9a8b50d', true),
(11, 3, 'Collar Colgante Esmeralda', 'Delicado collar con colgante de esmeralda colombiana de 0.75 quilates en forma de gota. Cadena veneciana de oro amarillo de 18k.', 'Oro amarillo 18k, Esmeralda', 420000, 3, 'https://images.unsplash.com/photo-1602173574767-37ac01994b2a', true),
(12, 3, 'Collar Choker Diamantes', 'Collar estilo choker con línea de pequeños diamantes engarzados en oro rosa de 14k. Diseño contemporáneo y elegante.', 'Oro rosa 14k, Diamantes', 370000, 2, 'https://images.unsplash.com/photo-1611107683227-e9060eccd846', true),
(13, 4, 'Aretes Candelabro', 'Impresionantes aretes estilo candelabro con rubíes y pequeños diamantes. Elaborados en oro amarillo de 18k, añaden glamour a cualquier atuendo.', 'Oro amarillo 18k, Rubíes, Diamantes', 490000, 2, 'https://images.unsplash.com/photo-1625741737936-ba10df029b09', true),
(14, 4, 'Aretes Botón Perla', 'Clásicos aretes tipo botón con perlas cultivadas de 10mm y un pequeño diamante en la parte superior. Montados en oro blanco de 14k.', 'Oro blanco 14k, Perlas, Diamantes', 180000, 8, 'https://images.unsplash.com/photo-1629224316810-9d8805b95e76', true),
(15, 4, 'Aretes Topacios Azules', 'Elegantes aretes con topacios azules en forma de lágrima. Montados en plata esterlina con detalles de filigrana.', 'Plata 925, Topacios', 350000, 4, 'https://images.unsplash.com/photo-1633810333882-a32d30ae2326', true),
(16, 4, 'Aretes Largos Cascada', 'Espectaculares aretes largos con diseño de cascada de diamantes pequeños. Elaborados en platino, son ideales para eventos especiales.', 'Platino, Diamantes', 540000, 2, 'https://images.unsplash.com/photo-1642669294727-66e841771aed', true);

/*Se crean 6 facturas */   /*1=En Proceso, 2=Pagada, 3=Anulada*/
INSERT INTO factura (id_factura, id_usuario, fecha, total, estado) VALUES
(1, 1, '2024-06-05', 1130000, 2),
(2, 2, '2024-06-07', 1400000, 2),
(3, 3, '2024-08-07', 1840000, 2),
(4, 1, '2024-08-15', 810000, 1),
(5, 2, '2024-09-17', 990000, 1),
(6, 3, '2024-09-21', 1260000, 1);

INSERT INTO venta (id_venta, id_factura, id_producto, precio, cantidad) values
(1, 1, 5, 380000, 1),
(2, 1, 9, 680000, 1),
(3, 1, 14, 180000, 1),
(4, 2, 3, 275000, 2),
(5, 2, 7, 120000, 1),
(6, 2, 12, 370000, 2),
(7, 3, 11, 420000, 1),
(8, 3, 1, 450000, 2),
(9, 3, 7, 120000, 1),
(10, 4, 6, 560000, 1),
(11, 4, 14, 180000, 2),
(12, 4, 7, 120000, 1),
(13, 5, 8, 290000, 1),
(14, 5, 10, 240000, 1),
(15, 5, 3, 275000, 1),
(16, 6, 2, 320000, 2),
(17, 6, 15, 350000, 1),
(18, 6, 14, 180000, 1);

CREATE TABLE role (  
  rol VARCHAR(20),
  PRIMARY KEY (rol)  
);

INSERT INTO role (rol) VALUES ('ADMIN'), ('VENDEDOR'), ('USER');

CREATE TABLE rol (
  id_rol INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(20),
  id_usuario INT,
  PRIMARY KEY (id_rol)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

INSERT INTO rol (id_rol, nombre, id_usuario) VALUES
 (1, 'ADMIN', 1), (2, 'VENDEDOR', 1), (3, 'USER', 1),
 (4, 'VENDEDOR', 2), (5, 'USER', 2),
 (6, 'USER', 3);


CREATE TABLE ruta (
    id_ruta INT AUTO_INCREMENT NOT NULL,
    patron VARCHAR(255) NOT NULL,
    rol_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (id_ruta))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

INSERT INTO ruta (patron, rol_name) VALUES 
('/producto/nuevo', 'ADMIN'),
('/producto/guardar', 'ADMIN'),
('/producto/modificar/**', 'ADMIN'),
('/producto/eliminar/**', 'ADMIN'),
('/categoria/nuevo', 'ADMIN'),
('/categoria/guardar', 'ADMIN'),
('/categoria/modificar/**', 'ADMIN'),
('/categoria/eliminar/**', 'ADMIN'),
('/usuario/**', 'ADMIN'),
('/constante/**', 'ADMIN'),
('/role/**', 'ADMIN'),
('/usuario_role/**', 'ADMIN'),
('/ruta/**', 'ADMIN'),
('/producto/listado', 'VENDEDOR'),
('/categoria/listado', 'VENDEDOR'),
('/pruebas/**', 'VENDEDOR'),
('/reportes/**', 'VENDEDOR'),
('/facturar/carrito', 'USER'),
('/payment/**', 'USER');

CREATE TABLE ruta_permit (
    id_ruta INT AUTO_INCREMENT NOT NULL,
    patron VARCHAR(255) NOT NULL,
    PRIMARY KEY (id_ruta))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

INSERT INTO ruta_permit (patron) VALUES 
('/'),
('/index'),
('/errores/**'),
('/carrito/**'),
('/registro/**'),
('/js/**'),
('/webjars/**');

CREATE TABLE constante (
    id_constante INT AUTO_INCREMENT NOT NULL,
    atributo VARCHAR(25) NOT NULL,
    valor VARCHAR(150) NOT NULL,
    PRIMARY KEY (id_constante))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

INSERT INTO constante (atributo, valor) VALUES 
('dominio', 'localhost'),
('certificado', 'c:/cert'),
('dolar', '520.75'),
('paypal.client-id', 'AUjOjw5Q1I0QLTYjbvRS0j4Amd8xrUU2yL9UYyb3TOTcrazzd3G3lYRc6o7g9rOyZkfWEj2wxxDi0aRz'),
('paypal.client-secret', 'EMdb08VRlo8Vusd_f4aAHRdTE14ujnV9mCYPovSmXCquLjzWd_EbTrRrNdYrF1-C4D4o-57wvua3YD2u'),
('paypal.mode', 'sandbox'),
('urlPaypalCancel', 'http://localhost/payment/cancel'),
('urlPaypalSuccess', 'http://localhost/payment/success');

-- Modificamos la tabla factura para incluir referencias a dirección y método de pago
ALTER TABLE factura 
ADD COLUMN id_direccion INT AFTER id_usuario,
ADD COLUMN id_metodo_pago INT AFTER id_direccion,
ADD FOREIGN KEY (id_direccion) REFERENCES direccion(id_direccion),
ADD FOREIGN KEY (id_metodo_pago) REFERENCES metodo_pago(id_metodo_pago);

-- Actualizamos las facturas existentes con los nuevos campos
UPDATE factura f
JOIN direccion d ON f.id_usuario = d.id_usuario AND d.es_principal = TRUE
JOIN metodo_pago m ON f.id_usuario = m.id_usuario AND m.es_principal = TRUE
SET f.id_direccion = d.id_direccion, f.id_metodo_pago = m.id_metodo_pago;
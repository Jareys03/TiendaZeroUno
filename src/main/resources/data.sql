-- ========================================================
--  POBLACIÓN INICIAL PARA H2 (mdai)
--  Tablas confirmadas: usuarios, producto, pedido
--  Bloques opcionales comentados al final
-- ========================================================

-- ==============================
-- USUARIOS
-- ==============================
INSERT INTO usuarios (nombre, correo) VALUES ('Javier', 'javier@zerouno.com');
INSERT INTO usuarios (nombre, correo) VALUES ('Laura',  'laura@zerouno.com');
INSERT INTO usuarios (nombre, correo) VALUES ('Carlos', 'carlos@zerouno.com');
INSERT INTO usuarios (nombre, correo) VALUES ('Luis',   'luis@zerouno.com');
INSERT INTO usuarios (nombre, correo) VALUES ('Pedro',  'pedro@zerouno.com');
INSERT INTO usuarios (nombre, correo) VALUES ('Antonio','antonio@zerouno.com');
INSERT INTO usuarios (nombre, correo) VALUES ('Maria',  'maria@zerouno.com');
INSERT INTO usuarios (nombre, correo) VALUES ('Lucía',  'lucia@zerouno.com');
INSERT INTO usuarios (nombre, correo) VALUES ('Marta',  'marta@zerouno.com');
INSERT INTO usuarios (nombre, correo) VALUES ('Sergio', 'sergio@zerouno.com');

-- ==============================
-- PRODUCTO (singular)
-- ==============================
INSERT INTO producto (nombre, precio) VALUES ('Portátil Lenovo IdeaPad 3', 599.99);
INSERT INTO producto (nombre, precio) VALUES ('Monitor Samsung 27"',       199.90);
INSERT INTO producto (nombre, precio) VALUES ('Ratón Logitech M185',         25.99);
INSERT INTO producto (nombre, precio) VALUES ('Teclado Mecánico Redragon',   65.49);
INSERT INTO producto (nombre, precio) VALUES ('Auriculares Sony WH-1000XM5',349.00);
INSERT INTO producto (nombre, precio) VALUES ('Disco SSD Samsung EVO 1TB',   89.99);
INSERT INTO producto (nombre, precio) VALUES ('Impresora HP LaserJet',      149.95);
INSERT INTO producto (nombre, precio) VALUES ('Silla gaming DXRacer',       229.90);
INSERT INTO producto (nombre, precio) VALUES ('Tablet Apple iPad Air',      699.00);
INSERT INTO producto (nombre, precio) VALUES ('Smartwatch Garmin Venu',     259.99);
INSERT INTO producto (nombre, precio) VALUES ('Cámara Canon EOS 250D',      679.00);
INSERT INTO producto (nombre, precio) VALUES ('Altavoces Logitech Z333',     79.90);
INSERT INTO producto (nombre, precio) VALUES ('Router TP-Link Archer C6',    49.95);
INSERT INTO producto (nombre, precio) VALUES ('Memoria USB Kingston 64GB',   12.99);
INSERT INTO producto (nombre, precio) VALUES ('Mochila Targus 15.6"',        45.50);

-- ==============================
-- PEDIDO
-- ==============================
INSERT INTO pedido (numero, total) VALUES ('P-0001',  825.88);
INSERT INTO pedido (numero, total) VALUES ('P-0002',  199.90);
INSERT INTO pedido (numero, total) VALUES ('P-0003', 1234.50);
INSERT INTO pedido (numero, total) VALUES ('P-0004',   59.99);
INSERT INTO pedido (numero, total) VALUES ('P-0005',  679.00);
INSERT INTO pedido (numero, total) VALUES ('P-0006',  145.40);
INSERT INTO pedido (numero, total) VALUES ('P-0007',  249.00);
INSERT INTO pedido (numero, total) VALUES ('P-0008', 1099.00);
INSERT INTO pedido (numero, total) VALUES ('P-0009',   39.99);
INSERT INTO pedido (numero, total) VALUES ('P-0010',  999.00);


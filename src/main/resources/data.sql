SET REFERENTIAL_INTEGRITY FALSE;
DELETE FROM ITEM_CARRITO;
DELETE FROM CARRITO;
DELETE FROM DIRECCIONES;
DELETE FROM PEDIDO;
DELETE FROM PRODUCTO;
DELETE FROM CATEGORIA;
DELETE FROM USUARIOS;
SET REFERENTIAL_INTEGRITY TRUE;
-- 1) CATEGORIAS (15 en total)
INSERT INTO CATEGORIA (ID, NOMBRE) VALUES
                                       (1, 'Portátiles'),
                                       (2, 'Monitores'),
                                       (3, 'Periféricos'),
                                       (4, 'Audio'),
                                       (5, 'Almacenamiento'),
                                       (6, 'Impresión'),
                                       (7, 'Sillas'),
                                       (8, 'Tablets'),
                                       (9, 'Wearables'),
                                       (10, 'Cámaras'),
                                       (11, 'Accesorios'),
                                       (12, 'Componentes'),
                                       (13, 'Redes'),
                                       (14, 'Gaming'),
                                       (15, 'Smart Home');

-- 2) PRODUCTOS
INSERT INTO PRODUCTO (ID, NOMBRE, PRECIO) VALUES
                                              -- Portátiles y monitores
                                              (1, 'Portátil Lenovo IdeaPad 3', 599.99),
                                              (2, 'Monitor Samsung 27"', 199.99),
                                              (25, 'Monitor LG Ultrawide 34"', 349.00),

                                              -- Periféricos
                                              (3, 'Ratón Logitech M185', 25.99),
                                              (4, 'Teclado Mecánico Redragon', 65.49),
                                              (23, 'Auriculares Logitech G Pro X', 139.00),

                                              -- Audio y accesorios
                                              (5, 'Auriculares Sony WH-1000XM5', 349.00),
                                              (12, 'Altavoces Logitech Z333', 79.90),
                                              (21, 'Altavoz Bluetooth JBL Flip 6', 119.00),
                                              (15, 'Mochila Targus 15.6"', 45.50),

                                              -- Almacenamiento
                                              (6, 'Disco SSD Samsung EVO 1TB', 89.99),
                                              (19, 'Disco Duro Seagate 2TB', 65.00),
                                              (14, 'Memoria USB Kingston 64GB', 12.99),

                                              -- Impresión
                                              (7, 'Impresora HP LaserJet', 149.95),

                                              -- Sillas y gaming
                                              (8, 'Silla gaming DXRacer', 229.90),
                                              (16, 'Tarjeta gráfica RTX 4070', 729.00),

                                              -- Tablets y wearables
                                              (9, 'Tablet Apple iPad Air', 699.00),
                                              (10, 'Smartwatch Garmin Venu', 259.99),
                                              (24, 'Tablet Samsung Galaxy Tab S9', 849.00),

                                              -- Cámaras
                                              (11, 'Cámara Canon EOS 250D', 679.00),
                                              (22, 'Cámara de acción GoPro Hero 11', 429.00),

                                              -- Componentes y redes
                                              (17, 'Placa base ASUS Prime B550M', 159.99),
                                              (18, 'Procesador AMD Ryzen 7 5800X', 349.90),
                                              (13, 'Router TP-Link Archer C6', 49.95),
                                              (20, 'Cable HDMI 2.1 de 2m', 14.99);


-- Asignación de categorías a productos por ID (uso UPDATE para no cambiar la estructura original)
UPDATE PRODUCTO SET CATEGORIA_ID = 1 WHERE ID IN (1);
UPDATE PRODUCTO SET CATEGORIA_ID = 2 WHERE ID IN (2,25);
UPDATE PRODUCTO SET CATEGORIA_ID = 3 WHERE ID IN (3,4,23);
UPDATE PRODUCTO SET CATEGORIA_ID = 4 WHERE ID IN (5);
UPDATE PRODUCTO SET CATEGORIA_ID = 11 WHERE ID IN (15);
UPDATE PRODUCTO SET CATEGORIA_ID = 5 WHERE ID IN (6,19,14);
UPDATE PRODUCTO SET CATEGORIA_ID = 6 WHERE ID IN (7);
UPDATE PRODUCTO SET CATEGORIA_ID = 7 WHERE ID IN (8);
UPDATE PRODUCTO SET CATEGORIA_ID = 14 WHERE ID IN (16);
UPDATE PRODUCTO SET CATEGORIA_ID = 8 WHERE ID IN (9,24);
UPDATE PRODUCTO SET CATEGORIA_ID = 9 WHERE ID IN (10);
UPDATE PRODUCTO SET CATEGORIA_ID = 10 WHERE ID IN (11,22);
UPDATE PRODUCTO SET CATEGORIA_ID = 12 WHERE ID IN (17,18);
UPDATE PRODUCTO SET CATEGORIA_ID = 13 WHERE ID IN (13);
UPDATE PRODUCTO SET CATEGORIA_ID = 11 WHERE ID IN (20);


-- 3) USUARIOS
-- Se asigna una contraseña por defecto 'password' a todos los usuarios seed (contraseña en texto plano por simplicidad)
INSERT INTO USUARIOS (ID, NOMBRE, CORREO, PASSWORD) VALUES
                                                        (1001, 'Javier', 'javier@example.com', 'pass1'),
                                                        (1002, 'Laura', 'laura@example.com', 'pass2'),
                                                        (1003, 'Carlos', 'carlos@example.com', 'pass3'),
                                                        (1004, 'Ana', 'ana@example.com', 'pass4'),
                                                        (1005, 'María', 'maria@example.com', 'pass5');
-- Usuario administrador
INSERT INTO usuarios (id, nombre, correo, password) VALUES
                                                        (100, 'admin', 'admin@zerouno.com', 'admin');


-- 4) CARRITOS
INSERT INTO CARRITO (ID, USUARIO_ID) VALUES
                                         (1, 1001), (2, 1002), (3, 1003), (4, 1004), (5, 1005);

-- 5) ITEM_CARRITO (sin ID, lo genera H2)
INSERT INTO ITEM_CARRITO (CANTIDAD, PRECIO_UNITARIO, CARRITO_ID, PRODUCTO_ID) VALUES
                                                                                  -- Javier
                                                                                  (1, 599.99, 1, 1),
                                                                                  (1, 199.99, 1, 2),
                                                                                  (2, 25.99, 1, 3),
                                                                                  -- Laura
                                                                                  (1, 349.00, 2, 5),
                                                                                  (1, 229.90, 2, 8),
                                                                                  (1, 79.90, 2, 12),
                                                                                  -- Carlos
                                                                                  (1, 729.00, 3, 16),
                                                                                  (1, 65.00, 3, 19),
                                                                                  -- Ana
                                                                                  (2, 14.99, 4, 20),
                                                                                  (1, 119.00, 4, 21),
                                                                                  -- María
                                                                                  (1, 849.00, 5, 24),
                                                                                  (1, 349.00, 5, 25);

-- 6) DIRECCIONES (una por usuario)
INSERT INTO DIRECCIONES (ID, CALLE, CIUDAD, USUARIO_ID) VALUES
                                                            (1, 'Avda. del Circuito 1', 'Badajoz', 1001),
                                                            (2, 'C/ Boxes 23', 'Cáceres', 1002),
                                                            (3, 'Avda. de la Velocidad 99', 'Mérida', 1003),
                                                            (4, 'C/ Pole Position 3', 'Plasencia', 1004),
                                                            (5, 'C/ Curva Rápida 45', 'Don Benito', 1005);

-- 7) PEDIDOS
INSERT INTO PEDIDO (ID, NUMERO, TOTAL) VALUES
                                           (1, 'P-0001', 1025.96),
                                           (2, 'P-0002', 658.80),
                                           (3, 'P-0003', 794.00),
                                           (4, 'P-0004', 133.99),
                                           (5, 'P-0005', 1198.00);

-- 8) DETALLES DE PEDIDO
-- Los importes de las líneas están calculados para que coincidan con el TOTAL del pedido

INSERT INTO DETALLE_PEDIDO (PEDIDO_ID, PRODUCTO_ID, CANTIDAD, PRECIO_UNITARIO) VALUES
                                                                                   -- Pedido 1: 599,99 + 2 * 199,99 + 25,99 = 1.025,96
                                                                                   (1, 1, 1, 599.99),   -- Portátil Lenovo IdeaPad 3
                                                                                   (1, 2, 2, 199.99),   -- 2 x Monitor Samsung 27"
                                                                                   (1, 3, 1, 25.99),    -- Ratón Logitech M185

                                                                                   -- Pedido 2: 349,00 + 79,90 + 229,90 = 658,80
                                                                                   (2, 5, 1, 349.00),   -- Auriculares Sony WH-1000XM5
                                                                                   (2, 12, 1, 79.90),   -- Altavoces Logitech Z333
                                                                                   (2, 8, 1, 229.90),   -- Silla gaming DXRacer

                                                                                   -- Pedido 3: 729,00 + 65,00 = 794,00
                                                                                   (3, 16, 1, 729.00),  -- Tarjeta gráfica RTX 4070
                                                                                   (3, 19, 1, 65.00),   -- Disco Duro Seagate 2TB

                                                                                   -- Pedido 4: 119,00 + 14,99 = 133,99
                                                                                   (4, 21, 1, 119.00),  -- Altavoz Bluetooth JBL Flip 6
                                                                                   (4, 20, 1, 14.99),   -- Cable HDMI 2.1 de 2m

                                                                                   -- Pedido 5: 849,00 + 349,00 = 1.198,00
                                                                                   (5, 24, 1, 849.00),  -- Tablet Samsung Galaxy Tab S9
                                                                                   (5, 25, 1, 349.00);  -- Monitor LG Ultrawide 34"



-- Ajustes: reiniciar los contadores IDENTITY para evitar colisiones en H2
ALTER TABLE USUARIOS ALTER COLUMN ID RESTART WITH 1006;
ALTER TABLE DIRECCIONES ALTER COLUMN ID RESTART WITH 6;
ALTER TABLE PRODUCTO ALTER COLUMN ID RESTART WITH 26;
ALTER TABLE CATEGORIA ALTER COLUMN ID RESTART WITH 16;
ALTER TABLE CARRITO ALTER COLUMN ID RESTART WITH 6;
ALTER TABLE PEDIDO ALTER COLUMN ID RESTART WITH 6;
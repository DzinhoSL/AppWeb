-- =============================================================
-- SCHEMA Y DATOS INICIALES - App Check-List Panel Sandwich
-- =============================================================

-- Tabla de almacenes
CREATE TABLE almacenes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

-- Tabla de usuarios
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    rol VARCHAR(50),
    id_almacen INT,
    FOREIGN KEY (id_almacen) REFERENCES almacenes(id)
);

-- Tabla de las maquinas
CREATE TABLE maquinas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    id_almacen INT,
    FOREIGN KEY (id_almacen) REFERENCES almacenes(id)
);

-- Tabla de items checklist
CREATE TABLE checklist_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_maquina INT,
    descripcion VARCHAR(255) NOT NULL,
    FOREIGN KEY (id_maquina) REFERENCES maquinas(id)
);

-- Tabla de revisiones
CREATE TABLE revisiones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT,
    id_maquina INT,
    fecha_hora DATETIME DEFAULT CURRENT_TIMESTAMP,
    firma TEXT,
    tiene_fallos BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    FOREIGN KEY (id_maquina) REFERENCES maquinas(id)
);

-- Tabla detalles revision
CREATE TABLE detalles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_revision INT,
    id_item INT,
    resultado BOOLEAN,
    comentario TEXT,
    FOREIGN KEY (id_revision) REFERENCES revisiones(id),
    FOREIGN KEY (id_item) REFERENCES checklist_items(id)
);

-- =============================================================
-- DATOS INICIALES
-- =============================================================

-- Almacenes
INSERT INTO almacenes (nombre) VALUES
    ('Almacén Central'),
    ('Almacén Norte'),
    ('Almacén Sur'),
    ('Almacén Este');

-- Usuarios
INSERT INTO usuarios (nombre, rol, id_almacen) VALUES
    ('Juan Hernández', 'Operario', 1),
    ('María García', 'Operario', 1),
    ('Pedro López', 'Supervisor', 1),
    ('Ana Martínez', 'Operario', 1),
    ('Carlos Ruiz', 'Operario', 2),
    ('Laura Sánchez', 'Operario', 2),
    ('David Fernández', 'Jefe', 2),
    ('Isabel Torres', 'Operario', 3);

-- Máquinas (todas en almacén 1 de ejemplo)
INSERT INTO maquinas (nombre, id_almacen) VALUES
    ('Puente Grúa', 1),
    ('Sierra de Corte', 1),
    ('Máquina 4 Caminos', 1),
    ('Puente Grúa', 2),
    ('Sierra de Corte', 2),
    ('Máquina 4 Caminos', 2);

-- Items Checklist - Puente Grúa (id_maquina = 1)
INSERT INTO checklist_items (id_maquina, descripcion) VALUES
    (1, 'Verificación funcionamiento botonera'),
    (1, 'Carril de rodadura longitudinal correcto'),
    (1, 'Movimiento de elevación fin de carrera'),
    (1, 'Pestillo de seguridad del gancho correcto'),
    (1, 'Cables eléctricos en buen estado'),
    (1, 'Mando tiene claramente indicadas las funciones'),
    (1, 'Desplazamiento lateral sin ruidos anómalos');

-- Items Checklist - Sierra de Corte (id_maquina = 2)
INSERT INTO checklist_items (id_maquina, descripcion) VALUES
    (2, 'Verificación funcionamiento botonera'),
    (2, 'Dirección rotación disco hacia dirección corte'),
    (2, 'Estado del cableado de alimentación'),
    (2, 'Estado cableado de maquina'),
    (2, 'Estado del carro de corte desde parte trasera a parte delantera'),
    (2, 'Ruidos anómalos al encender la máquina de corte'),
    (2, 'Estado de los sargentos de presión'),
    (2, 'Estado de los carros de alimentación de la sierra'),
    (2, 'Estado de las mesas de alimentación');

-- Items Checklist - Máquina 4 Caminos (id_maquina = 3)
INSERT INTO checklist_items (id_maquina, descripcion) VALUES
    (3, 'Estado de neumáticos'),
    (3, 'Estado del mástil'),
    (3, 'Estado de las palas y su carro'),
    (3, 'Inspección visual de la botonera de la maquina'),
    (3, 'Revisión de funcionamiento de todos los controles de la máquina'),
    (3, 'Estado de las luces de seguridad y protección'),
    (3, 'Inspección visual de la batería'),
    (3, 'Inspección del cinturón de seguridad');

-- Items Checklist - Puente Grúa Almacén 2 (id_maquina = 4)
INSERT INTO checklist_items (id_maquina, descripcion) VALUES
    (4, 'Verificación funcionamiento botonera'),
    (4, 'Carril de rodadura longitudinal correcto'),
    (4, 'Movimiento de elevación fin de carrera'),
    (4, 'Pestillo de seguridad del gancho correcto'),
    (4, 'Cables eléctricos en buen estado'),
    (4, 'Mando tiene claramente indicadas las funciones'),
    (4, 'Desplazamiento lateral sin ruidos anómalos');

-- Items Checklist - Sierra de Corte Almacén 2 (id_maquina = 5)
INSERT INTO checklist_items (id_maquina, descripcion) VALUES
    (5, 'Verificación funcionamiento botonera'),
    (5, 'Dirección rotación disco hacia dirección corte'),
    (5, 'Estado del cableado de alimentación'),
    (5, 'Estado cableado de maquina'),
    (5, 'Estado del carro de corte desde parte trasera a parte delantera'),
    (5, 'Ruidos anómalos al encender la máquina de corte'),
    (5, 'Estado de los sargentos de presión'),
    (5, 'Estado de los carros de alimentación de la sierra'),
    (5, 'Estado de las mesas de alimentación');

-- Items Checklist - Máquina 4 Caminos Almacén 2 (id_maquina = 6)
INSERT INTO checklist_items (id_maquina, descripcion) VALUES
    (6, 'Estado de neumáticos'),
    (6, 'Estado del mástil'),
    (6, 'Estado de las palas y su carro'),
    (6, 'Inspección visual de la botonera de la maquina'),
    (6, 'Revisión de funcionamiento de todos los controles de la máquina'),
    (6, 'Estado de las luces de seguridad y protección'),
    (6, 'Inspección visual de la batería'),
    (6, 'Inspección del cinturón de seguridad');

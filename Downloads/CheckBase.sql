

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

-- Tabla detalles revison
CREATE TABLE detalles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_revision INT,
    id_item INT,
    resultado BOOLEAN, 
    comentario TEXT, 
    FOREIGN KEY (id_revision) REFERENCES revisiones(id),
    FOREIGN KEY (id_item) REFERENCES checklist_items(id)
);


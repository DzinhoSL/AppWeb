package com.panelsandwich.checklist.basedatos

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatosLocal(context: Context) : SQLiteOpenHelper(context, "checklist.db", null, 4) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS almacenes (
                id INTEGER PRIMARY KEY,
                nombre TEXT NOT NULL
            )
        """)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS usuarios (
                id INTEGER PRIMARY KEY,
                nombre TEXT NOT NULL,
                rol TEXT,
                id_almacen INTEGER
            )
        """)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS maquinas (
                id INTEGER PRIMARY KEY,
                nombre TEXT NOT NULL,
                id_almacen INTEGER
            )
        """)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS checklist_items (
                id INTEGER PRIMARY KEY,
                id_maquina INTEGER,
                descripcion TEXT NOT NULL
            )
        """)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS revisiones (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_usuario INTEGER,
                id_maquina INTEGER,
                fecha_hora TEXT,
                firma TEXT,
                tiene_fallos INTEGER DEFAULT 0,
                sincronizado INTEGER DEFAULT 0,
                id_servidor INTEGER DEFAULT NULL,
                comentario TEXT,
                preguntas_fallidas TEXT DEFAULT '[]'
            )
        """)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS detalles (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_revision INTEGER,
                id_item INTEGER,
                resultado INTEGER,
                sincronizado INTEGER DEFAULT 0
            )
        """)
        cargarDatosIniciales(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            // Migración para la versión 3 (comentario y sincronización)
            try { db.execSQL("ALTER TABLE revisiones ADD COLUMN comentario TEXT") } catch (_: Exception) {}
            try { db.execSQL("ALTER TABLE revisiones ADD COLUMN sincronizado INTEGER DEFAULT 0") } catch (_: Exception) {}
            try { db.execSQL("ALTER TABLE revisiones ADD COLUMN id_servidor INTEGER DEFAULT NULL") } catch (_: Exception) {}
        }

        if (oldVersion < 4) {
            // Migración para la versión 4 (preguntas_fallidas)
            try { db.execSQL("ALTER TABLE revisiones ADD COLUMN preguntas_fallidas TEXT DEFAULT \'[]\'") } catch (_: Exception) {}
        }
    }

    private fun cargarDatosIniciales(db: SQLiteDatabase) {
        // Almacenes
        db.execSQL("INSERT OR IGNORE INTO almacenes VALUES (1, \'Almacén Central\')")
        db.execSQL("INSERT OR IGNORE INTO almacenes VALUES (2, \'Almacén Norte\')")
        db.execSQL("INSERT OR IGNORE INTO almacenes VALUES (3, 'Almacén Sur')")
        db.execSQL("INSERT OR IGNORE INTO almacenes VALUES (4, 'Almacén Este')")

        // Usuarios
        db.execSQL("INSERT OR IGNORE INTO usuarios VALUES (1, 'Juan Hernández', 'Operario', 1)")
        db.execSQL("INSERT OR IGNORE INTO usuarios VALUES (2, 'María García', 'Operario', 1)")
        db.execSQL("INSERT OR IGNORE INTO usuarios VALUES (3, 'Pedro López', 'Supervisor', 1)")
        db.execSQL("INSERT OR IGNORE INTO usuarios VALUES (4, 'Ana Martínez', 'Operario', 1)")
        db.execSQL("INSERT OR IGNORE INTO usuarios VALUES (5, 'Carlos Ruiz', 'Operario', 2)")
        db.execSQL("INSERT OR IGNORE INTO usuarios VALUES (6, 'Laura Sánchez', 'Operario', 2)")
        db.execSQL("INSERT OR IGNORE INTO usuarios VALUES (7, 'David Fernández', 'Jefe', 2)")
        db.execSQL("INSERT OR IGNORE INTO usuarios VALUES (8, 'Isabel Torres', 'Operario', 3)")

        // Máquinas
        db.execSQL("INSERT OR IGNORE INTO maquinas VALUES (1, 'Puente Grúa', 1)")
        db.execSQL("INSERT OR IGNORE INTO maquinas VALUES (2, 'Sierra de Corte', 1)")
        db.execSQL("INSERT OR IGNORE INTO maquinas VALUES (3, 'Máquina 4 Caminos', 1)")
        db.execSQL("INSERT OR IGNORE INTO maquinas VALUES (4, 'Puente Grúa', 2)")
        db.execSQL("INSERT OR IGNORE INTO maquinas VALUES (5, 'Sierra de Corte', 2)")
        db.execSQL("INSERT OR IGNORE INTO maquinas VALUES (6, 'Máquina 4 Caminos', 2)")

        // Checklist Items - Puente Grúa (id_maquina 1 y 4)
        listOf(1, 4).forEachIndexed { mIdx, mId ->
            val base = mIdx * 7
            db.execSQL("INSERT OR IGNORE INTO checklist_items VALUES (${base+1}, $mId, 'Verificación funcionamiento botonera')")
            db.execSQL("INSERT OR IGNORE INTO checklist_items VALUES (${base+2}, $mId, 'Carril de rodadura longitudinal correcto')")
            db.execSQL("INSERT OR IGNORE INTO checklist_items VALUES (${base+3}, $mId, 'Movimiento de elevación fin de carrera')")
            db.execSQL("INSERT OR IGNORE INTO checklist_items VALUES (${base+4}, $mId, 'Pestillo de seguridad del gancho correcto')")
            db.execSQL("INSERT OR IGNORE INTO checklist_items VALUES (${base+5}, $mId, 'Cables eléctricos en buen estado')")
            db.execSQL("INSERT OR IGNORE INTO checklist_items VALUES (${base+6}, $mId, 'Mando tiene claramente indicadas las funciones')")
            db.execSQL("INSERT OR IGNORE INTO checklist_items VALUES (${base+7}, $mId, 'Desplazamiento lateral sin ruidos anómalos')")


        // Checklist Items - Sierra de Corte (id_maquina 2 y 5)
        listOf(2, 5).forEachIndexed { mIdx, mId ->
            val base = 14 + mIdx * 9
            db.execSQL("INSERT OR IGNORE INTO checklist_items VALUES (${base+1}, $mId, 'Verificación funcionamiento botonera')")
            db.execSQL("INSERT OR IGNORE INTO checklist_items VALUES (${base+2}, $mId, 'Dirección rotación disco hacia dirección corte')")
            db.execSQL("INSERT OR IGNORE INTO checklist_items VALUES (${base+3}, $mId, 'Estado del cableado de alimentación')")
            db.execSQL("INSERT OR IGNORE INTO checklist_items VALUES (${base+4}, $mId, 'Estado cableado de maquina')")
            db.execSQL("INSERT OR IGNORE INTO checklist_items VALUES (${base+5}, $mId, 'Estado del carro de corte')")
            db.execSQL("INSERT OR IGNORE INTO checklist_items VALUES (${base+6}, $mId, 'Ruidos anómalos al encender la máquina')")
            db.execSQL("INSERT OR IGNORE INTO checklist_items VALUES (${base+7}, $mId, 'Estado de los sargentos de presión')")
            db.execSQL("INSERT OR IGNORE INTO checklist_items VALUES (${base+8}, $mId, 'Estado de los carros de alimentación')")
            db.execSQL("INSERT OR IGNORE INTO checklist_items VALUES (${base+9}, $mId, 'Estado de las mesas de alimentación')")


        // Checklist Items - Máquina 4 Caminos (id_maquina 3 y 6)
        listOf(3, 6).forEachIndexed { mIdx, mId ->
            val base = 32 + mIdx * 8
            db.execSQL("INSERT OR IGNORE INTO checklist_items VALUES (${base+1}, $mId, 'Estado de neumáticos')")
            db.execSQL("INSERT OR IGNORE INTO checklist_items VALUES (${base+2}, $mId, 'Estado del mástil')")
            db.execSQL("INSERT OR IGNORE INTO checklist_items VALUES (${base+3}, $mId, 'Estado de las palas y su carro')")
            db.execSQL("INSERT OR IGNORE INTO checklist_items VALUES (${base+4}, $mId, 'Inspección visual de la botonera')")
            db.execSQL("INSERT OR IGNORE INTO checklist_items VALUES (${base+5}, $mId, 'Revisión de todos los controles')")
            db.execSQL("INSERT OR IGNORE INTO checklist_items VALUES (${base+6}, $mId, 'Estado de las luces de seguridad')")
            db.execSQL("INSERT OR IGNORE INTO checklist_items VALUES (${base+7}, $mId, 'Inspección visual de la batería')")
            db.execSQL("INSERT OR IGNORE INTO checklist_items VALUES (${base+8}, $mId, 'Inspección del cinturón de seguridad')")

    }
}
        }
    }
}

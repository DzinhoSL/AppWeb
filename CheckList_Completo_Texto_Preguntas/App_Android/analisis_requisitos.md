# Análisis de Requisitos - App Check-List Panel Sandwich

## Flujo de Pantallas (Boceto PDF)

### Pantalla 1 - Selección de Almacén (PantallaAlmacenes)
- Fondo azul marino oscuro (#0D1B2A o similar)
- Logo "PANEL SANDWICH" con icono en la parte superior
- Grid 2x3 de botones redondeados con borde blanco
- Cada botón muestra el nombre del almacén en texto blanco en mayúsculas
- Los botones son de color azul más claro que el fondo

### Pantalla 2 - Selección de Usuario (PantallaUsuarios)
- Título: "ALMACÉN X" en la parte superior
- Logo Panel Sandwich
- Grid 2x2 de botones de usuario (mismo estilo que almacenes)
- Botón rojo "← ATRÁS" en la parte inferior

### Pantalla 3 - Menú Principal Usuario (PantallaMenuPrincipal)
- Título: "ALMACÉN X" + icono de usuario con iniciales (ej: J.H) en esquina superior derecha
- Logo Panel Sandwich
- Fecha y hora actual (ej: 19/03/26 - 8:03)
- Botón azul "NUEVO CHECK"
- Botón azul más oscuro "MIS CHECKS"
- Botón rojo "← ATRÁS"

### Pantalla 4 - Registro/Resumen de Checks (PantallaRegistro)
- Título: "REGISTRO" + icono usuario con iniciales
- Logo Panel Sandwich
- Fecha y hora
- Lista de máquinas con botones de colores:
  - ROJO: máquina con fallos
  - VERDE: máquina sin fallos
- Sección de comentarios por máquina (campos de texto)
- Botón rojo "← ATRÁS"

### Pantalla 5 - Lista de Maquinaria (PantallaMaquinaria)
- Título: "MAQUINARIA"
- Logo Panel Sandwich
- Lista de máquinas en botones azules (sin usuario visible aquí)
- Máquinas: PUENTE GRUA, SIERRA DE CORTE, MAQUINA 4 CAMINOS

### Pantalla 6 - Check de Máquina: Puente Grúa (PantallaCheckMaquina)
- Título: nombre de la máquina (ej: "PUENTE GRÚA")
- Logo Panel Sandwich
- Lista de ítems de checklist
- Cada ítem tiene: descripción + botones SI (verde) / NO (rojo)
- Si se marca NO → aparece campo "Descripcion Problema*" (obligatorio)
- Al final: campo "Firme aquí:" (área de firma táctil)
- Botón verde "ENVIAR"
- Botón rojo "MENU"

### Pantalla 7 - Sierra de Corte (misma estructura que Puente Grúa)
### Pantalla 8 - Máquina 4 Caminos (misma estructura que Puente Grúa)

---

## Checklist Items por Máquina (del PDF ProyectoPracticas)

### PUENTE GRUA
1. Verificación funcionamiento botonera
2. Carril de rodadura longitudinal correcto
3. Movimiento de elevación fin de carrera
4. Pestillo de seguridad del gancho correcto
5. Cables eléctricos en buen estado
6. Mando tiene claramente indicadas las funciones
7. Desplazamiento lateral sin ruidos anómalos

### SIERRA DE CORTE
1. Verificación funcionamiento botonera
2. Dirección rotación disco hacia dirección corte
3. Estado del cableado de alimentación
4. Estado cableado de maquina
5. Estado del carro de corte desde parte trasera a parte delantera
6. Ruidos anómalos al encender la máquina de corte
7. Estado de los sargentos de presión
8. Estado de los carros de alimentación de la sierra
9. Estado de las mesas de alimentación

### MAQUINA 4 CAMINOS
1. Estado de neumáticos
2. Estado del mástil
3. Estado de las palas y su carro
4. Inspección visual de la botonera de la maquina
5. Revisión de funcionamiento de todos los controles de la máquina
6. Estado de las luces de seguridad y protección
7. Inspección visual de la batería
8. Inspección del cinturón de seguridad

---

## Requisitos Funcionales (ProyectoPracticas.pdf)

### Acceso y Autenticación
- Selección de nodo logístico (almacén) - sin login con contraseña
- Selección de usuario desde un listado predefinido por almacén

### Gestión de Incidencias
- Visualización de listado de checks por elemento (Puente Grua, Sierra Corte y 4 Caminos)
- Posibilidad de marcar cada elemento como: SI / NO
- Si se marca algún fallo → se habilita sección de comentarios OBLIGATORIA
- Bloque de firma táctil obligatoria

### Notificaciones Automáticas
- Recordatorio por ausencia de registro:
  - A las 09:00 AM, si no se ha completado el check-list: envío automático de email a David (Jefe)
- Alerta por incidencias:
  - Si se detecta algún fallo → envía email a David (Jefe) con el detalle

### Plataformas
1. Aplicación Móvil (Android Studio)
   - Dirigida a operarios de almacén
   - Realización y envío de check-lists
   - Interfaz simple y rápida para uso diario
2. Aplicación Web (fuera del alcance actual)
   - Dirigida a control administrativo
   - Visualización de check-lists

---

## Base de Datos MySQL

### Tablas
- almacenes (id, nombre)
- usuarios (id, nombre, rol, id_almacen)
- maquinas (id, nombre, id_almacen)
- checklist_items (id, id_maquina, descripcion)
- revisiones (id, id_usuario, id_maquina, fecha_hora, firma, tiene_fallos)
- detalles (id, id_revision, id_item, resultado BOOLEAN, comentario TEXT)

---

## Paleta de Colores
- Fondo principal: Azul marino oscuro (#0D1B2A / #1A2B45)
- Botones almacén/usuario/máquina: Azul medio (#1E3A5F / #2B4D7A)
- Botón ATRÁS / MENU: Rojo (#C0392B / #CC0000)
- Botón ENVIAR: Verde (#27AE60 / #2E7D32)
- Botón SI: Verde (#2E7D32)
- Botón NO: Rojo (#C62828)
- Texto: Blanco (#FFFFFF)
- Bordes botones: Blanco con esquinas redondeadas

---

## Estructura de Carpetas Android (en español)

```
app/
  src/main/
    java/com/panelsandwich/checklist/
      actividades/          <- Activities
      adaptadores/          <- Adapters (RecyclerView)
      basedatos/            <- Room Database / Retrofit
        dao/
        entidades/
        red/                <- API calls
      modelos/              <- Data models
      utilidades/           <- Helpers, utils
      vistas/               <- Custom views (firma canvas)
    res/
      layout/               <- XML layouts
      values/               <- strings, colors, styles
      drawable/             <- icons, backgrounds
```

---

## Tecnologías a Usar
- Lenguaje: Java o Kotlin (preferible Kotlin)
- Base de datos local: Room (SQLite) para modo offline
- Conexión remota: Retrofit + MySQL (API REST)
- Firma táctil: Canvas personalizado
- Notificaciones email: WorkManager + API de email (o solo lógica de notificación)
- Arquitectura: MVVM

---

## Notas Importantes
- La app NO tiene login con contraseña, solo selección de almacén y usuario
- Los checks se guardan localmente y se sincronizan con MySQL
- La firma se guarda como texto (Base64) en la tabla revisiones.firma
- El campo tiene_fallos se calcula automáticamente si algún ítem tiene resultado=false
- Los comentarios son OBLIGATORIOS cuando tiene_fallos=true

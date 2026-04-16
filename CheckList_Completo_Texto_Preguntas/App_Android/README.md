# App Check-List Panel Sandwich

Aplicación Android para la gestión de check-lists de maquinaria industrial en almacenes de Panel Sandwich.

---

## Estructura del Proyecto

```
CheckListApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/panelsandwich/checklist/
│   │   │   ├── actividades/          ← Pantallas de la app (Activities)
│   │   │   │   ├── ActividadAlmacenes.kt        (Pantalla 1: Selección almacén)
│   │   │   │   ├── ActividadUsuarios.kt          (Pantalla 2: Selección usuario)
│   │   │   │   ├── ActividadMenuPrincipal.kt     (Pantalla 3: Menú principal)
│   │   │   │   ├── ActividadMaquinaria.kt        (Pantalla 4: Lista máquinas)
│   │   │   │   ├── ActividadCheckMaquina.kt      (Pantalla 5: Check de máquina)
│   │   │   │   └── ActividadRegistro.kt          (Pantalla 6: Historial checks)
│   │   │   ├── adaptadores/          ← Adaptadores RecyclerView
│   │   │   │   ├── AdaptadorBotonGrid.kt         (Grid almacenes/usuarios)
│   │   │   │   ├── AdaptadorItemsChecklist.kt    (Lista ítems SI/NO)
│   │   │   │   └── AdaptadorRegistroMaquinas.kt  (Lista máquinas con estado)
│   │   │   ├── basedatos/            ← Capa de datos
│   │   │   │   ├── red/
│   │   │   │   │   ├── ApiServicio.kt            (Interfaz Retrofit)
│   │   │   │   │   └── ClienteRetrofit.kt        (Configuración Retrofit)
│   │   │   │   └── Repositorio.kt               (Fuente única de datos)
│   │   │   ├── modelos/              ← Clases de datos
│   │   │   │   ├── Almacen.kt
│   │   │   │   ├── Usuario.kt
│   │   │   │   ├── Maquina.kt
│   │   │   │   ├── ItemChecklist.kt
│   │   │   │   ├── Revision.kt
│   │   │   │   ├── DetalleRevision.kt
│   │   │   │   └── EstadoRevisionMaquina.kt
│   │   │   ├── utilidades/           ← Clases de utilidad
│   │   │   │   ├── SesionManager.kt             (Gestión de sesión)
│   │   │   │   ├── Utilidades.kt                (Funciones helper)
│   │   │   │   └── TareaNotificacion.kt         (WorkManager notificaciones)
│   │   │   └── vistas/               ← Vistas personalizadas
│   │   │       └── VistaFirma.kt                (Canvas firma táctil)
│   │   ├── res/
│   │   │   ├── layout/               ← Archivos XML de pantallas
│   │   │   ├── values/               ← colors, strings, themes, dimens
│   │   │   └── drawable/             ← Fondos y formas
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── base_datos/
│   └── schema_y_datos.sql            ← Script MySQL con schema y datos iniciales
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## Flujo de Navegación

```
ActividadAlmacenes
    └─► ActividadUsuarios
            └─► ActividadMenuPrincipal
                    ├─► ActividadMaquinaria
                    │       └─► ActividadCheckMaquina
                    └─► ActividadRegistro
```

---

## Configuración Inicial

### 1. Base de Datos MySQL

Ejecutar el script SQL en tu servidor MySQL:

```bash
mysql -u root -p nombre_base_datos < base_datos/schema_y_datos.sql
```

### 2. API REST (Backend)

La app se conecta a una API REST. Configura la URL del servidor en:

```
app/src/main/java/com/panelsandwich/checklist/basedatos/red/ClienteRetrofit.kt
```

```kotlin
private const val BASE_URL = "http://TU_SERVIDOR:PUERTO/api/"
```

**Endpoints necesarios:**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/almacenes` | Lista todos los almacenes |
| GET | `/usuarios/almacen/{id}` | Usuarios de un almacén |
| GET | `/maquinas/almacen/{id}` | Máquinas de un almacén |
| GET | `/checklist_items/maquina/{id}` | Ítems de una máquina |
| POST | `/revisiones` | Crear nueva revisión |
| GET | `/revisiones/usuario/{id}` | Revisiones de un usuario |
| POST | `/detalles` | Crear detalle de revisión |
| GET | `/detalles/revision/{id}` | Detalles de una revisión |

### 3. Modo Offline (Sin servidor)

Si no hay conexión al servidor, la app usa datos de prueba predefinidos en `Repositorio.kt`. Esto permite probar la app sin backend.

### 4. Abrir en Android Studio

1. Abrir Android Studio
2. `File > Open` → seleccionar la carpeta `CheckListApp`
3. Esperar a que Gradle sincronice
4. Ejecutar en emulador o dispositivo físico (Android 7.0+)

---

## Funcionalidades Implementadas

| Funcionalidad | Estado |
|---------------|--------|
| Selección de almacén | ✅ |
| Selección de usuario por almacén | ✅ |
| Menú principal con fecha/hora | ✅ |
| Lista de máquinas del almacén | ✅ |
| Check-list con botones SI/NO | ✅ |
| Comentario obligatorio si hay fallo | ✅ |
| Firma táctil obligatoria | ✅ |
| Envío de check-list a la API | ✅ |
| Historial de checks (Mis Checks) | ✅ |
| Estado visual por máquina (rojo/verde) | ✅ |
| Datos de prueba offline | ✅ |
| Notificación a jefe si hay fallos | ✅ (requiere backend) |
| Recordatorio 09:00 AM | ✅ (WorkManager) |

---

## Paleta de Colores

| Color | Uso | Hex |
|-------|-----|-----|
| Azul marino oscuro | Fondo principal | `#0D1B2A` |
| Azul medio | Botones almacén/usuario/máquina | `#1E3A5F` |
| Azul oscuro | Botón "Mis Checks" | `#152B4A` |
| Rojo | Botón Atrás, máquina con fallos | `#C0392B` |
| Verde | Botón Enviar, máquina sin fallos | `#27AE60` |
| Blanco | Texto, bordes | `#FFFFFF` |

---

## Requisitos del Dispositivo

- Android 7.0 (API 24) o superior
- Conexión a Internet (para sincronización con servidor)
- Pantalla táctil (para firma)

---

## Tecnologías Utilizadas

- **Kotlin** — Lenguaje principal
- **Retrofit 2** — Comunicación con API REST
- **Gson** — Serialización JSON
- **WorkManager** — Tareas en segundo plano (notificaciones)
- **RecyclerView** — Listas dinámicas
- **Canvas API** — Vista de firma táctil personalizada
- **SharedPreferences** — Persistencia de sesión
- **Corrutinas** — Programación asíncrona

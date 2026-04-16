package com.panelsandwich.checklist.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.panelsandwich.checklist.R
import com.panelsandwich.checklist.modelos.ItemChecklist

/**
 * **AdaptadorItemsChecklist**: Este adaptador es fundamental para la `ActividadCheckMaquina`,
 * ya que se encarga de mostrar cada uno de los ítems que componen un checklist para una máquina específica.
 * Cada ítem se presenta con su descripción y dos botones: "SÍ" y "NO", permitiendo al usuario
 * registrar el estado de cada punto de la revisión.
 *
 * Además de mostrar los ítems, este adaptador gestiona el estado de las respuestas del usuario
 * y notifica a la actividad cuando hay cambios, lo que permite actualizar la interfaz (por ejemplo,
 * mostrar el campo de comentario si se detectan fallos).
 *
 * @param items La lista de objetos `ItemChecklist` que se deben mostrar en el RecyclerView.
 * @param alCambiarEstado Una función lambda que se invoca cada vez que el usuario responde a un ítem,
 *                        indicando que el estado del checklist ha cambiado y podría requerir una actualización de la UI.
 */
class AdaptadorItemsChecklist(
    private val items: List<ItemChecklist>,
    private val alCambiarEstado: () -> Unit
) : RecyclerView.Adapter<AdaptadorItemsChecklist.ViewHolder>() {

    // Callback para hacer scroll automático al siguiente ítem (lo ejecuta la Activity sobre el NestedScrollView)
    var alScrollSiguiente: ((posicion: Int) -> Unit)? = null

    // `resultados` es un mapa que almacena la respuesta de cada ítem del checklist.
    // la clave es el ID del ítem y el valor puede ser: `true` (SÍ), `false` (NO) o `null` (sin responder).
    private val resultados: MutableMap<Int, Boolean?> = mutableMapOf()

    /**
     * **ViewHolder**: Esta clase interna representa cada elemento individual del checklist.
     * Contiene las referencias a las vistas de cada ítem (la descripción y los botones SÍ/NO).
     * Su propósito es optimizar el rendimiento del `RecyclerView` al reciclar las vistas.
     *
     * @param vista La vista raíz del layout de un ítem del checklist (`item_checklist.xml`).
     */
    class ViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        // referencia al TextView que muestra la descripción del ítem.
        val descripcionItem: TextView = vista.findViewById(R.id.descripcionItem)
        // referencia al botón "SÍ".
        val botonSi: Button = vista.findViewById(R.id.botonSi)
        // referencia al botón "NO".
        val botonNo: Button = vista.findViewById(R.id.botonNo)
    }

    /**
     * `onCreateViewHolder`: Este método se llama cuando el `RecyclerView` necesita crear un nuevo `ViewHolder`.
     * Infla el layout de un ítem del checklist (`item_checklist.xml`) y lo envuelve en un `ViewHolder`.
     *
     * @param parent El `ViewGroup` al que se adjuntará la nueva vista después de que se vincule a una posición del adaptador.
     * @param viewType El tipo de vista del nuevo `ViewHolder` (no se usa en este adaptador simple).
     * @return Una nueva instancia de `ViewHolder` que contiene la vista del ítem.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // infla el layout `item_checklist.xml` para crear la vista de un solo elemento del checklist.
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checklist, parent, false)
        // devuelve un nuevo ViewHolder que contiene la vista inflada.
        return ViewHolder(vista)
    }

    /**
     * `onBindViewHolder`: Este método se llama para asociar los datos de un elemento con las vistas
     * de un `ViewHolder` específico. Aquí se actualiza la descripción del ítem, se configura
     * la apariencia de los botones SÍ/NO según la respuesta actual y se asignan los listeners de clic.
     *
     * @param holder El `ViewHolder` que debe ser actualizado para representar el contenido del elemento en la posición dada.
     * @param position La posición del elemento dentro del conjunto de datos del adaptador.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // obtiene el objeto `ItemChecklist` en la posición actual.
        val item = items[position]
        // obtiene el contexto de la vista para acceder a recursos como colores.
        val contexto = holder.itemView.context

        // establece la descripción del ítem en el TextView correspondiente.
        holder.descripcionItem.text = item.descripcion

        // obtiene el resultado actual para este ítem del mapa de resultados.
        val resultado = resultados[item.id]

        // actualiza la apariencia visual de los botones SÍ/NO según el resultado actual.
        actualizarBotones(holder, resultado, contexto)

        // configura el listener para el botón "SÍ".
        // al pulsarlo, se registra la respuesta como `true` (SÍ), se notifica al adaptador
        // que el ítem ha cambiado y se invoca el callback `alCambiarEstado`.
        holder.botonSi.setOnClickListener {
            resultados[item.id] = true
            notifyItemChanged(position)
            alCambiarEstado()
            // Auto-scroll al siguiente ítem vía callback de la Activity
            val siguientePos = position + 1
            if (siguientePos < items.size) {
                alScrollSiguiente?.invoke(siguientePos)
            }
        }

        // configura el listener para el botón "NO".
        // al pulsarlo, se registra la respuesta como `false` (NO), se notifica al adaptador
        // que el ítem ha cambiado y se invoca el callback `alCambiarEstado`.
        holder.botonNo.setOnClickListener {
            resultados[item.id] = false
            notifyItemChanged(position)
            alCambiarEstado()
            // Auto-scroll al siguiente ítem vía callback de la Activity
            val siguientePos = position + 1
            if (siguientePos < items.size) {
                alScrollSiguiente?.invoke(siguientePos)
            }
        }
    }

    /**
     * `actualizarBotones`: Este método privado se encarga de cambiar el color y la opacidad
     * de los botones "SÍ" y "NO" para reflejar visualmente la selección del usuario.
     *
     * @param holder El `ViewHolder` que contiene los botones a actualizar.
     * @param resultado El estado actual del ítem: `true` (SÍ), `false` (NO) o `null` (sin responder).
     * @param contexto El contexto de la aplicación, necesario para obtener los colores de los recursos.
     */
    private fun actualizarBotones(holder: ViewHolder, resultado: Boolean?, contexto: android.content.Context) {
        when (resultado) {
            true -> {
                // si la respuesta es SÍ, el botón SÍ se muestra opaco y verde, y el NO se atenúa.
                holder.botonSi.backgroundTintList =
                    ContextCompat.getColorStateList(contexto, R.color.boton_verde)
                holder.botonNo.backgroundTintList =
                    ContextCompat.getColorStateList(contexto, R.color.boton_rojo)
                holder.botonSi.alpha = 1.0f
                holder.botonNo.alpha = 0.4f
            }
            false -> {
                // si la respuesta es NO, el botón NO se muestra opaco y rojo, y el SÍ se atenúa.
                holder.botonSi.backgroundTintList =
                    ContextCompat.getColorStateList(contexto, R.color.boton_verde)
                holder.botonNo.backgroundTintList =
                    ContextCompat.getColorStateList(contexto, R.color.boton_rojo)
                holder.botonSi.alpha = 0.4f
                holder.botonNo.alpha = 1.0f
            }
            null -> {
                // si no hay respuesta, ambos botones se muestran opacos con sus colores predeterminados.
                holder.botonSi.backgroundTintList =
                    ContextCompat.getColorStateList(contexto, R.color.boton_verde)
                holder.botonNo.backgroundTintList =
                    ContextCompat.getColorStateList(contexto, R.color.boton_rojo)
                holder.botonSi.alpha = 1.0f
                holder.botonNo.alpha = 1.0f
            }
        }
    }

    /**
     * `getItemCount`: Este método devuelve el número total de elementos en el conjunto de datos
     * que el adaptador está gestionando. El `RecyclerView` utiliza este valor para saber cuántos
     * ítems debe mostrar.
     *
     * @return El número total de ítems en la lista `items`.
     */
    override fun getItemCount(): Int = items.size

    /**
     * `todosRespondidos`: Este método verifica si el usuario ha respondido a todos los ítems
     * del checklist. Es crucial para la validación antes de permitir avanzar a la siguiente etapa.
     *
     * @return `true` si todos los ítems tienen una respuesta (SÍ o NO), `false` en caso contrario.
     */
    fun todosRespondidos(): Boolean {
        // utiliza la función `all` de Kotlin para comprobar que para cada ítem, su resultado en el mapa
        // `resultados` no sea `null` (es decir, ha sido respondido).
        return items.all { resultados[it.id] != null }
    }

    /**
     * `hayFallos`: Este método verifica si se ha marcado algún ítem como "NO" en el checklist.
     * Es importante para determinar si se debe mostrar el campo de comentario general.
     *
     * @return `true` si al menos un ítem ha sido marcado como "NO", `false` en caso contrario.
     */
    fun hayFallos(): Boolean {
        // utiliza la función `any` de Kotlin para comprobar si algún valor en el mapa `resultados` es `false`.
        return resultados.values.any { it == false }
    }

    /**
     * `obtenerResultados`: Este método devuelve una copia inmutable del mapa de resultados.
     * Es utilizado por la actividad para obtener las respuestas del usuario y enviarlas
     * como parte de la revisión a la base de datos.
     *
     * @return Un `Map` donde la clave es el ID del ítem y el valor es `true` (SÍ), `false` (NO) o `null` (sin responder).
     */
    fun obtenerResultados(): Map<Int, Boolean?> = resultados.toMap()

    /**
     * Devuelve una lista con los textos descriptivos de las preguntas marcadas como "NO".
     */
    fun obtenerTextosFallidos(): List<String> {
        return items.filter { resultados[it.id] == false }.map { it.descripcion }
    }
}

package com.panelsandwich.checklist.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.panelsandwich.checklist.R

/**
 * **AdaptadorBotonGrid**: Este adaptador es un componente clave para mostrar listas de elementos
 * en formato de cuadrícula dentro de un `RecyclerView`. Su diseño es genérico, lo que permite
 * reutilizarlo para diferentes tipos de datos que necesiten ser presentados como botones en una cuadrícula,
 * como la lista de almacenes o la lista de usuarios.
 *
 * Recibe una lista de pares (ID, Nombre) y una función lambda que se ejecuta cuando el usuario
 * hace clic en uno de los botones de la cuadrícula. Esto lo hace muy flexible para manejar
 * interacciones de usuario de forma consistente.
 *
 * @param elementos Una lista de `Pair<Int, String>` donde `Int` es el ID del elemento y `String` es su nombre.
 * @param alHacerClic Una función lambda que se invoca cuando se hace clic en un botón, recibiendo el ID y el nombre del elemento.
 */
class AdaptadorBotonGrid(
    private val elementos: List<Pair<Int, String>>, // Representa una lista de elementos, cada uno con un ID (Int) y un nombre (String).
    private val alHacerClic: (Int, String) -> Unit // Una función que se ejecutará cuando se haga clic en un elemento, pasando su ID y nombre.
) : RecyclerView.Adapter<AdaptadorBotonGrid.ViewHolder>() {

    /**
     * **ViewHolder**: Esta clase interna representa cada elemento individual de la cuadrícula.
     * Contiene las referencias a las vistas de cada ítem (en este caso, solo un `TextView` que actúa como botón).
     * Su propósito es optimizar el rendimiento del `RecyclerView` al reciclar las vistas.
     *
     * @param vista La vista raíz del layout de un ítem de la cuadrícula (`item_boton_grid.xml`).
     */
    class ViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        // referencia al TextView dentro del layout del ítem, que mostrará el texto del botón.
        val textoBoton: TextView = vista.findViewById(R.id.textoBotonGrid)
    }

    /**
     * `onCreateViewHolder`: Este método se llama cuando el `RecyclerView` necesita crear un nuevo `ViewHolder`.
     * Infla el layout de un ítem de la cuadrícula (`item_boton_grid.xml`) y lo envuelve en un `ViewHolder`.
     *
     * @param parent El `ViewGroup` al que se adjuntará la nueva vista después de que se vincule a una posición del adaptador.
     * @param viewType El tipo de vista del nuevo `ViewHolder` (no se usa en este adaptador simple).
     * @return Una nueva instancia de `ViewHolder` que contiene la vista del ítem.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // infla el layout `item_boton_grid.xml` para crear la vista de un solo elemento de la cuadrícula.
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_boton_grid, parent, false)
        // devuelve un nuevo ViewHolder que contiene la vista inflada.
        return ViewHolder(vista)
    }

    /**
     * `onBindViewHolder`: Este método se llama para asociar los datos de un elemento con las vistas
     * de un `ViewHolder` específico. Aquí se actualiza el contenido del `TextView` y se configura
     * el listener de clic para el botón.
     *
     * @param holder El `ViewHolder` que debe ser actualizado para representar el contenido del elemento en la posición dada.
     * @param position La posición del elemento dentro del conjunto de datos del adaptador.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // obtiene el ID y el nombre del elemento en la posición actual de la lista.
        val (id, nombre) = elementos[position]
        // establece el texto del botón (TextView) con el nombre del elemento en mayúsculas.
        holder.textoBoton.text = nombre.uppercase()
        // configura el listener de clic para la vista completa del ítem y para el TextView.
        // esto asegura que el clic se detecte correctamente sin importar dónde se pulse dentro del área del botón.
        holder.itemView.setOnClickListener { alHacerClic(id, nombre) }
        holder.textoBoton.setOnClickListener { alHacerClic(id, nombre) }
    }

    /**
     * `getItemCount`: Este método devuelve el número total de elementos en el conjunto de datos
     * que el adaptador está gestionando. El `RecyclerView` utiliza este valor para saber cuántos
     * ítems debe mostrar.
     *
     * @return El número total de elementos en la lista `elementos`.
     */
    override fun getItemCount(): Int = elementos.size
}

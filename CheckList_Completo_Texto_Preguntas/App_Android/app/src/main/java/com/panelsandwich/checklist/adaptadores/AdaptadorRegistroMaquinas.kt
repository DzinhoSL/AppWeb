package com.panelsandwich.checklist.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.panelsandwich.checklist.R
import com.panelsandwich.checklist.modelos.EstadoRevisionMaquina

/**
 * **AdaptadorRegistroMaquinas**: Este adaptador es el encargado de presentar el historial
 * de revisiones de las máquinas en la `ActividadRegistro`. Su función es mostrar de forma clara
 * el estado de cada máquina (si tiene fallos o no) y, en caso de haberlos, el comentario
 * asociado a esos fallos.
 *
 * Cada elemento de la lista representa una máquina y su última revisión. El botón de la máquina
 * cambia de color (rojo si hay fallos, verde si está todo correcto, o azul si no hay revisión)
 * y, si hay fallos, se muestra un campo de texto con el comentario.
 *
 * @param estados Una lista de objetos `EstadoRevisionMaquina`, que encapsulan la máquina y el estado de su revisión.
 * @param alHacerClicMaquina Una función lambda que se invoca cuando se hace clic en el botón de una máquina.
 *                           Actualmente, no realiza ninguna acción, pero podría usarse para ver detalles de la revisión.
 */
class AdaptadorRegistroMaquinas(
    private val estados: List<EstadoRevisionMaquina>,
    private val alHacerClicMaquina: (EstadoRevisionMaquina) -> Unit
) : RecyclerView.Adapter<AdaptadorRegistroMaquinas.ViewHolder>() {

    /**
     * **ViewHolder**: Esta clase interna representa cada elemento individual en la lista de registro.
     * Contiene las referencias a las vistas de cada ítem (el botón de la máquina, la etiqueta del comentario
     * y el campo de texto del comentario). Su propósito es optimizar el rendimiento del `RecyclerView`
     * al reciclar las vistas.
     *
     * @param vista La vista raíz del layout de un ítem del registro (`item_registro_maquina.xml`).
     */
    class ViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        // referencia al botón que representa la máquina.
        val botonMaquina: Button = vista.findViewById(R.id.botonMaquinaRegistro)
        // referencia a la etiqueta del campo de comentario.
        val labelComentario: TextView = vista.findViewById(R.id.labelComentarioMaquina)
        // referencia al campo de texto donde se muestra el comentario.
        val campoComentario: EditText = vista.findViewById(R.id.campoComentarioMaquina)
    }

    /**
     * `onCreateViewHolder`: Este método se llama cuando el `RecyclerView` necesita crear un nuevo `ViewHolder`.
     * Infla el layout de un ítem del registro (`item_registro_maquina.xml`) y lo envuelve en un `ViewHolder`.
     *
     * @param parent El `ViewGroup` al que se adjuntará la nueva vista después de que se vincule a una posición del adaptador.
     * @param viewType El tipo de vista del nuevo `ViewHolder` (no se usa en este adaptador simple).
     * @return Una nueva instancia de `ViewHolder` que contiene la vista del ítem.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // infla el layout `item_registro_maquina.xml` para crear la vista de un solo elemento del registro.
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_registro_maquina, parent, false)
        // devuelve un nuevo ViewHolder que contiene la vista inflada.
        return ViewHolder(vista)
    }

    /**
     * `onBindViewHolder`: Este método se llama para asociar los datos de un elemento (`EstadoRevisionMaquina`)
     * con las vistas de un `ViewHolder` específico. Aquí se actualiza el texto del botón, se cambia su color
     * según el estado de la revisión y se muestra u oculta el campo de comentario.
     *
     * @param holder El `ViewHolder` que debe ser actualizado para representar el contenido del elemento en la posición dada.
     * @param position La posición del elemento dentro del conjunto de datos del adaptador.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // obtiene el objeto `EstadoRevisionMaquina` en la posición actual.
        val estado = estados[position]
        // obtiene el contexto de la vista para acceder a recursos como colores.
        val contexto = holder.itemView.context

        // establece el texto del botón con el nombre de la máquina en mayúsculas.
        holder.botonMaquina.text = estado.maquina.nombre.uppercase()

        // configura el color de fondo del botón de la máquina según el estado de la revisión:
        // - Rojo si `tieneFallos` es `true`.
        // - Verde si `tieneFallos` es `false`.
        // - Azul si `tieneFallos` es `null` (indicando que no hay revisión registrada).
        when (estado.tieneFallos) {
            true -> holder.botonMaquina.backgroundTintList =
                ContextCompat.getColorStateList(contexto, R.color.boton_rojo)
            false -> holder.botonMaquina.backgroundTintList =
                ContextCompat.getColorStateList(contexto, R.color.boton_verde)
            null -> holder.botonMaquina.backgroundTintList =
                ContextCompat.getColorStateList(contexto, R.color.boton_azul)
        }

        // controla la visibilidad y el contenido del campo de comentario.
        if (estado.tieneFallos == true) {
            // Si la máquina tiene fallos, hace visibles la etiqueta y el campo de comentario.
            holder.labelComentario.visibility = View.VISIBLE
            holder.campoComentario.visibility = View.VISIBLE
            // establece el texto de la etiqueta del comentario, incluyendo el nombre de la máquina.
            holder.labelComentario.text = "Comentario ${estado.maquina.nombre}*"
            // muestra el comentario asociado a la revisión en el campo de texto.
            holder.campoComentario.setText(estado.comentario)
        } else {
            // si no hay fallos, oculta la etiqueta y el campo de comentario.
            holder.labelComentario.visibility = View.GONE
            holder.campoComentario.visibility = View.GONE
        }

        // configura el listener para el clic del botón de la máquina.
        // al pulsarlo, se invoca la función `alHacerClicMaquina` con el estado de la revisión actual.
        holder.botonMaquina.setOnClickListener {
            alHacerClicMaquina(estado)
        }
    }

    /**
     * `getItemCount`: Este método devuelve el número total de elementos en el conjunto de datos
     * que el adaptador está gestionando. El `RecyclerView` utiliza este valor para saber cuántos
     * ítems debe mostrar.
     *
     * @return El número total de elementos en la lista `estados`.
     */
    override fun getItemCount(): Int = estados.size
}

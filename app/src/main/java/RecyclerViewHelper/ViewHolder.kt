package RecyclerViewHelper

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import diego.rodriguez.crud_abogados.R

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {    val textView: TextView = view.findViewById(
    R.id.txt_productoCard)
    val imgEditar: ImageView =view.findViewById(R.id.img_editar)
    val imgBorrar: ImageView =view.findViewById(R.id.img_borrar)
}
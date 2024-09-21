package RecyclerViewHelper

import Modelos.ClaseConexion
import Modelos.DataClassAbogados
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import diego.rodriguez.crud_abogados.Detalle_informacion
import diego.rodriguez.crud_abogados.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Adaptador(private var Datos: List<DataClassAbogados>) : RecyclerView.Adapter<ViewHolder>() {


    fun actualizarLista(nuevaLista:List<DataClassAbogados>){
        Datos=nuevaLista
        notifyDataSetChanged()
    }

    //funcion parar actualizar el reciler view cuando actualizo los datos

    fun actualizarListaDespuesDeActualizarDatos(uuid: String,nuevoNombre:String){
        val index=Datos.indexOfFirst { it.uuid==uuid }
        Datos[index].nombre=nuevoNombre
        notifyItemChanged(index)

    }


    fun actualizarAbogado(Nombre_Abogado: String , uuid:String){
        //crear na co rrutinan
        GlobalScope.launch(Dispatchers.IO){
            //creo un objeto de la clase conexion

            val objConexion = ClaseConexion().cadenaConexion()

            //variable que contenga prepared sttement
            val updateAbogados = objConexion?.prepareStatement("update TbAbogados set Nombre_Abogado = ? where uuid_abogado = ?")!!

            updateAbogados.setString(1,Nombre_Abogado)
            updateAbogados.setString(2,uuid)
            updateAbogados.executeUpdate()

            val commit = objConexion.prepareStatement("commit")
            commit.executeUpdate()

            withContext(Dispatchers.Main){
                actualizarListaDespuesDeActualizarDatos(uuid,Nombre_Abogado  )
            }

        }

    }


    fun eliminarRegistro(Nombre_Abogado:String,position: Int){

        //quitar el elementpo de la lista
        val listaDatos = Datos .toMutableList()
        listaDatos.removeAt(position)

        //quitar de la base de datos
        GlobalScope.launch(Dispatchers.IO) {

            //crear un objeto e la clase conexion
            val objConexion= ClaseConexion().cadenaConexion()

            val deleteAbogados = objConexion?.prepareStatement("delete TBAbogados where Nombre_Abogado=?")!!
            deleteAbogados.setString( 1,Nombre_Abogado)
            deleteAbogados.executeUpdate()

            val commit = objConexion.prepareStatement( "commit")!!
            commit.executeUpdate()
        }
        Datos=listaDatos.toList()
        notifyItemRemoved(position)
        notifyDataSetChanged()

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card, parent, false)
        return ViewHolder(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val Abogados = Datos[position]
        holder.textView.text = Abogados.nombre

        val item =Datos[position]


        holder.imgBorrar.setOnClickListener {
            //craeamos una alaerta

            //invocamos  el contexto
            val context = holder.itemView.context

            //CREO LA ALERTA

            val builder = AlertDialog.Builder(context)

            //le ponemos titulo a la alerta

            builder.setTitle("¿estas seguro?")

            //ponerle mendsaje a la alerta

            builder.setMessage("Deseas en verdad eliminar el registro")

            //agrgamos los botones

            builder.setPositiveButton("si"){dialog,wich ->
                eliminarRegistro(item.nombre,position)
            }

            builder.setNegativeButton("no"){dialog,wich ->

            }

            //cramos la alerta
            val alertDialog=builder.create()

            //mostramos la alerta

            alertDialog.show()

        }

        holder.imgEditar.setOnClickListener {
            val context=holder.itemView.context

            //creo la alerta
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Editar nombre")

            //agregar un cuadro de texto para que el usuario pueda escribir un nuevo nombre

            val cuadritoNuevoNombre = EditText(context)
            cuadritoNuevoNombre.setHint(item.nombre)
            builder.setView(cuadritoNuevoNombre)

            builder.setPositiveButton("Actualizar"){
                    dialog,which->actualizarAbogado(cuadritoNuevoNombre.text.toString(),item.uuid)
            }

            builder.setNegativeButton("cancelar"){
                    dialog,which->dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }

        //darle clic a la card
        holder.itemView.setOnClickListener {
            //invoco el contexto
            val context = holder.itemView.context

            //cambiamos de pantalla
            //abro pantalla detalle productos
            val pantallaDetalles= Intent(context,Detalle_informacion::class.java)
            //aqui antes de abrir la nueva pantalla le abro los parametros

            pantallaDetalles.putExtra("uuid",item.uuid)
            pantallaDetalles.putExtra("nombre",item.nombre)
            pantallaDetalles.putExtra("edad",item.edad)
            pantallaDetalles.putExtra("peso",item.peso)
            pantallaDetalles.putExtra("correo",item.correo)
            context.startActivity(pantallaDetalles)

        }

    }


}
package diego.rodriguez.crud_abogados

import Modelos.ClaseConexion
import Modelos.DataClassAbogados
import RecyclerViewHelper.Adaptador
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtNombre =findViewById<EditText>(R.id.txtNombre)
        val txtEdad = findViewById<EditText>(R.id.txtEdad)
        val txtPeso = findViewById<EditText>(R.id.txtPeso)
        val txtCorreo = findViewById<EditText>(R.id.txtCorreo)
        val btnAgregar = findViewById<Button>(R.id.btn_agregar)

        fun limpiar(){
            txtNombre.setText("")
            txtEdad.setText("")
            txtPeso.setText("")
            txtCorreo.setText("")
        }

        ////////////////////////////////TODO:mostrar datos ////////////////////////

        val rcvabogados=findViewById<RecyclerView>(R.id.rcv_abogados)

        //asignar un layout al reciledview

        rcvabogados.layoutManager= LinearLayoutManager(this)

        //funcion para obtener datos
        fun obtenerDatos():List<DataClassAbogados>{
            val objConexion=ClaseConexion().cadenaConexion()

            val statement = objConexion?.createStatement()
            val resultSet=statement?.executeQuery("select * from TBAbogados")!!


            val abogados = mutableListOf<DataClassAbogados>()
            while (resultSet.next()){
                val uuid=resultSet.getString("uuid_abogado")
                val nombre = resultSet.getString("nombre_abogado")
                val edad = resultSet.getInt("edad_abogado")
                val peso = resultSet.getDouble("peso_abogado")
                val correo = resultSet.getString("correo_abogado")
                val abogado = DataClassAbogados(uuid,nombre,edad,peso,correo)
                abogados.add(abogado)
            }
            return abogados
        }

        //asignar un adaptador

        CoroutineScope ( Dispatchers.IO) .launch {
            val productosBd=obtenerDatos()
            withContext(Dispatchers.Main){
                val miAdapter = Adaptador(productosBd)
                rcvabogados.adapter=miAdapter
            }
        }
        // programar el boton
        btnAgregar.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO){

                //guardar datos

                //crear un objeto de la clase conexion
                val claseConexion=ClaseConexion().cadenaConexion()

                //crar una variable que contenga un preparedstatement

                val addProducto=claseConexion?.prepareStatement("insert into TbAbogados (UUID_abogado,NOMBRE_ABOGADO,Edad_abogado,peso_abogado,correo_abogado)values(?,?,?,?,?)")!!

                addProducto.setString(1, UUID.randomUUID().toString())
                addProducto.setString(2,txtNombre.text.toString())
                addProducto.setInt(3,txtEdad.text.toString().toInt())
                addProducto.setDouble(5,txtPeso.text.toString().toDouble())
                addProducto.setString(4,txtCorreo.text.toString())
                addProducto.executeUpdate()

                val nuevosabogado=obtenerDatos()

                withContext(Dispatchers.Main){
                    (rcvabogados.adapter as? Adaptador)?.actualizarLista(nuevosabogado)
                }
            }

        }



    }
    }

}
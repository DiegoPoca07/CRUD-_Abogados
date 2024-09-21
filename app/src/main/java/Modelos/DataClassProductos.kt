package Modelos

class DataClassProductos {

    data class dataClassProductos(
        val uuid:String,
        var NombreProducto:String,
        var precio:Int,
        var cantidad:Int

    )

}
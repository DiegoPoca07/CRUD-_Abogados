package Modelos

import java.lang.Exception
import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {

    fun cadenaConexion(): Connection?{
        try {
            val url="jdbc:oracle:thin:@10.10.4.193:1521:xe"
            val user="system"
            val password="irt2024"

            val connection= DriverManager.getConnection(url, user, password)

            return connection
        }catch (e: Exception){
            println("este es el error:$e")
            return null
        }
    }
}
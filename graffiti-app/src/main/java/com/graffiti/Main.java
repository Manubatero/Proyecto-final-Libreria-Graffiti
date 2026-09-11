package com.graffiti;

import com.graffiti.config.ConexionBD;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        Connection cn = ConexionBD.getConexion();
        if (cn != null) {
            System.out.println(">>> Conexión establecida con éxito en la arquitectura MVC/DAO <<<");
        } else {
            System.out.println(">>> Error al conectar. Verifica que MySQL esté corriendo y la base 'graffiti_db' creada. <<<");
        }
    }
}
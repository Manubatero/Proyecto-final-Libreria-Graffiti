package com.graffiti.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {

    private static Connection conexion = null;

    private ConexionBD() {}

    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                Properties props = new Properties();

                // Carga el archivo db.properties de la carpeta resources
                try (InputStream input = ConexionBD.class.getClassLoader().getResourceAsStream("db.properties")) {
                    if (input == null) {
                        throw new RuntimeException("No se encontró el archivo db.properties en resources.");
                    }
                    props.load(input);
                }

                String url = props.getProperty("db.url");
                String user = props.getProperty("db.user");
                String password = props.getProperty("db.password");

                Class.forName("com.mysql.cj.jdbc.Driver");
                conexion = DriverManager.getConnection(url, user, password);
            }
        } catch (Exception e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
        }
        return conexion;
    }

    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                if (!conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
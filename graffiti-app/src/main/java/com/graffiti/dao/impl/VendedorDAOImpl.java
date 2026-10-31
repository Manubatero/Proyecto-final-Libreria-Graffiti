package com.graffiti.dao.impl;

import com.graffiti.config.ConexionBD;
import com.graffiti.dao.VendedorDAO;
import com.graffiti.model.Vendedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VendedorDAOImpl implements VendedorDAO {
    @Override public boolean insertar(Vendedor vendedor) {
        if (vendedor == null || vendedor.getNombre() == null || vendedor.getApellido() == null) return false;
        String sql = "INSERT INTO vendedores (nombre, apellido) VALUES (?, ?)";
        try (Connection cn = ConexionBD.getConexion(); PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, vendedor.getNombre().trim());
            ps.setString(2, vendedor.getApellido().trim());
            if (ps.executeUpdate() != 1) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) vendedor.setId(rs.getInt(1)); }
            return true;
        } catch (SQLException e) { System.err.println("Error al registrar vendedor: " + e.getMessage()); return false; }
    }

    @Override public List<Vendedor> listarTodos() {
        List<Vendedor> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, apellido FROM vendedores ORDER BY apellido, nombre";
        try (Connection cn = ConexionBD.getConexion(); PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Vendedor vendedor = new Vendedor();
                vendedor.setId(rs.getInt("id")); vendedor.setNombre(rs.getString("nombre")); vendedor.setApellido(rs.getString("apellido"));
                lista.add(vendedor);
            }
        } catch (SQLException e) { System.err.println("Error al listar vendedores: " + e.getMessage()); }
        return lista;
    }
}

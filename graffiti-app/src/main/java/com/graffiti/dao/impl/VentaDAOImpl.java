package com.graffiti.dao.impl;

import com.graffiti.config.ConexionBD;
import com.graffiti.dao.VentaDAO;
import com.graffiti.model.Venta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAOImpl implements VentaDAO {

    @Override
    public boolean registrarVenta(Venta v) {
        String sql = "INSERT INTO ventas (fecha_hora, total, id_usuario) VALUES (?, ?, ?)";
        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setTimestamp(1, v.getFechaHora() != null ? Timestamp.valueOf(v.getFechaHora()) : new Timestamp(System.currentTimeMillis()));
            ps.setBigDecimal(2, v.getTotal());

            if (v.getIdUsuario() != null) ps.setInt(3, v.getIdUsuario());
            else ps.setNull(3, Types.INTEGER);

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) v.setIdVenta(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar venta: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Venta buscarPorId(int idVenta) {
        String sql = "SELECT * FROM ventas WHERE id_venta = ?";
        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Venta v = new Venta();
                    v.setIdVenta(rs.getInt("id_venta"));
                    Timestamp ts = rs.getTimestamp("fecha_hora");
                    if (ts != null) v.setFechaHora(ts.toLocalDateTime());
                    v.setTotal(rs.getBigDecimal("total"));
                    v.setIdUsuario(rs.getInt("id_usuario"));
                    return v;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar venta: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Venta> listarTodas() {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT * FROM ventas ORDER BY fecha_hora DESC";
        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Venta v = new Venta();
                v.setIdVenta(rs.getInt("id_venta"));
                Timestamp ts = rs.getTimestamp("fecha_hora");
                if (ts != null) v.setFechaHora(ts.toLocalDateTime());
                v.setTotal(rs.getBigDecimal("total"));
                v.setIdUsuario(rs.getInt("id_usuario"));
                lista.add(v);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar ventas: " + e.getMessage());
        }
        return lista;
    }
}
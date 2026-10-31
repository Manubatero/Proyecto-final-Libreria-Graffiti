package com.graffiti.dao.impl;

import com.graffiti.config.ConexionBD;
import com.graffiti.dao.VentaDAO;
import com.graffiti.model.Venta;
import com.graffiti.model.DetalleVenta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class VentaDAOImpl implements VentaDAO {

    @Override
    public boolean registrarVenta(Venta v, List<DetalleVenta> detalles) {
        if (v == null || v.getIdVendedor() == null || detalles == null || detalles.isEmpty()) return false;
        String sqlVenta = "INSERT INTO ventas (fecha_hora, total, id_usuario, id_vendedor) VALUES (?, ?, ?, ?)";
        String sqlProducto = "SELECT stock_actual, activo, precio_venta FROM productos WHERE id_producto = ? FOR UPDATE";
        String sqlStock = "UPDATE productos SET stock_actual = stock_actual - ? WHERE id_producto = ? AND stock_actual >= ? AND activo = 1";
        String sqlDetalle = "INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        Connection cn = ConexionBD.getConexion();
        if (cn == null) return false;
        try {
            cn.setAutoCommit(false);
            BigDecimal total = BigDecimal.ZERO;
            for (DetalleVenta d : detalles) {
                if (d.getIdProducto() == null || d.getCantidad() == null || d.getCantidad() <= 0) throw new SQLException("Detalle de venta inválido");
                try (PreparedStatement ps = cn.prepareStatement(sqlProducto)) {
                    ps.setInt(1, d.getIdProducto());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() || !rs.getBoolean("activo") || rs.getInt("stock_actual") < d.getCantidad())
                            throw new SQLException("Producto sin stock suficiente o dado de baja: " + d.getIdProducto());
                        d.setPrecioUnitario(rs.getBigDecimal("precio_venta"));
                    }
                }
                if (d.getPrecioUnitario() == null || d.getPrecioUnitario().signum() < 0) throw new SQLException("Precio inválido");
                d.setSubtotal(d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad())));
                total = total.add(d.getSubtotal());
            }
            v.setTotal(total);
            try (PreparedStatement ps = cn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                ps.setTimestamp(1, v.getFechaHora() != null ? Timestamp.valueOf(v.getFechaHora()) : new Timestamp(System.currentTimeMillis()));
                ps.setBigDecimal(2, total);
                if (v.getIdUsuario() == null) ps.setNull(3, Types.INTEGER); else ps.setInt(3, v.getIdUsuario());
                ps.setInt(4, v.getIdVendedor());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("No se pudo obtener el número de venta");
                    v.setIdVenta(rs.getInt(1));
                }
            }
            for (DetalleVenta d : detalles) {
                try (PreparedStatement ps = cn.prepareStatement(sqlStock)) {
                    ps.setInt(1, d.getCantidad()); ps.setInt(2, d.getIdProducto()); ps.setInt(3, d.getCantidad());
                    if (ps.executeUpdate() != 1) throw new SQLException("El stock cambió durante el cobro");
                }
                d.setIdVenta(v.getIdVenta());
                try (PreparedStatement ps = cn.prepareStatement(sqlDetalle, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, v.getIdVenta()); ps.setInt(2, d.getIdProducto()); ps.setInt(3, d.getCantidad());
                    ps.setBigDecimal(4, d.getPrecioUnitario()); ps.setBigDecimal(5, d.getSubtotal());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) d.setIdDetalle(rs.getInt(1)); }
                }
            }
            cn.commit();
            return true;
        } catch (SQLException e) {
            try { cn.rollback(); } catch (SQLException rollbackError) { e.addSuppressed(rollbackError); }
            System.err.println("Error al registrar venta: " + e.getMessage());
            return false;
        } finally {
            try { cn.setAutoCommit(true); } catch (SQLException e) { System.err.println("Error al restaurar la conexión: " + e.getMessage()); }
        }
    }

    @Override
    public Venta buscarPorId(int idVenta) {
        String sql = "SELECT ve.*, CONCAT(v.nombre, ' ', v.apellido) AS nombre_vendedor FROM ventas ve LEFT JOIN vendedores v ON v.id = ve.id_vendedor WHERE ve.id_venta = ?";
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
                    v.setIdVendedor(rs.getInt("id_vendedor"));
                    v.setNombreVendedor(rs.getString("nombre_vendedor"));
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
        String sql = "SELECT ve.*, CONCAT(v.nombre, ' ', v.apellido) AS nombre_vendedor FROM ventas ve LEFT JOIN vendedores v ON v.id = ve.id_vendedor ORDER BY ve.fecha_hora DESC";
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
                v.setIdVendedor(rs.getInt("id_vendedor"));
                v.setNombreVendedor(rs.getString("nombre_vendedor"));
                lista.add(v);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar ventas: " + e.getMessage());
        }
        return lista;
    }
}

package com.graffiti.dao.impl;

import com.graffiti.config.ConexionBD;
import com.graffiti.dao.ProductoDAO;
import com.graffiti.model.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAOImpl implements ProductoDAO {

    @Override
    public boolean insertar(Producto p) {
        String sql = "INSERT INTO productos (codigo_barra, nombre, descripcion, precio_venta, precio_costo, " +
                "stock_actual, stock_minimo, imagen_path, id_categoria, id_proveedor, activo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getCodigoBarra());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getDescripcion());
            ps.setBigDecimal(4, p.getPrecioVenta());
            ps.setBigDecimal(5, p.getPrecioCosto());
            ps.setInt(6, p.getStockActual());
            ps.setInt(7, p.getStockMinimo());
            ps.setString(8, p.getImagenPath());

            if (p.getIdCategoria() != null) ps.setInt(9, p.getIdCategoria());
            else ps.setNull(9, Types.INTEGER);

            if (p.getIdProveedor() != null) ps.setInt(10, p.getIdProveedor());
            else ps.setNull(10, Types.INTEGER);

            ps.setBoolean(11, p.getActivo() != null ? p.getActivo() : true);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setIdProducto(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean actualizar(Producto p) {
        String sql = "UPDATE productos SET codigo_barra = ?, nombre = ?, descripcion = ?, precio_venta = ?, " +
                "precio_costo = ?, stock_actual = ?, stock_minimo = ?, imagen_path = ?, " +
                "id_categoria = ?, id_proveedor = ?, activo = ? WHERE id_producto = ?";

        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, p.getCodigoBarra());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getDescripcion());
            ps.setBigDecimal(4, p.getPrecioVenta());
            ps.setBigDecimal(5, p.getPrecioCosto());
            ps.setInt(6, p.getStockActual());
            ps.setInt(7, p.getStockMinimo());
            ps.setString(8, p.getImagenPath());

            if (p.getIdCategoria() != null) ps.setInt(9, p.getIdCategoria());
            else ps.setNull(9, Types.INTEGER);

            if (p.getIdProveedor() != null) ps.setInt(10, p.getIdProveedor());
            else ps.setNull(10, Types.INTEGER);

            ps.setBoolean(11, p.getActivo());
            ps.setInt(12, p.getIdProducto());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean eliminarLogico(int idProducto) {
        String sql = "UPDATE productos SET activo = 0 WHERE id_producto = ?";
        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al realizar borrado lógico del producto: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Producto buscarPorId(int idProducto) {
        String sql = "SELECT * FROM productos WHERE id_producto = ?";
        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar producto por ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Producto buscarPorCodigoBarra(String codigoBarra) {
        String sql = "SELECT * FROM productos WHERE codigo_barra = ?";
        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigoBarra);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar por código de barra: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Producto> listarTodos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE activo = 1";
        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar productos: " + e.getMessage());
        }
        return lista;
    }

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setIdProducto(rs.getInt("id_producto"));
        p.setCodigoBarra(rs.getString("codigo_barra"));
        p.setNombre(rs.getString("nombre"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setPrecioVenta(rs.getBigDecimal("precio_venta"));
        p.setPrecioCosto(rs.getBigDecimal("precio_costo"));
        p.setStockActual(rs.getInt("stock_actual"));
        p.setStockMinimo(rs.getInt("stock_minimo"));
        p.setImagenPath(rs.getString("imagen_path"));

        int catId = rs.getInt("id_categoria");
        p.setIdCategoria(rs.wasNull() ? null : catId);

        int provId = rs.getInt("id_proveedor");
        p.setIdProveedor(rs.wasNull() ? null : provId);

        p.setActivo(rs.getBoolean("activo"));
        return p;
    }
}
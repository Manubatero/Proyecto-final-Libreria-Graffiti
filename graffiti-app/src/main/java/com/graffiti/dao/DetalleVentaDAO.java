package com.graffiti.dao;

import com.graffiti.model.DetalleVenta;
import java.util.List;

public interface DetalleVentaDAO {
    boolean insertar(DetalleVenta detalle);
    List<DetalleVenta> listarPorVenta(int idVenta);
}
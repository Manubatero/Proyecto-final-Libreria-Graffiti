package com.graffiti.dao;

import com.graffiti.model.Venta;
import com.graffiti.model.DetalleVenta;
import java.util.List;

public interface VentaDAO {
    boolean registrarVenta(Venta venta, List<DetalleVenta> detalles);
    Venta buscarPorId(int idVenta);
    List<Venta> listarTodas();
}

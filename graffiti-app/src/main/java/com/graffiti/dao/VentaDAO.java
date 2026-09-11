package com.graffiti.dao;

import com.graffiti.model.Venta;
import java.util.List;

public interface VentaDAO {
    boolean registrarVenta(Venta venta);
    Venta buscarPorId(int idVenta);
    List<Venta> listarTodas();
}
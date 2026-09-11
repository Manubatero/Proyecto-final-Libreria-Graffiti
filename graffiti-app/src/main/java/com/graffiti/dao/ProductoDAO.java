package com.graffiti.dao;

import com.graffiti.model.Producto;
import java.util.List;

public interface ProductoDAO {
    boolean insertar(Producto producto);
    boolean actualizar(Producto producto);
    boolean eliminarLogico(int idProducto);
    Producto buscarPorId(int idProducto);
    Producto buscarPorCodigoBarra(String codigoBarra);
    List<Producto> listarTodos();
}
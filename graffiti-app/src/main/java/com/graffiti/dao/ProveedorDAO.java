package com.graffiti.dao;

import com.graffiti.model.Proveedor;
import java.util.List;

public interface ProveedorDAO {
    boolean insertar(Proveedor proveedor);
    boolean actualizar(Proveedor proveedor);
    boolean eliminarLogico(int idProveedor);
    Proveedor buscarPorId(int idProveedor);
    List<Proveedor> listarTodos();
}
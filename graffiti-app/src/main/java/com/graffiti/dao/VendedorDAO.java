package com.graffiti.dao;

import com.graffiti.model.Vendedor;
import java.util.List;

public interface VendedorDAO {
    boolean insertar(Vendedor vendedor);
    List<Vendedor> listarTodos();
}

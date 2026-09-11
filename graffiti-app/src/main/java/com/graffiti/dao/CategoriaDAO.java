package com.graffiti.dao;

import com.graffiti.model.Categoria;
import java.util.List;

public interface CategoriaDAO {
    boolean insertar(Categoria categoria);
    boolean actualizar(Categoria categoria);
    boolean eliminar(int idCategoria);
    Categoria buscarPorId(int idCategoria);
    List<Categoria> listarTodas();
}
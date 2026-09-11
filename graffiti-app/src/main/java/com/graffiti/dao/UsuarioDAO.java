package com.graffiti.dao;

import com.graffiti.model.Usuario;
import java.util.List;

public interface UsuarioDAO {
    boolean insertar(Usuario usuario);
    boolean actualizar(Usuario usuario);
    boolean eliminarLogico(int idUsuario);
    Usuario buscarPorId(int idUsuario);
    Usuario login(String nombreUsuario, String password);
    List<Usuario> listarTodos();
}
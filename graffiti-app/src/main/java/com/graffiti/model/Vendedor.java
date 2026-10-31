package com.graffiti.model;

public class Vendedor {
    private Integer id;
    private String nombre;
    private String apellido;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    @Override public String toString() { return nombre + " " + apellido; }
}

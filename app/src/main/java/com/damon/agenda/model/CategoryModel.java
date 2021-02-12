package com.damon.agenda.model;

public class CategoryModel {

    String titulo;
    int imagen;

    public CategoryModel() {
    }

    public CategoryModel(String titulo, int imagen) {
        this.titulo = titulo;
        this.imagen = imagen;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }
}

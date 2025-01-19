package com.dam.ezybites.pojos;

import java.util.List;

public class Receta {

    private String autor;
    private String id;
    private String dificultad;
    private String duracion;
    private List<String> ingredientes;
    private String nombre;
    private String pasos;
    private double rating;
    private List<String> referencias;
    private List<String> tags;
    private int tipo;
    private String url_foto;
    private int visible;

    public Receta(){}

    // Constructor
    public Receta(String id, String dificultad, String duracion, List<String> ingredientes,
                  String nombre, String pasos, double rating, List<String> referencias,
                  List<String> tags, int tipo, String url_foto, int visible) {
        this.id = id;
        this.dificultad = dificultad;
        this.duracion = duracion;
        this.ingredientes = ingredientes;
        this.nombre = nombre;
        this.pasos = pasos;
        this.rating = rating;
        this.referencias = referencias;
        this.tags = tags;
        this.tipo = tipo;
        this.url_foto = url_foto;
        this.visible = visible;
    }

    public Receta(String id, String url_foto, String autor , short tipo) {
        this.id = id;
        this.url_foto = url_foto;
        this.autor = autor;
        this.tipo = tipo;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public List<String> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<String> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPasos() {
        return pasos;
    }

    public void setPasos(String pasos) {
        this.pasos = pasos;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<String> getReferencias() {
        return referencias;
    }

    public void setReferencias(List<String> referencias) {
        this.referencias = referencias;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getUrl_foto() {
        return url_foto;
    }

    public void setUrl_foto(String url_foto) {
        this.url_foto = url_foto;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getAutor() {
        return autor;
    }

}


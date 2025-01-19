package com.dam.ezybites.pojos;

public class User {
    private String id;
    private String nombre;
    private String fotoUrl;

    public User(String id, String nombre, String fotoUrl) {
        this.id = id;
        this.nombre = nombre;
        this.fotoUrl = fotoUrl;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }
}


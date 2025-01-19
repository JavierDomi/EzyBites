package com.dam.ezybites.pojos;

public class RecetaConAutor {
    private Receta receta;
    private String urlFotoPerfilAutor;
    private String username;
    public RecetaConAutor(Receta receta, String urlFotoPerfilAutor, String username) {
        this.receta = receta;
        this.urlFotoPerfilAutor = urlFotoPerfilAutor;
        this.username = username;
    }

    public Receta getReceta() {
        return receta;
    }

    public void setReceta(Receta receta) {
        this.receta = receta;
    }

    public String getUrlFotoPerfilAutor() {
        return urlFotoPerfilAutor;
    }

    public void setUrlFotoPerfilAutor(String urlFotoPerfilAutor) {
        this.urlFotoPerfilAutor = urlFotoPerfilAutor;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

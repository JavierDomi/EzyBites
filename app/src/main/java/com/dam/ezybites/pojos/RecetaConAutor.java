package com.dam.ezybites.pojos;

import android.os.Parcel;
import android.os.Parcelable;

public class RecetaConAutor implements Parcelable {
    private Receta receta;
    private String urlFotoPerfilAutor;
    private String username;

    public RecetaConAutor(Receta receta, String urlFotoPerfilAutor, String username) {
        this.receta = receta;
        this.urlFotoPerfilAutor = urlFotoPerfilAutor;
        this.username = username;
    }

    protected RecetaConAutor(Parcel in) {
        receta = in.readParcelable(Receta.class.getClassLoader());
        urlFotoPerfilAutor = in.readString();
        username = in.readString();
    }

    public static final Creator<RecetaConAutor> CREATOR = new Creator<RecetaConAutor>() {
        @Override
        public RecetaConAutor createFromParcel(Parcel in) {
            return new RecetaConAutor(in);
        }

        @Override
        public RecetaConAutor[] newArray(int size) {
            return new RecetaConAutor[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(receta, flags);
        dest.writeString(urlFotoPerfilAutor);
        dest.writeString(username);
    }
}

package com.example.pickingapp;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class DetalleTutorial {
    private List<String> instrucciones;
    private int gif;
    private boolean usado;

    public DetalleTutorial(List<String> instrucciones, int gif) {
        this.instrucciones = instrucciones;
        this.gif = gif;
        this.usado = false;
    }

    public List<String> getInstrucciones() {
        return instrucciones;
    }

    public void setInstrucciones(List<String> instrucciones) {
        this.instrucciones = instrucciones;
    }

    public int getGif() {
        return gif;
    }

    public void setGif(int gif) {
        this.gif = gif;
    }

    public boolean isUsado() {
        return usado;
    }

    public void setUsado(boolean usado) {
        this.usado = usado;
    }
}

package com.example.ap2;

public class StateItem {

    private String naam;
    private String numb;

    public StateItem() {
    }

    public StateItem(String naam, String numb) {
        this.naam = naam;
        this.numb = numb;
    }

    public String getnumb() {
        return numb;
    }

    public void setnumb(String videoUrl) {
        this.numb = numb;
    }

    public String getnaam() {
        return naam;
    }

    public void setnaam(String previewUrl) {
        this.naam = naam;
    }
}


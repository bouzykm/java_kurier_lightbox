package com.lbapp.MasterKurier.Trasa;

public class WezelObject {
    String wezel, statusDostarczenia, liczbaToreb, liczbaBoxow;
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWezel() {
        return wezel;
    }

    public void setWezel(String wezel) {
        this.wezel = wezel;
    }

    public String getStatusDostarczenia() {
        return statusDostarczenia;
    }

    public void setStatusDostarczenia(String statusDostarczenia) {
        this.statusDostarczenia = statusDostarczenia;
    }
    public String getLiczbaToreb() {
        return liczbaToreb;
    }

    public void setLiczbaToreb(String liczbaToreb) {
        this.liczbaToreb = liczbaToreb;
    }

}

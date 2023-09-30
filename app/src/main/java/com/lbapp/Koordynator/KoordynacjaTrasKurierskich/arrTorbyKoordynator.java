package com.lbapp.Koordynator.KoordynacjaTrasKurierskich;

import java.util.ArrayList;

/**
 * Created by maciej on 19.07.2017.
 */

public class arrTorbyKoordynator {

    int nrTorby, nrTorby2;

    public int getNrTorby() {
        return nrTorby;
    }
    public void setNrTorby(int nrTorby) {
        this.nrTorby = nrTorby;
    }
    public int getNrTorby2() {
        return nrTorby2;
    } // dla 2 pudełek - dieta sport
    public void setNrTorby2(Integer nrTorby2) {
        this.nrTorby2 = nrTorby2;
    }

    public ArrayList<String> listaProduktow;
    public arrTorbyKoordynator(){
        listaProduktow=new ArrayList<String>();
    }

}
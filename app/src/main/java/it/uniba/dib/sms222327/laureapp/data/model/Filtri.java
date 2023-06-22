package it.uniba.dib.sms222327.laureapp.data.model;

import java.util.List;

/**
 * Classe istanza del document Filtri in firebase
 */
public class Filtri {

    private List<String> lista; //lista dei filtri

    public Filtri(){
    }

    public List<String> getLista() {
        return lista;
    }

    public void setLista(List<String> lista) {
        this.lista = lista;
    }
}



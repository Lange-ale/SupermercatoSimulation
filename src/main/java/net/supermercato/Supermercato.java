package net.supermercato;

import java.util.ArrayList;
import java.util.LinkedList;

public class Supermercato {
    private final ArrayList<Cassa> casse = new ArrayList<>();
    private int MAXCASSE;
    //se non ci sono casse aperte i clienti vanno in una coda di persone arrabbiate :(
    private final LinkedList<Carrello> arrabbiati = new LinkedList<>();


    public Supermercato(int MAXCASSE) {
        this.MAXCASSE = MAXCASSE;
        for(int i=0; i < MAXCASSE; i++)
            casse.add(new Cassa());
        casse.get(0).setAperta(true);
    }

    //aggiunge un oggetto carrello all'array di carrelli
    public void aggiungiCarrello(Carrello c){
        Cassa minore = getMinAperta();
        if (minore==null) //se nessuna cassa è aperta, il carrello viene messo nella coda di persone arrabbiate
            arrabbiati.add(c);
        else //se invece almeno una cassa è aperta, il carrello viene aggiunto nella cassa con meno carrelli
            minore.aggiungiCarrello(c);
    }

    public void apriCassa(int indexCassa){
        casse.get(indexCassa).setAperta(true);
    }

    public void chiudiCassa(int indexCassa){
        casse.get(indexCassa).setAperta(false);
    }

    public int getMAXCASSE() {
        return MAXCASSE;
    }

    public Cassa getCassa(int indice) {
        return casse.get(indice);
    }

    public void avanzamento() { //il carrello in testa alla coda viene diminuito di una certa quantità Q di prodotti
        for (Cassa cassa: casse)
            cassa.avanzamento();
    }

    public int getNArrabbiati() { //ritorna il numero di carrelli all'interno della coda di persone arrabbiate
        return arrabbiati.size();
    }

    //assegna tutti i carrelli all'interno della coda di persone arrabbiate alla cassa con meno carrelli
    public void assegnaArrabbiati() {
        Cassa minore = getMinAperta();
        if (minore == null) return; //se non ci sono casse aperte il metodo si ferma
        minore.aggiungiCarrelli(arrabbiati);
        arrabbiati.clear(); //pulisce la coda di persone arrabbiate
    }

    public void setMAXCASSE(int MAXCASSE) {
        this.MAXCASSE = MAXCASSE;
    }

    public void aggiungiCassa(){
        casse.add(new Cassa());
    }

    public void rimuoviUltimaCassa(){
        arrabbiati.addAll(casse.get(casse.size()-1).getCarrelli());
        casse.remove(casse.size()-1);
    }

    private Cassa getMax(){
        int max = getCassa(0).getNCarrelli();
        int index = 0;
        for(int i = 0; i < MAXCASSE; i++ ){
            if(getCassa(i).getNCarrelli() > max){
                max = getCassa(i).getNCarrelli();
                index = i;
            }
        }
        return getCassa(index);
    }

    private Cassa getMinAperta(){
        int min = Integer.MAX_VALUE;
        int index = 0;
        for(int i = 0; i < MAXCASSE; i++ ){
            if(getCassa(i).isAperta() && getCassa(i).getNCarrelli() < min){
                min = getCassa(i).getNCarrelli();
                index = i;
            }
        }
        if (min==Integer.MAX_VALUE)
            return null;
        return getCassa(index);
    }

    public void bilanciaCarrelli(){
        Cassa massimo = getMax();
        Cassa minimo_aperta = getMinAperta();
        if (minimo_aperta == null) return;

        while ((massimo.getNCarrelli() - minimo_aperta.getNCarrelli()) > 2){
            minimo_aperta.aggiungiCarrello(massimo.pollLast());
            massimo = getMax();
            minimo_aperta = getMinAperta();
        }
    }
}
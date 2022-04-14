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

    public void aggiungiCarrello(Carrello c){
        Cassa minore = getMinAperta();
        if (minore==null)
            arrabbiati.add(c);
        else
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

    public void avanzamento() {
        for (Cassa cassa: casse)
            cassa.avanzamento();
    }

    public int getNArrabbiati() {
        return arrabbiati.size();
    }

    public void assegnaArrabbiati() {
        getMinAperta().aggiungiCarrelli(arrabbiati);
        arrabbiati.clear();
    }

    public void setMAXCASSE(int MAXCASSE) {
        this.MAXCASSE = MAXCASSE;
    }

    public void aggiungiCassa(){
        casse.add(new Cassa());
    }

    public void rimuoviUltimaCassa(){
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
        Cassa minimo = getMinAperta();

        while ((massimo.getNCarrelli() - minimo.getNCarrelli()) > 2){
            minimo.aggiungiCarrello(massimo.pollLast());
            massimo = getMax();
            minimo = getMinAperta();
        }
    }
}
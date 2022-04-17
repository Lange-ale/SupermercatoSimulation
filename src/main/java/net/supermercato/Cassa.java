package net.supermercato;

import java.util.LinkedList;

public class Cassa {
    private final LinkedList<Carrello> carrelli = new LinkedList<>();
    private boolean aperta;

    public Cassa() {
        this.aperta = false;
    }

    public void aggiungiCarrello(Carrello c){
        carrelli.add(c);
    }

    public void aggiungiCarrelli(LinkedList<Carrello> arrabbiati) {
        carrelli.addAll(arrabbiati);
    }

    public int getNCarrelli() {
        return carrelli.size();
    }

    public void avanzamento(){
        if(carrelli.isEmpty())
            return;
        int elementi=carrelli.getFirst().getElementi();
        int scarto;
        if(elementi<=SupermercatoController.QTOLTA){
            scarto = SupermercatoController.QTOLTA-elementi;
            carrelli.pop();
            if(carrelli.isEmpty())
                return;
            elementi=carrelli.getFirst().getElementi();
            elementi-=scarto;
            carrelli.getFirst().setElementi(elementi);
        }
        else{
            elementi-= SupermercatoController.QTOLTA;
            carrelli.getFirst().setElementi(elementi);
        }
    }

    public boolean isAperta(){
        return aperta;
    }

    public void setAperta(boolean stato){
        aperta=stato;
    }

    public LinkedList<Carrello> getCarrelli() {
        return carrelli;
    }

    public Carrello pollLast() {
        return carrelli.pollLast();
    }
}
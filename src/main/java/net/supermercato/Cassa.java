package net.supermercato;

import java.util.LinkedList;

public class Cassa {
    private final LinkedList<Carrello> carrelli = new LinkedList<>(); //crea una LinkedList di carrelli
    private boolean aperta; //booleano che serve per capire se la cassa è aperta o chiusa

    public Cassa() {
        this.aperta = false;
    }//quando la cassa viene creata viene impostata a chiusa (false)

    //aggiunge un carrello nella LinkedList di carrelli della cassa
    public void aggiungiCarrello(Carrello c){
        carrelli.add(c);//aggiunge un carrello c nella LinkedList di carrelli
    }

    //aggiunge tutti i carrelli rimasti in attesa, perchè tutte le casse erano chiuse, in una cassa
    public void aggiungiCarrelli(LinkedList<Carrello> arrabbiati) {
        carrelli.addAll(arrabbiati);//aggiunge alla LinkedList di carrelli tutti i carrelli presenti nella coda di carrelli arrabbiati(caarrelli in attesa)
    }

    //ritorna il numero di carrelli presenti nella cassa
    public int getNCarrelli() {
        return carrelli.size();
    }

    //la cassa toglie prodotti all'interno del primo carrello
    public void avanzamento(){
        if(carrelli.isEmpty())//controlla se la linkedlist di carrelli è vuota
            return;//se sì, il metodo si chiude
        int elementi=carrelli.getFirst().getElementi();//prende il numero di elementi presenti nel primo carrello
        int scarto;
        if(elementi<=SupermercatoController.QTOLTA){//controlla se il numero di elementi all'interno del carrello è minore o uguale alla quantità che viene tolta ad ogni avanzamento
            scarto = SupermercatoController.QTOLTA-elementi; //se sì, lo scarto sarà pari alla differenza fra la QTOLTA e gli elementi rimasti
            carrelli.pop();//cancella il primo carrello (dato che è vuoto)
            if(carrelli.isEmpty())//controlla se la linkedlist di carrelli è vuota
                return;//se sì, il metodo si chiude
            elementi=carrelli.getFirst().getElementi(); //il valore degli elementi diventa il numero di elementi presenti nel nuovo primo carrello
            elementi-=scarto;//viene tolto ai nuovi elementi lo scarto del carrello precedente
            carrelli.getFirst().setElementi(elementi);//viene settato il nuovo numero di elementi presenti nel primo carrello
        }
        else{//se gli elementi sono superiori alla QTOLTA
            elementi-= SupermercatoController.QTOLTA;//togliamo la QTOLTA agli elementi
            carrelli.getFirst().setElementi(elementi);//settiamo gli elementi nel carrello con il valore di elementi
        }
    }

    public boolean isAperta(){
        return aperta;
    } //ritorna se la cassa è aperta o chiusa

    public void setAperta(boolean stato){
        aperta=stato;
    } //imposta la cassa aperta o chiusa

    public LinkedList<Carrello> getCarrelli() {
        return carrelli;
    } //ritorna i carrelli all'interno della LinkedList

    public Carrello pollLast() {
        return carrelli.pollLast();
    } //Recupera e rimuove l'ultimo elemento di questo elenco oppure restituisce null se questo elenco è vuoto.
}
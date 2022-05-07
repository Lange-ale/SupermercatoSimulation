package net.supermercato;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class SupermercatoController {
    private final int NCasseIniziali = 10;
    private Supermercato supermercato;
    public static final int QTOLTA = 1; //numero di elementi che vengono tolti ad ogni ciclo di avanzamento
    public static final int MAXELEMENTINELCARRELLO = 50;
    public static double tempoArrivoCarrello = 1;
    public static double tempoAvanzamento = 0.1;

    private Timeline timelineAggiuntaCarrello, timelineAvanzamento;
    @FXML
    HBox HboxCasse;
    @FXML
    MenuItem startPause, aggiungiCassa;
    @FXML
    Label lblArrabbiati;

    Button getButtonCassa(int indice) {
        return (Button) HboxCasse.getChildren().get(indice);
    } //ritorna il bottone associato alla classe i-esima

    public Button creaButtonCassa(int id) { //crea un nuovo bottone cassa
        Button button = new Button("0"); //crea un nuovo bottone
        button.setId("" + id); //imposta l'id del nuovo bottone
        button.setBackground(new Background(new BackgroundFill(Color.rgb(255, 0, 0, 0.7), new CornerRadii(5.0), new Insets(-5.0)))); //imposta lo sfondo del bottone
        //quando il bottone viene premuto viene richiamato questo metodo:
        button.setOnAction(event -> {//event contiene le informazioni sull'azione eseguita
            Button premuto = (Button) event.getSource();
            int index_cassa = Integer.parseInt(premuto.getId()); //crea un intero a cui viene passato l'id del bottone dove è stato eseguito l'evento
            synchronized (supermercato) { //sincronizza il supermercato
                if (supermercato.getCassa(index_cassa).isAperta()) { //controlla se la cassa del bottone selezionato è aperta
                    supermercato.chiudiCassa(index_cassa);//se sì, la chiude
                    premuto.setBackground(new Background(new BackgroundFill(Color.rgb(255, 0, 0, 0.7), new CornerRadii(5.0), new Insets(-5.0))));//imposta lo sfondo del bottone cassa di rosso
                } else { //se invece la cassa è chiusa
                    supermercato.apriCassa(index_cassa); //apre la cassa
                    premuto.setBackground(new Background(new BackgroundFill(Color.rgb(0, 255, 0, 0.7), new CornerRadii(5.0), new Insets(-5.0))));//imposta lo sfondo da rosso a verde
                    if (supermercato.getNArrabbiati() > 0)//controlla se nella coda di carrelli in attesa(carrelli arrabbiati) è presente almeno un carrello
                        supermercato.assegnaArrabbiati();//assegna i carrelli alla cassa con meno carrelli
                    supermercato.bilanciaCarrelli();//bilancia il numero di carrelli in ogni cassa(es: se una cassa ha 8 carrelli e l'altra 2 verranno bilanciate a 5 e 5)
                }
            }
        });
        return button; //ritorna il bottone creato
    }

    private void creaSupermercato(int MAXCASSE) {//crea il nostro supermercato
        supermercato = new Supermercato(MAXCASSE); //crea un oggetto supermercato
        HboxCasse.getChildren().clear();//pulisce l'hbox di casse
        for (int i = 0; i < supermercato.getMAXCASSE(); i++)//fa un ciclo lungo il numero di casse presenti nel supermercato
            HboxCasse.getChildren().add(creaButtonCassa(i));//crea un bottone cassa per ogni cassa

        HboxCasse.setSpacing(20);//imposta lo spazio fra un bottone cassa e l'altro
        HboxCasse.setAlignment(Pos.CENTER);//allinea tutti i bottoni nel centro dell'hbox
        supermercato.apriCassa(0);//apre la prima cassa del supermercato
        getButtonCassa(0).setBackground(new Background(new BackgroundFill(Color.rgb(0, 255, 0, 0.7), new CornerRadii(5.0), new Insets(-5.0))));//imposta lo sfondo della prima cassa del supermercato
    }

    private KeyFrame keyFrameTimelineAggiuntaCarrello() {
        return new KeyFrame( //ritorna la creazione di una nuova KeyFrame
                Duration.seconds(tempoArrivoCarrello),//tempo tra ogni scatto del timer
                e ->{
                    synchronized (supermercato) {//sincronizza il supermercato
                        supermercato.aggiungiCarrello(new Carrello((int) (Math.random() * SupermercatoController.MAXELEMENTINELCARRELLO)));//aggiunge un nuovo carrello al supermercato
                        aggiornavaloricasse();
                    }
                }
        );
    }

    private KeyFrame keyFrameTimelineAvanzamento() {
        return new KeyFrame(//ritorna la creazione di una nuova KeyFrame
                Duration.seconds(tempoAvanzamento),//tempo tra ogni scatto del timer
                e -> {
                    synchronized (supermercato) {//sincronizza il supermercato
                        supermercato.avanzamento();
                        aggiornavaloricasse();
                    }
                }
        );
    }

    @FXML
    void initialize() {
        creaSupermercato(NCasseIniziali);//richiama il metodo creaSupermercato, passandogli come parametro il numero di casse iniziali
        timelineAggiuntaCarrello = new Timeline(keyFrameTimelineAggiuntaCarrello());//crea una timeline di aggiunta carrello
        timelineAvanzamento = new Timeline(keyFrameTimelineAvanzamento());//crea una timeline di avanzamento

        timelineAggiuntaCarrello.setCycleCount(Animation.INDEFINITE); //fa andare il ciclo della timeline di aggiunta carrello all'infinito
        timelineAvanzamento.setCycleCount(Animation.INDEFINITE); //fa andare il ciclo della timeline di avanzamento carrello all'infinito
    }

    private void aggiornavaloricasse() {//aggiorna il numero presente in ogni cassa
        for (int i = 0; i < supermercato.getMAXCASSE(); i++)//ciclo lungo il numero di casse
            getButtonCassa(i).setText("" + supermercato.getCassa(i).getNCarrelli());//imposta il numero di carrelli dentro ogni cassa
        lblArrabbiati.setText("Clienti arrabbiati in attesa dell'apertura di una cassa: " + supermercato.getNArrabbiati());//imposta il numero di clienti in attesa(clienti arrabbiati)
    }

    @FXML
    private void onAggiungiCassa() {//tasto aggiungi cassa
        supermercato.aggiungiCassa();//aggiunge una cassa alla lista di casse del supermercato
        HboxCasse.getChildren().add(creaButtonCassa(supermercato.getMAXCASSE()));//crea un nuovo bottone cassa
        supermercato.setMAXCASSE(supermercato.getMAXCASSE() + 1);//imposta il nuovo numero di casse presenti nel supermercato
    }

    public void onEliminaCassa() {//tasto elimina cassa
        if (HboxCasse.getChildren().size() < 1) return; //controlla se non ci sono casse nel supermercato, se sì, chiude il metodo
        supermercato.rimuoviUltimaCassa();//rimuove l'ultima cassa presente nel supermercato
        supermercato.setMAXCASSE(supermercato.getMAXCASSE() - 1);//imposta il nuovo numero di casse presenti nel supermercato
        if (supermercato.getNArrabbiati() > 0)//controlla se nella coda di carrelli in attesa(carrelli arrabbiati) è presente almeno un carrello
            supermercato.assegnaArrabbiati();//assegna i carrelli in attesa alla cassa con meno carrelli
        supermercato.bilanciaCarrelli();//bilancia il numero di carrelli in ogni cassa(es: se una cassa ha 8 carrelli e l'altra 2 verranno bilanciate a 5 e 5)
        HboxCasse.getChildren().remove(HboxCasse.getChildren().size() - 1);//rimuove l'ultima cassa del supermercato dall'hbox
        aggiornavaloricasse();//aggiorna i valori dentro le casse
    }

    @FXML
    private void onStartPause() {//tasto start/pause
        if (startPause.getText().equals("Start")) {//se il testo del tasto StartPause è impostato a Start
            timelineAvanzamento.play();//la timeline avanzamento parte
            timelineAggiuntaCarrello.play();//la timeline aggiunta carrello parte
            startPause.setText("Pause");//il testo del tasto onStartPause viene impostato a Pause
        } else if (startPause.getText().equals("Pause")) {//se il testo del tasto StartPause è impostato a Pause
            timelineAggiuntaCarrello.stop();//la timeline avanzamento si ferma
            timelineAvanzamento.stop();//la timeline aggiunta carrello si ferma
            startPause.setText("Start");//il testo del tasto StartPause viene impostato a Start
        }
    }

    public void onResetClick() {//tasto reset
        timelineAvanzamento.stop();//la timeline si ferma
        timelineAggiuntaCarrello.stop();//la timeline si ferma
        creaSupermercato(NCasseIniziali);//ricrea il supermercato con annesse casse
        startPause.setText("Start");//il tasto StartPause viene impostato a Start
    }
}
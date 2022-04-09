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

import java.util.ArrayList;


public class SupermercatoController {
    private final Supermercato supermercato = new Supermercato(10);
    public static final int QTOLTA = 1;
    public static final int MAXELEMENTINELCARRELLO = 50;
    public static final double tempoArrivoCarrello = 0.0001;
    public static final double tempoAvanzamento = 1;

    private Timeline timelineAggiuntaCarrello, timelineAvanzamento;
    @FXML
    Label lblArrabbiati;
    @FXML
    MenuItem startPause;
    @FXML
    HBox HboxCasse;
    ArrayList<Button> buttonsCasse;
    @FXML
    MenuItem aggiungiCassa;

    public void creaCassa() {
        Button button = new Button("0");
        button.setId("" + buttonsCasse.size());
        button.setBackground(new Background(new BackgroundFill(Color.rgb(255, 0, 0, 0.7), new CornerRadii(5.0), new Insets(-5.0))));
        button.setOnAction(event -> {
            Button premuto = (Button) event.getSource();
            int index_cassa = Integer.parseInt(premuto.getId());
            synchronized (supermercato) {
                if (supermercato.getCassa(index_cassa).isAperta()) {
                    supermercato.chiudiCassa(index_cassa);
                    premuto.setBackground(new Background(new BackgroundFill(Color.rgb(255, 0, 0, 0.7), new CornerRadii(5.0), new Insets(-5.0))));
                }
                else {
                    supermercato.apriCassa(index_cassa);
                    premuto.setBackground(new Background(new BackgroundFill(Color.rgb(0, 255, 0, 0.7), new CornerRadii(5.0), new Insets(-5.0))));
                    if (supermercato.getNArrabbiati() > 0)
                        supermercato.assegnaArrabbiati();
                    supermercato.bilanciaCarrelli();
                }
            }
        });
        buttonsCasse.add(button);
    }

    @FXML
    void initialize() {
        buttonsCasse = new ArrayList<>();
        for (int i = 0; i < supermercato.getMAXCASSE(); i++)
            creaCassa();

        HboxCasse.getChildren().addAll(buttonsCasse);
        HboxCasse.setSpacing(20);
        HboxCasse.setAlignment(Pos.CENTER);
        supermercato.apriCassa(0);
        buttonsCasse.get(0).setBackground(new Background(new BackgroundFill(Color.rgb(0, 255, 0, 0.7), new CornerRadii(5.0), new Insets(-5.0))));

        timelineAggiuntaCarrello = new Timeline(new KeyFrame(
                Duration.seconds(tempoArrivoCarrello),
                e -> {
                    synchronized (supermercato) {
                        supermercato.aggiungiCarrello(new Carrello((int) (Math.random() * SupermercatoController.MAXELEMENTINELCARRELLO)));
                        aggiornavaloricasse();
                    }
                }
        ));
        timelineAvanzamento = new Timeline(new KeyFrame(
                Duration.seconds(tempoAvanzamento),
                e -> {
                    synchronized (supermercato) {
                        supermercato.avanzamento();
                        aggiornavaloricasse();
                    }
                }
        ));

        timelineAggiuntaCarrello.setCycleCount(Animation.INDEFINITE);
        timelineAvanzamento.setCycleCount(Animation.INDEFINITE);
    }

    private void aggiornavaloricasse() {
        for (int i = 0; i < supermercato.getMAXCASSE(); i++)
            buttonsCasse.get(i).setText("" + supermercato.getCassa(i).getNCarrelli());
        lblArrabbiati.setText("Clienti arrabbiati in attesa dell'apertura di una cassa: " + supermercato.getNArrabbiati());
    }


    @FXML
    private void onAggiungiCassa(){
        supermercato.setMAXCASSE(supermercato.getMAXCASSE()+1);
        creaCassa();
    }

    @FXML
    private void onStartPause() {
        if (startPause.getText().equals("Start")) {
            timelineAvanzamento.play();
            timelineAggiuntaCarrello.play();
            startPause.setText("Pause");
        } else if (startPause.getText().equals("Pause")) {
            timelineAggiuntaCarrello.stop();
            timelineAvanzamento.stop();
            startPause.setText("Start");
        }
    }
}
package net.supermercato;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
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
    public static final double tempoAvanzamento = 0.002;

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

    public void creaCassa(int id) {
        buttonsCasse.set(id, new Button("0"));
        buttonsCasse.get(id).setId("" + id);
        buttonsCasse.get(id).setBackground(new Background(new BackgroundFill(Color.rgb(255, 0, 0, 0.7), new CornerRadii(5.0), new Insets(-5.0))));
        buttonsCasse.get(id).setOnAction(event -> {
            Button button = (Button) event.getSource();
            int indexCassa = Integer.parseInt(button.getId());
            synchronized (supermercato) {
                if (supermercato.getCassa(indexCassa).isAperta()) {
                    supermercato.chiudiCassa(indexCassa);
                    button.setBackground(new Background(new BackgroundFill(Color.rgb(255, 0, 0, 0.7), new CornerRadii(5.0), new Insets(-5.0))));
                } else {
                    supermercato.apriCassa(indexCassa);
                    button.setBackground(new Background(new BackgroundFill(Color.rgb(0, 255, 0, 0.7), new CornerRadii(5.0), new Insets(-5.0))));
                    if (supermercato.getNArrabbiati() > 0)
                        supermercato.assegnaArrabbiati();
                    supermercato.bilanciaCarrelli();
                }
            }
        });

    }

    @FXML
    void initialize() {
        buttonsCasse = new ArrayList<>();
        for (int i = 0; i < supermercato.getMAXCASSE(); i++) {
            buttonsCasse.add(null);
            creaCassa(i);
        }
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
    private void onAggiungiCassa(ActionEvent actionEvent){
        int temp = supermercato.getMAXCASSE();
        supermercato.setMAXCASSE(temp);
        buttonsCasse.add(null);
        creaCassa(temp);

    }

    @FXML
    private void onStartPause(ActionEvent actionEvent) {
        /*if (startPause.getText().equals("Start"))
            startPause.setText("Pause");
        else if (startPause.getText().equals("Pause"))
            startPause.setText("Start");*/
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
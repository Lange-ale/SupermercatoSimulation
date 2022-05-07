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
    public static final int QTOLTA = 5;
    public static final int MAXELEMENTINELCARRELLO = 50;
    public static double tempoArrivoCarrello = 1;
    public static double tempoAvanzamento = 1;

    private Timeline timelineAggiuntaCarrello, timelineAvanzamento;
    @FXML
    HBox HboxCasse;
    @FXML
    MenuItem startPause, aggiungiCassa;
    @FXML
    Label lblArrabbiati;

    Button getButtonCassa(int indice) {
        return (Button) HboxCasse.getChildren().get(indice);
    }

    public Button creaButtonCassa(int id) {
        Button button = new Button("0");
        button.setId("" + id);
        button.setBackground(new Background(new BackgroundFill(Color.rgb(255, 0, 0, 0.7), new CornerRadii(5.0), new Insets(-5.0))));
        button.setOnAction(event -> {
            Button premuto = (Button) event.getSource();
            int index_cassa = Integer.parseInt(premuto.getId());
            synchronized (supermercato) {
                if (supermercato.getCassa(index_cassa).isAperta()) {
                    supermercato.chiudiCassa(index_cassa);
                    premuto.setBackground(new Background(new BackgroundFill(Color.rgb(255, 0, 0, 0.7), new CornerRadii(5.0), new Insets(-5.0))));
                } else {
                    supermercato.apriCassa(index_cassa);
                    premuto.setBackground(new Background(new BackgroundFill(Color.rgb(0, 255, 0, 0.7), new CornerRadii(5.0), new Insets(-5.0))));
                    if (supermercato.getNArrabbiati() > 0)
                        supermercato.assegnaArrabbiati();
                    supermercato.bilanciaCarrelli();
                }
            }
        });
        return button;
    }

    private void creaSupermercato(int MAXCASSE) {
        supermercato = new Supermercato(MAXCASSE);
        HboxCasse.getChildren().clear();
        for (int i = 0; i < supermercato.getMAXCASSE(); i++)
            HboxCasse.getChildren().add(creaButtonCassa(i));

        HboxCasse.setSpacing(20);
        HboxCasse.setAlignment(Pos.CENTER);
        supermercato.apriCassa(0);
        getButtonCassa(0).setBackground(new Background(new BackgroundFill(Color.rgb(0, 255, 0, 0.7), new CornerRadii(5.0), new Insets(-5.0))));
    }

    private KeyFrame keyFrameTimelineAggiuntaCarrello() {
        return new KeyFrame(
                Duration.seconds(tempoArrivoCarrello),
                e -> {
                    synchronized (supermercato) {
                        supermercato.aggiungiCarrello(new Carrello((int) (Math.random() * SupermercatoController.MAXELEMENTINELCARRELLO)));
                        aggiornavaloricasse();
                    }
                }
        );
    }

    private KeyFrame keyFrameTimelineAvanzamento() {
        return new KeyFrame(
                Duration.seconds(tempoAvanzamento),
                e -> {
                    synchronized (supermercato) {
                        supermercato.avanzamento();
                        aggiornavaloricasse();
                    }
                }
        );
    }

    @FXML
    void initialize() {
        creaSupermercato(NCasseIniziali);
        timelineAggiuntaCarrello = new Timeline(keyFrameTimelineAggiuntaCarrello());
        timelineAvanzamento = new Timeline(keyFrameTimelineAvanzamento());

        timelineAggiuntaCarrello.setCycleCount(Animation.INDEFINITE);
        timelineAvanzamento.setCycleCount(Animation.INDEFINITE);
    }

    private void aggiornavaloricasse() {
        for (int i = 0; i < supermercato.getMAXCASSE(); i++)
            getButtonCassa(i).setText("" + supermercato.getCassa(i).getNCarrelli());
        lblArrabbiati.setText("Clienti arrabbiati in attesa dell'apertura di una cassa: " + supermercato.getNArrabbiati());
    }

    @FXML
    private void onAggiungiCassa() {
        supermercato.aggiungiCassa();
        HboxCasse.getChildren().add(creaButtonCassa(supermercato.getMAXCASSE()));
        supermercato.setMAXCASSE(supermercato.getMAXCASSE() + 1);
    }

    public void onEliminaCassa() {
        if (HboxCasse.getChildren().size() < 1) return;
        supermercato.rimuoviUltimaCassa();
        supermercato.setMAXCASSE(supermercato.getMAXCASSE() - 1);
        if (supermercato.getNArrabbiati() > 0)
            supermercato.assegnaArrabbiati();
        supermercato.bilanciaCarrelli();
        HboxCasse.getChildren().remove(HboxCasse.getChildren().size() - 1);
        aggiornavaloricasse();
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

    public void onResetClick() {
        timelineAvanzamento.stop();
        timelineAggiuntaCarrello.stop();
        creaSupermercato(NCasseIniziali);
    }
}
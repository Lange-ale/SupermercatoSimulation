module net.supermercato.supermercato {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens net.supermercato to javafx.fxml;
    exports net.supermercato;
}
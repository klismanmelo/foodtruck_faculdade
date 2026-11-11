package ifrs.ibiruba.exemplo_bd;

import ifrs.ibiruba.exemplo_bd.persistencia.JpaUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                App.class.getResource("tela-principal.fxml")
        );

        Parent raiz = loader.load();
        Scene cena = new Scene(raiz);

        stage.setTitle("Food Truck - Administração");
        stage.setScene(cena);
        stage.setMinWidth(1024);
        stage.setMinHeight(800);
        stage.setMaximized(true);
        stage.show();
    }

    @Override
    public void stop() {
        JpaUtil.fechar();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
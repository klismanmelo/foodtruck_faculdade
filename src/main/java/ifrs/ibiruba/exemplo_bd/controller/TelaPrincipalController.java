package ifrs.ibiruba.exemplo_bd.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class TelaPrincipalController {

    @FXML
    private TabPane tabPanePrincipal;

    @FXML
    private TelaCategoriasController categoriasConteudoController;

    @FXML
    private TelaProdutosController produtosConteudoController;

    @FXML
    private TelaComplementosController complementosConteudoController;

    @FXML
    private TelaPedidosController pedidosConteudoController;

    @FXML
    private void initialize() {
        tabPanePrincipal.getSelectionModel().selectedIndexProperty()
                .addListener((obs, anterior, atual) -> {
                    switch (atual.intValue()) {
                        case 0:
                            categoriasConteudoController.recarregarDados();
                            break;
                        case 1:
                            produtosConteudoController.recarregarDados();
                            break;
                        case 2:
                            complementosConteudoController.recarregarDados();
                            break;
                        case 3:
                            pedidosConteudoController.recarregarDados();
                            break;
                        default:
                            break;
                    }
                });
    }
}

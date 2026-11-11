package ifrs.ibiruba.exemplo_bd.controller;

import ifrs.ibiruba.exemplo_bd.model.Categoria;
import ifrs.ibiruba.exemplo_bd.repository.CategoriaRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;

public class TelaCategoriasController {

    @FXML
    private TextField tfNomeCategoria;

    @FXML
    private TableView<Categoria> tvCategorias;

    @FXML
    private TableColumn<Categoria, Long> colCategoriaId;

    @FXML
    private TableColumn<Categoria, String> colCategoriaNome;

    @FXML
    private Label lbTotalCategorias;

    private final CategoriaRepository repository = new CategoriaRepository();
    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        //tvCategorias.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tvCategorias.setEditable(true);

        colCategoriaId.setCellValueFactory(dados ->
                new javafx.beans.property.SimpleObjectProperty<>(dados.getValue().getId()));

        colCategoriaNome.setCellValueFactory(dados ->
                new javafx.beans.property.SimpleStringProperty(dados.getValue().getNome()));
        colCategoriaNome.setCellFactory(TextFieldTableCell.forTableColumn());
        colCategoriaNome.setOnEditCommit(evento -> {
            Categoria categoria = evento.getRowValue();
            String nomeAnterior = evento.getOldValue();
            String novoNome = evento.getNewValue() == null ? "" : evento.getNewValue().trim();

            if (novoNome.isBlank()) {
                categoria.setNome(nomeAnterior);
                tvCategorias.refresh();
                mostrarErro("O nome da categoria não pode ficar vazio.");
                return;
            }

            try {
                categoria.setNome(novoNome);
                repository.atualizar(categoria);
            } catch (RuntimeException erro) {
                categoria.setNome(nomeAnterior);
                tvCategorias.refresh();
                mostrarErro("Não foi possível editar a categoria. Verifique se o nome já existe.");
            }
        });

        tvCategorias.setItems(categorias);
        tvCategorias.setPlaceholder(new Label("Nenhuma categoria cadastrada"));
        recarregarDados();
    }

    @FXML
    private void salvarCategoria() {
        String nome = tfNomeCategoria.getText().trim();
        if (nome.isBlank()) {
            mostrarErro("Informe o nome da categoria.");
            tfNomeCategoria.requestFocus();
            return;
        }

        try {
            repository.salvar(new Categoria(nome));
            tfNomeCategoria.clear();
            recarregarDados();
            tfNomeCategoria.requestFocus();
        } catch (RuntimeException erro) {
            mostrarErro("Não foi possível salvar. Verifique se a categoria já existe.");
        }
    }

    @FXML
    private void excluirCategoria() {
        Categoria selecionada = tvCategorias.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarErro("Selecione uma categoria na tabela.");
            return;
        }

        Alert confirmacao = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Excluir a categoria \"" + selecionada.getNome() + "\"?",
                ButtonType.YES,
                ButtonType.NO
        );
        confirmacao.setHeaderText("Confirmar exclusão");

        if (confirmacao.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            try {
                repository.excluir(selecionada);
                recarregarDados();
            } catch (RuntimeException erro) {
                mostrarErro("Esta categoria pode estar vinculada a produtos e não pode ser excluída.");
            }
        }
    }

    public void recarregarDados() {
        categorias.setAll(repository.listarTodos());
        lbTotalCategorias.setText(categorias.size() + " categoria(s)");
    }

    private void mostrarErro(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensagem, ButtonType.OK);
        alerta.setHeaderText("Atenção");
        alerta.showAndWait();
    }
}

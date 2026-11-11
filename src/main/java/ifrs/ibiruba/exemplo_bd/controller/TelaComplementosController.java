package ifrs.ibiruba.exemplo_bd.controller;

import ifrs.ibiruba.exemplo_bd.model.Complemento;
import ifrs.ibiruba.exemplo_bd.repository.ComplementoRepository;
import java.text.NumberFormat;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class TelaComplementosController {

    @FXML
    private TextField tfNomeComplemento;

    @FXML
    private TextField tfPrecoComplemento;

    @FXML
    private TableView<Complemento> tvComplementos;

    @FXML
    private TableColumn<Complemento, Long> colComplementoId;

    @FXML
    private TableColumn<Complemento, String> colComplementoNome;

    @FXML
    private TableColumn<Complemento, Double> colComplementoPreco;

    @FXML
    private Label lbTotalComplementos;

    private final ComplementoRepository repository = new ComplementoRepository();
    private final ObservableList<Complemento> complementos = FXCollections.observableArrayList();
    private final NumberFormat formatoMoeda =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
    private Complemento complementoEmEdicao;

    @FXML
    private void initialize() {
        //tvComplementos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        colComplementoId.setCellValueFactory(dados ->
                new javafx.beans.property.SimpleObjectProperty<>(dados.getValue().getId()));
        colComplementoNome.setCellValueFactory(dados ->
                new javafx.beans.property.SimpleStringProperty(dados.getValue().getNome()));
        colComplementoPreco.setCellValueFactory(dados ->
                new javafx.beans.property.SimpleObjectProperty<>(dados.getValue().getPrecoAdicional()));

        colComplementoPreco.setCellFactory(coluna -> new TableCell<>() {
            @Override
            protected void updateItem(Double preco, boolean vazio) {
                super.updateItem(preco, vazio);
                setText(vazio || preco == null ? null : formatoMoeda.format(preco));
            }
        });

        tvComplementos.setItems(complementos);
        tvComplementos.setPlaceholder(new Label("Nenhum complemento cadastrado"));
        tvComplementos.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, atual) -> carregarFormulario(atual));

        recarregarDados();
    }

    @FXML
    private void salvarComplemento() {
        String nome = tfNomeComplemento.getText().trim();
        Double preco = converterPreco(tfPrecoComplemento.getText());

        if (nome.isBlank() || preco == null || preco < 0) {
            mostrarErro("Informe um nome e um preço válido.");
            return;
        }

        try {
            if (complementoEmEdicao == null) {
                repository.salvar(new Complemento(nome, preco));
            } else {
                complementoEmEdicao.setNome(nome);
                complementoEmEdicao.setPrecoAdicional(preco);
                repository.atualizar(complementoEmEdicao);
            }
            limparFormulario();
            recarregarDados();
        } catch (RuntimeException erro) {
            mostrarErro("Não foi possível salvar. Verifique se o nome já existe.");
        }
    }

    @FXML
    private void excluirComplemento() {
        Complemento selecionado = tvComplementos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarErro("Selecione um complemento na tabela.");
            return;
        }

        Alert confirmacao = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Excluir o complemento \"" + selecionado.getNome() + "\"?",
                ButtonType.YES,
                ButtonType.NO
        );
        confirmacao.setHeaderText("Confirmar exclusão");

        if (confirmacao.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            try {
                repository.excluir(selecionado);
                limparFormulario();
                recarregarDados();
            } catch (RuntimeException erro) {
                mostrarErro("Este complemento pode estar vinculado a produtos e não pode ser excluído.");
            }
        }
    }

    @FXML
    private void limparFormulario() {
        complementoEmEdicao = null;
        tfNomeComplemento.clear();
        tfPrecoComplemento.clear();
        tvComplementos.getSelectionModel().clearSelection();
        tfNomeComplemento.requestFocus();
    }

    public void recarregarDados() {
        complementos.setAll(repository.listarTodos());
        lbTotalComplementos.setText(complementos.size() + " complemento(s)");
    }

    private void carregarFormulario(Complemento complemento) {
        complementoEmEdicao = complemento;
        if (complemento == null) {
            return;
        }
        tfNomeComplemento.setText(complemento.getNome());
        tfPrecoComplemento.setText(String.format(Locale.forLanguageTag("pt-BR"), "%.2f",
                complemento.getPrecoAdicional()));
    }

    private Double converterPreco(String texto) {
        try {
            String valor = texto.trim();
            if (valor.contains(",")) {
                valor = valor.replace(".", "").replace(',', '.');
            }
            return Double.valueOf(valor);
        } catch (NumberFormatException erro) {
            return null;
        }
    }

    private void mostrarErro(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensagem, ButtonType.OK);
        alerta.setHeaderText("Atenção");
        alerta.showAndWait();
    }
}

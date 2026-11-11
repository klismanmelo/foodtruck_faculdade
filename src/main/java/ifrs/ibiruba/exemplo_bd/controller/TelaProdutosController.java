package ifrs.ibiruba.exemplo_bd.controller;

import ifrs.ibiruba.exemplo_bd.model.Categoria;
import ifrs.ibiruba.exemplo_bd.model.Complemento;
import ifrs.ibiruba.exemplo_bd.model.Produto;
import ifrs.ibiruba.exemplo_bd.repository.CategoriaRepository;
import ifrs.ibiruba.exemplo_bd.repository.ComplementoRepository;
import ifrs.ibiruba.exemplo_bd.repository.ProdutoRepository;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class TelaProdutosController {

    @FXML
    private TextField tfNomeProduto;

    @FXML
    private TextArea taDescricaoProduto;

    @FXML
    private TextField tfPrecoProduto;

    @FXML
    private ComboBox<Categoria> cbCategoriaProduto;

    @FXML
    private ListView<Complemento> lvComplementosProduto;

    @FXML
    private TextField tfPesquisaProduto;

    @FXML
    private TableView<Produto> tvProdutos;

    @FXML
    private TableColumn<Produto, Long> colProdutoId;

    @FXML
    private TableColumn<Produto, String> colProdutoNome;

    @FXML
    private TableColumn<Produto, String> colProdutoDescricao;

    @FXML
    private TableColumn<Produto, Double> colProdutoPreco;

    @FXML
    private TableColumn<Produto, String> colProdutoCategoria;

    @FXML
    private Label lbTotalProdutos;

    private final ProdutoRepository produtoRepository = new ProdutoRepository();
    private final CategoriaRepository categoriaRepository = new CategoriaRepository();
    private final ComplementoRepository complementoRepository = new ComplementoRepository();

    private final ObservableList<Produto> produtos = FXCollections.observableArrayList();
    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList();
    private final ObservableList<Complemento> complementos = FXCollections.observableArrayList();

    private final NumberFormat formatoMoeda =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
    private Produto produtoEmEdicao;

    @FXML
    private void initialize() {
        //tvProdutos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        colProdutoId.setCellValueFactory(dados ->
                new javafx.beans.property.SimpleObjectProperty<>(dados.getValue().getId()));
        colProdutoNome.setCellValueFactory(dados ->
                new javafx.beans.property.SimpleStringProperty(dados.getValue().getNome()));
        colProdutoDescricao.setCellValueFactory(dados ->
                new javafx.beans.property.SimpleStringProperty(dados.getValue().getDescricao()));
        colProdutoPreco.setCellValueFactory(dados ->
                new javafx.beans.property.SimpleObjectProperty<>(dados.getValue().getPreco()));
        colProdutoCategoria.setCellValueFactory(dados ->
                new javafx.beans.property.SimpleStringProperty(
                        dados.getValue().getCategoria().getNome()
                ));

        colProdutoPreco.setCellFactory(coluna -> new TableCell<>() {
            @Override
            protected void updateItem(Double preco, boolean vazio) {
                super.updateItem(preco, vazio);
                setText(vazio || preco == null ? null : formatoMoeda.format(preco));
            }
        });

        tvProdutos.setItems(produtos);
        tvProdutos.setPlaceholder(new Label("Nenhum produto cadastrado"));
        cbCategoriaProduto.setItems(categorias);
        lvComplementosProduto.setItems(complementos);
        lvComplementosProduto.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        tvProdutos.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, atual) -> carregarFormulario(atual));

        tfPesquisaProduto.textProperty().addListener((obs, anterior, atual) ->
                pesquisarProdutos(atual));

        recarregarDados();
    }

    @FXML
    private void salvarProduto() {
        String nome = tfNomeProduto.getText().trim();
        String descricao = taDescricaoProduto.getText().trim();
        Double preco = converterPreco(tfPrecoProduto.getText());
        Categoria categoria = cbCategoriaProduto.getValue();

        if (nome.isBlank() || preco == null || preco < 0 || categoria == null) {
            mostrarErro("Informe nome, preço válido e categoria.");
            return;
        }

        try {
            if (produtoEmEdicao == null) {
                Produto novoProduto = new Produto(nome, descricao, preco, categoria);
                novoProduto.setComplementos(
                        new ArrayList<>(lvComplementosProduto.getSelectionModel().getSelectedItems())
                );
                produtoRepository.salvar(novoProduto);
            } else {
                produtoEmEdicao.setNome(nome);
                produtoEmEdicao.setDescricao(descricao);
                produtoEmEdicao.setPreco(preco);
                produtoEmEdicao.setCategoria(categoria);
                produtoEmEdicao.setComplementos(
                        new ArrayList<>(lvComplementosProduto.getSelectionModel().getSelectedItems())
                );
                produtoRepository.atualizar(produtoEmEdicao);
            }

            limparFormulario();
            pesquisarProdutos(tfPesquisaProduto.getText());
        } catch (RuntimeException erro) {
            mostrarErro("Não foi possível salvar. Verifique se o nome do produto já existe.");
        }
    }

    @FXML
    private void excluirProduto() {
        Produto selecionado = tvProdutos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarErro("Selecione um produto na tabela.");
            return;
        }

        Alert confirmacao = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Excluir o produto \"" + selecionado.getNome() + "\"?",
                ButtonType.YES,
                ButtonType.NO
        );
        confirmacao.setHeaderText("Confirmar exclusão");

        if (confirmacao.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            try {
                produtoRepository.excluir(selecionado);
                limparFormulario();
                pesquisarProdutos(tfPesquisaProduto.getText());
            } catch (RuntimeException erro) {
                mostrarErro("Não foi possível excluir o produto.");
            }
        }
    }

    @FXML
    private void limparFormulario() {
        produtoEmEdicao = null;
        tfNomeProduto.clear();
        taDescricaoProduto.clear();
        tfPrecoProduto.clear();
        cbCategoriaProduto.getSelectionModel().clearSelection();
        lvComplementosProduto.getSelectionModel().clearSelection();
        tvProdutos.getSelectionModel().clearSelection();
        tfNomeProduto.requestFocus();
    }

    public void recarregarDados() {
        categorias.setAll(categoriaRepository.listarTodos());
        complementos.setAll(complementoRepository.listarTodos());
        pesquisarProdutos(tfPesquisaProduto == null ? "" : tfPesquisaProduto.getText());
    }

    private void pesquisarProdutos(String texto) {
        produtos.setAll(produtoRepository.buscarPorNome(texto == null ? "" : texto));
        lbTotalProdutos.setText(produtos.size() + " produto(s)");
    }

    private void carregarFormulario(Produto produto) {
        produtoEmEdicao = produto;
        if (produto == null) {
            return;
        }

        tfNomeProduto.setText(produto.getNome());
        taDescricaoProduto.setText(produto.getDescricao());
        tfPrecoProduto.setText(String.format(Locale.forLanguageTag("pt-BR"), "%.2f", produto.getPreco()));
        cbCategoriaProduto.setValue(localizarCategoria(produto.getCategoria().getId()));

        lvComplementosProduto.getSelectionModel().clearSelection();
        for (Complemento complemento : produto.getComplementos()) {
            int indice = localizarComplemento(complemento.getId());
            if (indice >= 0) {
                lvComplementosProduto.getSelectionModel().select(indice);
            }
        }
    }

    private int localizarComplemento(Long id) {
        for (int i = 0; i < complementos.size(); i++) {
            if (complementos.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private Categoria localizarCategoria(Long id) {
        return categorias.stream()
                .filter(categoria -> categoria.getId().equals(id))
                .findFirst()
                .orElse(null);
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

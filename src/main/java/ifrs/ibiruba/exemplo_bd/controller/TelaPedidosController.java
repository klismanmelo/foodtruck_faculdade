package ifrs.ibiruba.exemplo_bd.controller;

import ifrs.ibiruba.exemplo_bd.model.Complemento;
import ifrs.ibiruba.exemplo_bd.model.ItemPedido;
import ifrs.ibiruba.exemplo_bd.model.Pedido;
import ifrs.ibiruba.exemplo_bd.model.Produto;
import ifrs.ibiruba.exemplo_bd.repository.PedidoRepository;
import ifrs.ibiruba.exemplo_bd.repository.ProdutoRepository;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TelaPedidosController {

    @FXML
    private ComboBox<Produto> cbProdutoPedido;

    @FXML
    private ListView<Complemento> lvComplementosPedido;

    @FXML
    private Spinner<Integer> spQuantidadePedido;

    @FXML
    private Label lbPrecoUnitarioPedido;

    @FXML
    private TableView<ItemPedido> tvItensPedido;

    @FXML
    private TableColumn<ItemPedido, String> colItemProduto;

    @FXML
    private TableColumn<ItemPedido, String> colItemComplementos;

    @FXML
    private TableColumn<ItemPedido, Integer> colItemQuantidade;

    @FXML
    private TableColumn<ItemPedido, Double> colItemPrecoUnitario;

    @FXML
    private TableColumn<ItemPedido, Double> colItemSubtotal;

    @FXML
    private Label lbTotalPedido;

    @FXML
    private TableView<Pedido> tvPedidos;

    @FXML
    private TableColumn<Pedido, Long> colPedidoCodigo;

    @FXML
    private TableColumn<Pedido, String> colPedidoDataHora;

    @FXML
    private TableColumn<Pedido, String> colPedidoStatus;

    @FXML
    private TableColumn<Pedido, Double> colPedidoTotal;

    @FXML
    private Label lbTotalPedidos;

    @FXML
    private TableView<ItemPedido> tvItensPedidoSalvo;

    @FXML
    private TableColumn<ItemPedido, String> colSalvoProduto;

    @FXML
    private TableColumn<ItemPedido, String> colSalvoComplementos;

    @FXML
    private TableColumn<ItemPedido, Integer> colSalvoQuantidade;

    @FXML
    private TableColumn<ItemPedido, Double> colSalvoPrecoUnitario;

    @FXML
    private TableColumn<ItemPedido, Double> colSalvoSubtotal;

    private final ProdutoRepository produtoRepository = new ProdutoRepository();
    private final PedidoRepository pedidoRepository = new PedidoRepository();

    private final ObservableList<Produto> produtos = FXCollections.observableArrayList();
    private final ObservableList<Complemento> complementosDisponiveis =
            FXCollections.observableArrayList();
    private final ObservableList<ItemPedido> itensPedidoAtual =
            FXCollections.observableArrayList();
    private final ObservableList<Pedido> pedidosRealizados =
            FXCollections.observableArrayList();
    private final ObservableList<ItemPedido> itensPedidoSelecionado =
            FXCollections.observableArrayList();

    private final NumberFormat formatoMoeda =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
    private final DateTimeFormatter formatoDataHora =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private void initialize() {
        configurarControles();
        configurarTabelaPedidoAtual();
        configurarTabelaPedidosRealizados();
        configurarTabelaItensSalvos();

        itensPedidoAtual.addListener(
                (javafx.collections.ListChangeListener<ItemPedido>) alteracao ->
                        atualizarTotalPedido()
        );

        cbProdutoPedido.valueProperty().addListener((obs, anterior, atual) ->
                carregarComplementosDoProduto(atual));

        lvComplementosPedido.getSelectionModel().getSelectedItems()
                .addListener((javafx.collections.ListChangeListener<Complemento>) alteracao ->
                        atualizarPrecoUnitario());

        tvPedidos.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, atual) -> mostrarItensDoPedido(atual));

        recarregarDados();
    }

    private void configurarControles() {
        cbProdutoPedido.setItems(produtos);
        lvComplementosPedido.setItems(complementosDisponiveis);
        lvComplementosPedido.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        spQuantidadePedido.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1)
        );
        spQuantidadePedido.setEditable(true);

        tvItensPedido.setItems(itensPedidoAtual);
        tvPedidos.setItems(pedidosRealizados);
        tvItensPedidoSalvo.setItems(itensPedidoSelecionado);

        tvItensPedido.setPlaceholder(new Label("Nenhum item adicionado ao pedido"));
        tvPedidos.setPlaceholder(new Label("Nenhum pedido realizado"));
        tvItensPedidoSalvo.setPlaceholder(new Label("Selecione um pedido para ver os itens"));
    }

    private void configurarTabelaPedidoAtual() {
        colItemProduto.setCellValueFactory(dados ->
                new SimpleStringProperty(dados.getValue().getProduto().getNome()));
        colItemComplementos.setCellValueFactory(dados ->
                new SimpleStringProperty(complementosComoTexto(dados.getValue())));
        colItemQuantidade.setCellValueFactory(dados ->
                new SimpleObjectProperty<>(dados.getValue().getQuantidade()));
        colItemPrecoUnitario.setCellValueFactory(dados ->
                new SimpleObjectProperty<>(dados.getValue().getPrecoUnitario()));
        colItemSubtotal.setCellValueFactory(dados ->
                new SimpleObjectProperty<>(dados.getValue().getSubtotal()));

        configurarColunaMoeda(colItemPrecoUnitario);
        configurarColunaMoeda(colItemSubtotal);
    }

    private void configurarTabelaPedidosRealizados() {
        colPedidoCodigo.setCellValueFactory(dados ->
                new SimpleObjectProperty<>(dados.getValue().getId()));
        colPedidoDataHora.setCellValueFactory(dados ->
                new SimpleStringProperty(dados.getValue().getDataHora().format(formatoDataHora)));
        colPedidoStatus.setCellValueFactory(dados ->
                new SimpleStringProperty(dados.getValue().getStatus()));
        colPedidoTotal.setCellValueFactory(dados ->
                new SimpleObjectProperty<>(dados.getValue().getValorTotal()));

        configurarColunaMoeda(colPedidoTotal);
    }

    private void configurarTabelaItensSalvos() {
        colSalvoProduto.setCellValueFactory(dados ->
                new SimpleStringProperty(dados.getValue().getProduto().getNome()));
        colSalvoComplementos.setCellValueFactory(dados ->
                new SimpleStringProperty(complementosComoTexto(dados.getValue())));
        colSalvoQuantidade.setCellValueFactory(dados ->
                new SimpleObjectProperty<>(dados.getValue().getQuantidade()));
        colSalvoPrecoUnitario.setCellValueFactory(dados ->
                new SimpleObjectProperty<>(dados.getValue().getPrecoUnitario()));
        colSalvoSubtotal.setCellValueFactory(dados ->
                new SimpleObjectProperty<>(dados.getValue().getSubtotal()));

        configurarColunaMoeda(colSalvoPrecoUnitario);
        configurarColunaMoeda(colSalvoSubtotal);
    }

    @FXML
    private void adicionarItem() {
        Produto produto = cbProdutoPedido.getValue();
        Integer quantidade = spQuantidadePedido.getValue();

        if (produto == null) {
            mostrarErro("Selecione um produto.");
            return;
        }

        if (quantidade == null || quantidade < 1) {
            mostrarErro("Informe uma quantidade válida.");
            return;
        }

        List<Complemento> complementosEscolhidos = new ArrayList<>(
                lvComplementosPedido.getSelectionModel().getSelectedItems()
        );

        double precoUnitario = calcularPrecoUnitario(produto, complementosEscolhidos);
        ItemPedido item = new ItemPedido(
                produto,
                quantidade,
                precoUnitario,
                complementosEscolhidos
        );

        itensPedidoAtual.add(item);
        limparSelecaoDoItem();
    }

    @FXML
    private void removerItem() {
        ItemPedido item = tvItensPedido.getSelectionModel().getSelectedItem();
        if (item == null) {
            mostrarErro("Selecione um item do pedido para remover.");
            return;
        }

        itensPedidoAtual.remove(item);
    }

    @FXML
    private void limparPedido() {
        itensPedidoAtual.clear();
        limparSelecaoDoItem();
    }

    @FXML
    private void finalizarPedido() {
        if (itensPedidoAtual.isEmpty()) {
            mostrarErro("Adicione pelo menos um item antes de finalizar o pedido.");
            return;
        }

        Pedido pedido = new Pedido(LocalDateTime.now(), "FINALIZADO");
        for (ItemPedido item : itensPedidoAtual) {
            pedido.adicionarItem(item);
        }
        pedido.calcularTotal();

        try {
            pedidoRepository.salvar(pedido);
            itensPedidoAtual.clear();
            limparSelecaoDoItem();
            recarregarPedidos();
            mostrarInformacao("Pedido nº " + pedido.getId() + " salvo com sucesso.");
        } catch (RuntimeException erro) {
            mostrarErro("Não foi possível salvar o pedido.");
        }
    }

    public void recarregarDados() {
        Produto produtoSelecionado = cbProdutoPedido.getValue();
        Long produtoSelecionadoId = produtoSelecionado == null ? null : produtoSelecionado.getId();

        produtos.setAll(produtoRepository.listarTodos());

        if (produtoSelecionadoId != null) {
            produtos.stream()
                    .filter(produto -> produto.getId().equals(produtoSelecionadoId))
                    .findFirst()
                    .ifPresent(cbProdutoPedido::setValue);
        }

        recarregarPedidos();
    }

    private void recarregarPedidos() {
        pedidosRealizados.setAll(pedidoRepository.listarTodos());
        itensPedidoSelecionado.clear();
        tvPedidos.getSelectionModel().clearSelection();
        lbTotalPedidos.setText(pedidosRealizados.size() + " pedido(s)");
    }

    private void carregarComplementosDoProduto(Produto produto) {
        lvComplementosPedido.getSelectionModel().clearSelection();
        complementosDisponiveis.clear();

        if (produto != null) {
            complementosDisponiveis.setAll(produto.getComplementos());
        }

        atualizarPrecoUnitario();
    }

    private void atualizarPrecoUnitario() {
        Produto produto = cbProdutoPedido.getValue();
        if (produto == null) {
            lbPrecoUnitarioPedido.setText(formatoMoeda.format(0));
            return;
        }

        double preco = calcularPrecoUnitario(
                produto,
                lvComplementosPedido.getSelectionModel().getSelectedItems()
        );
        lbPrecoUnitarioPedido.setText(formatoMoeda.format(preco));
    }

    private double calcularPrecoUnitario(Produto produto,
            List<? extends Complemento> complementos) {
        double adicionais = complementos.stream()
                .mapToDouble(Complemento::getPrecoAdicional)
                .sum();
        return produto.getPreco() + adicionais;
    }

    private void atualizarTotalPedido() {
        double total = itensPedidoAtual.stream()
                .mapToDouble(ItemPedido::getSubtotal)
                .sum();
        lbTotalPedido.setText(formatoMoeda.format(total));
    }

    private void mostrarItensDoPedido(Pedido pedido) {
        if (pedido == null) {
            itensPedidoSelecionado.clear();
        } else {
            itensPedidoSelecionado.setAll(pedido.getItens());
        }
    }

    private void limparSelecaoDoItem() {
        cbProdutoPedido.getSelectionModel().clearSelection();
        lvComplementosPedido.getSelectionModel().clearSelection();
        complementosDisponiveis.clear();
        spQuantidadePedido.getValueFactory().setValue(1);
        lbPrecoUnitarioPedido.setText(formatoMoeda.format(0));
    }

    private String complementosComoTexto(ItemPedido item) {
        if (item.getComplementos().isEmpty()) {
            return "Sem complementos";
        }

        return item.getComplementos().stream()
                .map(Complemento::getNome)
                .collect(Collectors.joining(", "));
    }

    private <T> void configurarColunaMoeda(TableColumn<T, Double> coluna) {
        coluna.setCellFactory(col -> new TableCell<T, Double>() {
            @Override
            protected void updateItem(Double valor, boolean vazio) {
                super.updateItem(valor, vazio);
                setText(vazio || valor == null ? null : formatoMoeda.format(valor));
            }
        });
    }

    private void mostrarErro(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensagem, ButtonType.OK);
        alerta.setHeaderText("Atenção");
        alerta.showAndWait();
    }

    private void mostrarInformacao(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION, mensagem, ButtonType.OK);
        alerta.setHeaderText("Pedido finalizado");
        alerta.showAndWait();
    }
}

package ifrs.ibiruba.exemplo_bd.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "itens_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "preco_unitario", nullable = false)
    private Double precoUnitario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToMany
    @JoinTable(
            name = "item_pedido_complemento",
            joinColumns = @JoinColumn(name = "item_pedido_id"),
            inverseJoinColumns = @JoinColumn(name = "complemento_id")
    )
    private List<Complemento> complementos = new ArrayList<>();

    public ItemPedido() {
    }

    public ItemPedido(Produto produto, Integer quantidade,
            Double precoUnitario, List<Complemento> complementos) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.complementos = new ArrayList<>(complementos);
    }

    public Long getId() {
        return id;
    }

    public Produto getProduto() {
        return produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public Double getPrecoUnitario() {
        return precoUnitario;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public List<Complemento> getComplementos() {
        return complementos;
    }

    public Double getSubtotal() {
        return precoUnitario * quantidade;
    }
}

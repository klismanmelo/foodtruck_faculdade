package ifrs.ibiruba.exemplo_bd.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "valor_total", nullable = false)
    private Double valorTotal;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<ItemPedido> itens = new ArrayList<>();

    public Pedido() {
    }

    public Pedido(LocalDateTime dataHora, String status) {
        this.dataHora = dataHora;
        this.status = status;
        this.valorTotal = 0.0;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
        item.setPedido(this);
    }

    public void calcularTotal() {
        valorTotal = itens.stream()
                .mapToDouble(ItemPedido::getSubtotal)
                .sum();
    }
}

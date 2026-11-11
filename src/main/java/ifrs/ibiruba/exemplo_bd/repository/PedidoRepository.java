package ifrs.ibiruba.exemplo_bd.repository;

import ifrs.ibiruba.exemplo_bd.model.ItemPedido;
import ifrs.ibiruba.exemplo_bd.model.Pedido;
import ifrs.ibiruba.exemplo_bd.persistencia.JpaUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class PedidoRepository {

    public Pedido salvar(Pedido pedido) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            em.getTransaction().begin();

            // O cascade do Pedido salva os itens na mesma transação.
            em.persist(pedido);

            em.getTransaction().commit();
            return pedido;
        } catch (RuntimeException erro) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw erro;
        } finally {
            em.close();
        }
    }

    public List<Pedido> listarTodos() {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            List<Pedido> pedidos = em.createQuery(
                    "SELECT DISTINCT p FROM Pedido p "
                    + "LEFT JOIN FETCH p.itens i "
                    + "LEFT JOIN FETCH i.produto "
                    + "ORDER BY p.dataHora DESC",
                    Pedido.class
            ).getResultList();

            // Inicializa os complementos enquanto o EntityManager está aberto.
            for (Pedido pedido : pedidos) {
                for (ItemPedido item : pedido.getItens()) {
                    item.getComplementos().size();
                }
            }

            return pedidos;
        } finally {
            em.close();
        }
    }
}

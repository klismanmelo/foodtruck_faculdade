package ifrs.ibiruba.exemplo_bd.repository;

import ifrs.ibiruba.exemplo_bd.model.Produto;
import ifrs.ibiruba.exemplo_bd.persistencia.JpaUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class ProdutoRepository {

    public Produto salvar(Produto produto) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(produto);
            em.getTransaction().commit();
            return produto;
        } catch (RuntimeException erro) {
            rollback(em);
            throw erro;
        } finally {
            em.close();
        }
    }

    public Produto atualizar(Produto produto) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            em.getTransaction().begin();
            Produto atualizado = em.merge(produto);
            em.getTransaction().commit();
            return atualizado;
        } catch (RuntimeException erro) {
            rollback(em);
            throw erro;
        } finally {
            em.close();
        }
    }

    public void excluir(Produto produto) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            em.getTransaction().begin();
            Produto gerenciado = em.find(Produto.class, produto.getId());
            if (gerenciado != null) {
                em.remove(gerenciado);
            }
            em.getTransaction().commit();
        } catch (RuntimeException erro) {
            rollback(em);
            throw erro;
        } finally {
            em.close();
        }
    }

    public List<Produto> listarTodos() {
        return buscarPorNome("");
    }

    public List<Produto> buscarPorNome(String trecho) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT p FROM Produto p "
                    + "LEFT JOIN FETCH p.complementos "
                    + "WHERE LOWER(p.nome) LIKE LOWER(:trecho) "
                    + "ORDER BY p.nome",
                    Produto.class
            ).setParameter("trecho", "%" + trecho.trim() + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }

    private void rollback(EntityManager em) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}

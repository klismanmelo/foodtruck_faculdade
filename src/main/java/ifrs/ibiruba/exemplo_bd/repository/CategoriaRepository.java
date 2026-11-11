package ifrs.ibiruba.exemplo_bd.repository;

import ifrs.ibiruba.exemplo_bd.model.Categoria;
import ifrs.ibiruba.exemplo_bd.persistencia.JpaUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class CategoriaRepository {

    public Categoria salvar(Categoria categoria) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(categoria);
            em.getTransaction().commit();
            return categoria;
        } catch (RuntimeException erro) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw erro;
        } finally {
            em.close();
        }
    }

    public Categoria atualizar(Categoria categoria) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            em.getTransaction().begin();
            Categoria categoriaAtualizada = em.merge(categoria);
            em.getTransaction().commit();
            return categoriaAtualizada;
        } catch (RuntimeException erro) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw erro;
        } finally {
            em.close();
        }
    }

    public void excluir(Categoria categoria) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            em.getTransaction().begin();
            Categoria categoriaGerenciada = em.find(Categoria.class, categoria.getId());
            if (categoriaGerenciada != null) {
                em.remove(categoriaGerenciada);
            }
            em.getTransaction().commit();
        } catch (RuntimeException erro) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw erro;
        } finally {
            em.close();
        }
    }

    public List<Categoria> listarTodos() {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            return em.createQuery(
                    "SELECT c FROM Categoria c ORDER BY c.nome",
                    Categoria.class
            ).getResultList();
        } finally {
            em.close();
        }
    }
}

package ifrs.ibiruba.exemplo_bd.repository;

import ifrs.ibiruba.exemplo_bd.model.Complemento;
import ifrs.ibiruba.exemplo_bd.persistencia.JpaUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class ComplementoRepository {

    public Complemento salvar(Complemento complemento) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(complemento);
            em.getTransaction().commit();
            return complemento;
        } catch (RuntimeException erro) {
            rollback(em);
            throw erro;
        } finally {
            em.close();
        }
    }

    public Complemento atualizar(Complemento complemento) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            em.getTransaction().begin();
            Complemento atualizado = em.merge(complemento);
            em.getTransaction().commit();
            return atualizado;
        } catch (RuntimeException erro) {
            rollback(em);
            throw erro;
        } finally {
            em.close();
        }
    }

    public void excluir(Complemento complemento) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            em.getTransaction().begin();
            Complemento gerenciado = em.find(Complemento.class, complemento.getId());
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

    public List<Complemento> listarTodos() {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            return em.createQuery(
                    "SELECT c FROM Complemento c ORDER BY c.nome",
                    Complemento.class
            ).getResultList();
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

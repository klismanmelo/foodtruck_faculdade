package ifrs.ibiruba.exemplo_bd.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "complementos")
public class Complemento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nome;

    @Column(name = "preco_adicional", nullable = false)
    private Double precoAdicional;

    public Complemento() {
    }

    public Complemento(String nome, Double precoAdicional) {
        this.nome = nome;
        this.precoAdicional = precoAdicional;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPrecoAdicional() {
        return precoAdicional;
    }

    public void setPrecoAdicional(Double precoAdicional) {
        this.precoAdicional = precoAdicional;
    }

    @Override
    public String toString() {
        return nome;
    }
}

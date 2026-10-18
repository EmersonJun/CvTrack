package br.com.cvmatch.vaga.entity;

import br.com.cvmatch.common.entity.EntidadeBase;
import br.com.cvmatch.usuario.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vaga")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vaga extends EntidadeBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(length = 150)
    private String empresa;

    @Column(nullable = false, columnDefinition = "text")
    private String descricao;

    public Vaga(Usuario usuario, String titulo, String empresa, String descricao) {
        this.usuario = usuario;
        atualizar(titulo, empresa, descricao);
    }

    /** Em vez de setters soltos, um método que expressa a intenção de negócio. */
    public void atualizar(String titulo, String empresa, String descricao) {
        this.titulo = titulo;
        this.empresa = empresa;
        this.descricao = descricao;
    }
}

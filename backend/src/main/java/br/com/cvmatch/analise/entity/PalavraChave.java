package br.com.cvmatch.analise.entity;

import br.com.cvmatch.common.entity.EntidadeBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Faz parte do agregado Analise: só é criada pela própria Analise
 * (construtor package-private) e é salva/removida junto com ela (cascade).
 */
@Entity
@Table(name = "palavra_chave")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PalavraChave extends EntidadeBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "analise_id", nullable = false)
    private Analise analise;

    @Column(nullable = false, length = 100)
    private String termo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoPalavraChave tipo;

    PalavraChave(Analise analise, String termo, TipoPalavraChave tipo) {
        this.analise = analise;
        this.termo = termo;
        this.tipo = tipo;
    }
}

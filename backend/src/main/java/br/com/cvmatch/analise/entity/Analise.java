package br.com.cvmatch.analise.entity;

import br.com.cvmatch.common.entity.EntidadeBase;
import br.com.cvmatch.curriculo.entity.Curriculo;
import br.com.cvmatch.vaga.entity.Vaga;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "analise")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Analise extends EntidadeBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "curriculo_id", nullable = false)
    private Curriculo curriculo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vaga_id", nullable = false)
    private Vaga vaga;

    @Min(0)
    @Max(100)
    @Column(nullable = false)
    private int nota;

    @Column(columnDefinition = "text")
    private String resumo;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "pontos_fortes", nullable = false, columnDefinition = "jsonb")
    private List<String> pontosFortes = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private List<String> sugestoes = new ArrayList<>();

    @Column(name = "provedor_ia", nullable = false, length = 30)
    private String provedorIa;

    @Column(nullable = false, length = 100)
    private String modelo;

    @Column(name = "tempo_processamento_ms")
    private Long tempoProcessamentoMs;

    @OneToMany(mappedBy = "analise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PalavraChave> palavrasChave = new ArrayList<>();

    public Analise(Curriculo curriculo, Vaga vaga, int nota, String resumo,
                   List<String> pontosFortes, List<String> sugestoes,
                   String provedorIa, String modelo, Long tempoProcessamentoMs) {
        if (!curriculo.getUsuario().equals(vaga.getUsuario())) {
            throw new IllegalArgumentException("Currículo e vaga devem pertencer ao mesmo usuário");
        }
        this.curriculo = curriculo;
        this.vaga = vaga;
        this.nota = nota;
        this.resumo = resumo;
        this.pontosFortes = new ArrayList<>(pontosFortes);
        this.sugestoes = new ArrayList<>(sugestoes);
        this.provedorIa = provedorIa;
        this.modelo = modelo;
        this.tempoProcessamentoMs = tempoProcessamentoMs;
    }

    public void adicionarPalavraChave(String termo, TipoPalavraChave tipo) {
        palavrasChave.add(new PalavraChave(this, termo, tipo));
    }

    public List<PalavraChave> getPalavrasChave() {
        return Collections.unmodifiableList(palavrasChave);
    }

    public List<String> getPontosFortes() {
        return Collections.unmodifiableList(pontosFortes);
    }

    public List<String> getSugestoes() {
        return Collections.unmodifiableList(sugestoes);
    }
}

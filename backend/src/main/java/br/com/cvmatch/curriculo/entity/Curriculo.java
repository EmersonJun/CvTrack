package br.com.cvmatch.curriculo.entity;

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
@Table(name = "curriculo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Curriculo extends EntidadeBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false)
    private int versao;

    @Column(name = "nome_arquivo", nullable = false)
    private String nomeArquivo;

    @Column(name = "texto_extraido", nullable = false, columnDefinition = "text")
    private String textoExtraido;

    @Column(name = "hash_conteudo", nullable = false, length = 64)
    private String hashConteudo;

    public Curriculo(Usuario usuario, String titulo, int versao, String nomeArquivo,
                     String textoExtraido, String hashConteudo) {
        if (versao < 1) {
            throw new IllegalArgumentException("A versão deve ser maior que zero");
        }
        this.usuario = usuario;
        this.titulo = titulo;
        this.versao = versao;
        this.nomeArquivo = nomeArquivo;
        this.textoExtraido = textoExtraido;
        this.hashConteudo = hashConteudo;
    }
}

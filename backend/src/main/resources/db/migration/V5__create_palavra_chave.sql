-- Palavras-chave ficam em tabela própria (e não em JSON) porque serão CONSULTADAS:
-- ex.: "termos que mais faltam nos meus currículos" no dashboard.
CREATE TABLE palavra_chave (
    id          UUID         PRIMARY KEY,
    analise_id  UUID         NOT NULL,
    termo       VARCHAR(100) NOT NULL,
    tipo        VARCHAR(10)  NOT NULL,
    criado_em   TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT fk_palavra_chave_analise
        FOREIGN KEY (analise_id) REFERENCES analise (id) ON DELETE CASCADE,
    CONSTRAINT ck_palavra_chave_tipo CHECK (tipo IN ('PRESENTE', 'AUSENTE'))
);

CREATE INDEX idx_palavra_chave_analise    ON palavra_chave (analise_id);
CREATE INDEX idx_palavra_chave_tipo_termo ON palavra_chave (tipo, termo);

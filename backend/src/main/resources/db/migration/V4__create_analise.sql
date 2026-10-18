CREATE TABLE analise (
    id                      UUID         PRIMARY KEY,
    curriculo_id            UUID         NOT NULL,
    vaga_id                 UUID         NOT NULL,
    nota                    INTEGER      NOT NULL,
    resumo                  TEXT,
    -- Listas de texto só para exibição: guardadas como JSON, sem tabela própria
    pontos_fortes           JSONB        NOT NULL DEFAULT '[]'::jsonb,
    sugestoes               JSONB        NOT NULL DEFAULT '[]'::jsonb,
    provedor_ia             VARCHAR(30)  NOT NULL,
    modelo                  VARCHAR(100) NOT NULL,
    tempo_processamento_ms  BIGINT,
    criado_em               TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT fk_analise_curriculo
        FOREIGN KEY (curriculo_id) REFERENCES curriculo (id) ON DELETE CASCADE,
    CONSTRAINT fk_analise_vaga
        FOREIGN KEY (vaga_id) REFERENCES vaga (id) ON DELETE CASCADE,
    -- Defesa em profundidade: o Bean Validation já valida, o banco garante
    CONSTRAINT ck_analise_nota CHECK (nota BETWEEN 0 AND 100)
);

CREATE INDEX idx_analise_curriculo ON analise (curriculo_id);
CREATE INDEX idx_analise_vaga      ON analise (vaga_id);

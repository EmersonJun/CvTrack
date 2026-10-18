CREATE TABLE vaga (
    id          UUID         PRIMARY KEY,
    usuario_id  UUID         NOT NULL,
    titulo      VARCHAR(150) NOT NULL,
    empresa     VARCHAR(150),
    descricao   TEXT         NOT NULL,
    criado_em   TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT fk_vaga_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE
);

CREATE INDEX idx_vaga_usuario ON vaga (usuario_id);

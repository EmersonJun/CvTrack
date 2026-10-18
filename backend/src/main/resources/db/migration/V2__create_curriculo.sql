-- Cada linha é uma VERSÃO de um currículo.
-- Linhas com o mesmo (usuario_id, titulo) formam o histórico do "mesmo currículo".
CREATE TABLE curriculo (
    id              UUID         PRIMARY KEY,
    usuario_id      UUID         NOT NULL,
    titulo          VARCHAR(100) NOT NULL,
    versao          INTEGER      NOT NULL,
    nome_arquivo    VARCHAR(255) NOT NULL,
    texto_extraido  TEXT         NOT NULL,
    hash_conteudo   VARCHAR(64)  NOT NULL,
    criado_em       TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT fk_curriculo_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE,
    -- Não existem duas "versão 2" do mesmo currículo
    CONSTRAINT uk_curriculo_usuario_titulo_versao UNIQUE (usuario_id, titulo, versao),
    -- O mesmo arquivo não pode ser enviado duas vezes pelo mesmo usuário
    CONSTRAINT uk_curriculo_usuario_hash UNIQUE (usuario_id, hash_conteudo),
    CONSTRAINT ck_curriculo_versao CHECK (versao > 0)
);
-- Não precisa de índice separado em usuario_id: as UNIQUE acima já começam por ele.

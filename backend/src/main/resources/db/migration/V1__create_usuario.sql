CREATE TABLE usuario (
    id          UUID         PRIMARY KEY,
    nome        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL,
    senha_hash  VARCHAR(100) NOT NULL,
    role        VARCHAR(20)  NOT NULL DEFAULT 'USER',
    criado_em   TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT uk_usuario_email UNIQUE (email),
    CONSTRAINT ck_usuario_role  CHECK (role IN ('USER', 'ADMIN'))
);

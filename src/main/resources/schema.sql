-- Idempotent schema updates needed by the login lock and admin notifications.
--ALTER TABLE usuario
  --  ADD COLUMN IF NOT EXISTS tentativas_login_falhas INT NOT NULL DEFAULT 0;

CREATE TABLE IF NOT EXISTS notificacao_admin (
    id BIGINT NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    email_usuario VARCHAR(180) NOT NULL,
    nome_usuario VARCHAR(120) NOT NULL,
    criada_em DATETIME(6) NOT NULL,
    lida BIT(1) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mensagem_suporte (
    id BIGINT NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    mensagem VARCHAR(4000) NOT NULL,
    criada_em DATETIME(6) NOT NULL,
    visualizada BIT(1) NOT NULL DEFAULT b'0',
    PRIMARY KEY (id),
    CONSTRAINT fk_mensagem_suporte_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS solicitacao_epi (
    id BIGINT NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    epi VARCHAR(120) NOT NULL,
    quantidade INT NOT NULL,
    motivo VARCHAR(500) NOT NULL,
    criada_em DATETIME(6) NOT NULL,
    visualizada BIT(1) NOT NULL DEFAULT b'0',
    PRIMARY KEY (id),
    CONSTRAINT fk_solicitacao_epi_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

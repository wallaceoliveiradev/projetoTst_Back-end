-- Idempotent schema updates needed by the login lock and admin notifications.
ALTER TABLE usuario
    ADD COLUMN IF NOT EXISTS tentativas_login_falhas INT NOT NULL DEFAULT 0;

CREATE TABLE IF NOT EXISTS notificacao_admin (
    id BIGINT NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    email_usuario VARCHAR(180) NOT NULL,
    nome_usuario VARCHAR(120) NOT NULL,
    criada_em DATETIME(6) NOT NULL,
    lida BIT(1) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

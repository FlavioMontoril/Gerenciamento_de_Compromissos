-- Como estamos em desenvolvimento e a estrutura mudou drasticamente,
-- vamos remover a tabela antiga e recriar para evitar erros de Foreign Key inexistente.

DROP TABLE IF EXISTS notifications;

-- Recria a tabela de notificações (Conteúdo compartilhado)
CREATE TABLE notifications (
    id CHAR(36) PRIMARY KEY,
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL
);

-- Cria tabela de vínculo (Muitos-para-Muitos com estado)
CREATE TABLE user_notifications (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    notification_id CHAR(36) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (notification_id) REFERENCES notifications(id)
);

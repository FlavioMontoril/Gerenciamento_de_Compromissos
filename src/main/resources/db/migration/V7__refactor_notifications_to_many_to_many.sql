-- Renomeia message para content
ALTER TABLE notifications CHANGE COLUMN message content TEXT NOT NULL;

-- Remove FK antiga (o nome pode variar, mas geralmente é notifications_ibfk_1 ou baseado no nome do campo)
-- Para garantir compatibilidade com o que foi criado pelo JPA/Flyway anteriormente:
ALTER TABLE notifications DROP FOREIGN KEY fk_notifications_user;

-- Remove campos que agora são individuais
ALTER TABLE notifications DROP COLUMN user_id;
ALTER TABLE notifications DROP COLUMN is_read;

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

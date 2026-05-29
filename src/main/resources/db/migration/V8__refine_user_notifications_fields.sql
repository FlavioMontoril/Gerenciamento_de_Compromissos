-- Remove title de notifications
ALTER TABLE notifications DROP COLUMN title;

-- Renomeia user_id para received_by_id em user_notifications
ALTER TABLE user_notifications RENAME COLUMN user_id TO received_by_id;

-- Adiciona triggered_by_id em user_notifications
ALTER TABLE user_notifications ADD COLUMN triggered_by_id CHAR(36);
ALTER TABLE user_notifications ADD CONSTRAINT fk_un_triggered_by FOREIGN KEY (triggered_by_id) REFERENCES users(id);

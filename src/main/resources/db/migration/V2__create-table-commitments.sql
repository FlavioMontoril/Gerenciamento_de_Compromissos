CREATE TABLE commitments (
    id CHAR(36) NOT NULL,
    owner_id CHAR(36) NOT NULL,
    description VARCHAR(255) NOT NULL,
    appointment_date DATETIME NOT NULL,
    reminder_date DATETIME,
    status VARCHAR(50) NOT NULL,
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT fk_commitments_owner FOREIGN KEY (owner_id) REFERENCES users(id)
);

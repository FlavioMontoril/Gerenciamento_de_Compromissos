CREATE TABLE commitments_participants (
    commitment_id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,
    PRIMARY KEY (commitment_id, user_id),
    CONSTRAINT fk_cp_commitment FOREIGN KEY (commitment_id) REFERENCES commitments(id),
    CONSTRAINT fk_cp_user FOREIGN KEY (user_id) REFERENCES users(id)
);

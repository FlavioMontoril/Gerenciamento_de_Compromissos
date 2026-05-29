-- Inserindo um usuário de teste inicial para permitir o primeiro acesso ao sistema.
-- Nome: Usuário Teste
-- Email: teste@email.com
-- Senha: senha123 (Criptografada com BCrypt)

INSERT INTO users (id, name, email, password, created_at) 
VALUES (
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 
    'Teste', 
    'teste@email.com', 
    '$2a$10$Y5bacIddAzFZ3HQNdtatweGQzY9L6N.6UatUq9f4YJ6oHjPq.6N6.',
    NOW()
);

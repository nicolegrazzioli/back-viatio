-- Usuário Admin (senha: a)
INSERT INTO users (id, email, password, name)
VALUES ('11111111-1111-1111-1111-111111111111', 'a@a', '$2a$12$x8H4COW0ic05gz.4BNG17uR9Q27HfKhVxwy/zR2yYHUoa8bbuqggq', 'Admin Teste');

-- Usuário Secundário (senha: 123)
INSERT INTO users (id, email, password, name)
VALUES ('22222222-2222-2222-2222-222222222222', 'teste@teste.com', '$2a$12$qap/2EVEvnrzACECWtYHuu4oGHa6DPjdrzR/uSKn.1vSkRkRZjz0u', 'Usuário Teste');

-- Viagem para o Usuário Admin
INSERT INTO trips (id, user_id, title, start_date, end_date, cover_type)
VALUES ('33333333-3333-3333-3333-333333333333', '11111111-1111-1111-1111-111111111111', 'Viagem para Paris', '2026-06-01', '2026-06-15', 'eiffel_tower');

-- Transações de compra de moedas (antes/no meio da viagem)
INSERT INTO currency_transactions (id, user_id, amount, currency, amount_brl, source, date, vet_rate)
VALUES
    ('55555555-5555-5555-5555-555555555551', '11111111-1111-1111-1111-111111111111', 100.00, 'EUR', 600.00, 'Câmbio Paris', '2026-06-01', 6.0000),
    ('55555555-5555-5555-5555-555555555552', '11111111-1111-1111-1111-111111111111', 100.00, 'USD', 500.00, 'Câmbio Orlando', '2026-06-01', 5.0000);

-- Despesas para a Viagem do Admin (Usando VET médio para o Café da Manhã)
INSERT INTO expenses (id, trip_id, title, amount, currency, category, date, is_average_cost, exchange_rate, amount_brl)
VALUES
    ('44444444-4444-4444-4444-444444444441', '33333333-3333-3333-3333-333333333333', 'Passagem Aérea', 3500.00, 'BRL', 'Transporte', '2026-05-10', FALSE, 1.0000, 3500.00),
    ('44444444-4444-4444-4444-444444444442', '33333333-3333-3333-3333-333333333333', 'Café da Manhã', 15.00, 'EUR', 'Alimentação', '2026-06-02', TRUE, 6.0000, 90.00);

-- Criar Wallet Inicial para as moedas estrangeiras ativas (EUR e USD) - Sem carteira de BRL
INSERT INTO wallets (user_id, currency, balance, average_vet)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'EUR', 85.00, 6.0000),
    ('11111111-1111-1111-1111-111111111111', 'USD', 100.00, 5.0000);
-- Criação da tabela JOGADOR
CREATE TABLE IF NOT EXISTS JOGADOR (
    id SERIAL PRIMARY KEY,
    nome_usuario VARCHAR(50) NOT NULL,
    senha VARCHAR(100) NOT NULL,
    discord_perfil VARCHAR(50) NOT NULL,
    idade INTEGER NOT NULL CHECK (idade >= 12 AND idade <= 120),
    categoria VARCHAR(20) NOT NULL CHECK (categoria IN ('Casual', 'Competitivo')),
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Criação da tabela SOLICITACAO_GRUPO
CREATE TABLE IF NOT EXISTS SOLICITACAO_GRUPO (
    id_solicitacao SERIAL PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    nome_jogo VARCHAR(50) NOT NULL,
    descricao TEXT,
    estilo_jogo VARCHAR(20) NOT NULL CHECK (estilo_jogo IN ('Cooperativo', 'Competitivo')),
    turno_preferido VARCHAR(20) NOT NULL CHECK (turno_preferido IN ('Manhã', 'Tarde', 'Noite', 'Madrugada')),
    plataforma VARCHAR(20) NOT NULL CHECK (plataforma IN ('PC', 'PlayStation', 'Xbox', 'Nintendo', 'Mobile', 'Multiplataforma')),
    status VARCHAR(20) DEFAULT 'ativa' CHECK (status IN ('ativa', 'encerrada')),
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_criador INTEGER NOT NULL,
    CONSTRAINT fk_criador FOREIGN KEY (id_criador) REFERENCES JOGADOR(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS GRUPO (
    id_grupo SERIAL PRIMARY KEY,
    id_solicitacao INTEGER NOT NULL,
    id_lider INTEGER NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_encerramento TIMESTAMP,
    max_jogadores INTEGER DEFAULT 4,

    CONSTRAINT fk_solicitacao FOREIGN KEY (id_solicitacao) REFERENCES SOLICITACAO_GRUPO(id_solicitacao) ON DELETE CASCADE,
    CONSTRAINT fk_lider FOREIGN KEY (id_lider) REFERENCES JOGADOR(id) ON DELETE SET NULL
);

-- Tabela relacional para jogadores participantes de um grupo
CREATE TABLE IF NOT EXISTS MEMBRO_GRUPO (
    id_grupo INTEGER NOT NULL,
    id_jogador INTEGER NOT NULL,
    data_entrada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (id_grupo, id_jogador),
    
    CONSTRAINT fk_grupo FOREIGN KEY (id_grupo) REFERENCES GRUPO(id_grupo) ON DELETE CASCADE,
    CONSTRAINT fk_jogador FOREIGN KEY (id_jogador) REFERENCES JOGADOR(id) ON DELETE CASCADE
);

-- Criação de índices para otimização
CREATE INDEX IF NOT EXISTS idx_jogador_nome ON JOGADOR(nome_usuario);
CREATE INDEX IF NOT EXISTS idx_solicitacao_jogo ON SOLICITACAO_GRUPO(nome_jogo);
CREATE INDEX IF NOT EXISTS idx_solicitacao_status ON SOLICITACAO_GRUPO(status);
CREATE INDEX IF NOT EXISTS idx_grupo_solicitacao ON GRUPO(id_solicitacao);

-- Função para atualizar status quando grupo é criado
CREATE OR REPLACE FUNCTION atualizar_status_solicitacao()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE SOLICITACAO_GRUPO 
    SET status = 'ativa' 
    WHERE id_solicitacao = NEW.id_solicitacao;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger para executar a função quando um grupo é criado
CREATE TRIGGER tr_atualiza_solicitacao
AFTER INSERT ON GRUPO
FOR EACH ROW
EXECUTE FUNCTION atualizar_status_solicitacao();

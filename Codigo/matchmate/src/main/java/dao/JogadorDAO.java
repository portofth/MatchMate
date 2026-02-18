package dao;

import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;
import java.util.ArrayList;
import java.util.List;
import model.Jogador;
import spark.Request;
import spark.Response;

public class JogadorDAO extends DAO {

    private DAO dao;
    private GrupoDAO grupo;

    public JogadorDAO() {
        this.dao = new DAO();
        this.grupo = new GrupoDAO();
    }

    // Insere um novo jogador no banco de dados
    public boolean inserirJogador(Jogador jogador) {
        String sql = "INSERT INTO jogador (nome_usuario, senha, discord_perfil, idade, categoria, data_cadastro) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = dao.getConexao().prepareStatement(sql)) {
            stmt.setString(1, jogador.getNomeUsuario());
            stmt.setString(2, jogador.getSenha());
            stmt.setString(3, jogador.getDiscordPerfil());
            stmt.setInt(4, jogador.getIdade());
            stmt.setString(5, jogador.getCategoria());
            stmt.setTimestamp(6, new java.sql.Timestamp(jogador.getDataCadastro().getTime()));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir jogador: " + e.getMessage(), e);
        }
    }

    //Atualiza Jogador no Banco de Dados
    public boolean atualizarJogador(Jogador jogador) {
        boolean status = false;
        try {
            // Buscar o jogador atual para manter a senha existente
            Jogador jogadorAtual = getJogador(jogador.getId());
            if (jogadorAtual == null) {
                throw new RuntimeException("Jogador não encontrado para atualização.");
            }

            // Se a nova senha for enviada, criptografa; senão, mantém a antiga
            String senhaFinal;
            if (jogador.getSenha() == null || jogador.getSenha().isEmpty()) {
                senhaFinal = jogadorAtual.getSenha();
            } else {
                senhaFinal = BCrypt.hashpw(jogador.getSenha(), BCrypt.gensalt());
            }

            String sql = "UPDATE jogador SET nome_usuario = ?, senha = ?, discord_perfil = ?, idade = ?, categoria = ? WHERE id = ?";
            PreparedStatement stmt = dao.getConexao().prepareStatement(sql);
            stmt.setString(1, jogador.getNomeUsuario());
            stmt.setString(2, senhaFinal);
            stmt.setString(3, jogador.getDiscordPerfil());
            stmt.setInt(4, jogador.getIdade());
            stmt.setString(5, jogador.getCategoria());
            stmt.setInt(6, jogador.getId());

            stmt.executeUpdate();
            stmt.close();
            status = true;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar jogador: " + e.getMessage(), e);
        }
        return status;
    }


    //Exclui Jogador do banco de dados
    public boolean excluirJogador(int id) {
        String sql = "DELETE FROM jogador WHERE id = ?";

        try (PreparedStatement stmt = dao.getConexao().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir jogador: " + e.getMessage(), e);
        }
    }

    //Retorna todos os jogadores cadastrados.
    public Jogador[] getJogadores() {
        List<Jogador> lista = new ArrayList<>();
        String sql = "SELECT * FROM jogador";

        try (PreparedStatement stmt = dao.getConexao().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Jogador jogador = new Jogador(
                    rs.getInt("id"),
                    rs.getString("nome_usuario"),
                    rs.getString("senha"),
                    rs.getString("discord_perfil"),
                    rs.getInt("idade"),
                    rs.getString("categoria")
                );
                lista.add(jogador);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar jogadores: " + e.getMessage(), e);
        }

        return lista.toArray(new Jogador[0]);
    }

    //Retorna os jogadores de uma categoria específica.
    public Jogador[] getJogadoresPorCategoria(String categoria) {
        List<Jogador> lista = new ArrayList<>();
        String sql = "SELECT * FROM jogador WHERE categoria = ?";

        try (PreparedStatement stmt = dao.getConexao().prepareStatement(sql)) {
            stmt.setString(1, categoria);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Jogador jogador = new Jogador(
                        rs.getInt("id"),
                        rs.getString("nome_usuario"),
                        rs.getString("senha"),
                        rs.getString("discord_perfil"),
                        rs.getInt("idade"),
                        rs.getString("categoria")
                    );
                    lista.add(jogador);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar jogadores por categoria: " + e.getMessage(), e);
        }

        return lista.toArray(new Jogador[0]);
    }

    //Busca um jogador pelo ID
    public Jogador getJogador(int id) {
        String sql = "SELECT * FROM jogador WHERE id = ?";

        try (PreparedStatement stmt = dao.getConexao().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Jogador(
                        rs.getInt("id"),
                        rs.getString("nome_usuario"),
                        rs.getString("senha"),
                        rs.getString("discord_perfil"),
                        rs.getInt("idade"),
                        rs.getString("categoria")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar jogador por ID: " + e.getMessage(), e);
        }

        return null;
    }
    
    public boolean atualizarPerfil(Request request, Response response, int idJogador) {
        boolean status = false;
        String nomeUsuario = request.queryParams("nome");
        String senha = request.queryParams("senha");
        String discordPerfil = request.queryParams("discord");
        int idade = Integer.parseInt(request.queryParams("idade"));
        String categoria = request.queryParams("categoria");

        try (Connection conn = dao.getConexao()) {
            // Buscar senha atual do jogador
            String senhaFinal = senha;
            if (senha == null || senha.isEmpty()) {
                String sqlBusca = "SELECT senha FROM jogador WHERE id = ?";
                try (PreparedStatement buscaStmt = conn.prepareStatement(sqlBusca)) {
                    buscaStmt.setInt(1, idJogador);
                    ResultSet rs = buscaStmt.executeQuery();
                    if (rs.next()) {
                        senhaFinal = rs.getString("senha");
                    } else {
                        throw new SQLException("Jogador não encontrado.");
                    }
                }
            } else {
                // Criptografar nova senha
                senhaFinal = BCrypt.hashpw(senha, BCrypt.gensalt());
            }

            // Atualizar dados
            String sqlUpdate = "UPDATE jogador SET nome_usuario = ?, senha = ?, discord_perfil = ?, idade = ?, categoria = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
                stmt.setString(1, nomeUsuario);
                stmt.setString(2, senhaFinal);
                stmt.setString(3, discordPerfil);
                stmt.setInt(4, idade);
                stmt.setString(5, categoria);
                stmt.setInt(6, idJogador);

                stmt.executeUpdate();
                status = true;
            }

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar: " + e.getMessage());
        }

        return status;
    }
}

package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dto.GrupoComMembrosDTO;
import model.Grupo;
import model.Solicitacao;
import spark.Request;
import spark.Response;

public class GrupoDAO {
	
    private DAO dao;

    public GrupoDAO() {
        dao = new DAO();
    }
    
	
		//Função a ser chama no app para criar grupo, chama função verificaSolicitação() do service Solicitação e cria os registros no banco de dados
		//caso a mesma retorne true (Dados validos)
		
    public boolean criarGrupo(Solicitacao solicitacao, int quant) {
        int idSolicitacao = solicitacao.getIdSolicitacao();
        int idLider = solicitacao.getIdCriador();
        boolean status = false;
        Date dataCriacao = new Date(System.currentTimeMillis());

        Grupo grupo = new Grupo(idSolicitacao, quant);
        grupo.setDataCriacao(dataCriacao);

        String sqlGrupo = "INSERT INTO grupo (id_solicitacao, data_criacao, data_encerramento, max_jogadores, id_lider) " +
                          "VALUES (?, ?, ?, ?, ?)";

        String sqlMembro = "INSERT INTO membro_grupo (id_grupo, id_jogador) VALUES (?, ?)";

        try (Connection conn = dao.getConexao(); // Conexão para criar o grupo
             PreparedStatement stmtGrupo = conn.prepareStatement(sqlGrupo, Statement.RETURN_GENERATED_KEYS)) {

            stmtGrupo.setInt(1, grupo.getIdSolicitacao());
            stmtGrupo.setTimestamp(2, new java.sql.Timestamp(grupo.getDataCriacao().getTime()));
            stmtGrupo.setNull(3, java.sql.Types.TIMESTAMP);
            stmtGrupo.setInt(4, grupo.getMaxJogadores());
            stmtGrupo.setInt(5, idLider);

            stmtGrupo.executeUpdate();

            try (ResultSet generatedKeys = stmtGrupo.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int idGrupo = generatedKeys.getInt(1);
                    grupo.setIdGrupo(idGrupo);

                    try (PreparedStatement stmtMembro = conn.prepareStatement(sqlMembro)) {
                        stmtMembro.setInt(1, idGrupo);
                        stmtMembro.setInt(2, idLider);
                        stmtMembro.executeUpdate();
                    }

                    status = true;
                } else {
                    throw new SQLException("Falha ao obter ID do grupo criado.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao criar grupo: " + e.getMessage());
        }

        return status;
    }


    //Busca todos os grupos de um jogo específico com seus membros e informações do líder.
    public List<GrupoComMembrosDTO> buscarGruposPorNomeJogo(String nomeJogo, int idJogador) {
        List<GrupoComMembrosDTO> gruposDTO = new ArrayList<>();

        String sql = """
        	    SELECT
        		  g.id_lider,
        	      g.id_grupo,
        	      s.nome_jogo,
        	      s.descricao,
        	      s.titulo,
        	      s.estilo_jogo,
        	      s.turno_preferido,
        	      s.plataforma,
        	      j.nome_usuario,
        	      j.id,
        	      j.discord_perfil,
        	      g.max_jogadores,
        	      g.data_criacao,
        	      jl.nome_usuario AS nome_lider,
        	      jl.discord_perfil AS discord_lider
        	    FROM grupo g
        	    JOIN solicitacao_grupo s ON g.id_solicitacao = s.id_solicitacao
        	    JOIN membro_grupo mg ON g.id_grupo = mg.id_grupo
        	    JOIN jogador j ON mg.id_jogador = j.id
        	    JOIN jogador jl ON g.id_lider = jl.id
        	    WHERE s.nome_jogo = ?
        	    ORDER BY g.id_grupo
        	""";

        // Nova conexão para a busca dos grupos
        try (Connection conn = dao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomeJogo);
            ResultSet rs = stmt.executeQuery();

            // Usamos um mapa para evitar duplicar grupos ao iterar os membros
            Map<Integer, GrupoComMembrosDTO> mapaGrupos = new HashMap<>();

            while (rs.next()) {
                int idGrupo = rs.getInt("id_grupo");
                String nomeUsuario = rs.getString("nome_usuario");
                String discordPerfil = rs.getString("discord_perfil");
                int idUsuario = rs.getInt("id");

                GrupoComMembrosDTO grupoDTO = mapaGrupos.get(idGrupo);
                if (grupoDTO == null) {
                    grupoDTO = new GrupoComMembrosDTO();
                    grupoDTO.setIdGrupo(idGrupo);
                    grupoDTO.setNomeJogo(rs.getString("nome_jogo"));
                    grupoDTO.setMaxJogadores(rs.getInt("max_jogadores"));
                    grupoDTO.setDataCriacao(rs.getDate("data_criacao"));
                    grupoDTO.setNomeLider(rs.getString("nome_lider"));
                    grupoDTO.setIdLider(rs.getInt("id_lider"));
                    grupoDTO.setDiscordLider(rs.getString("discord_lider"));
                    grupoDTO.setTitulo(rs.getString("titulo"));
                    grupoDTO.setPlataforma(rs.getString("plataforma"));
                    grupoDTO.setDescricao(rs.getString("descricao"));
                    grupoDTO.setEstilo_jogo(rs.getString("estilo_jogo"));
                    grupoDTO.setTurno_preferido(rs.getString("turno_preferido"));
                    grupoDTO.setIdLogado(idJogador);
                    grupoDTO.setNomesMembros(new ArrayList<>());
                    grupoDTO.setIdsMembros(new ArrayList<>());
                    grupoDTO.setDiscordsMembros(new ArrayList<>()); // ADICIONADO
                    mapaGrupos.put(idGrupo, grupoDTO);
                }

                grupoDTO.getIdsMembros().add(idUsuario);
                grupoDTO.getNomesMembros().add(nomeUsuario);
                grupoDTO.getDiscordsMembros().add(discordPerfil); // ADICIONADO
            }
            gruposDTO.addAll(mapaGrupos.values());

        } catch (SQLException e) {
            System.out.println("Erro ao buscar grupos: " + e.getMessage());
        }

        return gruposDTO;
    }
    
    //Adiciona um jogador a um grupo existente.
    public boolean inserirNoGrupo (int idJogador, int idGrupo) {
    	
    	boolean status = false;
    	
        String sqlMembro = "INSERT INTO membro_grupo (id_grupo, id_jogador) VALUES (?, ?)";
        
        try(Connection conn = dao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sqlMembro)){
        	
        	stmt.setInt(1, idGrupo);
        	stmt.setInt(2, idJogador);
        	stmt.executeUpdate();
        	status = true;
        	
        }catch (SQLException e) {
            System.out.println("Erro ao inserir: " + e.getMessage());
        }
    	return status;
    }

    //Remove um jogador do Grupo
    public boolean removerDoGrupo (int idJogador, int idGrupo) {
    	
    	boolean status = false;
    	
    	String sqlMembro = "DELETE FROM membro_grupo WHERE id_grupo = ? AND id_jogador = ?";
        
        try(Connection conn = dao.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sqlMembro)){
        	
        	stmt.setInt(1, idGrupo);
        	stmt.setInt(2, idJogador);
        	stmt.executeUpdate();
        	status = true;
        	
        }catch (SQLException e) {
            System.out.println("Erro ao remover: " + e.getMessage());
        }
    	return status;
    }
	//Função para deletar Grupo
    public boolean deletarGrupo (int idGrupo) {
        boolean status = false;
        Connection conn = null;
        
        try {
            conn = dao.getConexao();
            conn.setAutoCommit(false); 
            
            
            String sqlGetSolicitacao = "SELECT id_solicitacao FROM grupo WHERE id_grupo = ?";
            int idSolicitacao = -1;
            
            try (PreparedStatement stmt = conn.prepareStatement(sqlGetSolicitacao)) {
                stmt.setInt(1, idGrupo);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    idSolicitacao = rs.getInt("id_solicitacao");
                }
            }
            
            //Remove os Membros primeiro
            String sqlDeleteMembros = "DELETE FROM membro_grupo WHERE id_grupo = ?";
            try (PreparedStatement stmtMembros = conn.prepareStatement(sqlDeleteMembros)) {
                stmtMembros.setInt(1, idGrupo);
                stmtMembros.executeUpdate();
            }
            
            // Depois remove o grupo
            String sqlDeleteGrupo = "DELETE FROM grupo WHERE id_grupo = ?";
            try (PreparedStatement stmtGrupo = conn.prepareStatement(sqlDeleteGrupo)) {
                stmtGrupo.setInt(1, idGrupo);
                stmtGrupo.executeUpdate();
            }
            
            // Por fim remove a solicitação associada
            if (idSolicitacao != -1) {
                String sqlDeleteSolicitacao = "DELETE FROM solicitacao_grupo WHERE id_solicitacao = ?";
                try (PreparedStatement stmtSolicitacao = conn.prepareStatement(sqlDeleteSolicitacao)) {
                    stmtSolicitacao.setInt(1, idSolicitacao);
                    stmtSolicitacao.executeUpdate();
                }
            }
            
            conn.commit(); 
            status = true;
            
        } catch (SQLException e) {
            System.out.println("Erro ao remover grupo e registros associados: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); 
                } catch (SQLException ex) {
                    System.out.println("Erro ao realizar rollback: " + ex.getMessage());
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("Erro ao fechar conexão: " + e.getMessage());
                }
            }
        }
        return status;
    }
    
    //Função para atualizar Grupo
    public boolean atualizarGrupo(Request request, Response response) {
        
    	int idSol = -1;
    	boolean status = false;
    	int idGrupo = Integer.parseInt(request.queryParams("id"));
    	String titulo = request.queryParams("titulo");
        String nome = request.queryParams("nome");
        String descricao = request.queryParams("descricao");
        String estilo = request.queryParams("estilo");
        String turno = request.queryParams("turno");
        String plataforma = request.queryParams("plataforma");
        //int quant = Integer.parseInt(request.queryParams("quant"));
    	
    	String sql = "SELECT id_solicitacao FROM grupo WHERE id_grupo = ?";
        
        try(Connection conn = dao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){
        	stmt.setInt(1, idGrupo);
        	ResultSet rs = stmt.executeQuery();
        	
            if (rs.next()) {
                idSol = rs.getInt("id_solicitacao");
            }
        	
            String sqlUpdate = "UPDATE solicitacao_grupo SET titulo = ?, nome_jogo = ?, descricao = ?, estilo_jogo = ?, "
            		+ "turno_preferido = ?, plataforma = ? WHERE id_solicitacao = ?";
            
            try(Connection conn2 = dao.getConexao();
                    PreparedStatement stmt2 = conn.prepareStatement(sqlUpdate)){
            	stmt2.setString(1, titulo);
            	stmt2.setString(2, nome);
            	stmt2.setString(3, descricao);
            	stmt2.setString(4, estilo);
            	stmt2.setString(5, turno);
            	stmt2.setString(6, plataforma);
            	stmt2.setInt(7, idSol);
            	stmt2.executeUpdate();
            	status = true;
            }

        	return status;
        	
        }catch (SQLException e) {
            System.out.println("Erro ao remover: " + e.getMessage());
            return status;
        }
    }
    
    public List<GrupoComMembrosDTO> mostrarGrupos(int idJogador) {
        List<GrupoComMembrosDTO> gruposDTO = new ArrayList<>();
        
        String sql = """
        	    SELECT
        		  g.id_lider,
        	      g.id_grupo,
        	      s.nome_jogo,
        	      s.descricao,
        	      s.titulo,
        	      s.estilo_jogo,
        	      s.turno_preferido,
        	      s.plataforma,
        	      j.nome_usuario,
        	      j.id,
        	      j.discord_perfil,
        	      g.max_jogadores,
        	      g.data_criacao,
        	      jl.nome_usuario AS nome_lider,
        	      jl.discord_perfil AS discord_lider
        	    FROM grupo g
        	    JOIN solicitacao_grupo s ON g.id_solicitacao = s.id_solicitacao
        	    JOIN membro_grupo mg ON g.id_grupo = mg.id_grupo
        	    JOIN jogador j ON mg.id_jogador = j.id
        	    JOIN jogador jl ON g.id_lider = jl.id
        	    WHERE mg.id_jogador = ?
        	    ORDER BY g.id_grupo
        	""";

        // Nova conexão para a busca dos grupos
        try (Connection conn = dao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idJogador);
            ResultSet rs = stmt.executeQuery();

            // Usamos um mapa para evitar duplicar grupos ao iterar os membros
            Map<Integer, GrupoComMembrosDTO> mapaGrupos = new HashMap<>();

            while (rs.next()) {
                int idGrupo = rs.getInt("id_grupo");
                String nomeUsuario = rs.getString("nome_usuario");
                String discordPerfil = rs.getString("discord_perfil");
                int idUsuario = rs.getInt("id");

                GrupoComMembrosDTO grupoDTO = mapaGrupos.get(idGrupo);
                if (grupoDTO == null) {
                    grupoDTO = new GrupoComMembrosDTO();
                    grupoDTO.setIdGrupo(idGrupo);
                    grupoDTO.setNomeJogo(rs.getString("nome_jogo"));
                    grupoDTO.setMaxJogadores(rs.getInt("max_jogadores"));
                    grupoDTO.setDataCriacao(rs.getDate("data_criacao"));
                    grupoDTO.setNomeLider(rs.getString("nome_lider"));
                    grupoDTO.setIdLider(rs.getInt("id_lider"));
                    grupoDTO.setDiscordLider(rs.getString("discord_lider"));
                    grupoDTO.setTitulo(rs.getString("titulo"));
                    grupoDTO.setPlataforma(rs.getString("plataforma"));
                    grupoDTO.setDescricao(rs.getString("descricao"));
                    grupoDTO.setEstilo_jogo(rs.getString("estilo_jogo"));
                    grupoDTO.setTurno_preferido(rs.getString("turno_preferido"));
                    grupoDTO.setIdLogado(idJogador);
                    grupoDTO.setNomesMembros(new ArrayList<>());
                    grupoDTO.setIdsMembros(new ArrayList<>());
                    grupoDTO.setDiscordsMembros(new ArrayList<>()); // ADICIONADO
                    mapaGrupos.put(idGrupo, grupoDTO);
                }

                grupoDTO.getIdsMembros().add(idUsuario);
                grupoDTO.getNomesMembros().add(nomeUsuario);
                grupoDTO.getDiscordsMembros().add(discordPerfil); // ADICIONADO
            }
            gruposDTO.addAll(mapaGrupos.values());

        } catch (SQLException e) {
            System.out.println("Erro ao buscar grupos: " + e.getMessage());
        }

        return gruposDTO;
    }
    
	}

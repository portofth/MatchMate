package app;

import dao.*;
import dto.GrupoComMembrosDTO;
import model.*;
import service.*;
import spark.Route;
import org.mindrot.jbcrypt.BCrypt;
import static spark.Spark.*;
import com.google.gson.Gson;

import java.sql.*;
import java.util.*;

public class Aplicacao {
	public static void main(String[] args) throws SQLException {
		// Define o diretório externo para servir arquivos estáticos
		staticFiles.externalLocation("src/main/resources/public"); 
		port(6789);
        // Inicialização dos DAOs e Services usados pelas rotas
        DAO teste = new DAO();
        SolicitacaoDAO dao = new SolicitacaoDAO();
        GrupoDAO grupoDAO = new GrupoDAO();
        JogadorDAO jogadorDAO = new JogadorDAO();
        SolicitacaoService serviceSol = new SolicitacaoService();
        JogadorService serviceJogador = new JogadorService();
        
        Gson gson = new Gson();
        Gson gson2 = new Gson();
        
         // Rota para criar uma nova solicitação de grupo
        post("/solicitacao_grupo", (request, response) -> {
        	
        	//Solicita o id do jogador
            Integer idJogador = request.session().attribute("id");
            
            //Tratamento de não login
            if (idJogador == null) {
                response.status(401);
                return "Usuário não autenticado";
            }
            
            //Chama o service
            if(serviceSol.add(request, response, idJogador)){
            	response.status(200);
            }else {
            	response.status(400);
            	return response.body();
            }
			return request;
        });
        
         // Rota para registrar um novo jogador
        post("/jogador", (request, response) -> serviceJogador.add(request, response));

            // Rota POST para autenticação de login
        post("/login", (request, response) -> {
                String user = request.queryParams("user");
                String senha = request.queryParams("senha");
                
                //Função que valida o login (retorna -1 se recusado, retorna o id caso aceito)
                int jogadorId = validarLogin(user, senha);
                
                if (jogadorId != -1) {
                    request.session(true).attribute("usuario_logado", user); // salva na sessão
                    request.session().attribute("id", jogadorId);
                    response.status(200);
                    return "Login bem-sucedido!";
                }else {
                    response.status(401); // Unauthorized
                    return "Usuário ou senha inválidos.";
                }
            });
        
        // Rota para inserir jogador em um grupo
        post("/grupo/entrar", (request, response) -> {
        	
        	int user = Integer.parseInt(request.queryParams("idJogador"));
            int grupo = Integer.parseInt(request.queryParams("idGrupo"));
            
            if(grupoDAO.inserirNoGrupo(user, grupo)) {
                response.status(200);
                return "Inserido com sucesso!";
            }else {
            	return "Falha ao inserir";
            }
        });
        
        // Rota para remover jogador de um grupo
        post("/grupo/sair", (request, response) -> {

        	
        	int user = Integer.parseInt(request.queryParams("idJogador"));
            int grupo = Integer.parseInt(request.queryParams("idGrupo"));
            
            if(grupoDAO.removerDoGrupo(user, grupo)) {
                response.status(200);
                return "Removido com sucesso!";
            }else {
            	return "Falha ao remover";
            }
        });
        
         // Rota para deletar um grupo
        post("/grupo/delete", (request, response) -> {
            int grupo = Integer.parseInt(request.queryParams("idGrupo"));
            
            if(grupoDAO.deletarGrupo(grupo)) {
                response.status(200);
                return "Removido com sucesso!";
            }else {
            	return "Falha ao remover";
            }
        });
        
        // Rota para atualizar grupo
        
        post("/grupo/updateId", (request, response) -> {
            String titulo = request.queryParams("titulo");
            String descricao = request.queryParams("descricao"); // novo

            // Modera título
            String resultadoModeracaoTitulo = ContentSafetyService.verificarTexto(titulo);
            if (!resultadoModeracaoTitulo.equals("Mensagem Aprovada")) {
                response.status(400);
                return "Título recusado: " + resultadoModeracaoTitulo;
            }

            // Modera descrição
            String resultadoModeracaoDescricao = ContentSafetyService.verificarTexto(descricao);
            if (!resultadoModeracaoDescricao.equals("Mensagem Aprovada")) {
                response.status(400);
                return "Descrição recusada: " + resultadoModeracaoDescricao;
            }

            if (grupoDAO.atualizarGrupo(request, response)) {
                response.status(200);
                return "Atualizado!";
            } else {
                response.status(400);
                return "Não atualizado!!";
            }
        });

        
        get("/grupo/myGrupos", (request, response) -> {
        	
        	Integer idJogador = request.session().attribute("id");
        	
            List<GrupoComMembrosDTO> grupos = grupoDAO.mostrarGrupos(idJogador);

            response.type("application/json");
            return gson2.toJson(grupos);
        });
        
        
        // Rota para validar sessão
        get("/validar_sessao", (request, response) -> {
            String user = request.session().attribute("usuario_logado");
            if (user == null) {
                response.status(401);
                return "Não autenticado";
            }
            return "Sessão ativa";
        });
        
         // Rota que retorna os grupos de um determinado jogo (com membros) do jogador logado
        get("/grupos/:jogo", (req, res) -> {
            String nomeJogo = req.params("jogo");
            Integer idJogador = req.session().attribute("id");
            
            List<GrupoComMembrosDTO> grupos = grupoDAO.buscarGruposPorNomeJogo(nomeJogo, idJogador);

            res.type("application/json");
            return gson.toJson(grupos);
        });

        // Rota para logout
        get("/logout", (request, response) -> {
            request.session().invalidate();
            return "Logout feito";
        });
        
         // Rota para buscar o perfil do jogador logado
        get("/api/perfil", (request, response) -> {
            Integer idJogador = request.session().attribute("id");

            if (idJogador == null) {
                response.status(401);
                return "Usuário não autenticado";
            }

            Jogador jogador = jogadorDAO.getJogador(idJogador);

            if (jogador == null) {
                response.status(404);
                return "Jogador não encontrado";
            }

            response.type("application/json");
            return gson.toJson(jogador);
        });
        
        // Rota para atualizar os dados do perfil do jogador
        post("/api/perfil", (request, response) -> {
            Integer idJogador = request.session().attribute("id");

            if (idJogador == null) {
                response.status(401);
                return "Usuário não autenticado";
            }

            // Pega os dados do corpo da requisição e adiciona o id do jogador
            Jogador jogador = new Gson().fromJson(request.body(), Jogador.class);
            jogador.setId(idJogador); // Garantir que o id seja o da sessão

            // Atualiza os dados no banco
            boolean sucesso = jogadorDAO.atualizarJogador(jogador);

            if (sucesso) {
                response.status(200);
                return "Perfil atualizado com sucesso!";
            } else {
                response.status(500);
                return "Erro ao atualizar perfil";
            }
        });
        
        post("/jogador/updatePerfil", (request, response) -> {
        	Integer idJogador = request.session().attribute("id");
        	
            if (jogadorDAO.atualizarPerfil(request, response, idJogador)) {
            	response.status(200);
            	return "Atualizado!";
            }else {
            	response.status(401);
            	return "Não atualizado!!";
            }
        });

        
        }

		// Função para validar login no banco de dados
	public static int validarLogin(String user, String senhaDigitada) {
	    String url = "jdbc:postgresql://localhost:5432/matchmate";
	    String dbUser = "admin";
	    String dbPassword = "123";

	    String sql = "SELECT * FROM jogador WHERE nome_usuario = ?";

	    try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, user);

	        ResultSet rs = stmt.executeQuery();

	        if (rs.next()) {
	            String senhaCriptografada = rs.getString("senha");
	            if (BCrypt.checkpw(senhaDigitada, senhaCriptografada)) {
	                return rs.getInt("id");
	            }
	        }

	        return -1;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return -1;
	    }
	}
        
        //Solicitacao solicitacao = new Solicitacao ("teste2", "GASOG", "Entra ai", "competitivo", "tarde", "PC", 15);
        //solicitacao.setIdSolicitacao(1);
        
        //dao.createSolicitacao(solicitacao);
        
    }

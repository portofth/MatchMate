package service;

import dao.*;
import model.Solicitacao;
import spark.Request;
import spark.Response;

public class SolicitacaoService {

	private DAO dao;

	public SolicitacaoService() {
		dao = new DAO();
	}

	// Verifica se a solicitação é válida
	public boolean verificaSolicitacao(Solicitacao solicitacao) {
		return !(solicitacao.getTitulo().isEmpty() ||
		         solicitacao.getNomeJogo().isEmpty() ||
		         solicitacao.getDescricao().isEmpty());
	}

	public boolean add(Request request, Response response, int IdJogador) {

		boolean status = false;

		try {
			String titulo = request.queryParams("titulo");
			String nome = request.queryParams("nome");
			String descricao = request.queryParams("descricao");
			String estilo = request.queryParams("estilo");
			String turno = request.queryParams("turno");
			String plataforma = request.queryParams("plataforma");
			int quant = Integer.parseInt(request.queryParams("quant"));

			// MODERAÇÃO DE CONTEÚDO
			String resultadoModeracao = ContentSafetyService.verificarTexto(titulo);
			if (!resultadoModeracao.equals("Mensagem Aprovada")) {
				response.status(400);
				response.body(resultadoModeracao); // Informa o motivo ao front-end
				return false;
			}

			String resultadoModeracaoDescricao = ContentSafetyService.verificarTexto(descricao);
			if (!resultadoModeracaoDescricao.equals("Mensagem Aprovada")) {
			    response.status(400);
			    response.body("Descrição recusada: " + resultadoModeracaoDescricao);
			    return false;
			}
			
			Solicitacao solicitacao = new Solicitacao(titulo, nome, descricao, estilo, turno, plataforma, IdJogador);

			if (!verificaSolicitacao(solicitacao)) {
				response.status(400);
				response.body("Dados da solicitação inválidos.");
				return false;
			}

			SolicitacaoDAO solDAO = new SolicitacaoDAO();
			int idSolicitacao = solDAO.createSolicitacao(solicitacao);

			if (idSolicitacao != -1) {
				GrupoDAO grupoDAO = new GrupoDAO();
				solicitacao.setIdSolicitacao(idSolicitacao);
				grupoDAO.criarGrupo(solicitacao, quant);
				status = true;
			}
		} catch (Exception e) {
			response.status(500);
			response.body("Erro interno ao criar solicitação.");
			e.printStackTrace(); 
			return false;
		}

		return status;
	}
}

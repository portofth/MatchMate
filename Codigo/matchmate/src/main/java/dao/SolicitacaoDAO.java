package dao;

import java.sql.*;

import model.Solicitacao;
import service.SolicitacaoService;

public class SolicitacaoDAO {

	private DAO dao;
	private GrupoDAO grupo;
	
	public SolicitacaoDAO() {
		this.dao = new DAO();           // inicializa a conexão
		this.grupo = new GrupoDAO();  
	}
	
	//Cria Solicitação no Banco de dados
	public int createSolicitacao (Solicitacao solicitacao) throws SQLException {
		
		int idGerado = -1;
		
		SolicitacaoService serviceSolicitacao = new SolicitacaoService();
		
		if(serviceSolicitacao.verificaSolicitacao(solicitacao)) {
			solicitacao.setStatus ("ativa");
			
			String sql = "INSERT INTO solicitacao_grupo (titulo, nome_jogo, descricao, estilo_jogo, turno_preferido, plataforma, status, data_criacao, id_criador) " +
		             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

			try (Connection conn = dao.getConexao();
				     PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

				    stmt.setString(1, solicitacao.getTitulo());
				    stmt.setString(2, solicitacao.getNomeJogo());
				    stmt.setString(3, solicitacao.getDescricao());
				    stmt.setString(4, solicitacao.getEstiloJogo());
				    stmt.setString(5, solicitacao.getTurnoPreferido());
				    stmt.setString(6, solicitacao.getPlataforma());
				    stmt.setString(7, solicitacao.getStatus());
				    stmt.setTimestamp(8, new java.sql.Timestamp(solicitacao.getDataCriacao().getTime()));
				    stmt.setInt(9, solicitacao.getIdCriador());

				    stmt.executeUpdate();

				    ResultSet rs = stmt.getGeneratedKeys();
				    if (rs.next()) {
				        idGerado = rs.getInt(1);
				    }

				} catch (SQLException e) {
				    System.out.println("Erro ao inserir solicitação: " + e.getMessage());
				}

			//se a solicitação for valida, chama a criação do grupo (passa a solicitação para pegar o id da mesma)
			//grupo.criarGrupo(solicitacao);
		}else {
			System.out.println("Solicitação Invalida");
		}
		
		return idGerado;
		
	}
	
}

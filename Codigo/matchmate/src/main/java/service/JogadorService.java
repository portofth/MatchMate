package service;
import java.sql.Date;
import org.mindrot.jbcrypt.BCrypt;
import dao.DAO;
import spark.Request;
import spark.Response;
import dao.JogadorDAO;
import model.Jogador;
public class JogadorService {

	private DAO dao;
	
	public JogadorService() {
		dao = new DAO();
	}

    // CADASTRAR Jogador (POST)
	public Object add(Request request, Response response) {
	    try {
	        String nomeUsuario = request.queryParams("user");
	        String senha = request.queryParams("senha");
	        String discordPerfil = request.queryParams("disc");
	        int idade = Integer.parseInt(request.queryParams("idade"));
	        String categoria = request.queryParams("categoria");

	        // Criptografa a senha antes de salvar
	        String senhaCriptografada = BCrypt.hashpw(senha, BCrypt.gensalt());

	        Jogador jogador = new Jogador(nomeUsuario, senhaCriptografada, discordPerfil, idade, categoria);
	        JogadorDAO jogadordao = new JogadorDAO();

	        if (jogadordao.inserirJogador(jogador)) {
	            response.status(201); // 201 Created
	            return "<mensagem>Jogador cadastrado com sucesso</mensagem>";
	        } else {
	            response.status(500);
	            return "<erro>Falha ao cadastrar jogador</erro>";
	        }

	    } catch (Exception e) {
	        response.status(400); // Bad Request
	        return "<erro>Dados inválidos</erro>";
	    }
	}
	
}

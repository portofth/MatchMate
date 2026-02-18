package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAO {
    
    private static final String DRIVER = "org.postgresql.Driver";
    private static final String URL = "jdbc:postgresql://localhost:5432/matchmate";
    private static final String USUARIO = "admin";
    private static final String SENHA = "123";

    // Bloco estático que carrega o driver JDBC uma vez quando a classe é usada
    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("Erro ao carregar o driver JDBC: " + e.getMessage());
        }
    }

    /**
     * Retorna uma nova conexão com o banco de dados.
     * A conexão deve ser fechada pelo chamador (preferencialmente com try-with-resources).
     */
    public Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}

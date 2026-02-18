package model;

import java.sql.Date;

public class Grupo {
	private int idGrupo;
	private int idSolicitacao;
	private Date dataCriacao;
	private Date dataEncerramento;
	private int maxJogadores;
	
	// Construtor padrão
    public Grupo() {
        this.idGrupo = 0;
        this.idSolicitacao = 0;
        this.dataCriacao = new Date(System.currentTimeMillis());
        this.dataEncerramento = null;
        this.maxJogadores = 4; // Valor padrão razoável
    }

    // Construtor com parâmetros básicos
    public Grupo(int idSolicitacao, int maxJogadores) {
        this();
        this.setIdSolicitacao(idSolicitacao);
        this.setMaxJogadores(maxJogadores);
    }

    // Construtor completo
    public Grupo(int idSolicitacao, Date dataCriacao, 
                Date dataEncerramento, int maxJogadores) {
        this.setIdSolicitacao(idSolicitacao);
        this.setDataCriacao(dataCriacao);
        this.setDataEncerramento(dataEncerramento);
        this.setMaxJogadores(maxJogadores);
    }

    // Getters e Setters com validações básicas

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        if (idGrupo < 0) {
            throw new IllegalArgumentException("ID do grupo não pode ser negativo");
        }
        this.idGrupo = idGrupo;
    }

    public int getIdSolicitacao() {
        return idSolicitacao;
    }

    public void setIdSolicitacao(int idSolicitacao) {
        if (idSolicitacao < 0) {
            throw new IllegalArgumentException("ID da solicitação não pode ser negativo");
        }
        this.idSolicitacao = idSolicitacao;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        if (dataCriacao == null) {
            throw new IllegalArgumentException("Data de criação não pode ser nula");
        }
        this.dataCriacao = dataCriacao;
    }

    public Date getDataEncerramento() {
        return dataEncerramento;
    }

    public void setDataEncerramento(Date dataEncerramento) {
        // Permite null (grupo ainda não encerrado)
        this.dataEncerramento = dataEncerramento;
    }

    public int getMaxJogadores() {
        return maxJogadores;
    }

    public void setMaxJogadores(int maxJogadores) {
        if (maxJogadores < 2 || maxJogadores > 20) {
            throw new IllegalArgumentException("Número máximo de jogadores deve estar entre 2 e 20");
        }
        this.maxJogadores = maxJogadores;
    }

    // Método toString para representação textual
    @Override
    public String toString() {
        return "Grupo{" +
                "idGrupo=" + idGrupo +
                ", idSolicitacao=" + idSolicitacao +
                ", dataCriacao=" + dataCriacao +
                ", dataEncerramento=" + dataEncerramento +
                ", maxJogadores=" + maxJogadores +
                '}';
    }
}

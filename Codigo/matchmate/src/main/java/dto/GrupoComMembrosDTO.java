package dto;

import java.util.Date;
import java.util.List;

public class GrupoComMembrosDTO {

    private int idGrupo;
    private String nomeJogo;
    private String titulo;
    private String descricao;
    private String estilo_jogo;
    private String turno_preferido;
    private String plataforma;

    private String nomeLider;
    private String discordLider;
    private int idLider;

    private int maxJogadores;
    private Date dataCriacao;
    private int idLogado;

    private List<Integer> idsMembros;
    private List<String> nomesMembros;
    private List<String> discordsMembros;

    // Getters e Setters

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getNomeJogo() {
        return nomeJogo;
    }

    public void setNomeJogo(String nomeJogo) {
        this.nomeJogo = nomeJogo;
    }

    public String getNomeLider() {
        return nomeLider;
    }

    public void setNomeLider(String nomeLider) {
        this.nomeLider = nomeLider;
    }

    public String getDiscordLider() {
        return discordLider;
    }

    public void setDiscordLider(String discordLider) {
        this.discordLider = discordLider;
    }

    public int getMaxJogadores() {
        return maxJogadores;
    }

    public void setMaxJogadores(int maxJogadores) {
        this.maxJogadores = maxJogadores;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public List<String> getNomesMembros() {
        return nomesMembros;
    }

    public void setNomesMembros(List<String> nomesMembros) {
        this.nomesMembros = nomesMembros;
    }

    public List<String> getDiscordsMembros() {
        return discordsMembros;
    }

    public void setDiscordsMembros(List<String> discordsMembros) {
        this.discordsMembros = discordsMembros;
    }

	public int getIdLogado() {
		return idLogado;
	}

	public void setIdLogado(int idLogado) {
		this.idLogado = idLogado;
	}

	public int getIdLider() {
		return idLider;
	}

	public void setIdLider(int idLider) {
		this.idLider = idLider;
	}

	public List<Integer> getIdsMembros() {
		return idsMembros;
	}

	public void setIdsMembros(List<Integer> idsMembros) {
		this.idsMembros = idsMembros;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getTurno_preferido() {
		return turno_preferido;
	}

	public void setTurno_preferido(String turno_preferido) {
		this.turno_preferido = turno_preferido;
	}

	public String getPlataforma() {
		return plataforma;
	}

	public void setPlataforma(String plataforma) {
		this.plataforma = plataforma;
	}

	public String getEstilo_jogo() {
		return estilo_jogo;
	}

	public void setEstilo_jogo(String estilo_jogo) {
		this.estilo_jogo = estilo_jogo;
	}

}

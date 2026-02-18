package model;

import java.util.Date;
import dao.SolicitacaoDAO;

public class Solicitacao {
    
    private int idSolicitacao;
    private String titulo;
    private String nomeJogo;
    private String descricao;
    private String estiloJogo;
    private String turnoPreferido;
    private String plataforma;
    private String status;
    private Date dataCriacao;
    private int idCriador;
    
    // Construtor sem parâmetros
    public Solicitacao() {
        setIdSolicitacao(0);
        setTitulo("");
        setNomeJogo("");
        setDescricao("");
        setEstiloJogo("");
        setTurnoPreferido("");
        setPlataforma("");
        setStatus("Pendente");
        setDataCriacao(new Date());
        setIdCriador(0);
    }

    // Construtor com parâmetros básicos
    public Solicitacao(String titulo, String nomeJogo, String descricao, String estiloJogo, 
                      String turnoPreferido, String plataforma, int idCriador) {
        this(); // Chama o construtor padrão primeiro
        setTitulo(titulo);
        setNomeJogo(nomeJogo);
        setDescricao(descricao);
        setEstiloJogo(estiloJogo);
        setTurnoPreferido(turnoPreferido);
        setPlataforma(plataforma);
        setIdCriador(idCriador);
    }

    // Construtor completo
    public Solicitacao(int idSolicitacao, String titulo, String nomeJogo, String descricao, 
                      String estiloJogo, String turnoPreferido, String plataforma, 
                      String status, Date dataCriacao, int idCriador) {
        setTitulo(titulo);
        setNomeJogo(nomeJogo);
        setDescricao(descricao);
        setEstiloJogo(estiloJogo);
        setTurnoPreferido(turnoPreferido);
        setPlataforma(plataforma);
        setStatus(status);
        setIdSolicitacao(idSolicitacao);
        setDataCriacao(dataCriacao);
        setIdCriador(idCriador);
    }

    // Getters e Setters
    public int getIdSolicitacao() {
        return idSolicitacao;
    }

    public void setIdSolicitacao(int idSolicitacao) {
        this.idSolicitacao = idSolicitacao;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo != null ? titulo : "";
    }

    public String getNomeJogo() {
        return nomeJogo;
    }

    public void setNomeJogo(String nomeJogo) {
        this.nomeJogo = nomeJogo != null ? nomeJogo : "";
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao != null ? descricao : "";
    }

    public String getEstiloJogo() {
        return estiloJogo;
    }

    public void setEstiloJogo(String estiloJogo) {
        this.estiloJogo = estiloJogo != null ? estiloJogo : "";
    }

    public String getTurnoPreferido() {
        return turnoPreferido;
    }

    public void setTurnoPreferido(String turnoPreferido) {
        this.turnoPreferido = turnoPreferido != null ? turnoPreferido : "";
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma != null ? plataforma : "";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status != null ? status : "Pendente";
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao != null ? dataCriacao : new Date();
    }

    public int getIdCriador() {
        return idCriador;
    }

    public void setIdCriador(int idCriador) {
        this.idCriador = idCriador;
    }
    
    
    // Método toString
    @Override
    public String toString() {
        return "Solicitacao[" + 
               "idSolicitacao=" + idSolicitacao + 
               ", titulo='" + titulo + '\'' + 
               ", nomeJogo='" + nomeJogo + '\'' + 
               ", descricao='" + descricao + '\'' + 
               ", estiloJogo='" + estiloJogo + '\'' + 
               ", turnoPreferido='" + turnoPreferido + '\'' + 
               ", plataforma='" + plataforma + '\'' + 
               ", status='" + status + '\'' + 
               ", dataCriacao=" + dataCriacao + 
               ", idCriador=" + idCriador + ']';
    }
}
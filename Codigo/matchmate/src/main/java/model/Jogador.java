package model;

import java.util.Date;
public class Jogador {
	
	private int id;
	private String nomeUsuario;
    private String senha;
	private String discordPerfil;
	private int idade;
	private String categoria;
	private Date dataCadastro;
	
	// Construtor sem parametros
    public Jogador() {
        setId(0);
        setNomeUsuario("");
        setSenha("");
        setDiscordPerfil("");
        setIdade(0);
        setCategoria("Padrão");
        setDataCadastro(new Date());
    }

    // Construtor com parâmetros
    public Jogador(String nomeUsuario, String senha, String discordPerfil, int idade, String categoria) {
        this(); // Chama o construtor padrão primeiro
        setNomeUsuario(nomeUsuario);
        setSenha(senha);
        setDiscordPerfil(discordPerfil);
        setIdade(idade);
        setCategoria(categoria);
        setDataCadastro(new Date());
    }

    // Construtor completo
    public Jogador(int id, String nomeUsuario, String senha, String discordPerfil, int idade, String categoria) {
        setNomeUsuario(nomeUsuario);
        setSenha(senha);
        setDiscordPerfil(discordPerfil);
        setIdade(idade);
        setCategoria(categoria);
        setId(id);
        setDataCadastro(new Date());
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }
    
    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario != null ? nomeUsuario : "";
    }

    public String getSenha(){
        return senha;
    }

    public void setSenha(String senha){
        this.senha = senha;
    }

    public String getDiscordPerfil() {
        return discordPerfil;
    }

    public void setDiscordPerfil(String discordPerfil) {
        this.discordPerfil = discordPerfil != null ? discordPerfil : "";
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = Math.max(idade, 0); // Garante que idade não seja negativa
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria != null ? categoria : "Padrão";
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro != null ? dataCadastro : new Date();
    }
    
    //Converte todos os dados para string
    @Override
    public String toString() {
        return "Jogador[" + "id=" + id + ", nomeUsuario='" + nomeUsuario + '\'' + ", senha=" + senha + '\'' + ", discordPerfil='" + 
        discordPerfil + '\'' + ", idade=" + idade + ", categoria='" + categoria + '\'' + ", dataCadastro=" + dataCadastro + ']';
    }
	

}

//1-Verificar se a inicialização das variaveis não vão dar conflito no bd
//2-Validar nos sets tamanho do nome nas strings(minimo e maximo) e a questão se aceita campos vazios



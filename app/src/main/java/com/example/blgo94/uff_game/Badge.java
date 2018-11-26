package com.example.blgo94.uff_game;

public class Badge {

    private String descricao;
    private String id;
    private String pontuacao;
    private String nome;

    //DataSnapshot
    public Badge(){
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Badge(String id, String descricao, String pontuacao, String nome){
        this.descricao = descricao;
        this.id = id;
        this.pontuacao = pontuacao;
        this.nome = nome;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setPontuacao(String pontuacao) {
        this.pontuacao = pontuacao;
    }

    public String getPontuacao() {
        return pontuacao;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}

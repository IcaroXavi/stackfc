package com.stack.model;

public class Jogador {
    private String nome;
    private String posicao;
    private String pos;
    private int idade;
    private int defesa;
    private int ataque;
    private int total;
    private int clube;
    private int status;
    

    public Jogador(String nome, String posicao, String pos, int idade, int defesa, int ataque, int total, int clube, int status) {
        this.nome = nome;
        this.posicao = posicao;
        this.posicao = pos;
        this.idade = idade;
        this.defesa = defesa;
        this.ataque = ataque;
        this.total = total;
        this.clube = clube;
        this.status = status;
    }

    // Getters
    public String getNome() { return nome; }
    public String getPosicao() { return posicao; }
    public String getPos() { return pos; }
    public int getIdade() { return idade; }
    public int getDefesa() { return defesa; }
    public int getAtaque() { return ataque; }
    public int getTotal() { return total; }
    public int getClube() { return clube; }
    public int getStatus() { return status; }
}
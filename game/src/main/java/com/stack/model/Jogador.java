package com.stack.model;

public class Jogador {
    private int id;
    private String nome;
    private String posicao;
    private String pos;
    private int ordem;
    private int idade;
    private int defesa;
    private int ataque;
    private int total;
    private int clube;
    private int status;
    private int energia;
    

    public Jogador(int id, String nome, String posicao, String pos, int ordem, int idade, int defesa, int ataque, int total, int clube, int status, int energia) {
        this.id = id;
        this.nome = nome;
        this.posicao = posicao;
        this.pos = pos;
        this.ordem = ordem;        
        this.idade = idade;
        this.defesa = defesa;
        this.ataque = ataque;
        this.total = total;
        this.clube = clube;
        this.status = status;
        this.energia = energia;
    }

    // Getters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getPosicao() { return posicao; }
    public String getPos() { return pos; }
    public int getOrdem() { return ordem; }    
    public int getIdade() { return idade; }
    public int getDefesa() { return defesa; }
    public int getAtaque() { return ataque; }
    public int getTotal() { return total; }
    public int getClube() { return clube; }
    public int getStatus() { return status; }
    public int getEnergia() { return energia; }

    public void setOrdem(int ordem) { this.ordem = ordem; }
    public void setStatus(int status) { this.status = status; }
}
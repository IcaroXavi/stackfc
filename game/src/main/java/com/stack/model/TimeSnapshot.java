package com.stack.model;

public class TimeSnapshot {
    private int ataque;
    private int defesa;
    private int overall;

    public TimeSnapshot(int ataque, int defesa, int overall) {
        this.ataque = ataque;
        this.defesa = defesa;
        this.overall = overall;
    }

    // OS MÉTODOS QUE O MOTOR ESTÁ PROCURANDO:
    public int getAtaque() { return ataque; }
    public int getDefesa() { return defesa; }
    public int getOverall() { return overall; }
}
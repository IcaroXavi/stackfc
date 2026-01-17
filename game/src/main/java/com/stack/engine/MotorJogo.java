package com.stack.engine;

import com.stack.model.Jogador;
import com.stack.repository.JogadorRepository;
import java.util.ArrayList;
import java.util.List;

public class MotorJogo {
    private List<Jogador> titularesCasa;
    private List<Jogador> titularesVisitante;
    private List<Jogador> reservasCasa;
    private List<Jogador> reservasVisitante;
    private int idCasa;
    private int idVisitante;
    private int somaTotalCasa = 0;
    private int somaTotalVis = 0;
    private int somaAtqCasa = 0;
    private int somaAtqVis = 0;
    private int somaDefCasa = 0;
    private int somaDefVis = 0;
    private int placarCasa = 0;
    private int placarVisitante = 0;
    private int minutos = 0;
    private int tempo = 1;
    

    public MotorJogo(int idCasa, int idVisitante) {
        this.idCasa = idCasa;
        this.idVisitante = idVisitante;
        this.titularesCasa = new ArrayList<>();
        this.titularesVisitante = new ArrayList<>();
        this.reservasCasa = new ArrayList<>();
        this.reservasVisitante = new ArrayList<>();
        carregarEquipes();
    }

    private void carregarEquipes() {
        // 1. Limpar a lista a cada loop para carregar dados atualizados
        titularesCasa.clear();
        titularesVisitante.clear();

        // 2. Variáveis para as somas
        somaTotalCasa = 0; 
        somaTotalVis = 0; 
        somaTotalCasa = 0;
        somaAtqCasa = 0; 
        somaAtqVis = 0; 
        somaDefCasa = 0;
        somaDefVis = 0;
        
        JogadorRepository repo = new JogadorRepository();
        List<Jogador> todos = repo.carregarTodos();
        
        // 3. Loop para somar os valores de força
        for (Jogador j : todos) {

            if (j.getClube() == idCasa) {
                if (j.getStatus() == 1) {
                    titularesCasa.add(j);
                    // Soma apenas de quem está em campo
                    somaAtqCasa += j.getAtaque();
                    somaDefCasa += j.getDefesa();
                    somaTotalCasa += j.getTotal();
                } 
            } 
            else if (j.getClube() == idVisitante) {
                if (j.getStatus() == 1) {
                    titularesVisitante.add(j);
                    // Soma apenas de quem está em campo
                    somaAtqVis += j.getAtaque();
                    somaDefVis += j.getDefesa();
                    somaTotalVis += j.getTotal();
                } 
            }
        }

        System.out.println("FORÇA TIME CASA      | Atk: " + somaAtqCasa + " | Def: " + somaDefCasa + " | Total: " + somaTotalCasa);
        System.out.println("FORÇA TIME VISITANTE | Atk: " + somaAtqVis + " | Def: " + somaDefVis + " | Total: " + somaTotalVis);
        System.out.println("--------------------------------------------------------------------------");
        System.out.println(placarCasa + " x " + placarVisitante);
    }

    public int processarLance() {
            carregarEquipes(); 

            double dadoLance = Math.random() * 100;
            System.out.println(dadoLance);    
            if (dadoLance < 3) {
                System.out.println("[MOTOR] Evento: Lesão grave!");
                return 1; // Código para Lesão
            } 
            else if (dadoLance < 8) {
                System.out.println("[MOTOR] Evento: Cartão Vermelho!");
                return 2; // Código para Vermelho
            } 
            else if (dadoLance <= 20) {
                System.out.println("[MOTOR] Evento: Cartão Amarelo");
                return 3; // Código para Amarelo
            } 
            else if (dadoLance <= 50) {
                System.out.println("[MOTOR] Evento: Nada acontece (jogo truncado)");
                return 4; // Segue o jogo
            } 
            else {
                System.out.println("LANCE PERIGOSO");
                double dadoPosse = Math.random() * (somaTotalCasa + somaTotalVis);
                if (dadoPosse < somaTotalCasa) {
                System.out.println(dadoPosse + " ataque do time da casa");
                    return posseCasa();
            }
                else {System.out.println(dadoPosse + " ataque do time visitante");
                    return posseVisitante();
            } 
        }
    }
    private int posseCasa() {
        double dadoGol = Math.random() * 100;
        int diferenca = somaAtqCasa - somaDefVis;

        System.out.println("[FINALIZAÇÃO CASA] Dif: " + diferenca + " | Dado: " + dadoGol);

        if ((diferenca > 30 && dadoGol < 50) || 
            (diferenca > 20 && dadoGol < 40) || 
            (diferenca > 10 && dadoGol < 30) || 
            (diferenca > 0  && dadoGol < 20) || 
            (diferenca <= 0 && dadoGol < 10)) {
            
            placarCasa++; 
            System.out.println("GOL DO TIME DA CASA, O ESTÁDIO VAI A LOUCURA!");
            return 10; 
        } 
        
        System.out.println("ZAGA AFASTA O PERIGO!");
        return 0; 
    }

    private int posseVisitante() {
        double dadoGol = Math.random() * 100;
        int diferenca = somaAtqVis - somaDefCasa;

        System.out.println("[FINALIZAÇÃO FORA] Dif: " + diferenca + " | Dado: " + dadoGol);

        if ((diferenca > 30 && dadoGol < 50) || 
            (diferenca > 20 && dadoGol < 40) || 
            (diferenca > 10 && dadoGol < 30) || 
            (diferenca > 0  && dadoGol < 20) || 
            (diferenca <= 0 && dadoGol < 10)) {
            
            placarVisitante++;
            System.out.println("GOL DO VISITANTE! SILÊNCIO NO ESTÁDIO!");
            return 11; 
        }

        System.out.println("O GOLEIRO DEFENDEU!");
        return 0;
    }

    public String getPlacarFormatado() {
        return placarCasa + " - " + placarVisitante;
    }

    public void iniciarCronometro() {
        new Thread(() -> {
            try {
                while (minutos < 49) {
                    Thread.sleep(100); // 1000 = 1 segundo
                    minutos++;
                    if (minutos > 0 && minutos % 4 == 0) {
                    processarLance();
                }
                }
                if (minutos >= 49) {
                tempo++; // Aumenta de 1 para 2 (Segundo Tempo)
                minutos = 0; // Reseta para começar o 2º tempo do 45
            }
            } catch (InterruptedException e) {
            }
        }).start(); 
    }

    public String getCronometroFormatado() {
        if (minutos == 0) {
            return "0";
        } else if (minutos <= 45) {
            return String.valueOf(minutos);
        } else if (minutos <= 48) {
            return "Acréscimos"; 
        } 
        return "0";
    }

    public String getTempo() {
        if (tempo == 1 && minutos == 0) {
            return "PRÉ JOGO";
        } else if (tempo == 1 && minutos > 0) {
            return "1º Tempo";    
        } else if (tempo == 2 && minutos == 0) {
            return "Intervalo";
        } else if (tempo == 2 && minutos > 0) {
            return "2º Tempo" ; 
        } 
        return "Encerrada";
    }

}
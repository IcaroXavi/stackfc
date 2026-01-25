package com.stack.engine;

import java.util.ArrayList;
import java.util.List;

import com.stack.model.Jogador;
import com.stack.model.TimeSnapshot;
import com.stack.repository.JogadorRepository;

public class MotorJogo {
    private List<Jogador> titularesCasa;
    private List<Jogador> titularesVisitante;
    private List<Jogador> reservasCasa;
    private List<Jogador> reservasVisitante;
    private int idCasa;
    private int idVisitante;
    private TimeSnapshot dadosCasa; 
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
    private boolean pausado = false;
    private String formacaoCasa;
    private String posturaCasa;
    private final Object trava = new Object(); 
    private JogadorRepository repo; 
    private volatile TimeSnapshot snapshotCasa;
    private volatile TimeSnapshot snapshotVisitante;
    public List<Jogador> getTitularesCasa() { return titularesCasa; }
    public List<Jogador> getTitularesVisitante() {
        return titularesVisitante;
    }
    public List<Jogador> getReservasCasa() { return reservasCasa; }
    public List<Jogador> getReservasVisitante() {
        return reservasVisitante;
    }
    public MotorJogo(int idCasa, int idVisitante) {
        this.idCasa = idCasa;
        this.idVisitante = idVisitante;
        this.titularesCasa = new ArrayList<>();
        this.titularesVisitante = new ArrayList<>();
        this.reservasCasa = new ArrayList<>();
        this.reservasVisitante = new ArrayList<>();
        this.repo = new JogadorRepository(); 
        capturarDadosIniciais();
    }

    public void configurarEquipes(TimeSnapshot snapshot) {
        this.dadosCasa = snapshot;
        
        // Apenas para teste inicial, você pode imprimir no console:
        System.out.println("Motor configurado com Ataque: " + snapshot.getAtaque());
    }

    private void capturarDadosIniciais() {
        JogadorRepository repo = new JogadorRepository();
        List<Jogador> todosOsJogadoresDoBanco = repo.carregarTodos();

        for (Jogador j : todosOsJogadoresDoBanco) {
            // Verifica se o jogador pertence ao time da CASA
            if (j.getClube() == idCasa) {
                if (j.getStatus() == 1) {
                    titularesCasa.add(j);
            } else if (j.getStatus() == 2) {
                reservasCasa.add(j);
            }
            } 
            // Verifica se o jogador pertence ao time VISITANTE
            else if (j.getClube() == idVisitante) {
                if (j.getStatus() == 1) {
                    titularesVisitante.add(j);
            } else if (j.getStatus() == 2) {
                    reservasVisitante.add(j);
                }
            }
        }

            com.stack.repository.DadosRepository dadosRepo = new com.stack.repository.DadosRepository();
            java.util.Map<String, String> dadosGlobais = dadosRepo.carregarDados();

            if (dadosGlobais != null && !dadosGlobais.isEmpty()) {
                this.formacaoCasa = dadosGlobais.get("formacao");
                this.posturaCasa = dadosGlobais.get("postura");
            }

            // Caso o mapa venha vazio (segurança), define um valor para não dar erro na tela
            if (this.formacaoCasa == null) this.formacaoCasa = "4-4-2";
            if (this.posturaCasa == null) this.posturaCasa = "Equilibrada";
        recalcularForcaDasEquipes();
    }

    public void recalcularForcaDasEquipes() {
        // 1. Se o snapshot (dados vindos da Tela de Táticas) existir, use ele!
        if (dadosCasa != null) {
            somaAtqCasa = dadosCasa.getAtaque();
            somaDefCasa = dadosCasa.getDefesa();
            somaTotalCasa = dadosCasa.getOverall();
        } else {
            // Fallback: caso o snapshot não tenha sido injetado ainda
            somaAtqCasa = 0; somaDefCasa = 0; somaTotalCasa = 0;
            for (Jogador j : titularesCasa) {
                somaAtqCasa += j.getAtaque();
                somaDefCasa += j.getDefesa();
                somaTotalCasa += j.getTotal();
            }
        }

        // 2. Para o visitante (que ainda é processado automaticamente), mantém a soma
        somaAtqVis = 0; somaDefVis = 0; somaTotalVis = 0;
        for (Jogador j : titularesVisitante) {
            somaAtqVis += j.getAtaque();
            somaDefVis += j.getDefesa();
            somaTotalVis += j.getTotal();
        }
    }

    public int processarLance() {
            recalcularForcaDasEquipes();; 

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
                System.out.println("somaAtqCasa: " + somaAtqCasa + " somaDefCasa: " + somaDefCasa);
                System.out.println("somaAtqVis: " + somaAtqVis + " somaDefVis: " + somaDefVis);
                System.out.println("somaTotalCasa: " + somaTotalCasa + " somaTotalVisitante: " + somaTotalVis);
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
    
    public void atualizarEscalacao(List<Jogador> novosTitulares, List<Jogador> novosReservas, String novaFormacao, String novaPostura) {
        synchronized (trava) { // Garante que não mude o time no meio de um processamento de lance
            this.titularesCasa = novosTitulares;
            this.reservasCasa = novosReservas;
            this.formacaoCasa = novaFormacao;
            this.posturaCasa = novaPostura;
            
            // Recalcula as forças após a mudança
            recalcularForcaDasEquipes();
        }
    }

    public void iniciarCronometro() {
        new Thread(() -> {
            try {
                while (tempo <= 2) {
                    while (minutos < 49) { // Loop dos minutos
                        synchronized (trava) {
                            while (pausado) {
                                trava.wait(); 
                            }
                        }    
                        Thread.sleep(100); 
                        minutos++;
                        if (minutos > 0 && minutos % 4 == 0) {
                            processarLance();
                        }
                    }
                    if (tempo == 1) {
                        this.pausarJogo(); // Força a pausa para o intervalo
                        this.tempo = 2;    // Prepara para o próximo tempo
                        this.minutos = 0;  // Reseta o cronômetro  
                    } else {
                        this.tempo = 3;
                        this.minutos = 0;
                    }   
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
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

    public void pausarJogo() {
        pausado = true;
    }

    public boolean isPausado() {
        return pausado;
    }

    public void retomarJogo() {
        synchronized (trava) {
            pausado = false;
            trava.notify(); // Acorda a Thread que está no wait()
        }
    }

    public JogadorRepository getRepo() {
    return this.repo;
    }

    // Getters para as somas de atributos (Necessários para o log do Controller)
    public int getSomaAtqCasa() { return somaAtqCasa; }
    public int getSomaDefCasa() { return somaDefCasa; }
    public int getSomaAtqVis()  { return somaAtqVis; }
    public int getSomaDefVis()  { return somaDefVis; }
    public int getSomaTotalCasa() { return somaTotalCasa; }
    public int getSomaTotalVis() { return somaTotalVis; }

    // Dentro da classe MotorJogo
    public String getFormacaoCasa() {
        return this.formacaoCasa; // Ou o nome da variável que você usa para "4-4-2"
    }

    public String getPosturaCasa() {
        return this.posturaCasa; // Ou o nome da variável que você usa para "Ofensiva"
    }
    

}
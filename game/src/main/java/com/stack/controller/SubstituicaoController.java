package com.stack.controller;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
 
import com.stack.engine.MotorJogo;
import com.stack.model.Jogador;
import com.stack.repository.JogadorRepository;
import com.stack.view.TelaSubstituicao;

public class SubstituicaoController {
    private TelaSubstituicao vista;
    private MotorJogo motor;
    private JogadorRepository repo;
    private JDialog dialog;

    // Construtor Único e Funcional
    public SubstituicaoController(TelaSubstituicao vista, MotorJogo motor, JogadorRepository repo, JDialog dialog) {
        this.vista = vista;
        this.motor = motor;
        this.repo = repo;
        this.dialog = dialog;
        String fAtual = motor.getFormacaoCasa();
        String pAtual = motor.getPosturaCasa();
        // Configura os listeners uma única vez
        this.vista.getBtnVoltar().addActionListener(e -> dialog.dispose());
        this.vista.getBtnJogar().addActionListener(e -> confirmarMudancas());
        this.vista.atualizarConfiguracoes(fAtual, pAtual);
        this.vista.getBtnVoltar().addActionListener(e -> dialog.dispose());
    }

    private void confirmarMudancas() {
    try {
            // 1. Coleta os IDs que estão na tela (View)
            List<Integer> idsTitulares = vista.getIdsDosTitulares();
            List<Integer> idsReservas = vista.getIdsDosReservas();

            // 2. Cria a lista de Titulares e já define a Ordem (0 a 10)
            List<Jogador> novosTitulares = new ArrayList<>();
            for (int i = 0; i < idsTitulares.size(); i++) {
                Integer id = idsTitulares.get(i);
                if (id != null) {
                    Jogador j = repo.buscarPorId(id);
                    if (j != null) {
                        j.setOrdem(i);    // Define a posição na fila (0, 1, 2...)
                        j.setStatus(1);   // Define como Titular
                        novosTitulares.add(j);
                    }
                }
            }

            // 3. Cria a lista de Reservas e define a Ordem do banco (11 em diante)
            List<Jogador> novosReservas = new ArrayList<>();
            for (int i = 0; i < idsReservas.size(); i++) {
                Integer id = idsReservas.get(i);
                if (id != null) {
                    Jogador j = repo.buscarPorId(id);
                    if (j != null) {
                        j.setOrdem(i + 11); // Começa no 11, 12, 13...
                        j.setStatus(2);     // Define como Reserva
                        novosReservas.add(j);
                    }
                }
            }

            // 3. Pega formação e postura
            String formacao = (String) vista.getComboFormacao().getSelectedItem();
            String postura = (String) vista.getComboMentalidade().getSelectedItem();

            // 4. APLICA NO MOTOR (Essencial para refletir na partida)
            motor.atualizarEscalacao(novosTitulares, novosReservas, formacao, postura);
            // --- CONSOLE LOG DE DEPURACÃO ---
            System.out.println("\n" + "=".repeat(50));
            System.out.println("📊 RELATÓRIO DE ALTERAÇÃO TÁTICA");
            System.out.println("=".repeat(50));
            System.out.println("⚽ FORMAÇÃO: " + formacao + " | POSTURA: " + postura);
            
            System.out.println("\n🏃 TITULARES ESCALADOS (CASA):");
            for (Jogador j : motor.getTitularesCasa()) {
            // Note que adicionei "ORD: %d" no texto e j.getOrdem() na lista de variáveis
            System.out.printf("- ID: %d | ORD: %d | %-15s | ATQ: %d | DEF: %d | OVR: %d | ENG: %.2f%n" , 
                j.getId(), j.getOrdem(), j.getNome(), j.getAtaque(), j.getDefesa(), j.getTotal(), (double) j.getEnergia());
            }

            System.out.println("\n🪑 BANCO DE RESERVAS (CASA):");
            for (Jogador j : motor.getReservasCasa()) {
            System.out.printf("- ID: %d | ORD: %d | %-15s | ATQ: %d | DEF: %d | OVR: %d | ENG: %.2f%n" , 
                j.getId(), j.getOrdem(), j.getNome(), j.getAtaque(), j.getDefesa(), j.getTotal(), (double) j.getEnergia());
            }

            System.out.println("\n📈 STATUS DA PARTIDA (FORÇA TOTAL):");
            // Certifique-se que esses atributos no MotorJogo são public ou têm getters
            System.out.println("TIME CASA      -> ATQ: " + motor.getSomaAtqCasa() + " | DEF: " + motor.getSomaDefCasa());
            System.out.println("TIME VISITANTE -> ATQ: " + motor.getSomaAtqVis()  + " | DEF: " + motor.getSomaDefVis());
            System.out.println("=".repeat(50) + "\n");
            // ---------------------------------
            dialog.dispose(); 
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Erro ao confirmar substituições: " + ex.getMessage());
        }
    }

    public void realizarSubstituicao(Jogador sai, Jogador entra) {
        // 1. Pega os valores atuais de ordem
        int ordemDoQueSai = sai.getOrdem();
        int ordemDoQueEntra = entra.getOrdem();

        // 2. Inverte as ordens (O João pega a 0 e o Figo pega a 11)
        sai.setOrdem(ordemDoQueEntra);
        entra.setOrdem(ordemDoQueSai);

        // 3. Inverte os status (O João vira titular [1] e o Figo vira reserva [2])
        int statusTemporario = sai.getStatus();
        sai.setStatus(entra.getStatus());
        entra.setStatus(statusTemporario);
        
        // 4. Agora sim, o motor recalcula com as ordens novas
        motor.recalcularForcaDasEquipes();
    }
    
}
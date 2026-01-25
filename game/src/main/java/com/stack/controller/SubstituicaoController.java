package com.stack.controller;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import com.stack.engine.MotorJogo;
import com.stack.model.Jogador;
import com.stack.model.TimeSnapshot;
import com.stack.repository.JogadorRepository;
import com.stack.view.TelaSubstituicao;

public class SubstituicaoController {
    private TelaSubstituicao vista;
    private MotorJogo motor;
    private JogadorRepository repo;
    private JDialog dialog;

    public SubstituicaoController(TelaSubstituicao vista, MotorJogo motor, JogadorRepository repo, JDialog dialog) {
        this.vista = vista;
        this.motor = motor;
        this.repo = repo;
        this.dialog = dialog;

        // Configura a tela com a tática atual do motor antes de abrir
        this.vista.atualizarConfiguracoes(motor.getFormacaoCasa(), motor.getPosturaCasa());

        // Listeners
        this.vista.getBtnVoltar().addActionListener(e -> dialog.dispose());
        this.vista.getBtnJogar().addActionListener(e -> confirmarMudancas());
    }

    private void confirmarMudancas() {
        try {
            int novoTotalSubs = vista.calcularTotalSubsParaMotor(); 
            // 1. Coleta os IDs que estão na memória visual da TelaSubstituicao (Tabelas)
            if (novoTotalSubs > 5) {
            javax.swing.JOptionPane.showMessageDialog(dialog, 
                "Limite de substituições excedido!\nVocê só pode realizar 5 trocas por partida.", 
                "Limite Atingido", 
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return; // Interrompe o método aqui mesmo
            }
            List<Integer> idsTitulares = vista.getIdsDosTitulares();
            List<Integer> idsReservas = vista.getIdsDosReservas();

            // 2. Busca os objetos Jogador reais para atualizar o estado interno (status/ordem)
            List<Jogador> novosTitulares = buscarEAtualizar(idsTitulares, 1, 0);
            List<Jogador> novosReservas = buscarEAtualizar(idsReservas, 2, 11);

            // 3. CAPTURA O SNAPSHOT (A memória local da View com os cálculos de penalidade)
            TimeSnapshot snapshotVindoDaTela = vista.getSnapshotAtual();

            // 4. INJETA OS DADOS NO MOTOR
            // Primeiro: Passamos o snapshot para o Motor (isso preenche 'dadosCasa')
            motor.configurarEquipes(snapshotVindoDaTela);
            motor.setSubsRealizadasCasa(novoTotalSubs); // <--- ADICIONE ESTA LINHA
            // Segundo: Atualizamos a escalação (isso muda as listas e chama o recalcular)
            String formacao = (String) vista.getComboFormacao().getSelectedItem();
            String postura = (String) vista.getComboMentalidade().getSelectedItem();
            
            motor.atualizarEscalacao(novosTitulares, novosReservas, formacao, postura);

            // O motor agora usará os valores do snapshot dentro do recalcularForcaDasEquipes()
            System.out.println("✅ Dados da memória local (View) injetados no MotorJogo.");
            
            dialog.dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private List<Jogador> buscarEAtualizar(List<Integer> ids, int status, int ordemInicial) {
        List<Jogador> lista = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            Jogador j = repo.buscarPorId(ids.get(i));
            if (j != null) {
                j.setStatus(status);
                j.setOrdem(ordemInicial + i);
                lista.add(j);
            }
        }
        return lista;
    }
}
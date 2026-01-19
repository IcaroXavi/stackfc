package com.stack.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

import com.stack.model.Financas;
import com.stack.model.Jogador;
import com.stack.repository.JogadorRepository;
import com.stack.view.TelaDashboard;

public class DashboardController {
    private TelaDashboard view;
    private Financas modelFinancas;

    public DashboardController(TelaDashboard view, Financas financas) {
        this.view = view;
        this.modelFinancas = financas;
        
        configurarNavegacao();
        conectarAcoesEspeciais(); 
        carregarModuloDados();
    }

    private void carregarModuloDados() {
        JogadorRepository repo = new JogadorRepository();
        com.stack.repository.DadosRepository dadosRepo = new com.stack.repository.DadosRepository();
        
        // 1. Carrega as configurações globais (Clube, Formação, Postura)
        Map<String, String> dadosGlobais = dadosRepo.carregarDados();
        int idClubeUsuario = 1; 
        String formacao = "4-4-2";
        String postura = "Equilibrada";

        if (dadosGlobais != null && !dadosGlobais.isEmpty()) {
            idClubeUsuario = Integer.parseInt(dadosGlobais.get("clube"));
            formacao = dadosGlobais.get("formacao");
            postura = dadosGlobais.get("postura");
        }

        // 2. BUSCA DIRETA: Peça ao repositório apenas os jogadores do seu clube
        // (É mais eficiente do que carregar TODOS os jogadores do jogo e filtrar no Java)
        List<Jogador> meuElenco = repo.carregarPorClube(idClubeUsuario); 

        // 3. Configura a Tela
        if (view.getTelaTaticas() != null) {
            view.getTelaTaticas().getBtnVoltar().addActionListener(e -> view.voltarParaMenu());
            view.getTelaTaticas().setEstrategia(formacao, postura); 
            view.getTelaTaticas().carregarTaticaInicial(meuElenco);
        }
    }
    private void configurarNavegacao() {
        view.getBtnHome().addActionListener(e -> view.mudarAba("HOME"));
        view.getBtnElenco().addActionListener(e -> view.mudarAba("ELENCO"));
        view.getBtnMercado().addActionListener(e -> view.mudarAba("MERCADO"));
        view.getBtnSede().addActionListener(e -> view.mudarAba("SEDE"));
    }

    private void conectarAcoesEspeciais() {
        // Ação do botão "VOLTAR" (Navegação para o menu)
        if (view.getTelaTaticas() != null) {
            view.getTelaTaticas().getBtnVoltar().addActionListener(e -> view.voltarParaMenu());
        }

        view.getBtnAvancar().addActionListener(e -> view.mostrarTelaTaticas()); 

        if (view.getTelaTaticas() != null) {
            view.getTelaTaticas().getBtnJogar().addActionListener(e -> {
                var tela = view.getTelaTaticas();

                // --- 1. VALIDAÇÃO DE ELENCO COMPLETO ---
                int totalTitulares = 0;
                // Percorre a coluna de ID (coluna 0) da tabela de titulares
                for (int i = 0; i < tela.getTabelaTitulares().getRowCount(); i++) {
                    Object id = tela.getTabelaTitulares().getValueAt(i, 0);
                    if (id != null && !id.toString().trim().isEmpty()) {
                        totalTitulares++;
                    }
                }

                if (totalTitulares < 11) {
                    javax.swing.JOptionPane.showMessageDialog(
                        view, 
                        "Escale os 11 titulares para iniciar a partida.", 
                        "Escalação Incompleta", 
                        javax.swing.JOptionPane.WARNING_MESSAGE
                    );
                    return; // INTERROMPE O MÉTODO AQUI (não salva nem inicia o jogo)
                }
                // ---------------------------------------

                // Se passou da validação, segue o fluxo normal:
                String formacaoAtual = tela.getComboFormacao().getSelectedItem().toString();
                String posturaAtual = tela.getComboMentalidade().getSelectedItem().toString();
                new com.stack.repository.DadosRepository().salvarEstrategia(formacaoAtual, posturaAtual); 

                Map<Integer, Integer> mapaEstadoCompleto = new HashMap<>();
                coletarDadosTabela(tela.getTabelaTitulares(), 0, mapaEstadoCompleto);
                coletarDadosTabela(tela.getTabelaReservas(), 11, mapaEstadoCompleto);
                coletarDadosTabela(tela.getTabelaDisponiveis(), 17, mapaEstadoCompleto);

                new JogadorRepository().salvarEstadoCompleto(mapaEstadoCompleto);

                com.stack.engine.MotorJogo novoMotor = new com.stack.engine.MotorJogo(1, 2);
                view.iniciarNovaPartida(novoMotor);
            });
        }
    }

    private void coletarDadosTabela(javax.swing.JTable tabela, int offsetOrdem, Map<Integer, Integer> mapa) {
        DefaultTableModel model = (DefaultTableModel) tabela.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            Object idObj = model.getValueAt(i, 0); 
            if (idObj != null) {
                try {
                    int id = Integer.parseInt(idObj.toString());
                    mapa.put(id, offsetOrdem + i);
                } catch (NumberFormatException e) {
                    // Ignora linhas sem ID numérico (vagas vazias)
                }
            }
        }
    }

}
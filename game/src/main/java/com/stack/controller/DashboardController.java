package com.stack.controller;

import com.stack.model.Financas;
import com.stack.repository.JogadorRepository;
import com.stack.view.TelaDashboard;
import javax.swing.JOptionPane;

public class DashboardController {
    private TelaDashboard view;
    private Financas modelFinancas;

    public DashboardController(TelaDashboard view, Financas financas) {
        this.view = view;
        this.modelFinancas = financas;
        
        configurarNavegacao();
        conectarAcoesEspeciais(); 
        carregarModuloElenco(); // Chame aqui para os dados aparecerem!
    }

    private void carregarModuloElenco() {
        JogadorRepository repo = new JogadorRepository();
        //List<Jogador> meusJogadores = repo.buscarJogadoresDoTime(1);
        //view.getTelaElenco().atualizarTabela(meusJogadores);
    }

    // 1. MÉTODO DE NAVEGAÇÃO (Abas de baixo)
    private void configurarNavegacao() {
        view.getBtnHome().addActionListener(e -> view.mudarAba("HOME"));
        view.getBtnElenco().addActionListener(e -> view.mudarAba("ELENCO"));
        view.getBtnTaticas().addActionListener(e -> view.mudarAba("TATICAS"));
        view.getBtnMercado().addActionListener(e -> view.mudarAba("MERCADO"));
        view.getBtnSede().addActionListener(e -> view.mudarAba("SEDE"));
    }

    // 2. MÉTODO DE AÇÕES ESPECIAIS (O que estava faltando!)
    private void conectarAcoesEspeciais() {
        // Ação do botão avançar
        
        view.getBtnAvancar().addActionListener(e -> {
           com.stack.engine.MotorJogo novoMotor = new com.stack.engine.MotorJogo(1, 2);
           view.iniciarNovaPartida(novoMotor);
        });

        // Ação do botão de engrenagem (config)
        view.getBtnConfig().addActionListener(e -> {
            int resposta = JOptionPane.showConfirmDialog(view, "Deseja sair do Stack Manager?");
            if (resposta == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }

}
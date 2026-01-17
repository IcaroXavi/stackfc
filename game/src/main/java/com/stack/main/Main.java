package com.stack.main;

import com.stack.controller.DashboardController;
import com.stack.model.Financas;
import com.stack.view.TelaDashboard;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // 1. Configuração de aparência (Look and Feel)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // 2. Inicia a Interface na Thread correta do Swing
        SwingUtilities.invokeLater(() -> {
            // Instancia o Model (Os dados do jogo)
            Financas financas = new Financas(); 
            
            // Instancia a View (A janela)
            TelaDashboard dash = new TelaDashboard("MEU TIME");
            
            // Instancia o Controller e "entrega" a View e o Model para ele
            // O Controller vai configurar os cliques dos botões automaticamente
            new DashboardController(dash, financas);
            
            dash.setVisible(true);
        });
    }
}
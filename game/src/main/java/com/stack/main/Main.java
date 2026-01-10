package com.stack.main;

import com.stack.view.Dashboard;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Melhora a aparência dos botões e janelas para o estilo do Windows/Mac/Linux
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Lança o Dashboard
        SwingUtilities.invokeLater(() -> {
Dashboard dash = new Dashboard("Stack");
dash.setVisible(true);
        });
    }
}
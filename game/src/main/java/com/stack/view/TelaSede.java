package com.stack.view;

import java.awt.*;
import javax.swing.*;

public class TelaSede extends JPanel {

    public TelaSede() {
        setLayout(new BorderLayout());
        setBackground(new Color(10, 15, 30));
        setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // --- CABEÇALHO PADRÃO ---
        JPanel painelSuperior = new JPanel();
        painelSuperior.setLayout(new BoxLayout(painelSuperior, BoxLayout.Y_AXIS));
        painelSuperior.setOpaque(false);
        painelSuperior.add(Box.createRigidArea(new Dimension(0, 8)));
        
        JLabel lblTitulo = new JLabel("SEDE", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(new Color(150, 160, 180));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(40, 50, 80)),
            BorderFactory.createEmptyBorder(5, 0, 5, 0)
        ));
        lblTitulo.setMaximumSize(new Dimension(500, 30));
        
        painelSuperior.add(lblTitulo);
        add(painelSuperior, BorderLayout.NORTH);

        // --- CONTEÚDO (Campo de Futebol em breve) ---
        JPanel conteudo = new JPanel(new GridBagLayout());
        conteudo.setOpaque(false);
        JLabel aviso = new JLabel("Configuração tática disponível em breve...");
        aviso.setForeground(new Color(80, 90, 110));
        aviso.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        conteudo.add(aviso);
        
        add(conteudo, BorderLayout.CENTER);
    }
}
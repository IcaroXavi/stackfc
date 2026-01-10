package com.stack.view;

import java.awt.*;
import javax.swing.*;

public class Sede extends JPanel {

    public Sede() {
        // Configuração básica
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(10, 15, 30));
        setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // Título
        add(Box.createRigidArea(new Dimension(0, 8)));
        JLabel lblTitulo = new JLabel("SEDE", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(new Color(150, 160, 180));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(40, 50, 80)),
            BorderFactory.createEmptyBorder(5, 0, 5, 0)
        ));
        lblTitulo.setMaximumSize(new Dimension(500, 30));
        add(lblTitulo);

        // Espaço flexível para centralizar o texto no meio da tela
        add(Box.createVerticalGlue());

        // Mensagem de Versão
        JLabel lblAviso = new JLabel("Funcionalidade estará disponível na próxima versão");
        lblAviso.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblAviso.setForeground(new Color(100, 110, 130)); // Cor cinza suave
        lblAviso.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lblAviso);

        // Outro espaço flexível para empurrar o texto para o centro real
        add(Box.createVerticalGlue());
    }
}
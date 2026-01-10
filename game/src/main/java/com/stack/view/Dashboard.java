package com.stack.view;

import java.awt.*;
import javax.swing.*;

public class Dashboard extends JFrame {

    public Dashboard() {
        setTitle("Stack Football Manager - Dashboard");
        setSize(800, 600); // Tela maior para caber as infos do time
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Cabeçalho com nome do Jogo e status rápido
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 30, 30));
        header.setPreferredSize(new Dimension(800, 60));

        JLabel titulo = new JLabel("  STACK FOOTBALL MANAGER");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        
        header.add(titulo, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Painel Lateral de Menu
        JPanel menuLateral = new JPanel(new GridLayout(6, 1, 5, 5));
        menuLateral.setPreferredSize(new Dimension(200, 600));
        menuLateral.setBackground(new Color(50, 50, 50));

        menuLateral.add(new JButton("Escalação"));
        menuLateral.add(new JButton("Mercado"));
        menuLateral.add(new JButton("Tabela"));
        menuLateral.add(new JButton("Finanças"));
        menuLateral.add(new JButton("Próximo Jogo"));

        add(menuLateral, BorderLayout.WEST);

        // Área Central (Onde a mágica acontece)
        JPanel areaPrincipal = new JPanel();
        areaPrincipal.setBackground(Color.LIGHT_GRAY);
        areaPrincipal.add(new JLabel("Bem-vindo ao Dashboard, Treinador!"));
        
        add(areaPrincipal, BorderLayout.CENTER);
    }
}
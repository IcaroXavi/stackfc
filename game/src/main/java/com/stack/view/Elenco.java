package com.stack.view;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.*;

// A classe PRECISA estender JPanel para o Dashboard aceitá-la
public class Elenco extends JPanel {

    public Elenco() {
        // Configuração básica do painel
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(10, 15, 30));
        setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // Título
        add(Box.createRigidArea(new Dimension(0, 8)));
        JLabel lblTitulo = new JLabel("ELENCO", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(new Color(150, 160, 180));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(40, 50, 80)),
            BorderFactory.createEmptyBorder(5, 0, 5, 0)
        ));
        lblTitulo.setMaximumSize(new Dimension(500, 30));
        add(lblTitulo);
        add(Box.createRigidArea(new Dimension(0, 15)));

        // Painel para a lista de jogadores
        JPanel listaContainer = new JPanel();
        listaContainer.setLayout(new BoxLayout(listaContainer, BoxLayout.Y_AXIS));
        listaContainer.setBackground(new Color(10, 15, 30));

        // Carregar do CSV
        carregarJogadores(listaContainer);

        // Adicionar Scroll
        JScrollPane scroll = new JScrollPane(listaContainer);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(10, 15, 30));
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        add(scroll);
    }

    private void carregarJogadores(JPanel container) {
        String caminho = "C:\\Projetos\\Stack\\game\\src\\main\\java\\com\\stack\\repository\\jogadores.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] d = linha.split(",");
                if (d.length >= 3) {
                    container.add(criarLinhaJogador(d[0].trim(), d[1].trim(), d[2].trim()));
                    container.add(Box.createRigidArea(new Dimension(0, 8)));
                }
            }
        } catch (Exception e) {
            container.add(new JLabel("Erro ao ler jogadores.csv"));
        }
    }

    private JPanel criarLinhaJogador(String nome, String pos, String ovr) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(20, 30, 50));
        p.setMaximumSize(new Dimension(400, 60));
        p.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel n = new JLabel(nome.toUpperCase());
        n.setForeground(Color.WHITE);
        n.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JLabel i = new JLabel("<html><font color='#00B4FF'>" + pos + "</font> <font color='#96A0B4'>OVR:</font> " + ovr + "</html>");
        i.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        p.add(n, BorderLayout.WEST);
        p.add(i, BorderLayout.EAST);
        return p;
    }
}
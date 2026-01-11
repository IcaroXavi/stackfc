package com.stack.view;

import java.awt.*;
import javax.swing.*;

public class Dashboard extends JFrame {

    private JPanel painelPrincipal; 
    private CardLayout navegador;   
    private JLabel lblData;         
    private Partida telaPartida;
    
    // NOVOS COMPONENTES PARA A TRANSIÇÃO
    private JPanel painelMestre; 
    private CardLayout layoutMestre;
    private JPanel containerApp;

    public Dashboard(String nomeTime) {
        setTitle("Stack Football Manager");
        setIconImage(new ImageIcon("C:\\Projetos\\Stack\\game\\src\\main\\resources\\icons\\icon.png").getImage());
        setSize(448, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // 1. INICIALIZAÇÃO DO LAYOUT MESTRE (O que permite a tela cheia)
        layoutMestre = new CardLayout();
        painelMestre = new JPanel(layoutMestre);
        setLayout(new BorderLayout());
        add(painelMestre);

        // 2. CONTAINER DO APP (Tudo o que você já tinha)
        containerApp = new JPanel(new BorderLayout());

        // Navegação de abas (Seu código original)
        navegador = new CardLayout();
        painelPrincipal = new JPanel(navegador);
        painelPrincipal.setBackground(new Color(10, 15, 30));
        
        // Criamos a partida passando 'this' para podermos voltar depois
        telaPartida = new Partida(this); 
        
        painelPrincipal.add(criarTelaHome(), "HOME");      
        painelPrincipal.add(new Elenco(), "ELENCO"); 
        painelPrincipal.add(new Taticas(), "TATICAS"); 
        painelPrincipal.add(new Mercado(), "MERCADO"); 
        painelPrincipal.add(new Sede(), "SEDE"); 
        // Removido o telaPartida daqui, pois agora ela terá seu próprio card no mestre
        
        containerApp.add(painelPrincipal, BorderLayout.CENTER);

        // Topo (Seu código original)
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(new Color(10, 15, 30));
        topo.setBorder(BorderFactory.createEmptyBorder(10, 12, 0, 0));
        JLabel lblClube = new JLabel(nomeTime.toUpperCase());
        lblClube.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblClube.setForeground(new Color(240, 240, 240));
        topo.add(lblClube, BorderLayout.WEST);
        JPanel painelDireito = new JPanel();
        painelDireito.setLayout(new BoxLayout(painelDireito, BoxLayout.X_AXIS));
        painelDireito.setBackground(new Color(10, 15, 30));
        lblData = new JLabel("11/01/2026");
        lblData.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblData.setForeground(new Color(150, 160, 180));
        JButton btnConfig = criarBotaoIcone("C:\\Projetos\\Stack\\game\\src\\main\\resources\\icons\\config.png", 18);
        JPopupMenu menuConfig = criarMenuConfiguracoes();
        btnConfig.addActionListener(e -> menuConfig.show(btnConfig, -120, btnConfig.getHeight()));
        painelDireito.add(lblData);
        painelDireito.add(Box.createRigidArea(new Dimension(10, 0)));
        painelDireito.add(btnConfig);
        topo.add(painelDireito, BorderLayout.EAST);
        containerApp.add(topo, BorderLayout.NORTH);

        // Menu tab bar (Seu código original)
        JPanel tabBar = new JPanel(new GridLayout(1, 5));
        tabBar.setPreferredSize(new Dimension(448, 90));
        tabBar.setBackground(new Color(15, 20, 35));
        tabBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(40, 50, 80)));
        tabBar.add(criarBotaoTab("Home", "home.png"));
        tabBar.add(criarBotaoTab("Elenco", "elenco.png"));
        tabBar.add(criarBotaoTab("Táticas", "tatica.png"));
        tabBar.add(criarBotaoTab("Mercado", "mercado.png"));
        tabBar.add(criarBotaoTab("Sede", "sede.png"));
        containerApp.add(tabBar, BorderLayout.SOUTH);

        // 3. ADICIONANDO AS DUAS CAMADAS AO MESTRE
        painelMestre.add(containerApp, "APP");
        painelMestre.add(telaPartida, "PARTIDA_REAL");
        
        layoutMestre.show(painelMestre, "APP");
    }

    // MÉTODOS DE TRANSIÇÃO
    public void mostrarTelaPartida() {
        layoutMestre.show(painelMestre, "PARTIDA_REAL");
    }
    
    public void voltarParaMenu() {
        layoutMestre.show(painelMestre, "APP");
    }

    private JPanel criarTelaHome() {
        JPanel home = new JPanel();
        home.setLayout(new BoxLayout(home, BoxLayout.Y_AXIS));
        home.setBackground(new Color(10, 15, 30));
        home.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        home.add(Box.createRigidArea(new Dimension(0, 8)));
        home.add(criarLabelTitulo("DASHBOARD"));
        home.add(Box.createRigidArea(new Dimension(0, 12)));
        home.add(criarCardConfronto("PRÓXIMO JOGO", "Flamengo", "em casa", "Amistoso"));
        home.add(Box.createRigidArea(new Dimension(0, 20)));
        home.add(criarCardConfianca("Mensagem da Diretoria", "Estamos aguardando o início da temporada para avaliar<br>sua condição de manutenção no cargo."));
        home.add(Box.createRigidArea(new Dimension(0, 20)));
        JPanel gridCards = new JPanel(new GridLayout(2, 2, 12, 12));
        gridCards.setOpaque(false);
        gridCards.setMaximumSize(new Dimension(400, 150));
        gridCards.add(criarCardPequeno("FINANÇAS", "R$ 2.5M", new Color(0, 180, 255)));
        gridCards.add(criarCardPequeno("CAPACIDADE ESTÁDIO", "15.000", new Color(255, 255, 255)));
        gridCards.add(criarCardPequeno("CATEGORIA DE BASE", "Nível 3/5", new Color(255, 255, 255)));
        gridCards.add(criarCardPequeno("RANK TREINADOR", "48º", new Color(255, 255, 255)));
        home.add(gridCards);
        home.add(Box.createRigidArea(new Dimension(0, 30)));
        home.add(criarBotaoAvancar("AVANÇAR"));
        return home;
    }

    // --- MÉTODOS AUXILIARES (Inalterados) ---
    private JLabel criarLabelTitulo(String texto) {
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(150, 160, 180));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(40, 50, 80)),
            BorderFactory.createEmptyBorder(5, 0, 5, 0)
        ));
        label.setMaximumSize(new Dimension(500, 30));
        return label;
    }

    private JPanel criarCardConfronto(String titulo, String time, String local, String liga) {
        JPanel card = new JPanel(new GridLayout(4, 1, 0, 5));
        card.setBackground(new Color(20, 30, 50));
        card.setMaximumSize(new Dimension(400, 130));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        card.add(new JLabel("<html><font color='#e2f200' style='font-weight:bold;'>" + titulo + "</font></html>"));
        card.add(new JLabel("<html><font color='#96A0B4'>Adversário:</font> <font color='white'>" + time + "</font></html>"));
        card.add(new JLabel("<html><font color='#96A0B4'>Local:</font> <font color='white'>" + local + "</font></html>"));
        card.add(new JLabel("<html><font color='#96A0B4'>Competição:</font> <font color='white'>" + liga + "</font></html>"));
        return card;
    }

    private JPanel criarCardConfianca(String titulo, String texto) {
        JPanel card = new JPanel(new BorderLayout()); 
        card.setBackground(new Color(20, 30, 50));
        card.setMaximumSize(new Dimension(400, 110)); 
        card.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        JLabel t = new JLabel(titulo); 
        t.setFont(new Font("Segoe UI", Font.BOLD, 11)); 
        t.setForeground(new Color(150, 160, 180));
        JLabel m = new JLabel("<html><body style='width: 300px;'>" + texto + "</body></html>"); 
        m.setFont(new Font("Segoe UI", Font.BOLD, 13)); 
        m.setForeground(Color.WHITE);
        card.add(t, BorderLayout.NORTH);
        card.add(m, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarCardPequeno(String titulo, String valor, Color corDestaque) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 0));
        card.setBackground(new Color(20, 30, 50));
        card.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        JLabel t = new JLabel(titulo); t.setFont(new Font("Segoe UI", Font.BOLD, 10)); t.setForeground(new Color(150, 160, 180));
        JLabel v = new JLabel(valor); v.setFont(new Font("Segoe UI", Font.BOLD, 13)); v.setForeground(corDestaque);
        card.add(t); card.add(v);
        return card;
    }

    private JButton criarBotaoAvancar(String label) {
        JButton btn = new JButton(label);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(400, 60));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(20, 30, 50));
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(0, 180, 255), 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            mostrarTelaPartida(); // AÇÃO DE TROCA PARA TELA CHEIA
        });
        return btn;
    }

    private JButton criarBotaoTab(String nome, String nomeArquivo) {
        JButton btn = new JButton(nome);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setForeground(new Color(150, 160, 180));
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBackground(new Color(15, 20, 35));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        String caminho = "C:\\Projetos\\Stack\\game\\src\\main\\resources\\icons\\" + nomeArquivo;
        btn.setIcon(new ImageIcon(new ImageIcon(caminho).getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        btn.addActionListener(e -> {
            if (nome.equals("Home")) navegador.show(painelPrincipal, "HOME");
            else if (nome.equals("Elenco")) navegador.show(painelPrincipal, "ELENCO");
            else if (nome.equals("Táticas")) navegador.show(painelPrincipal, "TATICAS");
            else if (nome.equals("Mercado")) navegador.show(painelPrincipal, "MERCADO");
            else if (nome.equals("Sede")) navegador.show(painelPrincipal, "SEDE");
        });
        return btn;
    }

    private JPopupMenu criarMenuConfiguracoes() {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(new Color(20, 30, 50));
        JMenuItem itemSair = new JMenuItem("Sair para o Menu");
        itemSair.setForeground(Color.RED);
        itemSair.addActionListener(e -> System.exit(0));
        menu.add(itemSair);
        return menu;
    }

    private JButton criarBotaoIcone(String caminho, int tamanho) {
        JButton btn = new JButton(new ImageIcon(new ImageIcon(caminho).getImage().getScaledInstance(tamanho, tamanho, Image.SCALE_SMOOTH)));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
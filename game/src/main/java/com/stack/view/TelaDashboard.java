package com.stack.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.stack.engine.MotorJogo;
import com.stack.util.ResourceLoader;

public class TelaDashboard extends JFrame {

    private JPanel painelPrincipal; 
    private CardLayout navegador;   
    private JLabel lblData;         
    private TelaElenco telaElenco;
    private TelaTaticas telaTaticas;
    private TelaMercado telaMercado;
    private TelaSede telaSede;
    private TelaPartida telaPartida;
    private JPanel painelMestre; 
    private CardLayout layoutMestre;
    private JPanel containerApp;

    private JButton btnAvancar;
    private JButton btnConfig;
    private JButton btnHome, btnElenco, btnMercado, btnSede;

    public TelaDashboard(String nomeTime) {
        setTitle("Stack Football Manager");
        
        // Proteção para o ícone da janela
        try {
            ImageIcon icon = ResourceLoader.getIcon("icon.png");
            if(icon != null) setIconImage(icon.getImage());
        } catch (Exception e) { System.out.println("Ícone da janela não encontrado."); }
        
        setSize(448, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        telaElenco = new TelaElenco();
        telaSede = new TelaSede();
        telaMercado = new TelaMercado();
        telaTaticas = new TelaTaticas(); 
        layoutMestre = new CardLayout();
        painelMestre = new JPanel(layoutMestre);
        setLayout(new BorderLayout());
        add(painelMestre);

        containerApp = new JPanel(new BorderLayout());
        navegador = new CardLayout();
        painelPrincipal = new JPanel(navegador);
        painelPrincipal.setBackground(new Color(10, 15, 30));
        
        // Substituindo telas externas por painéis vazios para não dar erro de compilação
        painelPrincipal.add(criarTelaHome(), "HOME");      
        painelPrincipal.add(telaElenco, "ELENCO");
        painelPrincipal.add(telaMercado, "MERCADO"); 
        painelPrincipal.add(telaSede, "SEDE"); 
        containerApp.add(painelPrincipal, BorderLayout.CENTER);
        containerApp.add(criarTopo(nomeTime), BorderLayout.NORTH);
        containerApp.add(criarTabBar(), BorderLayout.SOUTH);
        painelMestre.add(containerApp, "APP");
        painelMestre.add(telaTaticas, "TATICAS"); 
        layoutMestre.show(painelMestre, "APP");
    }

    private JPanel criarTopo(String nomeTime) {
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

        btnConfig = criarBotaoIcone("config.png", 18);
        
        painelDireito.add(lblData);
        painelDireito.add(Box.createRigidArea(new Dimension(10, 0)));
        painelDireito.add(btnConfig);
        topo.add(painelDireito, BorderLayout.EAST);
        
        return topo;
    }

    public TelaElenco getTelaElenco() {
    return telaElenco;
    }
    
    public TelaTaticas getTelaTaticas() {
    return telaTaticas;
    }

    private JPanel criarTabBar() {
        JPanel tabBar = new JPanel(new GridLayout(1, 4));
        tabBar.setPreferredSize(new Dimension(448, 90));
        tabBar.setBackground(new Color(15, 20, 35));
        tabBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(40, 50, 80)));

        btnHome = criarBotaoTab("Home", "home.png");
        btnElenco = criarBotaoTab("Elenco", "elenco.png");
        btnMercado = criarBotaoTab("Mercado", "mercado.png");
        btnSede = criarBotaoTab("Sede", "sede.png");

        tabBar.add(btnHome);
        tabBar.add(btnElenco);
        tabBar.add(btnMercado);
        tabBar.add(btnSede);
        
        return tabBar;
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
        home.add(criarCardConfianca("CONFIANÇA DA DIRETORIA", "Estamos aguardando o início da temporada."));
        
        home.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JPanel gridCards = new JPanel(new GridLayout(2, 2, 12, 12));
        gridCards.setOpaque(false);
        gridCards.setMaximumSize(new Dimension(400, 150));
        gridCards.add(criarCardPequeno("FINANÇAS", "R$ 2.5M", new Color(0, 180, 255)));
        gridCards.add(criarCardPequeno("ESTÁDIO", "15.000", Color.WHITE));
        gridCards.add(criarCardPequeno("BASE", "Nível 3/5", Color.WHITE));
        gridCards.add(criarCardPequeno("RANK", "48º", Color.WHITE));
        
        home.add(gridCards);
        home.add(Box.createRigidArea(new Dimension(0, 30)));
        
        btnAvancar = criarBotaoEstilizado("AVANÇAR");
        home.add(btnAvancar);
        
        return home;
    }

    public void mostrarTelaPartida() { layoutMestre.show(painelMestre, "PARTIDA_REAL"); }
    public void voltarParaMenu() { layoutMestre.show(painelMestre, "APP"); }
    public void mudarAba(String nomeCard) { navegador.show(painelPrincipal, nomeCard); }

    // GETTERS
    public JButton getBtnAvancar() { return btnAvancar; }
    public JButton getBtnConfig() { return btnConfig; }
    public JButton getBtnHome() { return btnHome; }
    public JButton getBtnElenco() { return btnElenco; }
    public JButton getBtnMercado() { return btnMercado; }
    public JButton getBtnSede() { return btnSede; }

    // --- COMPONENTES AUXILIARES COM PROTEÇÃO CONTRA NULL ---

    private JButton criarBotaoEstilizado(String label) {
        JButton btn = new JButton(label);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(400, 60));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(20, 30, 50));
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createLineBorder(new Color(0, 180, 255), 1));
        return btn;
    }

    private JButton criarBotaoTab(String nome, String iconName) {
        JButton btn = new JButton(nome);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setForeground(new Color(150, 160, 180));
        btn.setContentAreaFilled(false);
        
        ImageIcon original = ResourceLoader.getIcon(iconName);
        if(original != null && original.getImage() != null) {
            btn.setIcon(new ImageIcon(original.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH)));
        }
        return btn;
    }

    private JButton criarBotaoIcone(String iconName, int tamanho) {
        JButton btn = new JButton();
        ImageIcon original = ResourceLoader.getIcon(iconName);
        if(original != null && original.getImage() != null) {
            btn.setIcon(new ImageIcon(original.getImage().getScaledInstance(tamanho, tamanho, Image.SCALE_SMOOTH)));
        } else {
            btn.setText("?"); // Se não achar o ícone, coloca uma interrogação
            btn.setForeground(Color.WHITE);
        }
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        return btn;
    }

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
        card.setMaximumSize(new Dimension(400, 150));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        card.add(new JLabel("<html><font color='#d0f309' size=5><b>" + titulo + "</b></font></html>"));
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
        JLabel m = new JLabel("<html><body style='width: 250px;'>" + texto + "</body></html>"); 
        m.setForeground(Color.WHITE);
        card.add(t, BorderLayout.NORTH);
        card.add(m, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarCardPequeno(String titulo, String valor, Color corDestaque) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 0));
        card.setBackground(new Color(20, 30, 50));
        card.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        JLabel t = new JLabel(titulo); 
        t.setFont(new Font("Segoe UI", Font.BOLD, 10)); 
        t.setForeground(new Color(150, 160, 180));
        JLabel v = new JLabel(valor); 
        v.setForeground(corDestaque);
        card.add(t); card.add(v);
        return card;
    }

    public void iniciarNovaPartida(MotorJogo motorPronto) {
    // 1. Se já existir uma tela de partida antiga, removemos para limpar a memória
    if (this.telaPartida != null) {
        painelMestre.remove(this.telaPartida);
    }

    // 2. Criamos a tela SÓ AGORA, passando o motor que o Controller acabou de criar
    this.telaPartida = new TelaPartida(this, motorPronto);

    // 3. Adicionamos ao painel mestre e mostramos
    painelMestre.add(this.telaPartida, "PARTIDA_REAL");
    layoutMestre.show(painelMestre, "PARTIDA_REAL");
 
    // Força a interface a se atualizar visualmente
    revalidate();
    repaint();
    }

    public void mostrarTelaTaticas() {
        layoutMestre.show(painelMestre, "TATICAS");
    }
}
package com.stack.view;

import java.awt.*;
import javax.swing.*;

public class Dashboard extends JFrame {

    public Dashboard(String nomeTime) {
        // Configurações da Janela (Simulando Celular)
        setTitle("Stack Football Manager");
        setIconImage(new ImageIcon("C:\\Projetos\\Stack\\game\\src\\main\\resources\\icons\\icon.png").getImage());
        setSize(448, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // para encerrar o programa
        setLocationRelativeTo(null); // abrir janela centralizada
        setResizable(false); // bloquear redimensionamento manual da janela
        setLayout(new BorderLayout());

        // Topo
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(new Color(10, 15, 30));
        topo.setBorder(BorderFactory.createEmptyBorder(15, 12, 10, 10)); 
        JLabel lblClube = new JLabel(nomeTime.toUpperCase());
        lblClube.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblClube.setForeground(new Color(240, 240, 240));
        topo.add(lblClube, BorderLayout.WEST);
        JPanel painelDireito = new JPanel();
        painelDireito.setLayout(new BoxLayout(painelDireito, BoxLayout.X_AXIS)); 
        painelDireito.setBackground(new Color(10, 15, 30));

        // Data
        JLabel lblData = new JLabel("11/01/2026");
        lblData.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblData.setForeground(new Color(150, 160, 180));

        // Menu Configurações
        JButton btnConfig = criarBotaoIcone("C:\\Projetos\\Stack\\game\\src\\main\\resources\\icons\\config.png", 18);
        JPopupMenu menuConfig = criarMenuConfiguracoes();
        btnConfig.addActionListener(e -> {
            menuConfig.show(btnConfig, -120, btnConfig.getHeight()); 
        });
        painelDireito.add(lblData);
        painelDireito.add(Box.createRigidArea(new Dimension(10, 0))); 
        painelDireito.add(btnConfig);

        topo.add(painelDireito, BorderLayout.EAST);
        add(topo, BorderLayout.NORTH);

        // Cards
        JPanel corpo = new JPanel();
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.setBackground(new Color(10, 15, 30));
        corpo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // Titulo
        corpo.add(Box.createRigidArea(new Dimension(0, 0))); 
        JLabel lblTituloPagina = new JLabel("DASHBOARD", SwingConstants.CENTER);
        lblTituloPagina.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTituloPagina.setForeground(new Color(150, 160, 180));
        lblTituloPagina.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTituloPagina.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(40, 50, 80)), // Linhas
            BorderFactory.createEmptyBorder(5, 0, 5, 0) // Espaço interno entre texto e linha
        ));
        lblTituloPagina.setMaximumSize(new Dimension(500, 30)); 
        corpo.add(lblTituloPagina);
        corpo.add(Box.createRigidArea(new Dimension(0, 12)));

        // Card de Confronto
        corpo.add(criarCardConfronto("PRÓXIMO JOGO", "Flamengo", "em casa", "Amistoso"));
        corpo.add(Box.createRigidArea(new Dimension(0, 20)));

        // Card Confiança da Diretoria
        corpo.add(criarCardConfianca("Mensagem da Diretoria", "Estamos aguardando o início da temporada!"));
        corpo.add(Box.createRigidArea(new Dimension(0, 20)));
        add(corpo, BorderLayout.CENTER);

        // Cards menores
        JPanel gridCards = new JPanel(new GridLayout(2, 2, 12, 12)); // 2 linhas, 2 colunas, 12px de espaço
        gridCards.setOpaque(false);
        gridCards.setMaximumSize(new Dimension(400, 160)); 
        gridCards.add(criarCardPequeno("FINANÇAS", "R$ 2.5M", new Color(0, 180, 255)));
        gridCards.add(criarCardPequeno("ESTÁDIO", "90% Lotado", new Color(0, 242, 16)));
        gridCards.add(criarCardPequeno("ACADEMIA", "Nível 3/5", new Color(255, 180, 0)));
        gridCards.add(criarCardPequeno("DM / MÉDICO", "2 Lesionados", new Color(255, 80, 80))); 
        corpo.add(gridCards);
        corpo.add(Box.createRigidArea(new Dimension(0, 40)));

        // Botão de avançar
        corpo.add(criarBotaoAvancar("AVANÇAR"));
        corpo.add(Box.createRigidArea(new Dimension(0, 20)));

        // Menu Tab Bar
        JPanel tabBar = new JPanel(new GridLayout(1, 5));
        tabBar.setPreferredSize(new Dimension(448, 90));
        tabBar.setBackground(new Color(15, 20, 35));
        tabBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(40, 50, 80)));
        tabBar.add(criarBotaoTab("Home", "home.png"));
        tabBar.add(criarBotaoTab("Elenco", "elenco.png"));
        tabBar.add(criarBotaoTab("Tática", "tatica.png"));
        tabBar.add(criarBotaoTab("Mercado", "mercado.png"));
        tabBar.add(criarBotaoTab("Sede", "sede.png"));
        add(tabBar, BorderLayout.SOUTH);
    }

    // Método para criar o Card de Jogo (Time 1 x Time 2)
    private JPanel criarCardConfronto(String titulo, String time, String local, String liga) {
        JPanel card = new JPanel(new GridLayout(4,1,0,5));
        card.setBackground(new Color(20, 30, 50));
        card.setMaximumSize(new Dimension(400, 130));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTit.setForeground(new Color(0, 242, 16));

        JLabel lblTime = new JLabel("<html><font color='#96A0B4' size='3' style='font-weight:normal;'>Adversário:</font> " + time + "</html>");
        lblTime.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTime.setForeground(new Color(240, 240, 240));

        JLabel lblLocal = new JLabel("<html><font color='#96A0B4' size='3' style='font-weight:normal;'>Local:</font> " + local + "</html>");
        lblLocal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblLocal.setForeground(new Color(240, 240, 240));

        JLabel lblLiga = new JLabel("<html><font color='#96A0B4' size='3' style='font-weight:normal;'>Competição:</font> " + liga + "</html>");
        lblLiga.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblLiga.setForeground(new Color(240, 240, 240));

        card.add(lblTit);
        card.add(lblTime);
        card.add(lblLocal);
        card.add(lblLiga);

        return card;
    }

    // Método para criar o Card Confiança
    private JPanel criarCardConfianca(String titulo, String texto) {
        JPanel card = new JPanel(new GridLayout(3, 1, 0, 5));
        card.setBackground(new Color(20, 30, 50));
        card.setMaximumSize(new Dimension(400, 110));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTit.setForeground(new Color(150, 160, 180));
        JLabel lblMsg = new JLabel(texto);
        lblMsg.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblMsg.setForeground(new Color(240, 240, 240));
        card.add(lblTit);
        card.add(lblMsg);

        return card;
    }

    // Método para criar Cards menores
    private JPanel criarCardPequeno(String titulo, String valor, Color corDestaque) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 0));
        card.setBackground(new Color(20, 30, 50));
        card.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTit.setForeground(new Color(150, 160, 180));

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblValor.setForeground(corDestaque);

        card.add(lblTit);
        card.add(lblValor);

        return card;
    }

    // Botão avançar 
    private JButton criarBotaoAvancar(String label) {
            JButton btn = new JButton(label);
            
            // Estilo e Tamanho (Igual aos Cards)
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(400, 60));
            btn.setPreferredSize(new Dimension(400, 60));
            
            // Cores
            btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btn.setForeground(new Color(240, 240, 240));
            btn.setBackground(new Color(20, 30, 50));
            
            // Remoção de efeitos padrão e adição de borda de destaque
            btn.setContentAreaFilled(false);
            btn.setOpaque(true);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(new Color(0, 180, 255), 1));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            return btn;
        }


    // Método para criar os botões da Tab Bar com ícones do resources
    private JButton criarBotaoTab(String nome, String nomeArquivo) {
        JButton btn = new JButton(nome);
        
        // Configurações de Texto
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setForeground(new Color(150, 160, 180)); 
        btn.setBackground(new Color(15, 20, 35));        
        btn.setContentAreaFilled(false);      // Remove o fundo padrão "claro" do Swing
        btn.setOpaque(true);           // Garante que ele use a cor de fundo que definimos
        btn.setBorderPainted(false);          // Remove bordas
        btn.setFocusPainted(false);           // Remove aquele quadradinho de foco ao clicar
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        String caminho = "C:\\Projetos\\Stack\\game\\src\\main\\resources\\icons\\" + nomeArquivo;
            ImageIcon icon = new ImageIcon(caminho);
            Image img = icon.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(img));

        return btn;
    }

        private JPopupMenu criarMenuConfiguracoes() {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(new Color(20, 30, 50)); // Mesmo azul dos cards
        menu.setBorder(BorderFactory.createLineBorder(new Color(40, 50, 80)));

        // Item Salvar Jogo
        JMenuItem itemSalvar = new JMenuItem("Salvar Jogo");
        estilizarItemMenu(itemSalvar);
        itemSalvar.addActionListener(e -> {
            System.out.println("Jogo Salvo!");
            // Aqui você chamará sua lógica de save futuramente
        });

        // Item Sair
        JMenuItem itemSair = new JMenuItem("Sair para o Menu");
        estilizarItemMenu(itemSair);
        itemSair.setForeground(new Color(255, 80, 80)); // Vermelho para alerta
        itemSair.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente sair?", "Sair", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) System.exit(0);
        });

        menu.add(itemSalvar);
        menu.addSeparator(); // Linha divisória
        menu.add(itemSair);

        return menu;
    }

    // Método auxiliar para não repetir código de estilo nos itens do menu
    private void estilizarItemMenu(JMenuItem item) {
        item.setBackground(new Color(20, 30, 50));
        item.setForeground(new Color(240, 240, 240));
        item.setFont(new Font("Segoe UI", Font.BOLD, 13));
        item.setPreferredSize(new Dimension(150, 40));
        item.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
    }

    private JButton criarBotaoIcone(String caminho, int tamanho) {
    JButton btn = new JButton(new ImageIcon(new ImageIcon(caminho).getImage().getScaledInstance(tamanho, tamanho, Image.SCALE_SMOOTH)));
    btn.setContentAreaFilled(false);
    btn.setBorderPainted(false);
    btn.setFocusPainted(false);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setMargin(new Insets(0, 0, 0, 0));
    
    return btn;
}
}
package com.stack.view;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class Partida extends JPanel {
    private Dashboard dashboard;
    private JLabel lblCronometro, lblStatusTempo, lblLance;
    private JTable tabelaTitulares, tabelaReservas;
    private DefaultTableModel modeloTitulares, modeloReservas;
    private JButton btnAcao;
    private Timer timer;
    private int segundosCorridos = 0;
    private boolean isSegundoTempo = false;
    private final String CAMINHO_CSV = "C:\\Projetos\\Stack\\game\\src\\main\\java\\com\\stack\\repository\\jogadores.csv";

    public Partida(Dashboard dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout());
        setBackground(new Color(10, 15, 30));

        // --- CABEÇALHO (ORIGINAL) ---
        JPanel headerSuperior = new JPanel();
        headerSuperior.setLayout(new BoxLayout(headerSuperior, BoxLayout.Y_AXIS));
        headerSuperior.setBackground(new Color(15, 25, 45));
        headerSuperior.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JPanel linhaPlacar = new JPanel(new GridLayout(1, 3));
        linhaPlacar.setOpaque(false);
        
        JLabel timeA = new JLabel("MEU TIME", SwingConstants.CENTER);
        timeA.setForeground(Color.WHITE);
        timeA.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JLabel placar = new JLabel("0 - 0", SwingConstants.CENTER);
        placar.setForeground(new Color(226, 242, 0));
        placar.setFont(new Font("Segoe UI", Font.BOLD, 32));

        JLabel timeB = new JLabel("FLAMENGO", SwingConstants.CENTER);
        timeB.setForeground(new Color(255, 80, 80));
        timeB.setFont(new Font("Segoe UI", Font.BOLD, 16));
        timeB.setCursor(new Cursor(Cursor.HAND_CURSOR));
        timeB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { pausarPartida(); abrirModalFlamengo(); }
        });

        linhaPlacar.add(timeA); linhaPlacar.add(placar); linhaPlacar.add(timeB);
        headerSuperior.add(linhaPlacar);

        lblCronometro = new JLabel("0", SwingConstants.CENTER);
        lblCronometro.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblCronometro.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblCronometro.setForeground(Color.WHITE);

        lblStatusTempo = new JLabel("PRÉ-JOGO");
        lblStatusTempo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblStatusTempo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatusTempo.setForeground(new Color(150, 160, 180));

        headerSuperior.add(lblCronometro);
        headerSuperior.add(lblStatusTempo);
        add(headerSuperior, BorderLayout.NORTH);

        // --- ÁREA CENTRAL ---
        JPanel containerCentro = new JPanel();
        containerCentro.setLayout(new BoxLayout(containerCentro, BoxLayout.Y_AXIS));
        containerCentro.setOpaque(false);
        containerCentro.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 15));

        // Espaço inicial para descer as tabelas
        containerCentro.add(Box.createVerticalStrut(10));

        // Card de Lances
        JPanel cardLance = new JPanel(new BorderLayout());
        cardLance.setPreferredSize(new Dimension(500, 50));
        cardLance.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        cardLance.setBackground(Color.BLACK);
        lblLance = new JLabel("<html><center>As equipes estão entrando em campo...</center></html>", SwingConstants.CENTER);
        lblLance.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLance.setForeground(new Color(200, 200, 200));
        cardLance.add(lblLance, BorderLayout.CENTER);
        containerCentro.add(cardLance);

        // TABELA TITULARES
        containerCentro.add(criarRotuloSecao("TITULARES (STATUS 1)"));
        modeloTitulares = criarModeloNaoEditavel();
        tabelaTitulares = new JTable(modeloTitulares);
        containerCentro.add(configurarEScrollar(tabelaTitulares, 270));

        // TABELA RESERVAS
        containerCentro.add(criarRotuloSecao("RESERVAS (STATUS 2)"));
        modeloReservas = criarModeloNaoEditavel();
        tabelaReservas = new JTable(modeloReservas);
        containerCentro.add(configurarEScrollar(tabelaReservas, 160));

        // Glue para empurrar tudo para cima e manter o tamanho fixo das tabelas
        containerCentro.add(Box.createVerticalGlue());

        add(containerCentro, BorderLayout.CENTER);

        // --- BOTÃO INFERIOR ---
        btnAcao = new JButton("DAR O PONTAPÉ INICIAL");
        btnAcao.setPreferredSize(new Dimension(0, 60));
        btnAcao.setBackground(new Color(0, 120, 80));
        btnAcao.setForeground(Color.WHITE);
        btnAcao.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnAcao.setOpaque(true);
        btnAcao.setContentAreaFilled(true);
        btnAcao.setBorderPainted(false);
        btnAcao.setFocusPainted(false);
        btnAcao.addActionListener(e -> gerenciarBotao());
        add(btnAcao, BorderLayout.SOUTH);

        carregarDados("1", modeloTitulares, modeloReservas);
    }

    private DefaultTableModel criarModeloNaoEditavel() {
        return new DefaultTableModel(new String[]{"POS", "NOME", "ATQ", "DEF", "OVR"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    private JScrollPane configurarEScrollar(JTable tabela, int altura) {
        tabela.setBackground(new Color(15, 20, 35));
        tabela.setForeground(Color.WHITE);
        tabela.setRowHeight(22);
        tabela.setShowGrid(false);
        tabela.setFillsViewportHeight(true);
        tabela.setSelectionBackground(new Color(40, 50, 70));

        // Cabeçalho
        JTableHeader header = tabela.getTableHeader();
        header.setBackground(new Color(25, 30, 50));
        header.setForeground(new Color(150, 160, 180));
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        // Colunas
        tabela.getColumnModel().getColumn(0).setPreferredWidth(45);
        tabela.getColumnModel().getColumn(0).setMaxWidth(45);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(200);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setPreferredSize(new Dimension(400, altura));
        scroll.setMinimumSize(new Dimension(400, altura));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, altura)); // TRAVA A ALTURA AQUI
        scroll.setBorder(BorderFactory.createLineBorder(new Color(40, 50, 70)));
        scroll.getViewport().setBackground(new Color(15, 20, 35));
        
        return scroll;
    }

    private JLabel criarRotuloSecao(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(new Color(100, 110, 130));
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setBorder(BorderFactory.createEmptyBorder(15, 5, 5, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void carregarDados(String idClube, DefaultTableModel modTit, DefaultTableModel modRes) {
        modTit.setRowCount(0); modRes.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader(CAMINHO_CSV))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty() || linha.toLowerCase().startsWith("id")) continue;
                String[] d = linha.split(",");
                if (d.length >= 9 && d[7].trim().equals(idClube)) {
                    Object[] row = {abreviar(d[2]), d[1].toUpperCase(), d[5], d[4], d[6]};
                    if (d[8].trim().equals("1")) modTit.addRow(row);
                    else if (d[8].trim().equals("2")) modRes.addRow(row);
                }
            }
        } catch (Exception e) {}
    }

    private void abrirModalFlamengo() {
        JDialog modal = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Escalação: Flamengo", true);
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(10, 15, 30)); p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        DefaultTableModel mTitB = criarModeloNaoEditavel();
        DefaultTableModel mResB = criarModeloNaoEditavel();
        carregarDados("2", mTitB, mResB);

        JTable tT = new JTable(mTitB); 
        JTable tR = new JTable(mResB); 
        
        p.add(criarRotuloSecao("TITULARES (FLAMENGO)")); p.add(configurarEScrollar(tT, 250));
        p.add(criarRotuloSecao("RESERVAS (FLAMENGO)")); p.add(configurarEScrollar(tR, 140));
        
        modal.add(p); modal.pack(); modal.setLocationRelativeTo(this); modal.setVisible(true);
    }

    private String abreviar(String p) {
        p = p.toLowerCase();
        if(p.contains("goleiro")) return "GL"; if(p.contains("zagueiro")) return "ZG";
        if(p.contains("lateral")) return p.contains("dir") ? "LD" : "LE";
        if(p.contains("meio")) return "MC"; if(p.contains("atacante")) return "AT";
        return p.toUpperCase().substring(0, Math.min(2, p.length()));
    }

    private void gerenciarBotao() {
        if(btnAcao.getText().contains("INICIAL") || btnAcao.getText().contains("RETOMAR")) {
            lblStatusTempo.setText(isSegundoTempo ? "2º TEMPO" : "1º TEMPO");
            btnAcao.setText("PAUSAR PARTIDA");
            btnAcao.setBackground(new Color(120, 50, 20));
            if(timer == null) timer = new Timer(1000, e -> {
                segundosCorridos++;
                lblCronometro.setText(String.valueOf(segundosCorridos));
                if(segundosCorridos == 45 && !isSegundoTempo) finalizarTempo();
                else if(segundosCorridos == 90) finalizarPartida();
            });
            timer.start();
        } else { pausarPartida(); }
    }

    private void pausarPartida() {
        if(timer != null) timer.stop();
        btnAcao.setText("RETOMAR PARTIDA");
        btnAcao.setBackground(new Color(0, 120, 80));
    }

    private void finalizarTempo() {
        timer.stop(); isSegundoTempo = true;
        lblStatusTempo.setText("INTERVALO");
        lblLance.setText("<html><center>Fim do primeiro tempo!</center></html>");
        btnAcao.setText("RETOMAR (2º TEMPO)");
    }

    private void finalizarPartida() {
        timer.stop();
        lblStatusTempo.setText("FIM DE JOGO");
        btnAcao.setText("VOLTAR PARA O MENU");
    }
}
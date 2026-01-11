package com.stack.view;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Partida extends JPanel {
    private Dashboard dashboard;
    private JLabel lblCronometro, lblStatusTempo, lblLance, lblPublico;
    private JTable tabelaMeuTime, tabelaAdversario;
    private DefaultTableModel modeloMeuTime, modeloAdversario;
    private JButton btnAcao, btnTaticas, btnVerAdversario;
    private Timer timer;
    private int segundosCorridos = 0;
    private boolean isSegundoTempo = false;
    private boolean jogoIniciado = false;
    private boolean partidaFinalizada = false;

    private final Color COR_FUNDO = new Color(10, 15, 30);
    private final Color COR_ZEBRA = new Color(20, 25, 45);
    private final Color COR_BORDA_TABELA = new Color(30, 40, 60);
    private final String CAMINHO_CSV = "C:\\Projetos\\Stack\\game\\src\\main\\java\\com\\stack\\repository\\jogadores.csv";

    public Partida(Dashboard dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout());
        setBackground(COR_FUNDO);

        // --- CABEÇALHO ---
        JPanel painelHeader = new JPanel();
        painelHeader.setLayout(new BoxLayout(painelHeader, BoxLayout.Y_AXIS));
        painelHeader.setBackground(new Color(15, 25, 45));
        painelHeader.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));

        JPanel barraTopo = new JPanel(new BorderLayout());
        barraTopo.setOpaque(false);
        lblPublico = new JLabel("Público: 13.537");
        lblPublico.setForeground(new Color(150, 160, 180));
        lblStatusTempo = new JLabel("PRÉ-JOGO", SwingConstants.CENTER);
        lblStatusTempo.setForeground(Color.WHITE);
        lblCronometro = new JLabel("00:00", SwingConstants.RIGHT);
        lblCronometro.setForeground(Color.WHITE);
        lblCronometro.setPreferredSize(new Dimension(100, 20));
        lblPublico.setPreferredSize(new Dimension(100, 20));

        barraTopo.add(lblPublico, BorderLayout.WEST);
        barraTopo.add(lblStatusTempo, BorderLayout.CENTER);
        barraTopo.add(lblCronometro, BorderLayout.EAST);

        JPanel containerLance = new JPanel(new BorderLayout());
        containerLance.setBackground(Color.BLACK);
        containerLance.setPreferredSize(new Dimension(0, 35));
        containerLance.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        containerLance.setBorder(BorderFactory.createLineBorder(new Color(40, 50, 70)));
        lblLance = new JLabel("As equipes estão entrando em campo...", SwingConstants.CENTER);
        lblLance.setForeground(new Color(200, 200, 200));
        containerLance.add(lblLance);

        // PLACAR CENTRALIZADO (GridLayout 1x3 garante o centro absoluto)
        JPanel painelPlacar = new JPanel(new GridLayout(1, 3));
        painelPlacar.setOpaque(false);
        painelPlacar.setPreferredSize(new Dimension(0, 50));
        painelPlacar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        painelPlacar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JLabel timeA = new JLabel("MEU TIME", SwingConstants.LEFT);
        timeA.setForeground(Color.WHITE);
        timeA.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        JLabel placarContador = new JLabel("0 - 0", SwingConstants.CENTER);
        placarContador.setForeground(new Color(226, 242, 0));
        placarContador.setFont(new Font("Segoe UI", Font.BOLD, 32));

        JLabel timeB = new JLabel("FLAMENGO", SwingConstants.RIGHT);
        timeB.setForeground(new Color(255, 80, 80));
        timeB.setFont(new Font("Segoe UI", Font.BOLD, 18));

        painelPlacar.add(timeA);
        painelPlacar.add(placarContador);
        painelPlacar.add(timeB);

        painelHeader.add(barraTopo);
        painelHeader.add(Box.createVerticalStrut(8));
        painelHeader.add(containerLance);
        painelHeader.add(Box.createVerticalStrut(8));
        painelHeader.add(painelPlacar);
        add(painelHeader, BorderLayout.NORTH);

        // --- ÁREA CENTRAL (TABELAS LARGAS E COMPACTAS) ---
        JPanel painelCentral = new JPanel(new GridBagLayout());
        painelCentral.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.anchor = GridBagConstraints.NORTH;    
        gbc.weighty = 1.0; 

        modeloMeuTime = criarModeloConfronto();
        tabelaMeuTime = new JTable(modeloMeuTime);
        gbc.gridx = 0;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(10, 20, 0, 10);
        painelCentral.add(configurarTabelaFinal(tabelaMeuTime), gbc);

        modeloAdversario = criarModeloConfronto();
        tabelaAdversario = new JTable(modeloAdversario);
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(10, 10, 0, 20);
        painelCentral.add(configurarTabelaFinal(tabelaAdversario), gbc);

        add(painelCentral, BorderLayout.CENTER);

        // --- RODAPÉ ---
        JPanel footer = new JPanel(new GridLayout(1, 3, 10, 0));
        footer.setBackground(new Color(15, 25, 45));
        footer.setPreferredSize(new Dimension(0, 60));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btnTaticas = criarBotao("ALTERAR TÁTICA", new Color(35, 50, 80));
        btnVerAdversario = criarBotao("VER ADVERSÁRIO", new Color(50, 50, 55));
        btnAcao = criarBotao("INICIAR PARTIDA", new Color(0, 90, 55));
        btnAcao.addActionListener(e -> gerenciarFluxoJogo());

        footer.add(btnTaticas); footer.add(btnVerAdversario); footer.add(btnAcao);
        add(footer, BorderLayout.SOUTH);

        carregarDadosTitulares();
    }

    private JScrollPane configurarTabelaFinal(JTable tabela) {
        tabela.setBackground(COR_FUNDO);
        tabela.setRowHeight(24);
        tabela.setShowGrid(false);
        tabela.setTableHeader(null);
        tabela.setFocusable(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));

        ZebraRenderer renderer = new ZebraRenderer();
        for(int i=0; i<3; i++) tabela.getColumnModel().getColumn(i).setCellRenderer(renderer);

        tabela.getColumnModel().getColumn(0).setPreferredWidth(45);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(500); // Largura aumentada
        tabela.getColumnModel().getColumn(2).setPreferredWidth(40);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(COR_BORDA_TABELA));
        scroll.getViewport().setBackground(COR_FUNDO);
        
        // Altura para 16 linhas (titulares + reservas)
        int alturaTotal = (24 * 16) + 2;
        scroll.setPreferredSize(new Dimension(600, alturaTotal));
        scroll.setMinimumSize(new Dimension(400, alturaTotal));
        
        return scroll;
    }

    private class ZebraRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setBackground(row % 2 == 0 ? COR_ZEBRA : COR_FUNDO);
            c.setForeground(new Color(220, 220, 220));
            
            // ALINHAMENTO À ESQUERDA PARA NOMES
            c.setHorizontalAlignment(SwingConstants.LEFT);
            c.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            
            if (column == 0) { // Posição centralizada
                c.setHorizontalAlignment(SwingConstants.CENTER);
                c.setBorder(null);
                c.setFont(new Font("Segoe UI", Font.BOLD, 11));
            }
            return c;
        }
    }

    private DefaultTableModel criarModeloConfronto() {
        return new DefaultTableModel(new String[]{"P", "N", "A"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    private JButton criarBotao(String t, Color bg) {
        JButton b = new JButton(t); b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", 1, 12)); b.setFocusPainted(false); b.setBorderPainted(false);
        return b;
    }

    private void carregarDadosTitulares() {
        modeloMeuTime.setRowCount(0); modeloAdversario.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader(CAMINHO_CSV))) {
            String l; while ((l = br.readLine()) != null) {
                if (l.trim().isEmpty() || l.toLowerCase().startsWith("id")) continue;
                String[] d = l.split(",");
                if (d.length >= 9 && d[8].trim().equals("1")) {
                    Object[] r = {abreviar(d[2]), d[1].toUpperCase(), ""};
                    if (d[7].trim().equals("1")) modeloMeuTime.addRow(r);
                    else if (d[7].trim().equals("2")) modeloAdversario.addRow(r);
                }
            }
        } catch (Exception e) {}
    }

    private void gerenciarFluxoJogo() {
        if (partidaFinalizada) return;
        if (!jogoIniciado || btnAcao.getText().contains("RETOMAR") || btnAcao.getText().contains("INICIAR")) {
            jogoIniciado = true;
            lblStatusTempo.setText(isSegundoTempo ? "2º TEMPO" : "1º TEMPO");
            btnAcao.setText("PAUSAR PARTIDA"); btnAcao.setBackground(new Color(140, 40, 40));
            if (timer == null) timer = new Timer(1000, e -> atualizarTempo());
            timer.start();
        } else { pausarPartida(); }
    }

    private void pausarPartida() {
        if (timer != null) timer.stop();
        btnAcao.setText("RETOMAR PARTIDA"); btnAcao.setBackground(new Color(0, 90, 55));
    }

    private void atualizarTempo() {
        segundosCorridos++;
        int min = segundosCorridos / 60;
        int seg = segundosCorridos % 60;
        lblCronometro.setText(String.format("%02d:%02d", min, seg));

        if (segundosCorridos == 45 && !isSegundoTempo) {
            pausarPartida(); isSegundoTempo = true;
            btnAcao.setText("INICIAR 2º TEMPO"); lblStatusTempo.setText("INTERVALO");
        } else if (segundosCorridos >= 90) {
            timer.stop(); partidaFinalizada = true;
            lblStatusTempo.setText("ENCERRADO"); btnAcao.setText("SAIR");
            btnAcao.setBackground(new Color(50, 50, 60));
        }
    }

    private String abreviar(String p) {
        p = p.toLowerCase();
        if(p.contains("goleiro")) return "GL"; if(p.contains("zagueiro")) return "ZG";
        if(p.contains("lateral")) return p.contains("dir") ? "LD" : "LE";
        if(p.contains("meio")) return "MC"; if(p.contains("atacante")) return "AT";
        return p.substring(0, Math.min(2, p.length())).toUpperCase();
    }
}
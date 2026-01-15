package com.stack.view;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Partida extends JPanel {
    private Dashboard dashboard;
    private JLabel lblCronometro, lblStatusTempo, lblLance, lblPublico, lblPlacar;
    private JTable tabelaMeuTime, tabelaAdversario;
    private DefaultTableModel modeloMeuTime, modeloAdversario;
    private JButton btnAcao, btnTaticas, btnVerAdversario;
    private JTextArea logMotor; 
    private Timer timer;
    
    private int minutosSimulados = 0, contadorTicks = 0;
    private boolean isSegundoTempo = false, jogoIniciado = false, partidaFinalizada = false;
    private int golsMeuTime = 0, golsAdv = 0;

    private final Map<String, int[]> dadosBrutos = new HashMap<>();

    private final Color COR_FUNDO = new Color(10, 15, 30);
    private final Color COR_ZEBRA = new Color(20, 25, 45);
    private final Color COR_BORDA_TABELA = new Color(30, 40, 60);
    private final String CAMINHO_CSV = "C:\\Projetos\\Stack\\game\\src\\main\\java\\com\\stack\\repository\\jogadores.csv";
    private final Map<String, Image> cacheImagens = new HashMap<>();
    private final String PATH_ICONS = "C:\\\\Projetos\\\\Stack\\\\game\\\\src\\\\main\\\\resources\\\\icons\\\\gol.png";

    public Partida(Dashboard dashboard) {
        this.dashboard = dashboard;
        carregarImagens();
        setLayout(new BorderLayout());
        setBackground(COR_FUNDO);

        // --- HEADER ---
        JPanel painelHeader = new JPanel();
        painelHeader.setLayout(new BoxLayout(painelHeader, BoxLayout.Y_AXIS));
        painelHeader.setBackground(new Color(15, 25, 45));
        painelHeader.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 20));

        JPanel barraTopo = new JPanel(new BorderLayout());
        barraTopo.setOpaque(false);
        barraTopo.setPreferredSize(new Dimension(0, 25));

        lblPublico = new JLabel("Público: 13.537");
        lblPublico.setForeground(new Color(150, 160, 180));
        lblPublico.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPublico.setPreferredSize(new Dimension(150, 25));

        lblStatusTempo = new JLabel("PRÉ-JOGO", SwingConstants.CENTER);
        lblStatusTempo.setForeground(Color.WHITE);
        lblStatusTempo.setFont(new Font("Segoe UI", Font.BOLD, 15));

        lblCronometro = new JLabel("0'", SwingConstants.RIGHT);
        lblCronometro.setForeground(Color.WHITE);
        lblCronometro.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblCronometro.setPreferredSize(new Dimension(150, 25));

        barraTopo.add(lblPublico, BorderLayout.WEST);
        barraTopo.add(lblStatusTempo, BorderLayout.CENTER);
        barraTopo.add(lblCronometro, BorderLayout.EAST);

        JPanel painelPlacar = new JPanel(new GridLayout(1, 3));
        painelPlacar.setOpaque(false);
        painelPlacar.setPreferredSize(new Dimension(0, 45));
        
        JLabel timeA = new JLabel("MEU TIME", SwingConstants.LEFT);
        timeA.setForeground(Color.WHITE); timeA.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        lblPlacar = new JLabel("0 - 0", SwingConstants.CENTER);
        lblPlacar.setForeground(new Color(226, 242, 0));
        lblPlacar.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel timeB = new JLabel("FLAMENGO", SwingConstants.RIGHT);
        timeB.setForeground(new Color(255, 80, 80));
        timeB.setFont(new Font("Segoe UI", Font.BOLD, 16));

        painelPlacar.add(timeA); painelPlacar.add(lblPlacar); painelPlacar.add(timeB);

        painelHeader.add(barraTopo);
        painelHeader.add(Box.createVerticalStrut(5));
        painelHeader.add(painelPlacar);
        add(painelHeader, BorderLayout.NORTH);

        // --- CENTRO ---
        JPanel painelCentral = new JPanel();
        painelCentral.setLayout(new BoxLayout(painelCentral, BoxLayout.Y_AXIS));
        painelCentral.setOpaque(false);
        painelCentral.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel containerTabelas = new JPanel(new GridLayout(1, 2, 15, 0));
        containerTabelas.setOpaque(false);
        containerTabelas.setPreferredSize(new Dimension(0, 340));
        containerTabelas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));

        modeloMeuTime = criarModeloConfronto();
        tabelaMeuTime = new JTable(modeloMeuTime);
        containerTabelas.add(configurarTabelaFinal(tabelaMeuTime));

        modeloAdversario = criarModeloConfronto();
        tabelaAdversario = new JTable(modeloAdversario);
        containerTabelas.add(configurarTabelaFinal(tabelaAdversario));

        JPanel containerLance = new JPanel(new BorderLayout());
        containerLance.setBackground(Color.BLACK);
        containerLance.setPreferredSize(new Dimension(0, 40));
        containerLance.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        containerLance.setBorder(BorderFactory.createLineBorder(new Color(40, 50, 70)));
        
        lblLance = new JLabel("Aguardando o apito inicial...", SwingConstants.CENTER);
        lblLance.setForeground(new Color(200, 200, 200));
        lblLance.setFont(new Font("Segoe UI", Font.BOLD, 14));
        containerLance.add(lblLance);

        logMotor = new JTextArea(10, 30);
        logMotor.setBackground(new Color(5, 10, 20));
        logMotor.setForeground(new Color(0, 210, 255));
        logMotor.setFont(new Font("Monospaced", Font.PLAIN, 11));
        logMotor.setEditable(false);
        JScrollPane scrollLog = new JScrollPane(logMotor);
        scrollLog.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(40, 50, 80)), "DEBUG MOTOR DE JOGO", 0, 0, null, Color.GRAY));

        painelCentral.add(containerTabelas);
        painelCentral.add(Box.createVerticalStrut(10));
        painelCentral.add(containerLance);
        painelCentral.add(Box.createVerticalStrut(10));
        painelCentral.add(scrollLog);

        add(painelCentral, BorderLayout.CENTER);

        // --- FOOTER ---
        JPanel footer = new JPanel(new GridLayout(1, 3, 10, 0));
        footer.setBackground(new Color(15, 25, 45));
        footer.setPreferredSize(new Dimension(0, 50));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btnTaticas = criarBotao("TÁTICAS", new Color(35, 50, 80));
        btnVerAdversario = criarBotao("SCOUT", new Color(50, 50, 55));
        btnAcao = criarBotao("INICIAR JOGO", new Color(0, 90, 55));
        btnAcao.addActionListener(e -> gerenciarFluxoJogo());

        footer.add(btnTaticas); footer.add(btnVerAdversario); footer.add(btnAcao);
        add(footer, BorderLayout.SOUTH);

        carregarDadosTitulares();
    }

    private void carregarImagens() {
        try {
            // Tentativa direta pelo caminho solicitado
            ImageIcon icon = new ImageIcon(PATH_ICONS);
            if (icon.getIconWidth() > 0) {
                cacheImagens.put("GOL", icon.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH));
            }
        } catch (Exception e) {}
    }

    private void adicionarLog(String msg) {
        logMotor.append(msg + "\n");
        logMotor.setCaretPosition(logMotor.getDocument().getLength());
    }

    private void processarMotorJogo() {
        int r = (int) (Math.random() * 101);
        adicionarLog(String.format("[%d'] SORTEIO GERAL: %d", minutosSimulados, r)); // LOGS VOLTARAM

        if (r <= 2) aplicarEventoDisciplina("LESAO");
        else if (r <= 5) aplicarEventoDisciplina("VERMELHO");
        else if (r <= 15) aplicarEventoDisciplina("AMARELO");
        else if (r <= 30) adicionarLog("   -> Nada aconteceu.");
        else executarLanceFunil();
    }

    private void executarLanceFunil() {
        int[] fM = calcularForcaBrutaAtiva(modeloMeuTime);
        int[] fA = calcularForcaBrutaAtiva(modeloAdversario);
        int somaMeu = fM[0] + fM[1];
        int somaAdv = fA[0] + fA[1];
        int total = somaMeu + somaAdv;
        if (total == 0) return;

        int rPosse = (int) (Math.random() * total);
        adicionarLog(String.format("   -> FUNIL POSSE: MEU(%d) vs ADV(%d) | Sorteio: %d", somaMeu, somaAdv, rPosse));

        if (rPosse <= somaMeu) {
            adicionarLog("   -> Posse: MEU TIME");
            resolverDueloGol("MEU TIME", fM[0], fA[1], modeloMeuTime);
        } else {
            adicionarLog("   -> Posse: FLAMENGO");
            resolverDueloGol("FLAMENGO", fA[0], fM[1], modeloAdversario);
        }
    }

    private void resolverDueloGol(String atacante, int atk, int def, DefaultTableModel modAtk) {
        int saldo = atk - def;
        int prob = (saldo >= 20) ? 60 : (saldo >= 0) ? 40 : 20;
        int r = (int) (Math.random() * 101);
        
        adicionarLog(String.format("      CHANCE GOL: Atk(%d) Def(%d) Saldo(%d) Prob(%d%%) Sorteio(%d)", atk, def, saldo, prob, r));

        if (r <= prob) {
            if (atacante.equals("MEU TIME")) golsMeuTime++; else golsAdv++;
            int autor = sorteiaAutorAtivo(modAtk);
            if (autor != -1) registrarEventoTabela(modAtk, autor, "GOL");
            lblPlacar.setText(golsMeuTime + " - " + golsAdv);
            lblLance.setText("GOOOOOL DO " + atacante + "!!!");
            lblLance.setForeground(new Color(226, 242, 0));
        } else {
            lblLance.setText("A defesa cortou o lance!");
            lblLance.setForeground(Color.WHITE);
        }
    }

    private void registrarEventoTabela(DefaultTableModel mod, int row, String ev) {
        String atual = (String) mod.getValueAt(row, 2);
        if (atual == null) atual = "";
        if (ev.equals("AMARELO") && atual.contains("AMARELO")) ev = "VERMELHO";
        
        String novoStatus = atual.isEmpty() ? ev : atual + "," + ev;
        mod.setValueAt(novoStatus, row, 2);
        
        adicionarLog("   !!! EVENTO: " + ev + " em " + mod.getValueAt(row, 1));

        SwingUtilities.invokeLater(() -> {
            mod.fireTableRowsUpdated(row, row);
            tabelaMeuTime.repaint();
            tabelaAdversario.repaint();
        });
    }

    private JScrollPane configurarTabelaFinal(JTable tabela) {
        tabela.setBackground(COR_FUNDO); 
        tabela.setRowHeight(20); // VISUAL ORIGINAL 20PX
        tabela.setShowGrid(false);
        tabela.setTableHeader(null); 
        tabela.setFocusable(false);
        
        ZebraRenderer renderer = new ZebraRenderer();
        for(int i=0; i<3; i++) tabela.getColumnModel().getColumn(i).setCellRenderer(renderer);
        
        tabela.getColumnModel().getColumn(0).setPreferredWidth(32); 
        tabela.getColumnModel().getColumn(1).setPreferredWidth(100); 
        tabela.getColumnModel().getColumn(2).setPreferredWidth(78); 
        
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(COR_BORDA_TABELA));
        scroll.getViewport().setBackground(COR_FUNDO);
        return scroll;
    }

    private class ZebraRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = (String) table.getValueAt(row, 2);
            boolean fora = status != null && (status.contains("VERMELHO") || status.contains("LESAO"));
            c.setBackground(row % 2 == 0 ? COR_ZEBRA : COR_FUNDO);
            c.setForeground(fora ? new Color(80, 85, 100) : new Color(220, 220, 220));
            c.setIcon(null);
            
            if (column == 2 && value != null && !value.toString().isEmpty()) {
                c.setText(""); 
                c.setIcon(new MultiIcon(value.toString()));
                c.setHorizontalAlignment(SwingConstants.LEFT); // Garante que comece na esquerda
            } else {
                c.setText(value != null ? value.toString() : "");
            }
            c.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            return c;
        }
    }

    class MultiIcon implements Icon {
        private String[] eventos;

        public MultiIcon(String s) {
            eventos = s.split(",");
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int offsetX = 0;
            for (String e : eventos) {
                e = e.trim(); // Limpa espaços
                if (e.equals("GOL")) {
                    Image img = cacheImagens.get("GOL");
                    if (img != null) {
                        // Usando sua lógica de desenho que funciona
                        g2.drawImage(img, x + offsetX, y + 3, 14, 14, null);
                    } else {
                        g2.setColor(Color.WHITE);
                        g2.fillOval(x + offsetX, y + 4, 12, 12);
                    }
                    offsetX += 16; // Espaço da bola
                } 
                else if (e.equals("AMARELO") || e.equals("VERMELHO")) {
                    g2.setColor(e.equals("AMARELO") ? new Color(255, 210, 0) : new Color(220, 0, 0));
                    g2.fillRect(x + offsetX, y + 3, 9, 13);
                    g2.setColor(Color.BLACK);
                    g2.drawRect(x + offsetX, y + 3, 9, 13);
                    offsetX += 11; // DIMINUÍDO: Agora o vermelho cola no amarelo
                } 
                else if (e.equals("LESAO")) {
                    g2.setColor(new Color(255, 50, 50));
                    g2.fillRect(x + offsetX, y + 8, 14, 4);
                    g2.fillRect(x + offsetX + 5, y + 3, 4, 14);
                    offsetX += 18;
                }
            }
        }

        @Override
        public int getIconWidth() {
            // Ajustado para refletir o novo espaçamento menor
            return eventos.length * 14; 
        }

        @Override
        public int getIconHeight() {
            return 20;
        }
    }

    // --- RESTANTE DOS MÉTODOS (ESTÁVEIS) ---
    private void carregarDadosTitulares() {
        modeloMeuTime.setRowCount(0); modeloAdversario.setRowCount(0);
        dadosBrutos.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(CAMINHO_CSV))) {
            String l; 
            while ((l = br.readLine()) != null) {
                if (l.trim().isEmpty() || l.toLowerCase().startsWith("id")) continue;
                String[] d = l.split(",");
                if (d.length >= 9 && d[8].trim().equals("1")) {
                    String nome = d[1].toUpperCase();
                    int def = Integer.parseInt(d[4].trim());
                    int atk = Integer.parseInt(d[5].trim());
                    dadosBrutos.put(nome, new int[]{atk, def});
                    Object[] r = {abreviar(d[2]), nome, ""};
                    if (d[7].trim().equals("1")) modeloMeuTime.addRow(r);
                    else if (d[7].trim().equals("2")) modeloAdversario.addRow(r);
                }
            }
        } catch (Exception e) {}
    }

    private void atualizarTempo() {
        contadorTicks++; minutosSimulados += 3;
        if (contadorTicks <= 15) {
            lblCronometro.setText(minutosSimulados + "'");
            if (contadorTicks % 2 == 0) processarMotorJogo();
        } else if (contadorTicks == 16) {
            lblCronometro.setText("Acréscimos");
            lblCronometro.setForeground(new Color(255, 200, 0));
            processarMotorJogo();
        } else { finalizarTempo(); }
    }

    private void finalizarTempo() {
        pausarPartida(); lblCronometro.setForeground(Color.WHITE);
        if (!isSegundoTempo) {
            isSegundoTempo = true; minutosSimulados = 0; contadorTicks = 0;
            lblCronometro.setText("0'"); btnAcao.setText("INICIAR 2º TEMPO");
            lblStatusTempo.setText("INTERVALO");
        } else {
            partidaFinalizada = true; lblStatusTempo.setText("ENCERRADO");
            btnAcao.setText("SAIR"); btnAcao.setBackground(new Color(50, 50, 60));
        }
    }

    private void aplicarEventoDisciplina(String tipo) {
        boolean isMeu = Math.random() > 0.5;
        DefaultTableModel mod = isMeu ? modeloMeuTime : modeloAdversario;
        int idx = sorteiaAutorAtivo(mod);
        if (idx != -1) registrarEventoTabela(mod, idx, tipo);
    }

    private int[] calcularForcaBrutaAtiva(DefaultTableModel mod) {
        int a = 0, d = 0;
        for (int i = 0; i < mod.getRowCount(); i++) {
            String s = (String) mod.getValueAt(i, 2);
            if (s == null || (!s.contains("VERMELHO") && !s.contains("LESAO"))) {
                int[] v = dadosBrutos.get((String) mod.getValueAt(i, 1));
                if (v != null) { a += v[0]; d += v[1]; }
            }
        }
        return new int[]{a, d};
    }

    private int sorteiaAutorAtivo(DefaultTableModel mod) {
        java.util.List<Integer> ativos = new ArrayList<>();
        for (int i = 0; i < mod.getRowCount(); i++) {
            String s = (String) mod.getValueAt(i, 2);
            if (s == null || (!s.contains("VERMELHO") && !s.contains("LESAO"))) ativos.add(i);
        }
        return ativos.isEmpty() ? -1 : ativos.get((int)(Math.random() * ativos.size()));
    }

    private void gerenciarFluxoJogo() {
        if (partidaFinalizada) { dashboard.voltarParaMenu(); return; }
        if (!jogoIniciado || btnAcao.getText().contains("RETOMAR") || btnAcao.getText().contains("INICIAR")) {
            jogoIniciado = true;
            lblStatusTempo.setText(isSegundoTempo ? "2º TEMPO" : "1º TEMPO");
            btnAcao.setText("PAUSAR"); btnAcao.setBackground(new Color(140, 40, 40));
            if (timer == null) timer = new Timer(1000, e -> atualizarTempo());
            timer.start();
        } else { pausarPartida(); }
    }

    private void pausarPartida() { if (timer != null) timer.stop(); btnAcao.setText("RETOMAR"); btnAcao.setBackground(new Color(0, 90, 55)); }

    private DefaultTableModel criarModeloConfronto() {
        return new DefaultTableModel(new String[]{"P", "N", "A"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    private JButton criarBotao(String t, Color bg) {
        JButton b = new JButton(t); b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 10)); b.setFocusPainted(false); b.setBorderPainted(false);
        return b;
    }

    private String abreviar(String p) {
        p = p.toLowerCase();
        if(p.contains("goleiro")) return "GL"; if(p.contains("zagueiro")) return "ZG";
        if(p.contains("lateral")) return p.contains("dir") ? "LD" : "LE";
        if(p.contains("meio")) return "MC"; if(p.contains("atacante")) return "AT";
        return p.substring(0, Math.min(2, p.length())).toUpperCase();
    }
}
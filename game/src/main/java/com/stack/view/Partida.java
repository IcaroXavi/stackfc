package com.stack.view;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
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
    
    private int minutosSimulados = 0;
    private int contadorTicks = 0;
    private int acrescimosTempo = 0;
    private boolean isAcrescimos = false;
    private boolean isSegundoTempo = false;
    private boolean jogoIniciado = false;
    private boolean partidaFinalizada = false;

    private int atkTotalMeuTime = 0, defTotalMeuTime = 0;
    private int atkTotalAdv = 0, defTotalAdv = 0;
    private int golsMeuTime = 0, golsAdv = 0;

    private final Color COR_FUNDO = new Color(10, 15, 30);
    private final Color COR_ZEBRA = new Color(20, 25, 45);
    private final Color COR_BORDA_TABELA = new Color(30, 40, 60);
    private final String CAMINHO_CSV = "C:\\Projetos\\Stack\\game\\src\\main\\java\\com\\stack\\repository\\jogadores.csv";
    
    // Cache de imagens para performance
    private final Map<String, Image> cacheImagens = new HashMap<>();
    private final String PATH_ICONS = "C:\\Projetos\\Stack\\game\\src\\main\\resources\\icons\\";

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
        containerTabelas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));

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
        String[] eventos = {"GOL", "AMARELO", "VERMELHO", "LESAO"};
        String[] arquivos = {"gol.png", "amarelo.png", "vermelho.png", "lesao.png"};
        for (int i = 0; i < eventos.length; i++) {
            try {
                ImageIcon icon = new ImageIcon(PATH_ICONS + arquivos[i]);
                if (icon.getIconWidth() > 0) {
                    cacheImagens.put(eventos[i], icon.getImage());
                }
            } catch (Exception e) {
                System.out.println("Erro ao carregar: " + arquivos[i]);
            }
        }
    }

    private void adicionarLog(String msg) {
        logMotor.append(" [" + minutosSimulados + "'] " + msg + "\n");
        logMotor.setCaretPosition(logMotor.getDocument().getLength());
    }

    private void carregarDadosTitulares() {
        modeloMeuTime.setRowCount(0); modeloAdversario.setRowCount(0);
        atkTotalMeuTime = 0; defTotalMeuTime = 0; atkTotalAdv = 0; defTotalAdv = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(CAMINHO_CSV))) {
            String l; 
            while ((l = br.readLine()) != null) {
                if (l.trim().isEmpty() || l.toLowerCase().startsWith("id")) continue;
                String[] d = l.split(",");
                if (d.length >= 9 && d[8].trim().equals("1")) {
                    int def = Integer.parseInt(d[4].trim());
                    int atk = Integer.parseInt(d[5].trim());
                    Object[] r = {abreviar(d[2]), d[1].toUpperCase(), ""};
                    if (d[7].trim().equals("1")) {
                        modeloMeuTime.addRow(r);
                        atkTotalMeuTime += atk; defTotalMeuTime += def;
                    } else if (d[7].trim().equals("2")) {
                        modeloAdversario.addRow(r);
                        atkTotalAdv += atk; defTotalAdv += def;
                    }
                }
            }
            adicionarLog("DATA: MeuTime(ATK:" + atkTotalMeuTime + "/DEF:" + defTotalMeuTime + ") vs Adv(ATK:" + atkTotalAdv + "/DEF:" + defTotalAdv + ")");
        } catch (Exception e) { adicionarLog("ERRO: Banco de dados."); }
    }

    private void atualizarTempo() {
        contadorTicks++;
        int limiteTempo = 45;
        if (minutosSimulados < limiteTempo) {
            minutosSimulados++;
            lblCronometro.setText(minutosSimulados + "'");
        } else {
            if (!isAcrescimos) {
                isAcrescimos = true;
                acrescimosTempo = (int)(Math.random() * 5) + 3; 
                adicionarLog("ÁRBITRO: Placa sinaliza +" + acrescimosTempo + " minutos.");
            }
            minutosSimulados++;
            lblCronometro.setText("Acréscimos");
            lblCronometro.setForeground(new Color(255, 200, 0));
        }
        if (contadorTicks % 4 == 0) processarMotorJogo();
        if (isAcrescimos && (minutosSimulados >= limiteTempo + acrescimosTempo)) finalizarTempo();
    }

    private void finalizarTempo() {
        pausarPartida();
        isAcrescimos = false;
        lblCronometro.setForeground(Color.WHITE);
        if (!isSegundoTempo) {
            isSegundoTempo = true;
            minutosSimulados = 0;
            lblCronometro.setText("0'");
            btnAcao.setText("INICIAR 2º TEMPO");
            lblStatusTempo.setText("INTERVALO");
            adicionarLog("EVENTO: Intervalo de jogo.");
        } else {
            partidaFinalizada = true;
            lblCronometro.setText("--"); 
            lblStatusTempo.setText("ENCERRADO");
            btnAcao.setText("SAIR");
            btnAcao.setBackground(new Color(50, 50, 60));
            adicionarLog("EVENTO: Partida encerrada.");
        }
    }

    private void processarMotorJogo() {
        int rEvento = (int) (Math.random() * 101);
        adicionarLog("MOTOR: Sorteio base: " + rEvento);

        if (rEvento > 50) {
            int tipo = (int)(Math.random() * 3);
            String ev = (tipo == 0) ? "AMARELO" : (tipo == 1) ? "VERMELHO" : "LESAO";
            boolean isMeu = Math.random() > 0.5;
            DefaultTableModel mod = isMeu ? modeloMeuTime : modeloAdversario;
            int idx = (int)(Math.random() * mod.getRowCount());
            registrarEventoTabela(mod, idx, ev);
            adicionarLog("DISCIPLINA: " + ev + " para [" + mod.getValueAt(idx, 1) + "]");
        }

        if (rEvento >= 10) executarLancePerigoso();
        else adicionarLog("RITMO: Posse de bola em disputa.");
    }

    private void registrarEventoTabela(DefaultTableModel mod, int row, String ev) {
        String atual = (String) mod.getValueAt(row, 2);
        if (atual == null || atual.isEmpty()) mod.setValueAt(ev, row, 2);
        else mod.setValueAt(atual + "," + ev, row, 2);
    }

    private void executarLancePerigoso() {
        int totalForca = (atkTotalMeuTime + defTotalMeuTime) + (atkTotalAdv + defTotalAdv);
        int rPosse = (int) (Math.random() * totalForca);
        adicionarLog("LANCE: Sorteio posse (" + rPosse + "/" + totalForca + ")");

        if (rPosse < (atkTotalMeuTime + defTotalMeuTime)) {
            adicionarLog("LANCE: Ofensiva do MEU TIME.");
            tentarGol("MEU TIME", atkTotalMeuTime, defTotalAdv);
        } else {
            adicionarLog("LANCE: Ofensiva do FLAMENGO.");
            tentarGol("FLAMENGO", atkTotalAdv, defTotalMeuTime);
        }
    }

    private void tentarGol(String atacante, int atk, int def) {
        int saldo = atk - def;
        int prob = (saldo < 0) ? 10 : (saldo <= 15 ? 20 : (saldo <= 30 ? 35 : 50));
        int rGol = (int)(Math.random() * 100);
        adicionarLog("FINALIZAÇÃO: Probabilidade de gol: " + prob + "% | Sorteio: " + rGol);

        if (rGol < prob) {
            DefaultTableModel mod = atacante.equals("MEU TIME") ? modeloMeuTime : modeloAdversario;
            if (atacante.equals("MEU TIME")) golsMeuTime++; else golsAdv++;
            int autorIdx = (int)(Math.random() * mod.getRowCount());
            registrarEventoTabela(mod, autorIdx, "GOL");
            lblPlacar.setText(golsMeuTime + " - " + golsAdv);
            lblLance.setText("GOOOOOL DO " + atacante + "!!!");
            lblLance.setForeground(new Color(226, 242, 0));
            adicionarLog("GOL: Confirmado! Autor: " + mod.getValueAt(autorIdx, 1));
        } else {
            lblLance.setText("Pressão do " + atacante + "!");
            lblLance.setForeground(Color.WHITE);
            adicionarLog("DEFESA: Lance neutralizado.");
        }
    }

    private void gerenciarFluxoJogo() {
        if (partidaFinalizada) { dashboard.voltarParaMenu(); return; }
        if (!jogoIniciado || btnAcao.getText().contains("RETOMAR") || btnAcao.getText().contains("INICIAR")) {
            jogoIniciado = true;
            if (!isAcrescimos) lblStatusTempo.setText(isSegundoTempo ? "2º TEMPO" : "1º TEMPO");
            btnAcao.setText("PAUSAR"); btnAcao.setBackground(new Color(140, 40, 40));
            if (timer == null) timer = new Timer(533, e -> atualizarTempo());
            timer.start();
        } else { pausarPartida(); }
    }

    private void pausarPartida() {
        if (timer != null) timer.stop();
        btnAcao.setText("RETOMAR"); btnAcao.setBackground(new Color(0, 90, 55));
    }

    private JScrollPane configurarTabelaFinal(JTable tabela) {
        tabela.setBackground(COR_FUNDO); tabela.setRowHeight(20); tabela.setShowGrid(false);
        tabela.setTableHeader(null); tabela.setFocusable(false);
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
            c.setBackground(row % 2 == 0 ? COR_ZEBRA : COR_FUNDO);
            c.setForeground(new Color(220, 220, 220));
            
            if (column == 1) c.setHorizontalAlignment(SwingConstants.LEFT);
            else if (column == 2) c.setHorizontalAlignment(SwingConstants.LEFT);
            else c.setHorizontalAlignment(SwingConstants.CENTER);

            c.setIcon(null); 
            c.setText(value != null ? value.toString() : "");

            if (column == 2 && value != null) {
                String val = value.toString();
                if (!val.isEmpty()) {
                    c.setText("");
                    c.setIcon(new MultiIcon(val));
                }
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
        // Deixa o desenho mais suave (sem serrilhado)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int offsetX = 0;
        for (String e : eventos) {
            if (e.equals("GOL")) {
                Image img = cacheImagens.get("GOL");
                if (img != null) {
                    g2.drawImage(img, x + offsetX, y + 3, 14, 14, null);
                } else {
                    // Círculo branco caso o png do gol falhe
                    g2.setColor(Color.WHITE);
                    g2.fillOval(x + offsetX, y + 4, 12, 12);
                }
            } 
            else if (e.equals("AMARELO")) {
                g2.setColor(new Color(255, 210, 0)); // Amarelo vivo
                g2.fillRect(x + offsetX, y + 3, 9, 13);
                g2.setColor(Color.BLACK); // Bordas sutis para destacar
                g2.drawRect(x + offsetX, y + 3, 9, 13);
            } 
            else if (e.equals("VERMELHO")) {
                g2.setColor(new Color(220, 0, 0)); // Vermelho forte
                g2.fillRect(x + offsetX, y + 3, 9, 13);
                g2.setColor(Color.BLACK);
                g2.drawRect(x + offsetX, y + 3, 9, 13);
            } 
            else if (e.equals("LESAO")) {
                g2.setColor(new Color(255, 50, 50)); // Vermelho cruz
                // Desenha a barra horizontal da cruz
                g2.fillRect(x + offsetX, y + 8, 14, 4);
                // Desenha a barra vertical da cruz
                g2.fillRect(x + offsetX + 5, y + 3, 4, 14);
            }
            
            offsetX += 18; // Espaço para o próximo ícone não encavalar
        }
    }

    @Override
    public int getIconWidth() { return eventos.length * 18; }
    @Override
    public int getIconHeight() { return 20; }
}

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
package com.stack.view;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
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
    private int segundosCorridos = 0;
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

    public Partida(Dashboard dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout());
        setBackground(COR_FUNDO);

        // --- CABEÇALHO CONDENSADO ---
        JPanel painelHeader = new JPanel();
        painelHeader.setLayout(new BoxLayout(painelHeader, BoxLayout.Y_AXIS));
        painelHeader.setBackground(new Color(15, 25, 45));
        painelHeader.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 20)); // Padding reduzido

        // BARRA TOPO (Público, Status, Cronômetro)
        JPanel barraTopo = new JPanel(new BorderLayout());
        barraTopo.setOpaque(false);
        barraTopo.setPreferredSize(new Dimension(0, 25)); // Altura reduzida

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

        // PLACAR REDUZIDO
        JPanel painelPlacar = new JPanel(new GridLayout(1, 3));
        painelPlacar.setOpaque(false);
        painelPlacar.setPreferredSize(new Dimension(0, 45)); // Altura reduzida de 60 para 45
        
        JLabel timeA = new JLabel("MEU TIME", SwingConstants.LEFT);
        timeA.setForeground(Color.WHITE); timeA.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        lblPlacar = new JLabel("0 - 0", SwingConstants.CENTER);
        lblPlacar.setForeground(new Color(226, 242, 0));
        lblPlacar.setFont(new Font("Segoe UI", Font.BOLD, 28)); // Fonte levemente menor

        JLabel timeB = new JLabel("FLAMENGO", SwingConstants.RIGHT);
        timeB.setForeground(new Color(255, 80, 80));
        timeB.setFont(new Font("Segoe UI", Font.BOLD, 16));

        painelPlacar.add(timeA); painelPlacar.add(lblPlacar); painelPlacar.add(timeB);

        painelHeader.add(barraTopo);
        painelHeader.add(Box.createVerticalStrut(5)); // Espaço reduzido
        painelHeader.add(painelPlacar);
        add(painelHeader, BorderLayout.NORTH);

        // --- ÁREA CENTRAL ---
        JPanel painelCentral = new JPanel();
        painelCentral.setLayout(new BoxLayout(painelCentral, BoxLayout.Y_AXIS));
        painelCentral.setOpaque(false);
        painelCentral.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Tabelas
        JPanel containerTabelas = new JPanel(new GridLayout(1, 2, 15, 0));
        containerTabelas.setOpaque(false);
        containerTabelas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));

        modeloMeuTime = criarModeloConfronto();
        tabelaMeuTime = new JTable(modeloMeuTime);
        containerTabelas.add(configurarTabelaFinal(tabelaMeuTime));

        modeloAdversario = criarModeloConfronto();
        tabelaAdversario = new JTable(modeloAdversario);
        containerTabelas.add(configurarTabelaFinal(tabelaAdversario));

        // PAINEL DE LANCES
        JPanel containerLance = new JPanel(new BorderLayout());
        containerLance.setBackground(Color.BLACK);
        containerLance.setPreferredSize(new Dimension(0, 40));
        containerLance.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        containerLance.setBorder(BorderFactory.createLineBorder(new Color(40, 50, 70)));
        
        lblLance = new JLabel("Aguardando o apito inicial...", SwingConstants.CENTER);
        lblLance.setForeground(new Color(200, 200, 200));
        lblLance.setFont(new Font("Segoe UI", Font.BOLD, 14));
        containerLance.add(lblLance);

        // Log Detalhado
        logMotor = new JTextArea(10, 30);
        logMotor.setBackground(new Color(5, 10, 20));
        logMotor.setForeground(new Color(0, 210, 255)); // Azul claro para melhor leitura
        logMotor.setFont(new Font("Monospaced", Font.PLAIN, 11));
        logMotor.setEditable(false);
        JScrollPane scrollLog = new JScrollPane(logMotor);
        scrollLog.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(40, 50, 80)), "LOG DETALHADO DA PARTIDA", 0, 0, null, Color.GRAY));

        painelCentral.add(containerTabelas);
        painelCentral.add(Box.createVerticalStrut(10));
        painelCentral.add(containerLance);
        painelCentral.add(Box.createVerticalStrut(10));
        painelCentral.add(scrollLog);

        add(painelCentral, BorderLayout.CENTER);

        // --- RODAPÉ ---
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

    private void adicionarLog(String msg) {
        logMotor.append(" [" + segundosCorridos + "'] " + msg + "\n");
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
            adicionarLog("SISTEMA: Equipes prontas no vestiário.");
            adicionarLog("FORÇA MEU TIME: " + (atkTotalMeuTime + defTotalMeuTime) + " (A:" + atkTotalMeuTime + " D:" + defTotalMeuTime + ")");
            adicionarLog("FORÇA ADVERSÁRIO: " + (atkTotalAdv + defTotalAdv) + " (A:" + atkTotalAdv + " D:" + defTotalAdv + ")");
        } catch (Exception e) { adicionarLog("ERRO: Banco de dados inacessível."); }
    }

    private void atualizarTempo() {
        segundosCorridos++;
        lblCronometro.setText(segundosCorridos + "'");
        processarMotorJogo();

        if (segundosCorridos == 45 && !isSegundoTempo) {
            pausarPartida();
            isSegundoTempo = true;
            btnAcao.setText("INICIAR 2º TEMPO");
            lblStatusTempo.setText("INTERVALO");
            adicionarLog("--- ÁRBITRO APITA: FIM DO PRIMEIRO TEMPO ---");
        } else if (segundosCorridos >= 90) {
            timer.stop();
            partidaFinalizada = true;
            lblStatusTempo.setText("ENCERRADO");
            btnAcao.setText("SAIR");
            btnAcao.setBackground(new Color(50, 50, 60));
            adicionarLog("--- ÁRBITRO APITA: FIM DE PARTIDA! ---");
        }
    }

    private void processarMotorJogo() {
        int sorteioEvento = (int) (Math.random() * 101);
        
        if (sorteioEvento >= 75) {
            adicionarLog("MOTOR: Chance de lance criada! (Sorteio: " + sorteioEvento + ")");
            executarLancePerigoso();
        } else {
            // Log de tempo real para quando não acontece nada especial
            String[] frasesMeio = {"Disputa intensa no meio de campo.", "Troca de passes na defesa.", "Lateral cobrado.", "Posse de bola estudada."};
            adicionarLog("RITMO: " + frasesMeio[(int)(Math.random()*frasesMeio.length)]);
        }
    }

    private void executarLancePerigoso() {
        int forcaA = atkTotalMeuTime + defTotalMeuTime;
        int forcaB = atkTotalAdv + defTotalAdv;
        int total = forcaA + forcaB;
        
        int sorteioPosse = (int) (Math.random() * total);

        adicionarLog("POSSE: Total " + total + " | Intervalo Meu: 0-" + forcaA + " | Adv: " + (forcaA+1) + "-" + total);
        adicionarLog("POSSE: Dado caiu em " + sorteioPosse);

        if (sorteioPosse <= forcaA) {
            adicionarLog("RESULTADO POSSE: Ataque do MEU TIME iniciado.");
            tentarGol("MEU TIME", atkTotalMeuTime, defTotalAdv);
        } else {
            adicionarLog("RESULTADO POSSE: Flamengo recupera e parte para o contra-ataque.");
            tentarGol("FLAMENGO", atkTotalAdv, defTotalMeuTime);
        }
    }

    private void tentarGol(String atacante, int atk, int def) {
        int saldo = atk - def;
        int prob = (saldo < 0) ? 10 : (saldo <= 15 ? 20 : (saldo <= 30 ? 35 : 50));

        int sorteioGol = (int) (Math.random() * 100);
        boolean foiGol = sorteioGol < prob;

        adicionarLog("CHANCE: Probabilidade de gol é " + prob + "% (Saldo ATK-DEF: " + saldo + ")");
        adicionarLog("CHANCE: Sorteio do chute foi " + sorteioGol);

        if (foiGol) {
            if (atacante.equals("MEU TIME")) golsMeuTime++; else golsAdv++;
            lblPlacar.setText(golsMeuTime + " - " + golsAdv);
            lblLance.setText("GOOOOOL DO " + atacante + "!!!");
            lblLance.setForeground(new Color(226, 242, 0));
            adicionarLog("!!! GOL REGISTRADO PARA " + atacante + " !!!");
        } else {
            lblLance.setText("Defesa afasta o perigo do " + atacante + "!");
            lblLance.setForeground(Color.WHITE);
            adicionarLog("DEFESA: O goleiro/zaga levou a melhor.");
        }
    }

    private void gerenciarFluxoJogo() {
        if (partidaFinalizada) { dashboard.voltarParaMenu(); return; }
        if (!jogoIniciado || btnAcao.getText().contains("RETOMAR") || btnAcao.getText().contains("INICIAR")) {
            jogoIniciado = true;
            lblStatusTempo.setText(isSegundoTempo ? "2º TEMPO" : "1º TEMPO");
            btnAcao.setText("PAUSAR"); btnAcao.setBackground(new Color(140, 40, 40));
            if (timer == null) timer = new Timer(500, e -> atualizarTempo());
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
        tabela.getColumnModel().getColumn(0).setPreferredWidth(35);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(140);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(30);
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
            c.setHorizontalAlignment(column == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);
            c.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
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
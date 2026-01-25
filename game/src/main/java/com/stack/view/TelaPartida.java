package com.stack.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.stack.controller.SubstituicaoController;
import com.stack.engine.MotorJogo;
import com.stack.model.Jogador;

public class TelaPartida extends JPanel {
    private TelaDashboard dashboard;
    private JLabel lblCronometro, lblStatusTempo, lblLance, lblPublico, lblPlacar;
    private JTable tabelaMeuTime, tabelaAdversario;
    private DefaultTableModel modeloMeuTime, modeloAdversario;
    private JButton btnAcao, btnTaticas, btnVerAdversario;
    private Timer timer;
    private final Color COR_FUNDO = new Color(10, 15, 30);
    private final Color COR_ZEBRA = new Color(20, 25, 45);
    private final Color COR_BORDA_TABELA = new Color(30, 40, 60);
    private final Color COR_TEXTO_POS = new Color(0, 255, 127); 
    private final MotorJogo motor;
    private com.stack.repository.JogadorRepository jogadorRepository;
    public TelaPartida(TelaDashboard dashboard, MotorJogo motorRecebido) {
        this.dashboard = dashboard;
        this.motor = motorRecebido;
        this.jogadorRepository = motorRecebido.getRepo();
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

        lblStatusTempo = new JLabel("", SwingConstants.CENTER);
        lblStatusTempo.setText(motor.getTempo());
        lblStatusTempo.setForeground(Color.WHITE);
        lblStatusTempo.setFont(new Font("Segoe UI", Font.BOLD, 15));

        lblCronometro = new JLabel("", SwingConstants.RIGHT);
        lblCronometro.setText(motor.getCronometroFormatado());
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
        timeA.setForeground(Color.WHITE); 
        timeA.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        lblPlacar = new JLabel("0 - 0", SwingConstants.CENTER); 
        lblPlacar.setForeground(new Color(226, 242, 0));
        lblPlacar.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblPlacar.setText(motor.getPlacarFormatado());
        
        JLabel timeB = new JLabel("ADVERSÁRIO", SwingConstants.RIGHT);
        timeB.setForeground(new Color(255, 80, 80));
        timeB.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        painelPlacar.add(timeA); painelPlacar.add(lblPlacar); painelPlacar.add(timeB);

        painelHeader.add(barraTopo);
        painelHeader.add(Box.createVerticalStrut(5));
        painelHeader.add(painelPlacar);
        add(painelHeader, BorderLayout.NORTH);

        // --- CORPO CENTRAL ---
        JPanel painelCentral = new JPanel();
        painelCentral.setLayout(new BoxLayout(painelCentral, BoxLayout.Y_AXIS));
        painelCentral.setOpaque(false);
        painelCentral.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel containerTabelas = new JPanel(new GridLayout(1, 2, 15, 0));
        containerTabelas.setOpaque(false);
        containerTabelas.setPreferredSize(new Dimension(0, 340));
        containerTabelas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));

        modeloMeuTime = new DefaultTableModel(new String[]{"P", "JOGADOR", "A"}, 0);
        tabelaMeuTime = new JTable(modeloMeuTime);
        containerTabelas.add(configurarTabelaEstilizada(tabelaMeuTime));

        modeloAdversario = new DefaultTableModel(new String[]{"P", "JOGADOR", "A"}, 0);
        tabelaAdversario = new JTable(modeloAdversario);
        containerTabelas.add(configurarTabelaEstilizada(tabelaAdversario));

        JPanel containerLance = new JPanel(new BorderLayout());
        containerLance.setBackground(Color.BLACK);
        containerLance.setPreferredSize(new Dimension(0, 40));
        containerLance.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        containerLance.setBorder(BorderFactory.createLineBorder(new Color(40, 50, 70)));
        
        lblLance = new JLabel("Aguardando o apito inicial...", SwingConstants.CENTER);
        lblLance.setForeground(new Color(200, 200, 200));
        lblLance.setFont(new Font("Segoe UI", Font.BOLD, 14));
        containerLance.add(lblLance);

        painelCentral.add(containerTabelas);
        painelCentral.add(Box.createVerticalStrut(10));
        painelCentral.add(containerLance);
        add(painelCentral, BorderLayout.CENTER);

        // --- FOOTER ---
        JPanel footer = new JPanel(new GridLayout(1, 3, 10, 0));
        footer.setBackground(new Color(15, 25, 45));
        footer.setPreferredSize(new Dimension(0, 50));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnTaticas = criarBotaoRodape("TÁTICAS", new Color(35, 50, 80));
        btnVerAdversario = criarBotaoRodape("ADVERSÁRIO", new Color(50, 50, 55));
        btnAcao = criarBotaoRodape("INICIAR PARTIDA", new Color(0, 90, 55));

        // AÇÃO DO BOTÃO TÁTICAS (Mantendo o layout original)
        btnTaticas.addActionListener(e -> {
            if (timer != null && !motor.getTempo().equals("Encerrada")) {
                motor.pausarJogo();
            }
            TelaSubstituicao painelSub = new TelaSubstituicao(motor.getTitularesCasa(), motor.getReservasCasa(), motor.getSubsRealizadasCasa(),!motor.getTempo().equals("PRÉ JOGO"));
            java.awt.Window parentWindow = SwingUtilities.getWindowAncestor(this);
            JDialog dialog = new JDialog(parentWindow, "Gerenciar Time", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
            new SubstituicaoController(painelSub, motor, jogadorRepository, dialog);
            dialog.setLayout(new BorderLayout());
            dialog.setUndecorated(true);
            dialog.setResizable(false); 
            dialog.add(painelSub, BorderLayout.CENTER);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            Point p = dialog.getLocation();
            dialog.setLocation(p.x, p.y + 45);
            dialog.setVisible(true);

            preencherTabelasVisuais(); 
            atualizarInterface();
        });

        btnAcao.addActionListener(e -> {
            if (btnAcao.getText().equals("AVANÇAR")) {
                dashboard.voltarParaMenu(); 
                return; 
            }
            if (motor.getTempo().equals("Encerrada")) return;
            if (timer == null) { 
                motor.iniciarCronometro();
                timer = new Timer(100, event -> atualizarInterface());
                timer.start();
            } else if (!motor.isPausado()) {
                motor.pausarJogo();
            } else {
                motor.retomarJogo();
            }
        });

        footer.add(btnTaticas); footer.add(btnVerAdversario); footer.add(btnAcao);
        add(footer, BorderLayout.SOUTH);
        
        preencherTabelasVisuais();
    }

    private void preencherTabelasVisuais() {
        modeloMeuTime.setRowCount(0);
        modeloAdversario.setRowCount(0);
        String fVisual = "4-4-2"; 

        List<Jogador> casa = new ArrayList<>(motor.getTitularesCasa());
        casa.sort(Comparator.comparingInt(Jogador::getOrdem));
        for (int i = 0; i < casa.size(); i++) {
            Jogador j = casa.get(i);
            modeloMeuTime.addRow(new Object[]{ definirSiglaPosicao(fVisual, i), "[" + j.getPos() + "] " + j.getNome(), "" });
        }

        List<Jogador> adv = new ArrayList<>(motor.getTitularesVisitante());
        adv.sort(Comparator.comparingInt(Jogador::getOrdem));
        for (int i = 0; i < adv.size(); i++) {
            Jogador j = adv.get(i);
            modeloAdversario.addRow(new Object[]{ definirSiglaPosicao(fVisual, i), "[" + j.getPos() + "] " + j.getNome(), "" });
        }
    }

    private String definirSiglaPosicao(String formacao, int i) {
        if (i == 0) return "GL";
        String[] p = formacao.split("-");
        int def = Integer.parseInt(p[0]);
        int mei = Integer.parseInt(p[1]);
        if (i <= def) {
            if (def >= 4 && i == 1) return "LD";
            if (def >= 4 && i == def) return "LE";
            return "ZG";
        }
        if (i <= (def + mei)) return "MC";
        return "AT";
    }

    private JScrollPane configurarTabelaEstilizada(JTable tabela) {
        tabela.setBackground(COR_FUNDO); 
        tabela.setRowHeight(22);
        tabela.setShowGrid(false);
        tabela.setTableHeader(null); 
        
        tabela.getColumnModel().getColumn(0).setPreferredWidth(35);
        tabela.getColumnModel().getColumn(0).setMaxWidth(40);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(180);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(30);

        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                c.setBackground(row % 2 == 0 ? COR_ZEBRA : COR_FUNDO);
                if (col == 0) {
                    c.setForeground(COR_TEXTO_POS);
                    c.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    c.setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    c.setForeground(new Color(220, 220, 220));
                    c.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    c.setHorizontalAlignment(col == 1 ? SwingConstants.LEFT : SwingConstants.CENTER);
                }
                c.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(COR_BORDA_TABELA));
        scroll.getViewport().setBackground(COR_FUNDO);
        return scroll;
    }

    private JButton criarBotaoRodape(String t, Color bg) {
        JButton b = new JButton(t);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        return b;
    }

    private void atualizarInterface() {
        lblCronometro.setText(motor.getCronometroFormatado());
        lblStatusTempo.setText(motor.getTempo());
        lblPlacar.setText(motor.getPlacarFormatado());

        String status = motor.getTempo();
        if (status.equals("Encerrada")) {
                btnAcao.setText("AVANÇAR");
                btnAcao.setBackground(new Color(0, 102, 204));
            } 
            // SEGUNDA CONDIÇÃO: Se o timer é nulo, o jogo ainda não recebeu o primeiro "Play"
            else if (timer == null) {
                btnAcao.setText("INICIAR PARTIDA");
                btnAcao.setBackground(new Color(0, 90, 55));
            } 
            else if (status.equals("Intervalo") || motor.isPausado()) {
                btnAcao.setText(status.equals("Intervalo") ? "INICIAR 2º TEMPO" : "RETOMAR");
                btnAcao.setBackground(new Color(0, 90, 55));
            } 
            else {
                btnAcao.setText("PAUSAR");
                btnAcao.setBackground(new Color(165, 42, 42));
            }
        }
}
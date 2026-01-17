package com.stack.view;

import com.stack.engine.MotorJogo;
import com.stack.model.Jogador;
import java.awt.*;
import java.util.ArrayList;
import java.util.List; 
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class TelaPartida extends JPanel {
    private TelaDashboard dashboard;
    private JLabel lblCronometro, lblStatusTempo, lblLance, lblPublico, lblPlacar;
    private JTable tabelaMeuTime, tabelaAdversario;
    private DefaultTableModel modeloMeuTime, modeloAdversario;
    private JButton btnAcao, btnTaticas, btnVerAdversario;
    private Timer timer;
    private int minutos = 0;
    private int golsMeuTime = 0;
    private int golsAdv = 0;
    private final Color COR_FUNDO = new Color(10, 15, 30);
    private final Color COR_ZEBRA = new Color(20, 25, 45);
    private final Color COR_BORDA_TABELA = new Color(30, 40, 60);
    private List<Jogador> elencoMeuTime = new ArrayList<>();
    private List<Jogador> elencoAdversario = new ArrayList<>();
    private final MotorJogo motor;


    public TelaPartida(TelaDashboard dashboard, MotorJogo motorRecebido) {
        this.dashboard = dashboard;
        this.motor = motorRecebido;
        setLayout(new BorderLayout());
        setBackground(COR_FUNDO);

        // --- HEADER (Fiel ao seu rascunho) ---
        JPanel painelHeader = new JPanel();
        painelHeader.setLayout(new BoxLayout(painelHeader, BoxLayout.Y_AXIS));
        painelHeader.setBackground(new Color(15, 25, 45));
        painelHeader.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 20));

        // BARRA TOPO: O segredo da centralização está aqui
        JPanel barraTopo = new JPanel(new BorderLayout());
        barraTopo.setOpaque(false);
        barraTopo.setPreferredSize(new Dimension(0, 25));

        lblPublico = new JLabel("Público: 13.537");
        lblPublico.setForeground(new Color(150, 160, 180));
        lblPublico.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        // Definimos uma largura fixa para a esquerda
        lblPublico.setPreferredSize(new Dimension(150, 25)); 

        lblStatusTempo = new JLabel("", SwingConstants.CENTER);
        lblStatusTempo.setText(motor.getTempo());
        lblStatusTempo.setForeground(Color.WHITE);
        lblStatusTempo.setFont(new Font("Segoe UI", Font.BOLD, 15));

        lblCronometro = new JLabel("", SwingConstants.RIGHT);
        lblCronometro.setText(motor.getCronometroFormatado());
        lblCronometro.setForeground(Color.WHITE);
        lblCronometro.setFont(new Font("Segoe UI", Font.BOLD, 17));
        // Definimos a MESMA largura fixa para a direita para forçar o centro a ser o centro real
        lblCronometro.setPreferredSize(new Dimension(150, 25)); 

        barraTopo.add(lblPublico, BorderLayout.WEST);
        barraTopo.add(lblStatusTempo, BorderLayout.CENTER);
        barraTopo.add(lblCronometro, BorderLayout.EAST);

        // PLACAR
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
        JLabel timeB = new JLabel("FLAMENGO", SwingConstants.RIGHT);
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

        // Tabelas
        JPanel containerTabelas = new JPanel(new GridLayout(1, 2, 15, 0));
        containerTabelas.setOpaque(false);
        containerTabelas.setPreferredSize(new Dimension(0, 340));
        containerTabelas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));

        modeloMeuTime = new DefaultTableModel(new String[]{"P", "N", "A"}, 0);
        tabelaMeuTime = new JTable(modeloMeuTime);
        containerTabelas.add(configurarTabelaEstilizada(tabelaMeuTime));

        modeloAdversario = new DefaultTableModel(new String[]{"P", "N", "A"}, 0);
        tabelaAdversario = new JTable(modeloAdversario);
        containerTabelas.add(configurarTabelaEstilizada(tabelaAdversario));

        // Banner de Lances
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
        painelCentral.add(Box.createVerticalStrut(10));


        add(painelCentral, BorderLayout.CENTER);

        // --- FOOTER ---
        JPanel footer = new JPanel(new GridLayout(1, 3, 10, 0));
        footer.setBackground(new Color(15, 25, 45));
        footer.setPreferredSize(new Dimension(0, 50));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btnTaticas = criarBotaoRodape("TÁTICAS", new Color(35, 50, 80));
        btnVerAdversario = criarBotaoRodape("ADVERSÁRIO", new Color(50, 50, 55));
        btnAcao = criarBotaoRodape("INICIAR PARTIDA", new Color(0, 90, 55));

        btnAcao.addActionListener(e -> {
            String estadoTempo = motor.getTempo();

            if (estadoTempo.equals("Encerrada")) {
                //dashboard.voltarAoMenu(); 
                return;
            }

            if (timer == null) { 
                motor.iniciarCronometro();
                timer = new Timer(100, event -> atualizarInterface());
                timer.start();
            } 

            else if (!motor.isPausado()) {
                motor.pausarJogo();
            } 
 
 
            else {
                motor.retomarJogo();
            }
        });

        footer.add(btnTaticas); footer.add(btnVerAdversario); footer.add(btnAcao);
        add(footer, BorderLayout.SOUTH);
        preencherTabelasVisuais();
    }

    private void preencherTabelasVisuais() {
    // Limpa as tabelas antes de preencher (evita duplicar se chamar mais de uma vez)
    modeloMeuTime.setRowCount(0);
    modeloAdversario.setRowCount(0);

    // Preencher Time da Casa (Meu Time)
    for (Jogador j : motor.getTitularesCasa()) {
        modeloMeuTime.addRow(new Object[]{
            "", //será a posição da formação definida pela tática
            "["+ j.getPosicao() +"] " + j.getNome(),
            "icones", //serão os icones referente as ações de gol, lesão, etc
        });
    }

    // Preencher Time Visitante (Adversário)
    for (Jogador j : motor.getTitularesVisitante()) {
        modeloAdversario.addRow(new Object[]{
            "", //será a posição da formação definida pela tática
            "["+ j.getPosicao() +"] " + j.getNome(),
            "icones", //serão os icones referente as ações de gol, lesão, etc
        });
    }
    }

    private JScrollPane configurarTabelaEstilizada(JTable tabela) {
        tabela.setBackground(COR_FUNDO); 
        tabela.setRowHeight(20);
        tabela.setShowGrid(false);
        tabela.setTableHeader(null); 
        
        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                c.setBackground(row % 2 == 0 ? COR_ZEBRA : COR_FUNDO);
                c.setForeground(new Color(220, 220, 220));
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
        b.setFont(new Font("Segoe UI", Font.BOLD, 10));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        return b;
    }

    private void atualizarInterface() {
    lblCronometro.setText(motor.getCronometroFormatado());
    lblStatusTempo.setText(motor.getTempo());
    lblPlacar.setText(motor.getPlacarFormatado());

    String estado = motor.getTempo();

    if (motor.getTempo().equals("Encerrada")) {
        btnAcao.setText("AVANÇAR");
        btnAcao.setBackground(new Color(0, 0, 225)); // Cinza para fim de jogo
    } 
    else if (motor.getTempo().equals("Intervalo")) {
        btnAcao.setText("INICIAR 2º TEMPO");
        btnAcao.setBackground(new Color(0, 90, 55)); // Verde
    } 
    else if (motor.isPausado()) {
        btnAcao.setText("RETOMAR");
        btnAcao.setBackground(new Color(0, 90, 55)); // Verde
    } 
    else {
        btnAcao.setText("PAUSAR");
        btnAcao.setBackground(new Color(165, 42, 42)); // Vermelho/Marrom
    }
}


}
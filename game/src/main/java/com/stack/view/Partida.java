package com.stack.view;

import java.awt.*;
import javax.swing.*;

public class Partida extends JPanel {
    private Dashboard dashboard;
    private JLabel lblCronometro;
    private JLabel lblStatusTempo;
    private JLabel lblLance;
    private JButton btnAcao;
    private Timer timer;
    private int segundosCorridos = 0;
    private boolean isSegundoTempo = false;
    private String taticaAtual = "4-4-2";

    public Partida(Dashboard dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout());
        setBackground(new Color(10, 15, 30));

        // PAINEL SUPERIOR
        JPanel headerSuperior = new JPanel();
        headerSuperior.setLayout(new BoxLayout(headerSuperior, BoxLayout.Y_AXIS));
        headerSuperior.setBackground(new Color(15, 25, 45));
        headerSuperior.setBorder(BorderFactory.createEmptyBorder(20, 0, 15, 0));

        // Placar
        JPanel linhaPlacar = new JPanel(new GridLayout(1, 3));
        linhaPlacar.setOpaque(false);
        JLabel timeA = new JLabel("MEU TIME", SwingConstants.CENTER);
        timeA.setForeground(Color.WHITE);
        timeA.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel placar = new JLabel("0 - 0", SwingConstants.CENTER);
        placar.setForeground(new Color(226, 242, 0));
        placar.setFont(new Font("Segoe UI", Font.BOLD, 36));
        JLabel timeB = new JLabel("FLAMENGO", SwingConstants.CENTER);
        timeB.setForeground(Color.WHITE);
        timeB.setFont(new Font("Segoe UI", Font.BOLD, 16));
        linhaPlacar.add(timeA);
        linhaPlacar.add(placar);
        linhaPlacar.add(timeB);
        headerSuperior.add(linhaPlacar);

        // Cronômetro
        lblCronometro = new JLabel("0\"", SwingConstants.CENTER);
        lblCronometro.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblCronometro.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblCronometro.setForeground(Color.WHITE);

        lblStatusTempo = new JLabel("PRÉ-JOGO");
        lblStatusTempo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblStatusTempo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatusTempo.setForeground(new Color(150, 160, 180));

        headerSuperior.add(Box.createRigidArea(new Dimension(0, 10)));
        headerSuperior.add(lblCronometro);
        headerSuperior.add(lblStatusTempo);
        add(headerSuperior, BorderLayout.NORTH);

        // ÁREA CENTRAL (CARD DE LANCES - PRETO E FINO)
        JPanel painelCentral = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 25));
        painelCentral.setOpaque(false);
        JPanel cardLance = new JPanel(new BorderLayout());
        cardLance.setPreferredSize(new Dimension(430, 80));
        cardLance.setBackground(Color.BLACK);
        lblLance = new JLabel("<html><center>As equipes estão entrando em campo...</center></html>", SwingConstants.CENTER);
        lblLance.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblLance.setForeground(new Color(180, 180, 180));
        cardLance.add(lblLance, BorderLayout.CENTER);
        painelCentral.add(cardLance);
        add(painelCentral, BorderLayout.CENTER);

        // BOTÃO MULTIFUNCIONAL
        btnAcao = new JButton("DAR O PONTAPÉ INICIAL");
        btnAcao.setPreferredSize(new Dimension(448, 80));
        btnAcao.setBackground(new Color(0, 150, 100));
        btnAcao.setForeground(Color.WHITE);
        btnAcao.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAcao.setFocusPainted(false);
        btnAcao.setBorderPainted(false);
        btnAcao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAcao.addActionListener(e -> gerenciarBotao());
        add(btnAcao, BorderLayout.SOUTH);
    }

    private void gerenciarBotao() {
        String texto = btnAcao.getText();
        if (texto.equals("DAR O PONTAPÉ INICIAL") || texto.contains("RETOMAR")) {
            iniciarOuRetomar();
        } else if (texto.equals("PAUSAR PARTIDA")) {
            pausarPartida();
            abrirModalTaticas(); // Abre a modal logo após pausar
        } else if (texto.equals("VOLTAR PARA O MENU")) {
            resetarEstadoPartida();
            dashboard.voltarParaMenu();
        }
    }

    private void iniciarOuRetomar() {
        lblStatusTempo.setText(isSegundoTempo ? "2º TEMPO" : "1º TEMPO");
        btnAcao.setText("PAUSAR PARTIDA");
        btnAcao.setBackground(new Color(120, 50, 20)); // Laranja escuro / Marrom
        if (timer == null) timer = new Timer(1000, e -> atualizarTempo());
        timer.start();
    }

    private void pausarPartida() {
        if (timer != null) timer.stop();
        btnAcao.setText("RETOMAR PARTIDA");
        btnAcao.setBackground(new Color(0, 150, 100)); // Volta para verde
    }

    private void abrirModalTaticas() {
        String[] opcoes = {"4-4-2", "4-3-3", "5-3-2"};
        JComboBox<String> combo = new JComboBox<>(opcoes);
        combo.setSelectedItem(taticaAtual);

        int result = JOptionPane.showConfirmDialog(this, combo, "AJUSTE TÁTICO", 
                     JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            taticaAtual = (String) combo.getSelectedItem();
            lblLance.setText("<html><center>Alteração feita! O time agora joga no " + taticaAtual + ".</center></html>");
        }
    }

    private void atualizarTempo() {
        segundosCorridos++;
        int tempoExibicao = isSegundoTempo ? segundosCorridos - 48 : segundosCorridos;

        // Lógica: 1-45 (Número), 46-47 (Acréscimos), 48 (0")
        if (tempoExibicao <= 45) {
            lblCronometro.setText(tempoExibicao + "\"");
        } else if (tempoExibicao <= 47) {
            lblCronometro.setText("ACRÉSCIMOS");
        } else if (tempoExibicao == 48) {
            lblCronometro.setText("0\"");
            if (!isSegundoTempo) finalizarPrimeiroTempo();
            else finalizarPartida();
        }
    }

    private void finalizarPrimeiroTempo() {
        timer.stop();
        lblStatusTempo.setText("INTERVALO");
        lblLance.setText("<html><center>Fim do primeiro tempo! Equipes em intervalo.</center></html>");
        isSegundoTempo = true;
        btnAcao.setText("RETOMAR PARTIDA (2º TEMPO)");
        btnAcao.setBackground(new Color(0, 150, 100));
    }

    private void finalizarPartida() {
        timer.stop();
        lblStatusTempo.setText("ENCERRADA");
        lblCronometro.setText("FIM DE JOGO");
        lblLance.setText("<html><center>Partida encerrada. Obrigado por acompanhar!</center></html>");
        btnAcao.setText("VOLTAR PARA O MENU");
        btnAcao.setBackground(new Color(40, 45, 60));
    }

    private void resetarEstadoPartida() {
        segundosCorridos = 0;
        isSegundoTempo = false;
        if (timer != null) timer.stop();
        lblCronometro.setText("0\"");
        lblStatusTempo.setText("PRÉ-JOGO");
        lblLance.setText("<html><center>As equipes estão entrando em campo...</center></html>");
        btnAcao.setText("DAR O PONTAPÉ INICIAL");
        btnAcao.setBackground(new Color(0, 150, 100));
    }
}
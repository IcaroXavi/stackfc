package com.stack.view;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.*;

public class Elenco extends JPanel {

    private JTable tabela;
    private DefaultTableModel modelo;

    public Elenco() {
        setLayout(new BorderLayout());
        setBackground(new Color(10, 15, 30));
        setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // --- TITULO FORMATADO PADRÃO DASHBOARD ---
        JPanel painelSuperior = new JPanel();
        painelSuperior.setLayout(new BoxLayout(painelSuperior, BoxLayout.Y_AXIS));
        painelSuperior.setOpaque(false);
        painelSuperior.add(Box.createRigidArea(new Dimension(0, 8)));
        
        JLabel lblTitulo = new JLabel("ELENCO", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(new Color(150, 160, 180));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(40, 50, 80)),
            BorderFactory.createEmptyBorder(5, 0, 5, 0)
        ));
        lblTitulo.setMaximumSize(new Dimension(500, 30));
        
        painelSuperior.add(lblTitulo);
        painelSuperior.add(Box.createRigidArea(new Dimension(0, 5)));
        add(painelSuperior, BorderLayout.NORTH);

        String[] colunas = {"POS", "NOME", "IDD", "DEF", "ATQ", "OVR", "SAL", "VLR"};
        modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex >= 2 && columnIndex <= 5) return Integer.class;
                return String.class;
            }
        };

        tabela = new JTable(modelo);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getTableHeader().setResizingAllowed(false);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        
        configurarEstiloTabela();
        configurarDimensoesColunas();
        
        tabela.setRowSorter(new TableRowSorter<>(modelo));
        carregarDadosCSV();

        // --- LISTENER DE CLIQUE (CORRIGIDO: NO CONSTRUTOR) ---
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tabela.rowAtPoint(e.getPoint());
                int col = tabela.columnAtPoint(e.getPoint());
                if (col == 1 && row != -1) {
                    int modelRow = tabela.convertRowIndexToModel(row);
                    String nome = modelo.getValueAt(modelRow, 1).toString();
                    String ovr = modelo.getValueAt(modelRow, 5).toString();
                    String pos = modelo.getValueAt(modelRow, 0).toString();
                    abrirModalJogador(nome, pos, ovr);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(40, 50, 80)));
        scroll.getViewport().setBackground(new Color(15, 20, 35));
        
        JPanel corner = new JPanel();
        corner.setBackground(new Color(20, 25, 45)); 
        scroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corner);

        scroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(60, 70, 90);
                this.trackColor = new Color(15, 20, 35);
            }
            @Override
            protected JButton createDecreaseButton(int orientation) { return criarBotaoVazio(); }
            @Override
            protected JButton createIncreaseButton(int orientation) { return criarBotaoVazio(); }
            private JButton criarBotaoVazio() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                return b;
            }
        });
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

        add(scroll, BorderLayout.CENTER);
    }

    private void configurarDimensoesColunas() {
        TableColumnModel cm = tabela.getColumnModel();
        int[] larguras = {35, 116, 35, 35, 35, 35, 45, 46}; 
        for (int i = 0; i < larguras.length; i++) {
            cm.getColumn(i).setPreferredWidth(larguras[i]);
            cm.getColumn(i).setMinWidth(larguras[i]);
            cm.getColumn(i).setMaxWidth(larguras[i]);
        }
    }

    private void configurarEstiloTabela() {
        tabela.setBackground(new Color(15, 20, 35));
        tabela.setForeground(Color.WHITE);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.setRowHeight(32);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = tabela.getTableHeader();
        header.setOpaque(false);
        header.setBackground(new Color(20, 25, 45));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(new Color(20, 25, 45));
                setForeground(new Color(180, 190, 210));
                setFont(new Font("Segoe UI", Font.BOLD, 11));
                setHorizontalAlignment(JLabel.CENTER);
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(60, 70, 100)));
                return this;
            }
        });

        DefaultTableCellRenderer renderizadorCustom = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                JComponent jc = (JComponent) c;

                // Zebra
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(25, 35, 55) : new Color(15, 20, 35));
                } else {
                    c.setBackground(new Color(0, 100, 180));
                }

                // Estilo Link na coluna Nome
                if (column == 1 && value != null) {
                    setText("<html><u><font color='#64B4FF'>" + value.toString() + "</font></u></html>");
                    setHorizontalAlignment(JLabel.LEFT);
                    jc.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(40, 50, 70, 100)),
                        BorderFactory.createEmptyBorder(0, 8, 0, 0)));
                } else {
                    setForeground(Color.WHITE);
                    setText(value != null ? value.toString() : "");
                    setHorizontalAlignment(JLabel.CENTER);
                    jc.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(40, 50, 70, 100)));
                }

                return c;
            }
        };

        for (int i = 0; i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(renderizadorCustom);
        }
    }

    private void carregarDadosCSV() {
        String caminho = "C:\\Projetos\\Stack\\game\\src\\main\\java\\com\\stack\\repository\\jogadores.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] d = linha.split(",");
                if (d.length >= 8 && d[7].trim().equals("1")) {
                    Vector<Object> row = new Vector<>();
                    row.add(abreviarPosicao(d[2].trim())); 
                    row.add(d[1].trim().toUpperCase());      
                    row.add(Integer.parseInt(d[3].trim()));  
                    row.add(Integer.parseInt(d[4].trim()));  
                    row.add(Integer.parseInt(d[5].trim()));  
                    row.add(Integer.parseInt(d[6].trim()));  
                    row.add("-"); 
                    row.add("-"); 
                    modelo.addRow(row);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    private String abreviarPosicao(String pos) {
        pos = pos.toLowerCase();
        if (pos.contains("goleiro")) return "GL";
        if (pos.contains("zagueiro")) return "ZG";
        if (pos.contains("lateral direito")) return "LD";
        if (pos.contains("lateral esquerdo")) return "LE";
        if (pos.contains("meio campo")) return "MC";
        if (pos.contains("atacante")) return "AT";
        return pos.toUpperCase().substring(0, Math.min(pos.length(), 2));
    }

    private void abrirModalJogador(String nome, String pos, String ovr) {
        JDialog modal = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ficha do Jogador", true);
        modal.setLayout(new BorderLayout());
        modal.getContentPane().setBackground(new Color(15, 20, 35));
        modal.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(40, 50, 80), 2));

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel lblNome = new JLabel(nome);
        lblNome.setForeground(new Color(100, 180, 255));
        lblNome.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        JLabel lblPos = new JLabel("Posição: " + pos);
        lblPos.setForeground(Color.WHITE);
        
        JLabel lblOvr = new JLabel("Overall: " + ovr);
        lblOvr.setForeground(new Color(255, 215, 0)); 
        lblOvr.setFont(new Font("Segoe UI", Font.BOLD, 14));

        infoPanel.add(lblNome);
        infoPanel.add(lblPos);
        infoPanel.add(lblOvr);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        btnPanel.setOpaque(false);

        JButton btnAcao1 = criarBotaoModal("DISPENSAR", new Color(180, 50, 50));
        JButton btnAcao2 = criarBotaoModal("RENOVAR", new Color(50, 150, 80));

        btnAcao1.addActionListener(e -> modal.dispose());
        btnAcao2.addActionListener(e -> modal.dispose());

        btnPanel.add(btnAcao1);
        btnPanel.add(btnAcao2);

        modal.add(infoPanel, BorderLayout.CENTER);
        modal.add(btnPanel, BorderLayout.SOUTH);

        modal.pack();
        modal.setSize(350, 250);
        modal.setLocationRelativeTo(this);
        modal.setResizable(false);
        modal.setVisible(true);
    }

    private JButton criarBotaoModal(String texto, Color corFundo) {
        JButton btn = new JButton(texto);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBackground(corFundo);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setPreferredSize(new Dimension(100, 30));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
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
        
        JLabel lblTitulo = new JLabel("GESTÃO DE ELENCO", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(new Color(150, 160, 180));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(40, 50, 80)),
            BorderFactory.createEmptyBorder(5, 0, 5, 0)
        ));
        lblTitulo.setMaximumSize(new Dimension(500, 30));
        
        painelSuperior.add(lblTitulo);
        painelSuperior.add(Box.createRigidArea(new Dimension(0, 15)));
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
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        configurarEstiloTabela();
        configurarDimensoesColunas();
        
        tabela.setRowSorter(new TableRowSorter<>(modelo));
        carregarDadosCSV();

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(40, 50, 80)));
        scroll.getViewport().setBackground(new Color(15, 20, 35));
        
        // --- CUSTOMIZAÇÃO DA BARRA DE ROLAGEM ---
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
        int[] larguras = {35, 120, 35, 35, 35, 35, 40, 40}; 
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

        // Cabeçalho Escuro com Bordas
        JTableHeader header = tabela.getTableHeader();
        header.setBackground(new Color(10, 15, 25));
        header.setForeground(new Color(180, 190, 210));
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 70, 100)));

        // Renderizador Zebra Realçado com Bordas Laterais
        DefaultTableCellRenderer renderizadorCustom = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    // Zebra realçado (contraste maior entre as linhas)
                    c.setBackground(row % 2 == 0 ? new Color(25, 35, 55) : new Color(15, 20, 35));
                } else {
                    c.setBackground(new Color(0, 100, 180));
                }

                // Borda fina na direita de cada célula para separar colunas
                JComponent jc = (JComponent) c;
                jc.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(40, 50, 70, 100)));

                if (column == 1) { 
                    setHorizontalAlignment(JLabel.LEFT);
                    jc.setBorder(BorderFactory.createCompoundBorder(
                        jc.getBorder(), BorderFactory.createEmptyBorder(0, 8, 0, 0)));
                } else {
                    setHorizontalAlignment(JLabel.CENTER);
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
}
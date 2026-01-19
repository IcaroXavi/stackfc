package com.stack.view;

import com.stack.model.Jogador;
import java.awt.*;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.*;

public class TelaElenco extends JPanel {

    private JTable tabela;
    private DefaultTableModel modelo;

    public TelaElenco() {
        setLayout(new BorderLayout());
        setBackground(new Color(10, 15, 30));
        setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // --- TÍTULO FORMATADO ---
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

        // --- CONFIGURAÇÃO DA TABELA ---
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
        
        configurarEstiloTabela();
        configurarDimensoesColunas();
        tabela.setRowSorter(new TableRowSorter<>(modelo));

        // --- SCROLL PANE ESTILIZADO ---
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(40, 50, 80)));
        scroll.getViewport().setBackground(new Color(15, 20, 35));
        scroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                this.thumbColor = new Color(60, 70, 90);
                this.trackColor = new Color(15, 20, 35);
            }
            @Override protected JButton createDecreaseButton(int orientation) { return new JButton() {{ setPreferredSize(new Dimension(0,0)); }}; }
            @Override protected JButton createIncreaseButton(int orientation) { return new JButton() {{ setPreferredSize(new Dimension(0,0)); }}; }
        });
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

        add(scroll, BorderLayout.CENTER);
    }

    // --- MÉTODO QUE O CONTROLLER CHAMA (DADOS REAIS) ---
    public void atualizarTabela(List<Jogador> lista) {
        modelo.setRowCount(0);
        for (Jogador j : lista) {
            Vector<Object> row = new Vector<>();
            row.add(j.getPos().toUpperCase());  
            row.add(j.getNome().toUpperCase());      
            row.add(j.getIdade());  
            row.add(j.getDefesa()); // Valor real do Model
            row.add(j.getAtaque()); // Valor real do Model
            row.add(j.getTotal());  
            row.add("-"); 
            row.add("-"); 
            modelo.addRow(row);
        }
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

        tabela.getTableHeader().setBackground(new Color(20, 25, 45));
        tabela.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(new Color(20, 25, 45));
                setForeground(new Color(180, 190, 210));
                setFont(new Font("Segoe UI", Font.BOLD, 11));
                setHorizontalAlignment(JLabel.CENTER);
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(60, 70, 100)));
                return this;
            }
        });

        DefaultTableCellRenderer renderCustom = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(25, 35, 55) : new Color(15, 20, 35));
                } else {
                    c.setBackground(new Color(0, 100, 180));
                }
                
                if (column == 1) { // Nome Azul
                    setText("<html><u><font color='#64B4FF'>" + (value != null ? value.toString() : "") + "</font></u></html>");
                }
                setHorizontalAlignment(column == 1 ? JLabel.LEFT : JLabel.CENTER);
                ((JComponent)c).setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(40, 50, 70, 100)));
                return c;
            }
        };

        for (int i = 0; i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(renderCustom);
        }
    }

}
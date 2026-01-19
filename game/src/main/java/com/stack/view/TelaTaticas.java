package com.stack.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.stack.model.Jogador;

public class TelaTaticas extends JPanel {

    private JTable tabelaTitulares, tabelaReservas, tabelaDisponiveis;
    private DefaultTableModel modeloTitulares, modeloReservas, modeloDisponiveis;
    private JComboBox<String> comboFormacao, comboMentalidade;
    private JLabel lblMediaDef, lblMediaAtq, lblMediaOvr;
    private JButton btnJogar, btnVoltar;

    private String nomeArrastado = "";
    private Point pontoMouseGlass = new Point(0, 0);
    private boolean arrastando = false;
    private JTable tabelaAtiva = null; 
    private int linhaAtiva = -1;      

    private final Color COR_FUNDO = new Color(10, 15, 30);
    private final Color COR_ZEBRA = new Color(20, 25, 45);
    private final Color COR_AMARELO = new Color(225, 225, 0);
    private final Color COR_VERDE = new Color(0, 255, 127);
    private final Color COR_LARANJA = new Color(255, 69, 0);
    private final Color COR_HOVER = new Color(40, 60, 100); 
    private final Color COR_DROP_ZONE = new Color(60, 100, 180); 
    private final Color COR_GLASS = new Color(60, 80, 160, 220);
    private final Color COR_CINZA_CLARO = new Color(130, 130, 130);
    private final Color COR_SILVER = new Color(200, 200, 200);

    public void atualizarConfiguracoes(String formacao, String postura) {
        comboFormacao.setSelectedItem(formacao);
        comboMentalidade.setSelectedItem(postura);
        configurarVagasDinamicas();
    }

    public TelaTaticas() {
        setLayout(null);
        setBackground(COR_FUNDO);
        // Agora o tamanho preferencial condiz com a tela cheia
        setPreferredSize(new Dimension(448, 800));

    // --- 1. TOP BAR ORGANIZADA (Labels de Identificação) ---

    // 1.1 Títulos das Seleções
    JLabel lblTituloForm = criarLabelTitulo("FORMAÇÃO", 12, 15);
    add(lblTituloForm);

    comboFormacao = new JComboBox<>(new String[]{"4-5-1", "4-4-2", "4-3-3", "3-5-2", "3-4-3", "5-4-1", "5-3-2"});
    comboFormacao.setBounds(12, 32, 60, 25);
    comboFormacao.addActionListener(e -> configurarVagasDinamicas());
    add(comboFormacao);

    JLabel lblTituloPostura = criarLabelTitulo("POSTURA", 80, 15);
    add(lblTituloPostura);

    comboMentalidade = new JComboBox<>(new String[]{"Defensiva", "Equilibrada", "Ofensiva"});
    comboMentalidade.setBounds(80, 32, 100, 25);
    add(comboMentalidade);

    lblMediaDef = criarLabelStat("DEFESA: 0", 190, 33);
    lblMediaAtq = criarLabelStat("ATAQUE: 0", 265, 33);
    lblMediaOvr = criarLabelStat("OVERALL: 0", 345, 33);
    add(lblMediaDef); add(lblMediaAtq); add(lblMediaOvr);

    // --- 2. TABELAS (RECALCULADAS PARA 800PX) ---
    // Titulares (Espaço nobre)
    modeloTitulares = criarModeloBloqueado(new String[]{"ID", "POS", "JOGADOR", "DEFESA", "ATAQUE", "OVERALL", "ENERGIA"}, 11);
    tabelaTitulares = criarTabelaCustom(modeloTitulares);
    JScrollPane spTitulares = criarScroll(tabelaTitulares, "TITULARES");
    spTitulares.setBounds(10, 67, 412, 240);
    add(spTitulares);

    // Reservas
    modeloReservas = criarModeloBloqueado(new String[]{"ID", "POS", "JOGADOR", "DEFESA", "ATAQUE", "OVERALL", "ENERGIA"}, 6);
    tabelaReservas = criarTabelaCustom(modeloReservas);
    JScrollPane spReservas = criarScroll(tabelaReservas, "BANCO DE RESERVAS");
    spReservas.setBounds(10, 315, 412, 152);
    add(spReservas);

    // Disponíveis (Aumentada para ocupar o resto do espaço)
    modeloDisponiveis = criarModeloBloqueado(new String[]{"ID", "POS", "JOGADOR", "DEFESA", "ATAQUE", "OVERALL", "ENERGIA"}, 11);
    tabelaDisponiveis = criarTabelaCustom(modeloDisponiveis);
    JScrollPane spDisp = criarScroll(tabelaDisponiveis, "NÃO RELACIONADOS");
    spDisp.setBounds(10, 475, 412, 240);
    add(spDisp);

    // --- 3. RODAPÉ ---
    btnVoltar = criarBotaoBase("VOLTAR", new Color(60, 60, 70));
    btnVoltar.setBounds(12, 720, 190, 30);
    add(btnVoltar);

    btnJogar = criarBotaoBase("CONFIRMAR E JOGAR", new Color(0, 120, 60));
    btnJogar.setBounds(230, 720, 190, 30);
    add(btnJogar);

    configurarVagasDinamicas();
    configurarEventosInterativos();
    }

    // Método para criar os títulos pequenos (Cinza)
    private JLabel criarLabelTitulo(String txt, int x, int y) {
        JLabel l = new JLabel(txt);
        l.setBounds(x, y, 90, 15);
        l.setForeground(COR_CINZA_CLARO); // Usa a cor cinza que você já definiu
        l.setFont(new Font("Segoe UI", Font.BOLD, 10)); // Fonte menor para não poluir
        return l;
    }
    // GETTERS PARA O CONTROLLER
    public JTable getTabelaTitulares() { return tabelaTitulares; }
    public JTable getTabelaReservas() { return tabelaReservas; }
    public JTable getTabelaDisponiveis() { return tabelaDisponiveis; }
    public JButton getBtnJogar() { return btnJogar; }
    public JButton getBtnVoltar() { return btnVoltar; }
    public JComboBox<String> getComboFormacao() {
    return comboFormacao;
    }


    public JComboBox<String> getComboMentalidade() {
            return comboMentalidade;
    }

    public void setEstrategia(String formacao, String postura) {
        if (formacao != null && !formacao.isEmpty()) {
            // Desmarcar temporariamente o listener se necessário, 
            // mas o setSelectedItem já deve resolver se a string for idêntica ("4-3-3")
            comboFormacao.setSelectedItem(formacao);
        }
        if (postura != null && !postura.isEmpty()) {
            comboMentalidade.setSelectedItem(postura);
        }
        configurarVagasDinamicas();
    }
    private JTable criarTabelaCustom(DefaultTableModel m) {
        JTable t = new JTable(m);
        t.getTableHeader().setPreferredSize(new Dimension(0, 22));
        t.getTableHeader().setReorderingAllowed(false);
        t.getTableHeader().setResizingAllowed(false);
        t.getColumnModel().getColumn(0).setMinWidth(0);
        t.getColumnModel().getColumn(0).setMaxWidth(0);
        t.getColumnModel().getColumn(0).setPreferredWidth(18);  // ID
        t.getColumnModel().getColumn(1).setPreferredWidth(18);  // POS
        t.getColumnModel().getColumn(2).setPreferredWidth(110); // [PO] Nome do Jogador
        t.getColumnModel().getColumn(3).setPreferredWidth(30);  // DEF
        t.getColumnModel().getColumn(4).setPreferredWidth(30);  // ATQ
        t.getColumnModel().getColumn(5).setPreferredWidth(30);  // OVERALL
        t.getColumnModel().getColumn(6).setPreferredWidth(30);  // ENERGIA
        t.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(new Color(30, 40, 65));
                setForeground(COR_CINZA_CLARO);
                setFont(new Font("Segoe UI", Font.BOLD, 11));
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.darkGray));
                setHorizontalAlignment(JLabel.CENTER);
                return this;
            }
        });

        t.setBackground(COR_FUNDO);
        t.setRowHeight(17); // Altura das linhas
        t.setShowGrid(false);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, val, false, false, row, col);
                
                // Fontes base
                Font fonteNormal = new Font("Segoe UI", Font.PLAIN, 13);
                Font fonteDestaque = new Font("Segoe UI", Font.BOLD, 13);
                c.setFont(fonteNormal);

                // 1. LÓGICA DE FUNDO (Zebra e Hover)
                if (table == tabelaAtiva && row == linhaAtiva) {
                    c.setBackground(arrastando ? COR_DROP_ZONE : COR_HOVER);
                } else {
                    c.setBackground(row % 2 == 0 ? COR_FUNDO : COR_ZEBRA);
                }

                // 2. LOGICA DE CORES E VALIDAÇÃO
                if (table == tabelaTitulares) {
                    Object nomeObj = table.getValueAt(row, 2);
                    String posTatica = table.getValueAt(row, 1).toString();
                    boolean foraDePosicao = (nomeObj != null && !nomeObj.toString().isEmpty()) 
                                            && estaForaDePosicao(posTatica, nomeObj.toString());

                    if (col == 1) {
                        // A COLUNA DA POSIÇÃO É SEMPRE VERDE NOS TITULARES
                        c.setForeground(COR_VERDE);
                    } else {
                        if (foraDePosicao) {
                            // RESTANTE DAS COLUNAS FICA VERMELHO E NEGRITO
                            c.setForeground(new Color(255, 80, 80));
                            c.setFont(fonteDestaque);
                        } else {
                            c.setForeground(Color.WHITE);
                        }
                    }
                } else {
                    // Lógica para Reservas e Disponíveis (Mantida)
                    if (col == 1) {
                        if (table == tabelaReservas) c.setForeground(COR_AMARELO);
                        else if (table == tabelaDisponiveis) c.setForeground(COR_LARANJA);
                    } else {
                        c.setForeground(Color.WHITE);
                    }
                }

                // Alinhamento
                c.setHorizontalAlignment(col == 2 ? JLabel.LEADING : JLabel.CENTER);
                c.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                
                return c;
            }
        });
        return t;
    }

    private JScrollPane criarScroll(JTable t, String tit) {
        JScrollPane s = new JScrollPane(t);
        
        // Criamos uma borda de linha simples com a cor azulada/cinza
        javax.swing.border.Border linhaBorda = BorderFactory.createLineBorder(new Color(50, 60, 90));
        
        // Criamos a TitledBorder com as especificações de cores
        javax.swing.border.TitledBorder bordaTitulo = BorderFactory.createTitledBorder(
            linhaBorda, 
            tit, 
            javax.swing.border.TitledBorder.LEADING, 
            javax.swing.border.TitledBorder.TOP, 
            new Font("Segoe UI", Font.BOLD, 11), 
            Color.WHITE // Cor do texto do título
        );

        s.setBorder(bordaTitulo);
        s.getViewport().setBackground(COR_FUNDO);
        s.setBackground(COR_FUNDO); // Garante que o fundo da borda seja o mesmo do painel
        s.setOpaque(false); // Ajuda a evitar que o fundo branco padrão do Swing vaze
        
        return s;
    }

    private void configurarEventosInterativos() {
        MouseAdapter interacaoAdapter = new MouseAdapter() {
            private JTable tabelaOrigem = null;
            private int linhaOrigem = -1;

            @Override
            public void mousePressed(MouseEvent e) {
                tabelaOrigem = (JTable) e.getSource();
                linhaOrigem = tabelaOrigem.rowAtPoint(e.getPoint());
                if (linhaOrigem != -1) {
                    int colNome = 2;
                    Object val = tabelaOrigem.getValueAt(linhaOrigem, colNome);
                    if (val != null && !val.toString().trim().isEmpty()) {
                        nomeArrastado = val.toString();
                        arrastando = true;
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                tabelaAtiva = (JTable) e.getSource();
                linhaAtiva = tabelaAtiva.rowAtPoint(e.getPoint());
                repaintTodasTabelas();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (!arrastando) return;
                pontoMouseGlass = SwingUtilities.convertPoint((Component)e.getSource(), e.getPoint(), TelaTaticas.this.getRootPane());
                Point pPainel = SwingUtilities.convertPoint((Component)e.getSource(), e.getPoint(), TelaTaticas.this);
                Component compAbaixo = SwingUtilities.getDeepestComponentAt(TelaTaticas.this, pPainel.x, pPainel.y);
                if (compAbaixo instanceof JTable) {
                    tabelaAtiva = (JTable) compAbaixo;
                    linhaAtiva = tabelaAtiva.rowAtPoint(SwingUtilities.convertPoint(TelaTaticas.this, pPainel, tabelaAtiva));
                } else {
                    tabelaAtiva = null; linhaAtiva = -1;
                }
                TelaTaticas.this.getRootPane().getGlassPane().setVisible(true);
                TelaTaticas.this.getRootPane().getGlassPane().repaint();
                repaintTodasTabelas();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (arrastando && tabelaAtiva != null && linhaAtiva != -1) {
                    executarSwap(tabelaOrigem, linhaOrigem, tabelaAtiva, linhaAtiva);
                }
                arrastando = false; nomeArrastado = "";
                TelaTaticas.this.getRootPane().getGlassPane().setVisible(false);
                repaintTodasTabelas();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!arrastando) { tabelaAtiva = null; linhaAtiva = -1; repaintTodasTabelas(); }
            }
        };

        for (JTable t : new JTable[]{tabelaTitulares, tabelaReservas, tabelaDisponiveis}) {
            t.addMouseListener(interacaoAdapter);
            t.addMouseMotionListener(interacaoAdapter);
        }
        configurarGlassPane();
    }

    private void configurarGlassPane() {
        this.addHierarchyListener(e -> {
            JRootPane root = SwingUtilities.getRootPane(this);
            if (root != null) {
                root.setGlassPane(new JComponent() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        if (arrastando && !nomeArrastado.isEmpty()) {
                            Graphics2D g2 = (Graphics2D) g;
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(COR_GLASS);
                            g2.fillRoundRect(pontoMouseGlass.x + 15, pontoMouseGlass.y - 10, 110, 26, 8, 8);
                            g2.setColor(Color.WHITE);
                            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                            g2.drawString(nomeArrastado, pontoMouseGlass.x + 23, pontoMouseGlass.y + 7);
                        }
                    }
                });
            }
        });
    }

    private void repaintTodasTabelas() {
        tabelaTitulares.repaint(); tabelaReservas.repaint(); tabelaDisponiveis.repaint();
    }

    private void executarSwap(JTable t1, int r1, JTable t2, int r2) {
        DefaultTableModel m1 = (DefaultTableModel) t1.getModel();
        DefaultTableModel m2 = (DefaultTableModel) t2.getModel();
        
        // Trocamos a Coluna 0 (ID)
        Object tempId = m1.getValueAt(r1, 0);
        m1.setValueAt(m2.getValueAt(r2, 0), r1, 0);
        m2.setValueAt(tempId, r2, 0);

        // PULAMOS A COLUNA 1 (A tática/RES/OUT permanece onde está)

        // Trocamos da Coluna 2 até a 6 (Nome, Def, Atq, Ovr, Energia)
        for (int i = 2; i < 7; i++) { 
            Object temp = m1.getValueAt(r1, i);
            m1.setValueAt(m2.getValueAt(r2, i), r1, i);
            m2.setValueAt(temp, r2, i);
        }
        calcularResumo();
    }

    private DefaultTableModel criarModeloBloqueado(String[] col, int lin) {
        return new DefaultTableModel(col, lin) { @Override public boolean isCellEditable(int r, int c) { return false; } };
    }

    private JButton criarBotaoBase(String txt, Color bg) {
        JButton b = new JButton(txt);
        b.setBackground(bg); b.setForeground(Color.WHITE); b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR)); b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return b;
    }

    private JLabel criarLabelStat(String t, int x, int y) {
        JLabel l = new JLabel(t);
        l.setBounds(x, y, 75, 20); l.setForeground(COR_SILVER); l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        return l;
    }

    public void carregarTaticaInicial(List<Jogador> elenco) {
        for (Jogador j : elenco) {
            String n = "["+j.getPos()+"] "+j.getNome();
            if (j.getOrdem() <= 10) preencherLinha(modeloTitulares, j.getOrdem(), n, j);
            else if (j.getOrdem() <= 16) preencherLinha(modeloReservas, j.getOrdem() - 11, n, j);
            else { int l = j.getOrdem() - 17; if (l < 11) preencherLinha(modeloDisponiveis, l, n, j); }
        }
        calcularResumo();
    }

    private void preencherLinha(DefaultTableModel m, int r, String n, Jogador j) {
        m.setValueAt(j.getId(), r, 0);       // ID SEMPRE NA COLUNA 0
        //m.setValueAt(j.getPos(), r, 1);      // POSIÇÃO NA COLUNA 1
        m.setValueAt(n, r, 2);               // NOME NA COLUNA 2
        m.setValueAt(j.getDefesa(), r, 3);   // DEFESA NA COLUNA 3
        m.setValueAt(j.getAtaque(), r, 4);   // ATAQUE NA COLUNA 4
        m.setValueAt(j.getTotal(), r, 5);    // OVERALL NA COLUNA 5
        m.setValueAt(j.getEnergia()*100+"%", r, 6); // ENERGIA NA COLUNA 6
    }

private void configurarVagasDinamicas() {
    String selected = (String) comboFormacao.getSelectedItem();
    if (selected == null) return;
    String[] p = selected.split("-");
    int qtdDefensores = Integer.parseInt(p[0]);
    int qtdMeios = Integer.parseInt(p[1]);
    int qtdAtacantes = Integer.parseInt(p[2]);
    modeloTitulares.setValueAt("GL", 0, 1);
    int r = 1; 
    if (qtdDefensores >= 4) {
        modeloTitulares.setValueAt("LD", r++, 1); 
        for (int i = 0; i < (qtdDefensores - 2); i++) {
            if (r < 11) modeloTitulares.setValueAt("ZG", r++, 1);
        }   
        modeloTitulares.setValueAt("LE", r++, 1); 
    } else {
        for (int i = 0; i < qtdDefensores; i++) {
            if (r < 11) modeloTitulares.setValueAt("ZG", r++, 1);
        }
    }
    for (int i = 0; i < qtdMeios; i++) {
        if (r < 11) modeloTitulares.setValueAt("MC", r++, 1);
    }
    for (int i = 0; i < qtdAtacantes; i++) {
        if (r < 11) modeloTitulares.setValueAt("AT", r++, 1);
    }
        for(int i=0; i<6; i++) modeloReservas.setValueAt("RES", i, 1);
        for(int i=0; i<11; i++) modeloDisponiveis.setValueAt("OUT", i, 1);
    }
    public void calcularResumo() {
        double totalDef = 0, totalAtq = 0, totalOvr = 0;
        
        for (int i = 0; i < 11; i++) {
            String posTatica = modeloTitulares.getValueAt(i, 1).toString();
            Object nomeObj = modeloTitulares.getValueAt(i, 2);
            
            if (nomeObj != null && !nomeObj.toString().trim().isEmpty()) {
                try {
                    double d = Double.parseDouble(modeloTitulares.getValueAt(i, 3).toString());
                    double a = Double.parseDouble(modeloTitulares.getValueAt(i, 4).toString());
                    double o = Double.parseDouble(modeloTitulares.getValueAt(i, 5).toString());

                    // Aplica penalidade de 50% se estiver fora de posição
                    if (estaForaDePosicao(posTatica, nomeObj.toString())) {
                        d *= 0.5;
                        a *= 0.5;
                        o *= 0.5;
                    }

                    totalDef += d;
                    totalAtq += a;
                    totalOvr += o;
                } catch (Exception e) { /* Ignora linhas vazias */ }
            }
        }
        
        // Exibe como inteiro para manter a interface limpa
        lblMediaDef.setText("DEFESA: " + (int)totalDef);
        lblMediaAtq.setText("ATAQUE: " + (int)totalAtq);
        lblMediaOvr.setText("OVERALL: " + (int)totalOvr);
    }

    private boolean estaForaDePosicao(String posTatica, String nomeJogador) {
        if (nomeJogador == null || !nomeJogador.contains("[") || !nomeJogador.contains("]")) return false;
        
        // Extrai a posição natural, ex: "[AT] Nome" -> "AT"
        String posNatural = nomeJogador.substring(nomeJogador.indexOf("[") + 1, nomeJogador.indexOf("]"));
        
        switch (posTatica) {
            case "GL": return !posNatural.equals("GL");
            case "LD": 
            case "LE": 
            case "ZG": 
                // Defensores podem jogar em qualquer vaga da zaga/lateral sem penalidade (opcional)
                return !posNatural.equals("ZG") && !posNatural.equals("LD") && !posNatural.equals("LE");
            case "MC": return !posNatural.equals("MC");
            case "AT": return !posNatural.equals("AT");
            default: return false; // RES e OUT não aplicam penalidade visual aqui
        }
    }
}
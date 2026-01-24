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


public class TelaSubstituicao extends JPanel {

    private JTable tabelaTitulares, tabelaReservas;
    private DefaultTableModel modeloTitulares, modeloReservas;
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
        if (formacao != null) comboFormacao.setSelectedItem(formacao);
        if (postura != null) comboMentalidade.setSelectedItem(postura);
        
        // Força a atualização visual das siglas (GL, ZG, MC...) 
        configurarVagasDinamicas();
        // Força o cálculo das médias de ATQ/DEF baseadas na postura carregada
        calcularResumo();
    }

    public TelaSubstituicao(List<Jogador> titulares, List<Jogador> reservas) {
        this(); // Chama o construtor vazio para inicializar o layout/componentes
        carregarTaticaInicial(titulares, reservas);
    }

    public TelaSubstituicao() {
        setLayout(null);
        setBackground(COR_FUNDO);
        // Agora o tamanho preferencial condiz com a tela cheia
        setPreferredSize(new Dimension(430, 660));

    // --- 1. TOP BAR ORGANIZADA (Labels de Identificação) ---

    // 1.1 Títulos das Seleções
    JLabel lblTituloForm = criarLabelTitulo("FORMAÇÃO", 12, 18);
    add(lblTituloForm);

    comboFormacao = new JComboBox<>(new String[]{"4-5-1", "4-4-2", "4-3-3", "3-5-2", "3-4-3", "5-4-1", "5-3-2"});
    comboFormacao.setBounds(12, 35, 60, 25);
    comboFormacao.addActionListener(e -> {
        configurarVagasDinamicas();
        calcularResumo();      
        repaintTodasTabelas(); 
    });
    add(comboFormacao);

    JLabel lblTituloPostura = criarLabelTitulo("POSTURA", 80, 18);
    add(lblTituloPostura);

    comboMentalidade = new JComboBox<>(new String[]{"Defensiva", "Equilibrada", "Ofensiva"});
    comboMentalidade.setBounds(80, 35, 100, 25);
    comboMentalidade.addActionListener(e -> {
        calcularResumo();
    });

    add(comboMentalidade);

    lblMediaDef = criarLabelStat("DEFESA: 0", 190, 36);
    lblMediaAtq = criarLabelStat("ATAQUE: 0", 265, 36);
    lblMediaOvr = criarLabelStat("OVERALL: 0", 345, 36);
    add(lblMediaDef); add(lblMediaAtq); add(lblMediaOvr);

    // --- 2. TABELAS (RECALCULADAS PARA 800PX) ---
    // Titulares (Espaço nobre)
    modeloTitulares = criarModeloBloqueado(new String[]{"ID", "POS", "JOGADOR", "DEFESA", "ATAQUE", "OVERALL", "ENERGIA"}, 11);
    tabelaTitulares = criarTabelaCustom(modeloTitulares);
    JScrollPane spTitulares = criarScroll(tabelaTitulares, "TITULARES");
    spTitulares.setBounds(10, 80, 412, 240);
    add(spTitulares);

    // Reservas
    modeloReservas = criarModeloBloqueado(new String[]{"ID", "POS", "JOGADOR", "DEFESA", "ATAQUE", "OVERALL", "ENERGIA"}, 6);
    tabelaReservas = criarTabelaCustom(modeloReservas);
    JScrollPane spReservas = criarScroll(tabelaReservas, "BANCO DE RESERVAS");
    spReservas.setBounds(10, 340, 412, 152);
    add(spReservas);

    // --- 3. RODAPÉ ---
    btnVoltar = criarBotaoBase("CANCELAR", new Color(60, 60, 70));
    btnVoltar.setBounds(12, 520, 190, 30);
    add(btnVoltar);

    btnJogar = criarBotaoBase("CONFIRMAR E JOGAR", new Color(0, 120, 60));
    btnJogar.setBounds(230, 520, 190, 30);
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
                    // Lógica para Reservas 
                    if (col == 1) {
                        if (table == tabelaReservas) c.setForeground(COR_AMARELO);
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
                pontoMouseGlass = SwingUtilities.convertPoint((Component)e.getSource(), e.getPoint(), TelaSubstituicao.this.getRootPane());
                Point pPainel = SwingUtilities.convertPoint((Component)e.getSource(), e.getPoint(), TelaSubstituicao.this);
                Component compAbaixo = SwingUtilities.getDeepestComponentAt(TelaSubstituicao.this, pPainel.x, pPainel.y);
                if (compAbaixo instanceof JTable) {
                    tabelaAtiva = (JTable) compAbaixo;
                    linhaAtiva = tabelaAtiva.rowAtPoint(SwingUtilities.convertPoint(TelaSubstituicao.this, pPainel, tabelaAtiva));
                } else {
                    tabelaAtiva = null; linhaAtiva = -1;
                }
                TelaSubstituicao.this.getRootPane().getGlassPane().setVisible(true);
                TelaSubstituicao.this.getRootPane().getGlassPane().repaint();
                repaintTodasTabelas();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (arrastando && tabelaAtiva != null && linhaAtiva != -1) {
                    executarSwap(tabelaOrigem, linhaOrigem, tabelaAtiva, linhaAtiva);
                }
                arrastando = false; nomeArrastado = "";
                TelaSubstituicao.this.getRootPane().getGlassPane().setVisible(false);
                repaintTodasTabelas();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!arrastando) { tabelaAtiva = null; linhaAtiva = -1; repaintTodasTabelas(); }
            }
        };

        for (JTable t : new JTable[]{tabelaTitulares, tabelaReservas}) {
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
        tabelaTitulares.repaint(); tabelaReservas.repaint();
    }

    protected void executarSwap(JTable t1, int r1, JTable t2, int r2) {
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

    public void carregarTaticaInicial(List<Jogador> titulares, List<Jogador> reservas) {
        // Loop 1: Titulares
        for (Jogador j : titulares) {
            String n = "["+j.getPos()+"] "+j.getNome();
            if (j.getOrdem() <= 10) preencherLinha(modeloTitulares, j.getOrdem(), n, j);
        }
        // Loop 2: Reservas
        for (Jogador j : reservas) {
            String n = "["+j.getPos()+"] "+j.getNome();
            if (j.getOrdem() >= 11 && j.getOrdem() <= 16) {
                preencherLinha(modeloReservas, j.getOrdem() - 11, n, j);
            }
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
    }

    public void calcularResumo() {
    double baseDef = 0, baseAtq = 0, baseOvr = 0;
    
    // 1. CÁLCULO DOS VALORES BASE (com penalidades de posição)
    for (int i = 0; i < 11; i++) {
        Object nomeObj = modeloTitulares.getValueAt(i, 2);
        if (nomeObj != null && !nomeObj.toString().trim().isEmpty()) {
            try {
                String posTatica = modeloTitulares.getValueAt(i, 1).toString();
                double d = Double.parseDouble(modeloTitulares.getValueAt(i, 3).toString());
                double a = Double.parseDouble(modeloTitulares.getValueAt(i, 4).toString());
                double o = Double.parseDouble(modeloTitulares.getValueAt(i, 5).toString());

                if (estaForaDePosicao(posTatica, nomeObj.toString())) {
                    d *= 0.5; a *= 0.5; o *= 0.5;
                }

                baseDef += d;
                baseAtq += a;
                baseOvr += o;
            } catch (Exception e) {}
        }
    }

        // 2. APLICAÇÃO DA POSTURA (Transferência de 30%)
        double totalDef = baseDef;
        double totalAtq = baseAtq;
        String postura = (String) comboMentalidade.getSelectedItem();

        if ("Ofensiva".equals(postura)) {
            // Tira 30% da defesa, arredonda para baixo, e joga no ataque
            int transferir = (int) Math.floor(baseDef * 0.30);
            totalDef = baseDef - transferir;
            totalAtq = baseAtq + transferir;
            
        } else if ("Defensiva".equals(postura)) {
            // Tira 30% do ataque, arredonda para baixo, e joga na defesa
            int transferir = (int) Math.floor(baseAtq * 0.30);
            totalAtq = baseAtq - transferir;
            totalDef = baseDef + transferir;
        }

        // 3. ATUALIZAÇÃO DOS LABELS
        lblMediaDef.setText("DEFESA: " + (int)totalDef);
        lblMediaAtq.setText("ATAQUE: " + (int)totalAtq);
        lblMediaOvr.setText("OVERALL: " + (int)baseOvr); // O potencial bruto do time permanece visível
    }



    private boolean estaForaDePosicao(String posTatica, String nomeJogador) {
        if (nomeJogador == null || !nomeJogador.contains("[") || !nomeJogador.contains("]")) return false;
        
        // Extrai a posição natural, ex: "[AT] Nome" -> "AT"
        String posNatural = nomeJogador.substring(nomeJogador.indexOf("[") + 1, nomeJogador.indexOf("]"));
        
        switch (posTatica) {
            case "GL": return !posNatural.equals("GL");
            case "LD": return !posNatural.equals("LD");
            case "LE": return !posNatural.equals("LE");
            case "ZG": return !posNatural.equals("ZG");
            case "MC": return !posNatural.equals("MC");
            case "AT": return !posNatural.equals("AT");
            default: return false; 
        }
    }

    public List<Integer> getIdsDosTitulares() {
        List<Integer> ids = new java.util.ArrayList<>();
        for (int i = 0; i < 11; i++) {
            Object val = modeloTitulares.getValueAt(i, 0);
            if (val instanceof Integer) {
                ids.add((Integer) val);
            }
        }
        return ids;
    }

    public List<Integer> getIdsDosReservas() {
        List<Integer> ids = new java.util.ArrayList<>();
        // O banco de reservas tem 6 linhas (0 a 5)
        for (int i = 0; i < 6; i++) {
            Object val = modeloReservas.getValueAt(i, 0);
            if (val != null) {
                ids.add((Integer) val);
            }
        }
        return ids;
    }
}
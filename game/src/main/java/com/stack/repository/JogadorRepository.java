package com.stack.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.stack.model.Jogador;
import com.stack.util.DatabaseConfig;

public class JogadorRepository {

    public List<Jogador> carregarTodos() {
        List<Jogador> todos = new ArrayList<>();
        String sql = "SELECT * FROM jogadores";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                todos.add(extrairJogador(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar jogadores do SQLite: " + e.getMessage());
        }
        return todos;
    }

    public List<Jogador> carregarPorClube(int clubeId) {
        List<Jogador> clube = new ArrayList<>();
        // Note: usei 'clube' conforme o seu método extrairJogador
        String sql = "SELECT * FROM jogadores WHERE clube = ?"; 

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, clubeId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                clube.add(extrairJogador(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao filtrar por clube: " + e.getMessage());
        }
        return clube;
    }

    private Jogador extrairJogador(ResultSet rs) throws SQLException {
        return new Jogador(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("posicao"),
            rs.getString("pos"), 
            rs.getInt("ordem"), 
            rs.getInt("idade"),
            rs.getInt("defesa"),
            rs.getInt("ataque"),
            rs.getInt("total"),
            rs.getInt("clube"), 
            rs.getInt("status"),
            rs.getInt("energia")
        );
    }

    /**
     * NOVO MÉTODO: Recebe o mapa de ID -> Ordem (Gaveta 0 a 27)
     * Resolve o erro de compilação no DashboardController.
     */
    public void salvarEstadoCompleto(Map<Integer, Integer> mapaOrdem) {
        String sql = "UPDATE jogadores SET status = ?, ordem = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (Map.Entry<Integer, Integer> entrada : mapaOrdem.entrySet()) {
                    int id = entrada.getKey();
                    int ordem = entrada.getValue();
                    
                    // Lógica de Status automática baseada na gaveta:
                    // 0-10: Titular (1) | 11-16: Reserva (2) | 17-27: Disponível (3)
                    int status = (ordem <= 10) ? 1 : (ordem <= 16 ? 2 : 3);

                    pstmt.setInt(1, status);
                    pstmt.setInt(2, ordem); 
                    pstmt.setInt(3, id);
                    pstmt.addBatch();
                }

                pstmt.executeBatch();
                conn.commit();
                System.out.println("Elenco sincronizado com sucesso.");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar estado completo: " + e.getMessage());
        }
    }
}
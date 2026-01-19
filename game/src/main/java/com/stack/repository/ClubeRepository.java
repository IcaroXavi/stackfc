package com.stack.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.stack.util.DatabaseConfig;

public class ClubeRepository {

    /**
     * Carrega as configurações 
     */
    public Map<String, String> carregarDados() {
        Map<String, String> mapa = new HashMap<>();
        String sql = "SELECT * FROM dados LIMIT 1"; // Pega a única linha de dados do jogo

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                mapa.put("treinador", rs.getString("treinador"));
                mapa.put("clube", String.valueOf(rs.getInt("clube")));
                mapa.put("formacao", rs.getString("formacao"));
                mapa.put("postura", rs.getString("postura"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar dados do banco: " + e.getMessage());
        }
        return mapa;
    }

    /**
     * Atualiza a formação e a postura no banco de dados
     */
    public void salvarEstrategia(String formacao, String postura) {
        String sql = "UPDATE dados SET formacao = ?, postura = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, formacao);
            pstmt.setString(2, postura);
            pstmt.executeUpdate();
            
            System.out.println("Estratégia salva no banco de dados.");
        } catch (SQLException e) {
            System.err.println("Erro ao salvar estratégia: " + e.getMessage());
        }
    }
}
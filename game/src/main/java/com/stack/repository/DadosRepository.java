package com.stack.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.stack.util.DatabaseConfig;

public class DadosRepository {

    /**
     * Este método será chamado na tela de criação de novo jogo.
     * Insere a primeira e única linha na tabela 'dados'.
     */
    public void criarNovoJogo(String nomeTreinador, int codigoClube) {
        // Limpamos a tabela antes para garantir que só exista um "save" ativo
        String sqlDelete = "DELETE FROM dados";
        String sqlInsert = "INSERT INTO dados (treinador, clube, formacao, postura) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false); // Transação para garantir que limpe e insira
            
            try (Statement st = conn.createStatement();
                 PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                
                st.executeUpdate(sqlDelete);

                pstmt.setString(1, nomeTreinador);
                pstmt.setInt(2, codigoClube);
                pstmt.setString(3, "4-4-2");        // Padrão solicitado
                pstmt.setString(4, "Equilibrada"); // Padrão solicitado
                
                pstmt.executeUpdate();
                conn.commit();
                System.out.println("Novo jogo iniciado para: " + nomeTreinador);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar dados do jogo: " + e.getMessage());
        }
    }

    public Map<String, String> carregarDados() {
        Map<String, String> mapa = new HashMap<>();
        String sql = "SELECT * FROM dados LIMIT 1";

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
            System.err.println("Erro ao carregar dados: " + e.getMessage());
        }
        return mapa;
    }

    public void salvarEstrategia(String formacao, String postura) {
        String sql = "UPDATE dados SET formacao = ?, postura = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, formacao);
            pstmt.setString(2, postura);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar estratégia: " + e.getMessage());
        }
    }
}
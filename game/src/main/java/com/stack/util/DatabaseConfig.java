package com.stack.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    // Definimos o nome do arquivo
    private static final String DB_NAME = "jogo.db";
    private static final String URL = "jdbc:sqlite:" + DB_NAME;

    public static Connection getConnection() throws SQLException {
        try {
            // Garante que o Driver seja carregado (evita o erro "No suitable driver")
            Class.forName("org.sqlite.JDBC");
            
            // Log para você saber exatamente qual arquivo o Java está abrindo
            File dbFile = new File(DB_NAME);
            
            if (!dbFile.exists()) {
                System.err.println("AVISO: O arquivo " + DB_NAME + " não foi encontrado. Um novo banco VAZIO será criado.");
            }

        } catch (ClassNotFoundException e) {
            System.err.println("Driver do SQLite não encontrado!");
        }
        
        return DriverManager.getConnection(URL);
    }
}
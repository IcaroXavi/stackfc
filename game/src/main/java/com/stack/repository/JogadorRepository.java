package com.stack.repository;

import com.stack.model.Jogador;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JogadorRepository {

    public List<Jogador> carregarTodos() {
        List<Jogador> todos = new ArrayList<>();
        String path = "/com/stack/resources/tabelas/jogadores.csv";

        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) throw new FileNotFoundException("Arquivo não encontrado: " + path);

            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String linha;
            br.readLine(); // Pula o cabeçalho

            while ((linha = br.readLine()) != null) {
                String[] d = linha.split(",");
                if (d.length >= 9) {
                    todos.add(new Jogador(
                        d[1].trim(),                        // nome
                        d[2].trim(),                        // posicao
                        d[3].trim(),                        // pos
                        Integer.parseInt(d[4].trim()),      // idade
                        Integer.parseInt(d[5].trim()),      // defesa
                        Integer.parseInt(d[6].trim()),      // ataque
                        Integer.parseInt(d[7].trim()),      // total
                        Integer.parseInt(d[8].trim()),      // timeId
                        Integer.parseInt(d[9].trim())       // status 
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Erro crítico no Repository: " + e.getMessage());
        }
        return todos;
    }
}
package com.stack.util;
import java.net.URL;
import javax.swing.ImageIcon;


//Carregamento dos ícones e imagens. Por enquanto usando caminhos fixos para compilar pelo VSCode e deixando a pasta resources dentro de 
// stack, depois alterar essa lógica para rodar em qualquer máquina.
public class ResourceLoader {
    public static ImageIcon getIcon(String nomeArquivo) {
        // Procuramos em: /com/stack/resources/icons/
        URL url = ResourceLoader.class.getResource("/com/stack/resources/icons/" + nomeArquivo);
        
        if (url == null) {
            System.err.println("ERRO: Não encontrado em: /com/stack/resources/icons/" + nomeArquivo);
            return null;
        }
        return new ImageIcon(url);
    }
}
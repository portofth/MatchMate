package service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class ContentSafetyService {

    private static final String ENDPOINT = "https://moderador09.cognitiveservices.azure.com/";
    private static final String API_KEY = "";

    private static String enviaproAzure(String text) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String json = "{ \"text\": \"" + text.replace("\"", "\\\"") + "\" }";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(ENDPOINT + "/contentsafety/text:analyze?api-version=2023-10-01"))
            .header("Ocp-Apim-Subscription-Key", API_KEY)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private static String analisarConteudo(String response) {
        if (!response.contains("severity")) {
            return "Erro na análise do conteúdo";
        }

        int maxSeverity = 0;
        String categoria = "";
        
        if (response.contains("\"category\":\"Hate\"")) {
            int severity = extrairSeveridade(response, "Hate");
            if (severity > maxSeverity) {
                maxSeverity = severity;
                categoria = "Discurso de Ódio";
            }
        }
        
        if (response.contains("\"category\":\"Sexual\"")) {
            int severity = extrairSeveridade(response, "Sexual");
            if (severity > maxSeverity) {
                maxSeverity = severity;
                categoria = "Conteúdo Sexual";
            }
        }
        
        if (response.contains("\"category\":\"Violence\"")) {
            int severity = extrairSeveridade(response, "Violence");
            if (severity > maxSeverity) {
                maxSeverity = severity;
                categoria = "Violência";
            }
        }
        
        if (response.contains("\"category\":\"SelfHarm\"")) {
            int severity = extrairSeveridade(response, "SelfHarm");
            if (severity > maxSeverity) {
                maxSeverity = severity;
                categoria = "Auto-mutilação";
            }
        }

        if (maxSeverity >= 3) {
            return "Conteúdo Inapropriado detectado! Causa: " + categoria + ". Envie novamente a mensagem sem conteúdo inapropriado";
        } else {
            return "Mensagem Aprovada";
        }
    }

    private static int extrairSeveridade(String response, String categoria) {
       
        String pattern = "\\{\"category\":\"" + categoria + "\",\"severity\":(\\d+)\\}";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(response);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }


    // Função Principal

    public static String verificarTexto(String input) {
        try {
            String response = enviaproAzure(input);
            return analisarConteudo(response);
        } catch (Exception e) {
            return "Erro ao analisar texto: " + e.getMessage();
        }
    }

    // Apenas para testes
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            for(int i=0; i<10; i++) {
                System.out.println("Digite um texto para verificar se vai ser censurado:");
                String input = scanner.nextLine();
                String resultado = verificarTexto(input); // a função em si
                System.out.println(resultado);
            }
        }
    }
}

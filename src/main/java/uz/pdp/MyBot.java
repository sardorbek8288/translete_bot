package uz.pdp;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import uz.pdp.ResponseObject.Candidate;


public class MyBot extends TelegramLongPollingBot {
    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        String text = update.getMessage().getText();
        switch (text) {
            case "/start" -> {
                String prompt = "Tasavvur qil, sen tarjima qiluvchi botsan. Foydalanuvchi har qanday tilda xabar yuborsa, javobni barcha mavjud tillarda tarjima qilib ber.";
                String response = getGeminiResponse(prompt + "User xabari:" + text);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(update.getMessage().getChatId());
                sendMessage.setText(response);
                sendMessage.enableMarkdown(true);
                execute(sendMessage);
            }
            default -> {
                String s = update.getMessage().getText();
                String prompt = "Foydalanuvchi yuborgan matnni uzbek va rus va ingiliz tillariga tarjima qilib ber ";
                String response = getGeminiResponse(prompt + "user xabari: " + s);
                SendMessage message = new SendMessage();
                message.setChatId(update.getMessage().getChatId());
                message.setText("Sizning savolingizga javob: \n \n" + response);
                message.enableMarkdown(true);
                execute(message);

            }
        }
    }
//    private static String getGeminiResponse(String request) throws IOException, InterruptedException {
//        String json = "{" +
//                "  \"contents\": [" +
//                "    {" +
//                "      \"parts\": [" +
//                "        {" +
//                "          \"text\": \"" + request + "\"" +
//                "        }" +
//                "      ]" +
//                "    }" +
//                "  ]" +
//                "}";
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest httpRequest = HttpRequest
//                .newBuilder()
//                .header("Content-Type", "application/json")
//                .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyAQPl0UALnkAjR3KAbh1KL-uknPMpP9COc"))
//                .POST(HttpRequest.BodyPublishers.ofString(json))
//                .build();
//
//        HttpResponse<String> httpResponse = client
//                .send(httpRequest, HttpResponse.BodyHandlers.ofString());
//        String response = httpResponse.body();
//        Gson gson = new Gson();
//        ResponseObject responseObject = gson
//                .fromJson(response, ResponseObject.class);
//        ResponseObject.Candidate candidate = ResponseObject.Candidate
//                .get(0);
//
//        ResponseObject.Part part = candidate
//                .content
//                .parts
//                .get(0);
//
//        return part.text;
//    }
private static String getGeminiResponse(String request) throws IOException, InterruptedException {
    String jsonRequest = "{ \"contents\": [{ \"parts\": [{ \"text\": \"" + request + "\" }]}]}";

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyAKHEKvxheJo4y4kKmZIaIQXxqjjXGWKqA"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
            .build();

    HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    String responseBody = httpResponse.body();

    System.out.println("API Response: " + responseBody);


    ResponseObject responseObject = ResponseObject.fromJson(responseBody);

    if (responseObject.candidates != null && !responseObject.candidates.isEmpty()) {
        ResponseObject.Candidate candidate = responseObject.candidates.get(0);

        if (candidate.content != null && candidate.content.parts != null && !candidate.content.parts.isEmpty()) {
            return candidate.content.parts.get(0).text;
        }
    }

    throw new IllegalStateException("Invalid API response structure");
}


    @Override
    public String getBotUsername() {
        return "t.me/translatertt_bot";
    }

    @Override
    public String getBotToken() {
        return "7702015507:AAEyVrdaaFG1CYlxUnvpz8ggKKrTheLyjaM";
    }
}
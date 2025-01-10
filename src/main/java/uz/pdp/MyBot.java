package uz.pdp;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class MyBot extends TelegramLongPollingBot {
    private static final String API_TOKEN_GEMINI = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyAKHEKvxheJo4y4kKmZIaIQXxqjjXGWKqA";
    private static final String START_PROMPT = "Tasavur qil sen translator assistentsan seni vazifang foydalanuvchi yuborgan xabarni" +
            " ozbek,ingliz,rus tillariga tarjima qilish. Agar foydalanuvchi /start bosa seni javobing shunday bo'lishi kerak:" +
            " {Assalomu aleykum men tarjimon botman. Men rus tili, ingliz tili va o'zbek tiliga tarjima qilib beraman.}" +
            "Agar foydalanuvchi oddiy text yozsa tarjima qilib berasan. Agar foydalanuvchi /help yozsa unga men translator botman deb tushuntirasan.User xabari: ";
    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        String response = getGeminiResponse(START_PROMPT + text);
        System.out.println("response = " + response);
        sendMessage(chatId, response);
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.enableMarkdown(true);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    private static String getGeminiResponse(String request) throws IOException, InterruptedException {
        String json = "{" +
                "  \"contents\": [" +
                "    {" +
                "      \"parts\": [" +
                "        {" +
                "          \"text\": \"" + request + "\"" +
                "        }" +
                "      ]" +
                "    }" +
                "  ]" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .header("Content-Type", "application/json")
                .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp:generateContent?key=" + API_TOKEN_GEMINI))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> httpResponse = client
                .send(httpRequest, HttpResponse.BodyHandlers.ofString());
        String response = httpResponse.body();
        Gson gson = new Gson();
        ResponseObject responseObject = gson
                .fromJson(response, ResponseObject.class);
        ResponseObject.Candidate candidate = responseObject
                .candidates
                .get(0);

        ResponseObject.Part part = candidate
                .content
                .parts
                .get(0);

        return part.text;
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
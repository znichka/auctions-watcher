package watcherbot.bot;

import lombok.extern.java.Log;
import okhttp3.*;
import org.springframework.stereotype.Component;
import watcherbot.description.BotCredentials;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

@Log
@Component
public class TelegramBotSender {
    private void send(String response) throws MalformedURLException {
        URL url = new URL(response);
        try {
            URLConnection conn = url.openConnection();
            InputStream is = new BufferedInputStream(conn.getInputStream());
        } catch (IOException e) {
            log.severe(String.format("Error while sending telegram response: %s", response));
        }
    }

    public void sendMessage(BotCredentials credentials, String message) throws IOException {
        String response = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
        response = String.format(response, credentials.getToken(), credentials.getChatId(), message);
        send(response);
    }

    public void sendImage(BotCredentials credentials, String photoUrl, String photoCaption, String itemLink) throws IOException {
        String response = "https://api.telegram.org/bot%s/sendPhoto?chat_id=%s&photo=%s&caption=%s&parse_mode=HTML";
        String caption =  String.format("<a href=\"%s\">%s</a>", itemLink, photoCaption);

        response = String.format(response, credentials.getToken(), credentials.getChatId(), photoUrl, caption);
        send(response);
    }

    public void sendImageUpload(BotCredentials credentials, String photoUrl, String photoCaption, String itemUrl) throws IOException {
        String requestUrl = "https://api.telegram.org/bot%s/sendPhoto";
        String caption =  String.format("<a href=\"%s\">%s</a>", itemUrl, photoCaption);

        requestUrl = String.format(requestUrl, credentials.getToken());

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(photoUrl)
                .method("GET", null)
                .addHeader("User-Agent", "any")
                .build();
        Response response = client.newCall(request).execute();

        if (response.code() == 200) {
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("photo", "filename",
                            RequestBody.create(MediaType.parse("application/octet-stream"), response.body().bytes()
                            ))
                    .addFormDataPart("chat_id", credentials.getChatId())
                    .addFormDataPart("caption", caption)
                    .addFormDataPart("parse_mode", "HTML")
                    .build();
            Request request2 = new Request.Builder()
                    .url(requestUrl)
                    .method("POST", body)
                    .build();
            Response response2 = client.newCall(request2).execute();
            response.close();
            response2.close();
        }
        else {
            response.close();
            sendMessage(credentials, photoUrl);
            sendMessage(credentials, photoCaption);
        }
    }

}

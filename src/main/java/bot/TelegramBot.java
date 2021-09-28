package bot;

import lombok.AllArgsConstructor;
import okhttp3.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

@AllArgsConstructor
public class TelegramBot {
    private final String token;
    private final String chatId;

    private void send(String response) throws MalformedURLException {
        URL url = new URL(response);
        try {
            URLConnection conn = url.openConnection();
            InputStream is = new BufferedInputStream(conn.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) throws IOException {
        String response = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
        response = String.format(response, token, chatId, message);
        send(response);
    }

    public void sendImage(String photoUrl, String photoCaption, String itemLink) throws IOException {
        String response = "https://api.telegram.org/bot%s/sendPhoto?chat_id=%s&photo=%s&caption=%s&parse_mode=HTML";
        String caption =  String.format("<a href=\"%s\">%s</a>", itemLink, photoCaption);

        response = String.format(response, token, chatId, photoUrl, caption);
        send(response);
    }

    public void sendImageUpload(String photoUrl, String photoCaption, String itemUrl) throws IOException {
        String requestUrl = "https://api.telegram.org/bot%s/sendPhoto";
        String caption =  String.format("<a href=\"%s\">%s</a>", itemUrl, photoCaption);

        requestUrl = String.format(requestUrl, token);

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
                    .addFormDataPart("chat_id", chatId)
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
            sendMessage(photoUrl);
            sendMessage(photoCaption);
        }
    }

}

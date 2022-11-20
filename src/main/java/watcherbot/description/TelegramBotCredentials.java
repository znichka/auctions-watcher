package watcherbot.description;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class TelegramBotCredentials {
    private  String token;
    private  String chatId;
}

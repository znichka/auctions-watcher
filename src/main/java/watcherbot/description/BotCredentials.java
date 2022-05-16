package watcherbot.description;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class BotCredentials {
    private  String token;
    private  String chatId;
}

package Example.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface BotService {
    SendMessage startMenu(Message message);
    SendMessage openMainMenu(Message message);
    SendMessage takeText(Message message);
    SendMessage takePhoto(Message message);

}

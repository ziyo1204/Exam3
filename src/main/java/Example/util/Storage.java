package Example.util;

import Example.model.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;

public class Storage {
    public static List<User> users = new ArrayList<>();

    public static User getUser(String chatId){
        for (User user : users) {
            if (user.getChatId().equals(chatId)){
                return user;
            }
        }
        User user = new User();
        user.setChatId(chatId);
        users.add(user);
        return  user;
    }

    public static SendMessage errorCommand(Message message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("*Was entered an error command!*");
        return sendMessage;
    }
}

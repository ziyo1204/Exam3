package Example.service.impl;

import Example.model.BotState;
import Example.model.User;
import Example.service.BotService;
import Example.util.Storage;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.glassfish.grizzly.ProcessorExecutor.execute;

public class BotserviceImpl implements BotService {
    private static final BotService botService = new BotserviceImpl();

    public static BotService getInstance() {
        return botService;
    }

    @Override
    public SendMessage startMenu(Message message) {
        User user = Storage.getUser(message.getChatId().toString());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("Select the *MENU!*");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("MENU"));
        keyboardRows.add(row);
        user.setBotState(BotState.MENU);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return sendMessage;
    }

    @Override
    public SendMessage openMainMenu(Message message) {
        User user = Storage.getUser(message.getChatId().toString());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("Welcome to *QR Code* Bot");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow buttons = new KeyboardRow();
        buttons.add(new KeyboardButton("QR code generation"));
        buttons.add(new KeyboardButton("QR code reading"));
        keyboardRows.add(buttons);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        user.setBotState(BotState.MAIN_MENU);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    @Override
    public SendMessage takeText(Message message) {
        User user = Storage.getUser(message.getChatId().toString());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("Text kiriting:");
        user.setBotState(BotState.SEND_TEXT);
        return sendMessage;
    }

    public static void generateQRcode(String data, String path, String charset, Map map, int h, int w) throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset), charset), BarcodeFormat.QR_CODE, w, h);
        MatrixToImageWriter.writeToFile(matrix, path.substring(path.lastIndexOf('.') + 1), new File(path));
    }

//    private static void takeQRPhoto() throws IOException, WriterException {
//        String str= "THE HABIT OF PERSISTENCE IS THE HABIT OF VICTORY.";
//        String path = "src/main/resources/Quote.png";
//        String charset = "UTF-8";
//        generateQRcode(str, path, charset, 200, 200);
//    }

    @Override
    public SendMessage takePhoto(Message message) {
        User user = Storage.getUser(message.getChatId().toString());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("QR code [rasm] yuboring:");
        user.setBotState(BotState.SEND_QRCODE);
        return sendMessage;
    }

}

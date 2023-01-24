package Example.config;

import Example.model.BotState;
import Example.model.User;
import Example.service.impl.BotserviceImpl;
import Example.service.impl.GenerateQRCode;
import Example.service.impl.ReadQRcode;
import Example.util.Storage;
import com.google.zxing.EncodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.util.*;

public class ExamBot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
        User user = Storage.getUser(update.getMessage().getChatId().toString());
        if (update.getMessage().hasText()) {
            Message message = update.getMessage();
            String messageText = message.getText();
            if (messageText != null && messageText.equals("/start")) {
                sendMessage(BotserviceImpl.getInstance().startMenu(message));
            } else if (messageText.equals("MENU") && user.getBotState().equals(BotState.MENU)) {
                sendMessage(BotserviceImpl.getInstance().openMainMenu(message));
            } else if (messageText.equals("QR code generation") && user.getBotState().equals(BotState.MAIN_MENU)) {
                sendMessage(BotserviceImpl.getInstance().takeText(message));
            } else if (user.getBotState().equals(BotState.SEND_TEXT)) {
                try {
                    java.io.File file1 = new java.io.File("src/main/resources/Quote.png");
                    Map<EncodeHintType, ErrorCorrectionLevel> hashMap = new HashMap<>();
                    hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
                    String charset = "UTF-8";
                    String path = "src/main/resources/Quote.png";
                    GenerateQRCode.generateQRcode(messageText, path, charset, hashMap, 200, 200);
                    SendPhoto sendPhoto = new SendPhoto();
                    sendPhoto.setPhoto(new InputFile(file1, "QRCode"));
                    sendPhoto.setChatId(message.getChatId());
                    user.setBotState(BotState.MENU);
                    execute(sendPhoto);
                } catch (TelegramApiException | IOException | WriterException e) {
                    throw new RuntimeException(e);
                }

            } else if (messageText.equals("QR code reading") && user.getBotState().equals(BotState.MAIN_MENU)) {
                sendMessage(BotserviceImpl.getInstance().takePhoto(message));
            } else if (user.getBotState().equals(BotState.SEND_QRCODE)) {
                List<PhotoSize> photo = update.getMessage().getPhoto();
                if (photo != null && !photo.isEmpty()) {
                    photo.sort(Comparator.comparing(PhotoSize::getFileSize).reversed());
                    PhotoSize photoSize = photo.get(0);
                    GetFile getFile = new GetFile(photoSize.getFileId());
                    try {
                        File file = execute(getFile);
                        java.io.File file1 = new java.io.File("src/main/resources/Text");
                        downloadFile(file, file1);
                        String charset = "UTF-8";
                        Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
                        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(message.getChatId());
                        sendMessage.setText(ReadQRcode.readQRcode(file1.toString(), charset, hintMap));
                        user.setBotState(BotState.MENU);
                        execute(sendMessage);
                    } catch (TelegramApiException | IOException | NotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public void sendMessage(Object object) {
        try {
            if (object instanceof SendMessage) {
                execute((SendMessage) object);
            } else if (object instanceof SendPhoto) {
                execute((SendPhoto) object);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotToken() {
        return "5904787852:AAEAAgHmAwhSaLcgFHtQCeqoIyIWvMtXMSc";
    }

    @Override
    public String getBotUsername() {
        return "B26Ztest_bot";
    }
}

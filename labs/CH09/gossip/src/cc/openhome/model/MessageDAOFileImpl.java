package cc.openhome.model;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MessageDAOFileImpl implements MessageDAO {
    private final String USERS;

    public MessageDAOFileImpl(String USERS) {
        this.USERS = USERS;
    }
    
    @Override
    public List<Message> messagesBy(String username) {
        var userhome = Paths.get(USERS, username);
        
        var messages = new ArrayList<Message>();
        try(var txts = Files.newDirectoryStream(userhome, "*.txt")) {
            for(var txt : txts) {
                var millis = txt.getFileName().toString().replace(".txt", "");
                var blabla = Files.readAllLines(txt).stream()
                        .collect(
                            Collectors.joining(System.lineSeparator())
                        );
                
                messages.add(
                    new Message(username, Long.parseLong(millis), blabla));
            }
            return messages;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void createMessage(Message message) {
        var txt = Paths.get(USERS, message.getUsername(), String.format("%s.txt", message.getMillis()));
        try (var writer = Files.newBufferedWriter(txt)) {
            writer.write(message.getBlabla());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void deleteMessageBy(String username, String millis) {
        var txt = Paths.get(USERS, username, String.format("%s.txt", millis));
        try {
            Files.delete(txt);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}

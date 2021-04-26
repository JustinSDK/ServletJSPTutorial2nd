package cc.openhome.model;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class UserService {
    private final AccountDAO acctDAO;
    private final MessageDAO messageDAO;

    public UserService(AccountDAO acctDAO, MessageDAO messageDAO) {
        this.acctDAO = acctDAO;
        this.messageDAO = messageDAO;
    }

    public void tryCreateUser(String email, String username, String password) {
        if(acctDAO.accountBy(username).isEmpty()) {
            createUser(username, email, password);
        }
    }

    private void createUser(String username, String email, String password) {
        var salt = ThreadLocalRandom.current().nextInt();
        var encrypt = String.valueOf(salt + password.hashCode());
        acctDAO.createAccount(new Account(username, email, encrypt, String.valueOf(salt)));
    }

    public boolean login(String username, String password) {
        var optionalAcct = acctDAO.accountBy(username);
        return optionalAcct.isPresent() && isCorrectPassword(password, optionalAcct.get());
    }

    private boolean isCorrectPassword(String password, Account acct) {
        var encrypt = Integer.parseInt(acct.getEncrypt());
        var salt = Integer.parseInt(acct.getSalt());
        return password.hashCode() + salt == encrypt;
    }
    
    public Optional<String> encryptedPassword(String username, String password) {
        var optionalAcct = acctDAO.accountBy(username);
        if(optionalAcct.isPresent()) {
            var acct = optionalAcct.get();
            var salt = Integer.parseInt(acct.getSalt());
            return Optional.of(String.valueOf(password.hashCode() + salt));
        }
        return Optional.empty();
    }

    public List<Message> messages(String username) {
        var messages = messageDAO.messagesBy(username);
        messages.sort(Comparator.comparing(Message::getMillis).reversed());
        return messages;
    }

    public void addMessage(String username, String blabla) {
        messageDAO.createMessage(new Message(username, Instant.now().toEpochMilli(), blabla));
    }

    public void deleteMessage(String username, String millis) {
        messageDAO.deleteMessageBy(username, millis);
    }

    public boolean exist(String username) {
        return acctDAO.accountBy(username).isPresent();
    }

    public List<Message> newestMessages(int n) {
        return messageDAO.newestMessages(n);
    }
}

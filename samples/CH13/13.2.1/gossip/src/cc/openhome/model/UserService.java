package cc.openhome.model;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final AccountDAO acctDAO;
    private final MessageDAO messageDAO;

    @Autowired
    public UserService(AccountDAO acctDAO, MessageDAO messageDAO) {
        this.acctDAO = acctDAO;
        this.messageDAO = messageDAO;
    }

    public Optional<Account> tryCreateUser(String email, String username, String password) {
        if(emailExisted(email) || userExisted(username)) {
            return Optional.empty();
        }
        return Optional.of(createUser(username, email, password));
    }

    private Account createUser(String username, String email, String password) {
        var salt = ThreadLocalRandom.current().nextInt();
        var encrypt = String.valueOf(salt + password.hashCode());
        var acct = new Account(username, email, encrypt, String.valueOf(salt));
        acctDAO.createAccount(acct);
        return acct;
    } 
    
    public boolean userExisted(String username) {
        return acctDAO.accountBy(username).isPresent();
    }
    
    public boolean emailExisted(String email) {
        return acctDAO.accountByEmail(email).isPresent();
    }
    
    public Optional<Account> verify(String email, String token) {
        var optionalAcct= acctDAO.accountByEmail(email);
        if(optionalAcct.isPresent()) {
            var acct = optionalAcct.get();
            if(acct.getEncrypt().equals(token)) {
                acctDAO.activateAccount(acct);
                return Optional.of(acct);
            }
        }
        return Optional.empty();
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
    
    public Optional<Account> accountByNameEmail(String name, String email) {
        var optionalAcct = acctDAO.accountBy(name);
        if(optionalAcct.isPresent() &&
            optionalAcct.get().getEmail().equals(email)) {
            return optionalAcct;
        }
        return Optional.empty();
    }
    
    public void resetPassword(String name, String password) {
        var salt = ThreadLocalRandom.current().nextInt();;
        var encrypt = salt + password.hashCode();
        acctDAO.updatEncryptSalt(
            name, String.valueOf(encrypt), String.valueOf(salt));
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

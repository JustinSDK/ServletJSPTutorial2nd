package cc.openhome.model;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class AccountDAOFileImpl implements AccountDAO {
    private final String USERS;

    public AccountDAOFileImpl(String USERS) {
        this.USERS = USERS;
    }
    
    @Override
    public void createAccount(Account acct) {
        var userhome = Paths.get(USERS, acct.getName());
        try {
            createUser(userhome, acct.getEmail(), acct.getEncrypt(), acct.getSalt());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Optional<Account> accountBy(String name) {
        var userhome = Paths.get(USERS, name);
        if(Files.notExists(userhome)) {
            return Optional.empty();
        }
        
        return readProfile(name, userhome);        
    }

    private Optional<Account> readProfile(String name, Path userhome) {
        var profile = userhome.resolve("profile");
        try (var reader = Files.newBufferedReader(profile)) {
            var data = reader.readLine().split("\t");
            var email = data[0];
            var encrypt = data[1];
            var salt = data[2];
            return Optional.of(new Account(name, email, encrypt, salt));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void createUser(Path userhome, String email, String encrypt, String salt) throws IOException {
        Files.createDirectories(userhome);
        var profile = userhome.resolve("profile");
        try (var writer = Files.newBufferedWriter(profile)) {
            writer.write(String.format("%s\t%s\t%d", email, encrypt, salt));
        }
    }    
}

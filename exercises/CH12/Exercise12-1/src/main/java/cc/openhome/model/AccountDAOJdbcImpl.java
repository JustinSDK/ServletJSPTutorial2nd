package cc.openhome.model;

import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AccountDAOJdbcImpl implements AccountDAO {
    private JdbcTemplate jdbc;
    
    public AccountDAOJdbcImpl(@Autowired DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }

    @Override
    public void createAccount(Account acct) {
        jdbc.update("INSERT INTO t_account(name, email, encrypt, salt) VALUES(?, ?, ?, ?)", 
                acct.getName(), acct.getEmail(), acct.getEncrypt(), acct.getSalt());
        jdbc.update("INSERT INTO t_account_role(name, role) VALUES(?, 'unverified')", 
              acct.getName());
    }

    @Override
    public Optional<Account> accountBy(String name) {
        return jdbc.queryForList("SELECT * FROM t_account WHERE name = ?", name)
                .stream()
                .findFirst()
                .map(row -> {
                   return new Account(
                           row.get("NAME").toString(),
                           row.get("EMAIL").toString(),
                           row.get("ENCRYPT").toString(),
                           row.get("SALT").toString()
                       );
                });        
    }

    @Override
    public Optional<Account> accountByEmail(String email) {
        return jdbc.queryForList("SELECT * FROM t_account WHERE email = ?", email)
                .stream()
                .findFirst()
                .map(row -> {
                   return new Account(
                           row.get("NAME").toString(),
                           row.get("EMAIL").toString(),
                           row.get("PASSWORD").toString(),
                           row.get("SALT").toString()
                       );
                });       
    }

    public void activateAccount(Account acct) {
        jdbc.update("UPDATE t_account_role SET role = ? WHERE name = ?", "member", acct.getName());
    }
    
    @Override
    public void updatEncryptSalt(String name, String encrypt, String salt) {
        jdbc.update("UPDATE t_account SET encrypt = ?, salt = ? WHERE name = ?", encrypt, salt, name);
    }
}

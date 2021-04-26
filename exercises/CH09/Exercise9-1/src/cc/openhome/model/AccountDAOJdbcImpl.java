package cc.openhome.model;

import java.util.Optional;

import javax.sql.DataSource;

public class AccountDAOJdbcImpl implements AccountDAO {
    private JdbcTemplate jdbc;
    
    public AccountDAOJdbcImpl(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }

    @Override
    public void createAccount(Account acct) {
        jdbc.update("INSERT INTO t_account(name, email, encrypt, salt) VALUES(?, ?, ?, ?)", 
            acct.getName(),
            acct.getEmail(),
            acct.getEncrypt(),
            acct.getSalt()
        );
    }

    @Override
    public Optional<Account> accountBy(String name) {
        var results = jdbc.queryForList("SELECT * FROM t_account WHERE name = ?", name);
        
        if(results.isEmpty()) {
            return Optional.empty();
        }
        
        var row = results.get(0);
        return Optional.of(new Account(
                row.get("NAME").toString(),
                row.get("EMAIL").toString(),
                row.get("ENCRYPT").toString(),
                row.get("SALT").toString()
            ));
    }

}

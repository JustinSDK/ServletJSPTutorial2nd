package cc.openhome.model;

import java.sql.SQLException;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AccountDAOJdbcImpl implements AccountDAO {
    private DataSource dataSource;
    
    @Autowired
    public AccountDAOJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createAccount(Account acct) {
        try(var conn = dataSource.getConnection();
            var stmt = conn.prepareStatement(
               "INSERT INTO t_account(name, email, encrypt, enabled) VALUES(?, ?, ?, 0)")) {
            var stmt2 = conn.prepareStatement(
               "INSERT INTO t_account_role(name, role) VALUES(?, 'ROLE_MEMBER')");

            conn.setAutoCommit(false);
            
            stmt.setString(1, acct.getName());
            stmt.setString(2, acct.getEmail());
            stmt.setString(3, acct.getEncrypt());
            stmt.executeUpdate();
            
            stmt2.setString(1, acct.getName());
            stmt2.executeUpdate();    
            
            conn.commit();
        } catch (SQLException e) {
           throw new RuntimeException(e);
        } 
    }

    @Override
    public Optional<Account> accountBy(String name) {
        try(var conn = dataSource.getConnection();
            var stmt = conn.prepareStatement(
                        "SELECT name, email, encrypt FROM t_account WHERE name = ?")) {
            stmt.setString(1, name);
            var rs = stmt.executeQuery();
            if(rs.next()) {
                return Optional.of(new Account(
                    rs.getString(1),
                    rs.getString(2),
                    rs.getString(3)
                ));
            } 
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Account> accountByEmail(String email) {
        try(var conn = dataSource.getConnection();
            var stmt = conn.prepareStatement(
                        "SELECT name, email, encrypt FROM t_account WHERE email = ?")) {
            stmt.setString(1, email);
            var rs = stmt.executeQuery();
            if(rs.next()) {
                return Optional.of(new Account(
                    rs.getString(1),
                    rs.getString(2),
                    rs.getString(3)
                ));
            } else {
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void activateAccount(Account acct) {
        try(var conn = dataSource.getConnection();
            var stmt = conn.prepareStatement(
               "UPDATE t_account SET enabled = ? WHERE name = ?")) {
            stmt.setInt(1, 1);
            stmt.setString(2, acct.getName());
            stmt.executeUpdate();

        } catch (SQLException e) {
           throw new RuntimeException(e);
        } 
    }
    
    @Override
    public void updateEncrypt(String name, String encrypt) {
        try(var conn = dataSource.getConnection();
            var stmt = conn.prepareStatement(
             "UPDATE t_account SET password = ? WHERE name = ?")) {
            stmt.setString(1, encrypt);
            stmt.setString(2, name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } 
    }
}

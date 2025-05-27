package dataAccess;
import model.userData;
import java.sql;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class userDAO {
    public final Database database;

    public userDAO(Database database) {
        this.database = database;
    }

    void createUser(userData user) throws DataAccessException{
        String sql = "INSERT INTO users(username, password, email) VALUES(?, ?, ?)";
        try(Connection conn = database.getConnection());
        PreparedStatement statement = conn.preparestatement(sql){
            statement.setString(1, user.username());
            statement.setString(2, user.password());
            statement.setString(3, user.email());
            statement.executeUpdate();
        }catch(SQLException e){
            throw new DataAccessException("Error creating user" + e.getMessage());
        }
    }
    userData getUser(String username) throws DataAccessException{
        String sql = "SELECT username, password, email FROM users WHERE username = ?";
        try(Connection conn = database.getConnection());
        PreparedStatement statement = conn.preparestatement(sql){
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();
            if(statement.next()){
                return new userData(
                    result.getString("username"),
                    result.getString("password"),
                    result.getString("email")
                );
            }
            return null;
        }catch(SQLException e) {
            throw new DataAccessException("Error getting user" + e.getMessage());
        }
    }
    void clear() throws DataAccessException {
        String sql = "DELETE FROM users";
        try(Connection conn = database.getConnection());
        Statement statement = conn.createStatement(){
            statement.executeUpdate(sql);
        } catch(SQLException e){
            throw new DataAccessException("Error clearing users" + e.getMessage());
        }
    }


}
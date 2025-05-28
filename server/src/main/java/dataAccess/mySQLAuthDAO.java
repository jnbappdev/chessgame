package dataAccess;

import model.authData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class mySQLAuthDAO implements authDAO {
    private final DatabaseManager dbManager;

    public mySQLAuthDAO() throws DataAccessException {
        try {
            this.dbManager = new DatabaseManager();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to initialize database: " + e.getMessage());
        }
    }

    public mySQLAuthDAO(DatabaseManager dbManager) throws DataAccessException {
        this.dbManager = dbManager;
    }

    @Override
    public authData createAuthToken(String username) throws DataAccessException{
        String authToken = UUID.randomUUID().toString();
        String sql = "INSERT INTO authTokens (authToken, username) VALUES(?,?)";
        try(Connection conn = dbManager.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ){
            statement.setString(1, authToken);
            statement.setString(2, username);
            statement.executeUpdate();
            return new authData(authToken, username);
        }catch(SQLException e){
            throw new DataAccessException("Unable to create authToken" + e.getMessage());
        }
    }
    @Override
    public authData getAuthToken(String authToken) throws DataAccessException {
        String sql = "SELECT authToken, username FROM authtokens WHERE authToken = ?";
        try (Connection conn = dbManager.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)){
            statement.setString(1, authToken);
            try(ResultSet result = statement.executeQuery()){
                if(result.next()){
                    return new authData(result.getString("authToken"),
                            result.getString("username"));
                }
            }
            return null;
        }catch(SQLException e){
            throw new DataAccessException("Unable to get authData" + e.getMessage());
        }
    }
    @Override
    public void deleteAuthToken(String authToken) throws DataAccessException {
        String sql = "DELETE FROM authTokens WHERE authToken = ?";
        try(Connection conn = dbManager.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)){
            statement.setString(1, authToken);
            statement.executeUpdate();}
        catch(SQLException e) {
            throw new DataAccessException("authToken not found" + e.getMessage());
        }
    }

    @Override
    public void clearAll() throws DataAccessException {
        String sql = "DELETE FROM authTokens";
        try(Connection conn = dbManager.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)){
            statement.executeUpdate();}
        catch(SQLException e) {
            throw new DataAccessException("unable to clear tokens" + e.getMessage());
        }
    }
}


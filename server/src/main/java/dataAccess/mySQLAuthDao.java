import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import model.*;
import java.sql.*;
import java.util.UUID;

public class mySQLAuthDao implements authData {
    private final DatabaseManager dbManager;
    public mySQLAuthDao throws DataAccessException {
        this.dbManager = new DatabaseManager();
    }

    public mySQLAuthDao (DatabaseManager dbManager) throws DataAccessException {
        this.dbManager = dbManager;
    }

    @Override
    public authData createAuthToken(String username) throws DataAccessException{
        String authToken = UUID.randomUUID().toString();
        String sql = "INSERT INTO authToken(authToken, username) VALUES(?,?)";
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
    public authData getAuthToken (String authToken) throws DataAccessException {
        String sql = "SELECT authToken, username FROM WHERE authToken = ?";
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
    public void deleteAuthToken (String authToken) throws DataAccessException {
        String sql = "DELETE authToken WHERE authToken = ?";
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
        String sql = "DELETE FROM authToken";
        try(Connection conn = dbManager.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)){
            statement.executeUpdate();}
        catch(SQLException e) {
            throw new DataAccessException("unable to clear tokens" + e.getMessage());
        }
    }
}


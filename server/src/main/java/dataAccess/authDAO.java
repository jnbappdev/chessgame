package dataAccess;
import model.authData;




public interface authDAO {
    authData createAuth(String username) throws DataAccessException;
    authData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;

    authData createAuthToken(String username) throws DataAccessException;

    authData getAuthToken(String authToken) throws DataAccessException;

    void deleteAuthToken(String authToken) throws DataAccessException;

    void clearAll() throws DataAccessException;
}
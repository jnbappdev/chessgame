package dataAccess;
import model.userData;

public interface userDAO {
    void createUser(userData user) throws DataAccessException;
    userData getUser(String username) throws DataAccessException;
    void clear() throws DataAccessException;

}
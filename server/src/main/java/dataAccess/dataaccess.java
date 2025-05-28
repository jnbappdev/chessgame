package dataAccess;

public interface dataaccess {
    userDAO getUserDAO();
    gameDAO getGameDAO();
    authDAO getAuthDAO();

    void clear() throws DataAccessException;
}

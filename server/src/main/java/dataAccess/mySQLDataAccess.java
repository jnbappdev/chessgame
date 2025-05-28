package dataAccess;

import java.sql.SQLException;

public class MySQLDataAccess implements dataaccess {
    private final userDAO userDAO;
    private final gameDAO gameDAO;
    private final authDAO authDAO;
    private final DatabaseManager dbManager;

    public MySQLDataAccess() throws DataAccessException {
        try {
            this.dbManager = new DatabaseManager();
            this.userDAO = new mySQLUserDAO(dbManager);
            this.gameDAO = new mySQLGameDAO(dbManager);
            this.authDAO = new mySQLAuthDAO(dbManager);
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Failed to initialize database or DAOs: " + e.getMessage());
        }
    }

    @Override
    public userDAO getUserDAO(){
        return userDAO;
    }

    @Override
    public gameDAO getGameDAO(){
        return gameDAO;
    }

    @Override
    public authDAO getAuthDAO(){
        return authDAO;
    }

    @Override
    public void clear() throws DataAccessException{
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
    }
}
package dataAccess;

public class mySQLDataAccess implements dataaccess{
    private final userDAO userDAO;
    private final gameDAO gameDAO;
    private final authDAO authDAO;
    private final DatabaseManager;

    public mySQLDataAccess() throws DataAccessException{
        try{
            this.DatabaseManager = new DatabaseManager();
            this.userDAO = new mySQLUserDAO(dbManager);
            this.gameDAO = new mySQLGameDAO(dbManager);
            this.authDAO = new mySQLAuthDAO(dbManager);

        }catch(DataAccessException e){
            throw new DataAccessException("cannot access data" + e.getMessage());
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
    public void clear(){
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
    }
}
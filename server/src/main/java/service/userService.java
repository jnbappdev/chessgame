package service;
import dataAccess.authDAO;
import dataAccess.userDAO;
import model.authData;
import model.userData;
import dataAccess.DataAccessException;


public class userService{
    private final userDAO user_DAO;
    private final authDAO auth_DAO;

    public userService(userDAO user_DAO, authDAO auth_DAO){
        this.user_DAO = user_DAO;
        this.auth_DAO = auth_DAO;
    }

    public authData register(userData user) throws DataAccessException{
        if (user == null || user.username() == null || user.password() == null || user.email() == null) {
            throw new DataAccessException("Bad Request");
        }
        if(user_DAO.getUser(user.username()) != null){
            throw new DataAccessException("already taken");
        }
    user_DAO.createUser(user);
    return auth_DAO.createAuth(user.username());
    }


    public authData login(userData user) throws DataAccessException{
        if(user == null || user.username() == null || user.password() == null){
            throw new DataAccessException("bad request");

        }

        userData storedUser = user_DAO.getUser(user.username());
        if(storedUser == null || !storedUser.password().equals(user.password())){
            throw new DataAccessException("unauthorized");
        }
        return auth_DAO.createAuth(user.username());
    }
    public void clear() throws DataAccessException{
        user_DAO.clear();
    }
}
package service;
import dataAccess.authDAO;
import dataAccess.userDAO;
import model.authData;
import model.userData;
import org.eclipse.jetty.server.Authentication;

public class userService{
    private final userDAO user_DAO;
    private final authDAO auth_DAO;

    public userService(userDAO user_DAO, authDAO auth_DAO){
        this.user_DAO = user_DAO;
        this.auth_DAO = auth_DAO;
    }

    public authData register(userData user) throws DataAccessException{
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new DataAccessException("Missing required fields");
        }
        try{
            user_DAO.createUser(user);
            return auth_DAO.createAuth(user.username());
        }
        catch{
            user_DAO(DataAccessException e){
                throw new DataAccessException("Unable to register");
            }
        }
    }

    public authData login(userData user) throws DataAccessException{
        userData storedUser = user_DAO.getUser(user.username());
        if(!storedUser.password().equals(user.password())){
            throw new DataAccessException("Invalid password");
        }
        return auth_DAO.createAuth(user.username());
    }
}
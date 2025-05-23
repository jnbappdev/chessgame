package service;
import dataAccess.authDAO;
import dataAccess.userDAO;
import model.authData;
import model.userData;
import dataAccess.DataAccessException;

public class userService {
    private final userDAO user_DAO;
    private final authDAO auth_DAO;

    public userService(userDAO user_DAO, authDAO auth_DAO) {
        this.user_DAO = user_DAO;
        this.auth_DAO = auth_DAO;
    }

    public authData register(userData user) throws DataAccessException {
        // Validate input
        if (user == null || user.username() == null || user.password() == null || user.email() == null ||
                user.username().trim().isEmpty() || user.password().trim().isEmpty() || user.email().trim().isEmpty()) {
            throw new DataAccessException("Bad Request");
        }

        // Check if user already exists
        if (user_DAO.getUser(user.username()) != null) {
            throw new DataAccessException("already taken");
        }

        // Create user and return auth token
        user_DAO.createUser(user);
        return auth_DAO.createAuth(user.username());
    }

    public authData login(userData user) throws DataAccessException {
        // Validate input
        if (user == null || user.username() == null || user.password() == null ||
                user.username().trim().isEmpty() || user.password().trim().isEmpty()) {
            throw new DataAccessException("Bad Request");
        }
        // Check credentials
        userData storedUser = user_DAO.getUser(user.username());
        if (storedUser == null || !storedUser.password().equals(user.password())) {
            throw new DataAccessException("unauthorized");
        }
        return auth_DAO.createAuth(user.username());
    }

    public void clear() throws DataAccessException {
        user_DAO.clear();
        auth_DAO.clear();
    }
}
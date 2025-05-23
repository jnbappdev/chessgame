package dataAccess;
import model.userData;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

public class MemoryUserDAO implements userDAO{
    private final Map<String, userData> users = new HashMap<>();
    @Override
    public void createUser(userData user) throws DataAccessException {
        if(user == null || user.username() == null){
            throw new DataAccessException("bad request");
        }
        if(users.containsKey(user.username())){
            throw new DataAccessException("already exists");
        }
        users.put(user.username(), user);
    }

    @Override
    public userData getUser(String username) throws DataAccessException {
        userData user = users.get(username);
        if(username == null){
            throw new DataAccessException("bad request");
        }
        return user;
    }

    @Override
    public void clear(){
        users.clear();
        System.out.println("all users cleared");
    }
    public Collection<userData> getAllUsers(){
        return users.values();
    }
}
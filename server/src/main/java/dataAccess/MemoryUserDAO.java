package dataAccess;
import model.userData;
import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements userDAO{
    private final Map<String, userData> users = new HashMap<>();
    @Override
    public void createUser(userData user) throws DataAccessException {
        if(users.containsKey(user.username())){
            throw new DataAccessException("User already exists");
        }
        users.put(user.username(), user);
    }

    @Override
    public userData getUser(String username) throws DataAccessException {
        userData user = users.get(username);
        if(user == null){
            throw new DataAccessException("User not found.");
        }
        return user;
    }

    @Override
    public void clear(){
        users.clear();
    }


}
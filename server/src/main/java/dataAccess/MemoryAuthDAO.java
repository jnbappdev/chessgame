package dataAccess;
import model.authData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryAuthDAO implements authDAO{
    private final Map<String, authData> auths = new HashMap<>();
    @Override
    public authData createAuth(String username) throws DataAccessException{
        String authToken = UUID.randomUUID().toString();
        authData auth = new authData(authToken, username);
        auths.put(authToken, auth);
        return auth;
    }

    @Override
    public authData getAuth(String authToken) throws DataAccessException{
        authData auth = auths.get(authToken);
        if(auth == null){
            throw new DataAccessException("Invalid auth token.");
        }
        return auth;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException{
        if(!auths.containsKey(authToken)){
            throw new DataAccessException("Invalid auth token.");
        }
        auths.remove(authToken);
    }

    @Override
    public void clear(){
        auths.clear();
    }
}
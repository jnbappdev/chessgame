package dataAccess;
import model.authData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryAuthDAO implements authDAO{
    private final Map<String, authData> auths = new HashMap<>();
    @Override
    public authData createAuth(String username) throws DataAccessException{
        if(username == null || username.trim().isEmpty()){
            System.out.println("empty username or null (createAuth failed)");
            throw new DataAccessException("bad request");
        }

        String authToken = UUID.randomUUID().toString();
        authData auth = new authData(authToken, username);
        auths.put(authToken, auth);
        return auth;
    }

    @Override
    public authData getAuth(String authToken) throws DataAccessException{
        if(authToken == null){
            System.out.println("null authToken bruh");
            throw new DataAccessException("unauthorized");
        }

        authData auth = auths.get(authToken);
        if(auth == null){
            throw new DataAccessException("unauthorized");
        }
        return auth;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException{
        if(authToken == null || !auths.containsKey(authToken)) {
            System.out.println("invalid token: " + authToken);
            throw new DataAccessException("unauthorized");
        }
        auths.remove(authToken);
        System.out.println("deleted authToken(s): " + authToken);
    }

    @Override
    public void clear(){
        auths.clear();
        System.out.println("Cleared all authTokens :D");
    }
}
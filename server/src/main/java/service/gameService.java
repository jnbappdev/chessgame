package service;
import dataAccess.authDAO;
import dataAccess.gameDAO;
import model.gameData;
import dataAccess.DataAccessException;
import java.util.Collection;

public class gameService{
    private final gameDAO game_DAO;
    private final authDAO auth_DAO;

    public gameService(gameDAO game_DAO, authDAO auth_DAO){
        this.game_DAO = game_DAO;
        this.auth_DAO = auth_DAO;
    }

    public gameData createGame(String authToken, String gameName) throws DataAccessException{
        auth_DAO.getAuth(authToken);
        if(authToken == null || auth_DAO.getAuth(authToken) == null){
            throw new DataAccessException("unauthorized");
        }
        return game_DAO.createGame(gameName);
    }

    public Collection<gameData>listGames (String authToken) throws DataAccessException{
        if(authToken == null || auth_DAO.getAuth(authToken) == null){
            throw new DataAccessException("unauthorized");
        }
        return game_DAO.listgame();
    }

    public void joinGame(String authToken, int gameID, String playerColor) throws DataAccessException{
        if(authToken == null || auth_DAO.getAuth(authToken) == null){
            throw new DataAccessException("unauthorized");
        }
        if(playerColor == null || (!playerColor.equalsIgnoreCase("WHITE") && !playerColor.equalsIgnoreCase("BLACK"))){
            throw new DataAccessException("bad request");
        }
        gameData game = game_DAO.getGame(gameID);
        gameData updatedGame;
        String username = auth_DAO.getAuth(authToken).username();
        if(playerColor.equalsIgnoreCase("WHITE") && game.whiteUsername() != null) {
            throw new DataAccessException("Player color required");
        }
        if(playerColor.equalsIgnoreCase("BLACK") && game.blackUsername() != null){
            throw new DataAccessException("already exists");
        }
        if(playerColor.equalsIgnoreCase("WHITE")){
            updatedGame = new gameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        }else{
            updatedGame = new gameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        }
        game_DAO.updateGame(updatedGame);
    }
    public void clear() throws DataAccessException{
        game_DAO.clear();
    }
}
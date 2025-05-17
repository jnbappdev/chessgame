package service;
import dataAccess.authDAO;
import dataAccess.gameDAO;
import model.gameData;
import model.authData;
import chess.ChessGame;

public class gameService{
    private final gameDAO game_DAO;
    private final authDAO auth_DAO;

    public gameService(gameDAO game_DAO, authDAO auth_DAO){
        this.game_DAO = game_DAO;
        this.auth_DAO = auth_DAO;
    }

    public gameData createGame(String authToken, String gameName) throws DataAccessException{
        auth_DAO.getAuth(authToken);
        if(gameName == null){
            throw DataAccessException("Game name required.");
        }
        return game_DAO.createGame(gameName);
    }

    public void joinGame(String authToken, int gameID, String playerColor) throws DataAccessException{
        authData auth = auth_DAO.getAuth(authToken);
        gameData game = game_DAO.getGame(gameID);
        if(playerColor == null) {
            throw new DataAccessException("Player color required");
        }
        String username = auth.username();
        gameData updatedGame;
        if(playerColor.equalsIgnoreCase("WHITE")){
            if(game.whiteUsername() != null){
                throw new DataAccessException("White player already assigned");
            }
            updatedGame = new gameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        }
        else if (playerColor.equalsIgnoreCase("BLACK")){
            if(game.blackUsername() != null){
                throw new DataAccessExeption("Black player already assigned");
            }
            updatedGame = new gameData(game.gameID(), username, game.whiteUsername(), game.gameName(), game.game());
        }
        else{
            throw new DataAccessException("Invalid player color");
        }
        game_DAO.updateGame(updatedGame);
    }
    public void clear() throws DataAccessException{
        game_DAO.clear();
    }


}
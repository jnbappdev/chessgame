package dataAccess;
import model.gameData;
import chess.ChessGame;

import java.util.HashMap;
import java.util.Map;

public class memoryGameDAO implements gameDAO{
    private final Map<Integer, gameData> games = new HashMap<>();
    private int nextGameID = 1;

    @Override
    public gameData createGame(String gameName) throws DataAccessException{
        gameData game = new gameData(nextGameID ++, null, null, gameName, new ChessGame());
        games.put(game.gameID(), game);
        return game;
    }

    @Override
    public gameData getGame(int gameID) throws DataAccessException{
        gameData game = games.get(gameID);
        if(game == null){
            throw new DataAccessException("Game not found");
        }
        return game;
    }

    @Override
    public void updateGame(gameData game) throws DataAccessException{
        if(!games.containsKey(game.gameID())){
            throw new DataAccessException("Game not found");
        }
        games.put(game.gameID(), game);
    }

    @Override
    public void clear(){
        games.clear();
        nextGameID = 1;
    }
}
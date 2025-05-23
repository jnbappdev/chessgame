package dataAccess;
import model.gameData;
import chess.ChessGame;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements gameDAO{
    private final Map<Integer, gameData> games = new HashMap<>();
    private int nextGameID = 1;

    @Override
    public gameData createGame(String gameName) throws DataAccessException{
        if(gameName == null || gameName.trim().isEmpty()){
            throw new DataAccessException("bad request");
        }
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
    public Collection<gameData> listgame() throws DataAccessException {
        return games.values();
    }

    @Override
    public void updateGame(gameData game) throws DataAccessException{
        if(game == null || !games.containsKey(game.gameID())){
            throw new DataAccessException("Bad request");
        }
        games.put(game.gameID(), game);
    }

    @Override
    public void clear(){
        games.clear();
        System.out.println("All games cleared");
    }
}
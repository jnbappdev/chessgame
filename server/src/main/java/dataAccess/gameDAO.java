package dataAccess;
import model.gameData;

import java.util.Collection;
import java.util.List;

public interface gameDAO{
    gameData createGame(String gameName) throws DataAccessException;
    gameData getGame(int gameID) throws DataAccessException;
    Collection<gameData> listgame() throws DataAccessException;
    void updateGame(gameData game) throws DataAccessException;
    void clear() throws DataAccessException;


}
//@Override

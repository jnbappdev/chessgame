package dataAccess;
import model.gameData;
import chess.ChessGame;

import java.util.HashMap;
import java.util.Map;

public class memoryGameDAO implements gameDAO{
    private final Map<Integer, gameData> games = new HashMap<>();

}
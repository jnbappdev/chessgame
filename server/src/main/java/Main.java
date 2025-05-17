import chess.*;
import server.Server;
import spark.Spark;
import com.google.gson.Gson;


public class Main {
    private final UserService userService;
    private final AuthService authService;
    private final GameService gameService;
    private final Gson gson;

    public main(){
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        this.userService = new userService(userDAO, authDAO, gameDAO);


    }

    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        Server runServer = new Server();
        runServer.run(8081);


    }

}
import chess.*;
import server.ErrorResponse;
import server.GameResponse;
import spark.Spark;
import com.google.gson.Gson;


import service.authService;
import service.gameService;
import service.userService;
import dataAccess.authDAO;
import dataAccess.gameDAO;
import dataAccess.userDAO;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import dataAccess.MemoryGameDAO;
import model.*;
import server.*;

import java.util.Collection;

public class Main {
    private final userService userService;
    private final authService authService;
    private final gameService gameService;
    private final Gson gson;

    public Main(){
        userDAO userDAO = new MemoryUserDAO();
        authDAO authDAO = new MemoryAuthDAO();
        gameDAO gameDAO = new MemoryGameDAO();
        this.userService = new userService(userDAO, authDAO);
        this.authService = new authService(authDAO);
        this.gameService = new gameService(gameDAO, authDAO);
        this.gson = new Gson();

    }



    public class Server {

        public int run(int desiredPort) {
            Spark.port(8081);

            Spark.staticFiles.location("web");
            // Register your endpoints and handle exceptions here.
            Spark.post("/user", (req, res) -> {
                res.type("application/json");
                userData user = gson.fromJson(req.body(), userData.class);
                try{
                    authData authData = userService.register(user);
                    return gson.toJson(authData);
                }
                catch(Exception e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Error " + e.getMessage()));
                }
            });

            Spark.post("/session", (req, res) -> {
                res.type("application/json");
                userData user = gson.fromJson(res.body(), userData.class);

                try{
                    authData authData = userService.login(user);
                    return gson.toJson(authData);
                }
                catch(Exception e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Error " + e.getMessage()));
                }
            } );

            Spark.post("/delete", (req, res) -> {
                res.type("application/json");
                String authToken = req.headers("authorization");

                try{
                    authService.logout(authToken);
                    res.status(200);
                    return "{}";
                }
                catch(Exception e){
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Error " + e.getMessage()));
                }
            } );

            Spark.get("/game", (req, res) -> {
                res.type("application/json");
                String authToken = req.headers("authorization");

                try{
                    return gson.toJson(new ListGameResponse(gameService.joinGame(authToken, gameID, playerColor);));
                }

            //This line initializes the server and can be removed once you have a functioning endpoint
            Spark.init();

            Spark.awaitInitialization();
            return Spark.port();
        }

        public void stop() {
            Spark.stop();
            Spark.awaitStop();
        }


    }

    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        Main runServer = new Main();
        runServer.run(8081);


    }

}
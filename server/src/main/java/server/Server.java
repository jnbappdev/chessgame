package server;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.authData;
import model.gameData;
import model.userData;
import spark.Request;
import spark.Response;
import spark.Spark;
import com.google.gson.Gson;
import service.authService;
import service.gameService;
import service.userService;
import dataAccess.authDAO;
import dataAccess.gameDAO;
import dataAccess.userDAO;
import dataAccess.*;
import java.util.*;
import com.google.gson.GsonBuilder;

public class Server {

    private final userService userService;
    private final authService authService;
    private final gameService gameService;
    private final Gson gson;

    public Server(){
        userDAO userDAO = new MemoryUserDAO();
        authDAO authDAO = new MemoryAuthDAO();
        gameDAO gameDAO = new MemoryGameDAO();
        this.userService = new userService(userDAO, authDAO);
        this.authService = new authService(authDAO);
        this.gameService = new gameService(gameDAO, authDAO);
        this.gson = new GsonBuilder().create();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listgames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clear);
        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


    // Register your endpoints and handle exceptions here.
    private Object register(Request request, Response response) {
        response.type("application/json");
        String requestBody = request.body();
        System.out.println(requestBody);
        userData user;
        try {
            user = gson.fromJson(requestBody, userData.class);
            System.out.println(user);
        }catch(Exception e) {
            System.out.println(e.getMessage());
            response.status(400);
            return gson.toJson(new ErrorResponse("Error: bad request"));
        }
        try {
            authData authData = userService.register(user);
            response.status(200);
            return gson.toJson(authData);
        }catch(DataAccessException e) {
            response.status(getErrorStatus(e));
            String mes = e.getMessage().toLowerCase().contains("already taken") ?
                "Error: already taken" :"Error: " + e.getMessage();
            return gson.toJson(new ErrorResponse(mes));
        }
    }

    private Object clear(Request request, Response response) {
        response.type("application/json");
        try {
            userService.clear();
            gameService.clear();
            authService.clear();
            response.status(200);
            return "{}";
        }catch(DataAccessException e) {
            response.status(getErrorStatus(e));
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    private Object joinGame(Request request, Response response) {
        response.type("application/json");
        String authToken= request.headers("Authorization");
        JoinGameRequest join;
        try {
            join = gson.fromJson(request.body(), JoinGameRequest.class);
        }catch(Exception e){
            System.out.println("For debugging - " + e.getMessage());
            response.status(400);
            return gson.toJson(new ErrorResponse("Error - Bad Request smh"));
        }
        if(join == null || join.gameID() <= 0 || join.playerColor() == null) {
            System.out.println("Debuggggggigin -> request" + join);
            response.status(400);
            return gson.toJson(new ErrorResponse("Error - Bad Request, real bad buddy"));
        }
        try {
            gameService.joinGame(authToken, join.gameID(), join.playerColor());
            response.status(200);
            return "{}";
        }catch(DataAccessException e) {
            response.status(getErrorStatus(e));
            return gson.toJson(new ErrorResponse("error - " + e.getMessage()));
        }
    }

    private Object createGame(Request request, Response response) {
        response.type("application/json");
        String authToken = request.headers("Authorization");
        GameNameRequest gameName;
        try {
            gameName = gson.fromJson(request.body(), GameNameRequest.class);
        }catch (Exception e) {
            response.status(400);
            return gson.toJson(new ErrorResponse("Error - Bad Request"));
        }
        if (gameName == null || gameName.gameName() == null || gameName.gameName().trim().isEmpty()) {
            response.status(400);
            return gson.toJson(new ErrorResponse("Error - Bad Request"));
        }
        try {
            gameData game = gameService.createGame(authToken, gameName.gameName());
            response.status(200);
            return gson.toJson(new CreateGameResponse(game.gameID()));
        }catch (DataAccessException e) {
            response.status(getErrorStatus(e));
            return gson.toJson(new ErrorResponse("Error - " + e.getMessage()));
        }
    }

    private Object listgames(Request request, Response response) {
        response.type("application/json");
        String authToken = request.headers("Authorization");
        try {
            Collection<gameData> games = gameService.listGames(authToken);
            response.status(200);
            return gson.toJson(new ListGamesResponse(games));
        }catch(DataAccessException e) {
            response.status(getErrorStatus(e));
            return gson.toJson(new ErrorResponse("Error - " + e.getMessage()));
        }
    }

    private Object logout(Request request, Response response) {
        response.type("application/json");
        String authToken = request.headers("Authorization");
        try {
            authService.logout(authToken);
            response.status(200);
            return "{}";
        }catch(DataAccessException e){
            response.status(getErrorStatus(e));
            return gson.toJson(new ErrorResponse("Error" + e.getMessage()));
        }
    }

    private Object login(Request request, Response response) {
        response.type("application/json");
        String requestBody = request.body();
        System.out.println(requestBody);
        if(requestBody == null || requestBody.trim().isEmpty()) {
            response.status(400);
            return gson.toJson(new ErrorResponse("Bad Request"));
        }
        JsonObject json;
        try {
            json = JsonParser.parseString(requestBody).getAsJsonObject();
            System.out.println(json.keySet());
        }catch(Exception e) {
            System.out.println(e.getMessage());
            response.status(400);
            return gson.toJson(new ErrorResponse("Bad Request"));
        }
        boolean hasUsername = json.has("username") && !json.get("username").isJsonNull();
        boolean hasPassword = json.has("password") && !json.get("password").isJsonNull();
        if(!hasUsername || !hasPassword) {
            System.out.println(hasUsername + " " + hasPassword);
            response.status(400);
            return gson.toJson(new ErrorResponse("Error: Bad Request"));
        }
        try {
            String username = json.get("username").getAsString();
            String password = json.get("password").getAsString();
            System.out.println(username + " " + password);
            if(username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
                response.status(400);
                return gson.toJson(new ErrorResponse("Bad Request"));
            }
            userData user = new userData(username.trim(), password.trim(), null);
            System.out.println("Attempting login from " + user);

            try {
                authData authData = userService.login(user);
                System.out.println("successful login from " + username.trim());
                response.status(200);
                return gson.toJson(authData);
            }catch(DataAccessException e) {
                System.out.println("Login failed from " + username.trim() + "this the error: " + e.getMessage());
                int status = getErrorStatus(e);
                response.status(status);
                return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
            }
        }catch(Exception e) {
            System.out.println(e.getMessage());
            response.status(400);
            return gson.toJson(new ErrorResponse("Bad Requesttttt"));
        }
    }

    private int getErrorStatus(DataAccessException e) {
        String mes = e.getMessage().toLowerCase();
        if(mes.contains("bad request") || mes.contains("game not found")) {
            return 400;
        }else if(mes.contains("unauthorized")) {
            return 401;
        }else if(mes.contains("already exists") || mes.contains("already taken")) {
            return 403;
        }return 500;
    }

    private record ErrorResponse(String message) {}
    private record ListGamesResponse(Collection<gameData> games) {}
    private record CreateGameResponse(int gameID) {}
    private record GameNameRequest(String gameName) {}
    private record JoinGameRequest(int gameID, String playerColor) {}

}

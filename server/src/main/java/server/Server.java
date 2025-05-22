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
import service.*;
import java.util.*;

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
        this.gson = new Gson();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);


        Spark.staticFiles.location("web");

        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.post("/delete", this::logout);
        Spark.post("/game", this::listgames);
        Spark.post("/game", this::createGame);
        Spark.post("/game", this::joinGame);
        Spark.post("/game", this::clear);

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
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            response.status(400);
            return gson.toJson(new ErrorResponse("Error Bad Request"));
        }
        try {
            authData authData = userService.register(user);
            response.status(200);
            return gson.toJson(authData);
        }
        catch(DataAccessException e) {
            response.status(getErrorStatus(e));
            return gson.toJson(new ErrorResponse("Error" + e.getMessage()));
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
        }
        catch(DataAccessException e) {
            response.status(getErrorStatus(e));
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    private Object joinGame(Request request, Response response) {
        response.type("application/json");
        String authToken= request.headers("Auth.");
        JoinGameRequest join;
        try {
            join = gson.fromJson(request.body(), JoinGameRequest.class);
        }
        catch(Exception e){
            System.out.println("For debugging - " + e.getMessage());
            response.status(400);
            return gson.toJson(new ErrorResponse("Error - Bad Request smh"));
        }
        if(join == null || join.gameID() <= 0 || join.playerColor() == null) {
            System.out.println("Debuggggggigin -> request" + join);
            response.status(400);
            return gson.toJson(new ErrorResponse("Error - Baddd Request, real bad buddy"));
        }
        try {
            gameService.joinGame(authToken, join.gameID(), join.playerColor());
            response.status(200);
            return "{}";
        }
        catch(DataAccessException e) {
            response.status(getErrorStatus(e));
            return gson.toJson(new ErrorResponse("eror - " + e.getMessage()));
        }
    }

    private Object createGame(Request request, Response response) {
        response.type("application/json");
        String authToken = request.headers("Auth.");
        GameNameRequest gameName;
        try {
            gameName = gson.fromJson(request.body(), GameNameRequest.class);
        } catch (Exception e) {
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
        } catch (DataAccessException e) {
            response.status(getErrorResponse(e));
            return gson.toJson(new ErrorResponse("Error - " + e.getMessage()));
        }
    }

    private Object listgames(Request request, Response response) {
    }

    private Object logout(Request request, Response response) {
        response.type("application/json");
        String authToken = request.headers("Authorization");

        try {
            authService.logout(authToken);
            response.status(200);
            return "{}";
        }
        catch(DataAccessException e){
            response.status(getErrorStatus(e));
            return gson.toJson(new ErrorResponse("Error" + e.getMessage()));
        }
    }

    private Object login(Request request, Response response) {
        response.type("application/json");
        String requestBody = request.body();
        System.out.println(requestBody);
        userData user;
//        check for empty body
        if(requestBody == null || requestBody.trim().isEmpty()) {
            response.status(400);
            return gson.toJson(new ErrorResponse("Bad Request"));
        }
        JsonObject json;
        try {
            json = JsonParser.parseString(requestBody).getAsJsonObject();
            System.out.println(json.keySet());
        }
        catch(Exception e) {
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
            userData user1 = new userData(username.trim(), password.trim(), null);
            System.out.println("Attempting login from " + user1);

            try {
                authData authData = userService.login(user);
                System.out.println("successful login from " + username.trim());
                response.status(200);
                return gson.toJson(authData);
            }
            catch(DataAccessException e) {
                System.out.println("Login failed from " + username.trim() + "this the error: " + e.getMessage());
                int status = getErrorStatus(e);
                response.status(status);
                return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
            }
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            response.status(400);
            return gson.toJson(new ErrorResponse("Bad Requesttttt"));
        }
    }

    private int getErrorStatus(DataAccessException e) {
        String mes = e.getMessage().toLowerCase();
        if(mes.contains("bad request")) {
            return 400;
        }
        else if(mes.contains("unauthorized")) {
            return 401;
        }
        else if(mes.contains("already taken")) {
            return 403;
        }
        else {
            return 500;
        }
    }

//helper for each response (JSON)

private record ErrorResponse(String message) {}
private record ListGamesResponse(Collection<gameData> games) {}
private record CreateGamesResponse(int gameID) {}
private record GameNameRequest(String gameName) {}
private record JoinGameRequest(int gameID, String playerColor) {}


        Spark.post("/user", (req, res) -> {
            res.type("application/json");
            userData user = gson.fromJson(req.body(), userData.class);
            try {
                authData authData = userService.register(user);
                return gson.toJson(authData);
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Error " + e.getMessage()));
            }
        });

        Spark.post("/session", (req, res) -> {
            res.type("application/json");
            userData user = gson.fromJson(res.body(), userData.class);

            try {
                authData authData = userService.login(user);
                return gson.toJson(authData);
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Error " + e.getMessage()));
            }
        });

        Spark.post("/delete", (req, res) -> {
            res.type("application/json");
            String authToken = req.headers("authorization");

            try {
                authService.logout(authToken);
                res.status(200);
                return "{}";
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Error " + e.getMessage()));
            }
        });

        Spark.get("/game", (req, res) -> {
            res.type("application/json");
            String authToken = req.headers("authorization");

            try {
                return gson.toJson(new ListGameResponse(gameService.listGames(authToken)));
            } catch (Exception e) {
                res.status(401);
                return gson.toJson(new ErrorResponse("Error " + e.getMessage()));
            }
        });

        Spark.post("/game", (req, res) -> {
            res.type("application/json");
            String auth = req.headers("authorization");
            GameRequest request = gson.fromJson(req.body(), GameRequest.class);

            try {
                gameData game = gameService.createGame(auth, request.gameName());
                return gson.toJson(new GameResponse(game.gameID()));
            } catch (Exception e) {
                res.status(401);
                return gson.toJson(new ErrorResponse("Error " + e.getMessage()));
            }
        });

        Spark.put("/game", (req, res) -> {
            res.type("application/json");
            String auth2 = req.headers("authorization");
            JoinGameRequest joinGameRequest = gson.fromJson(req.body(), JoinGameRequest.class);
            try {
                gameService.joinGame(auth2, joinGameRequest.gameID(), joinGameRequest.playerColor());
                res.status(200);
                return "{ }";
            } catch (Exception e) {
                res.status(401);
                return gson.toJson(new ErrorResponse("Error " + e.getMessage()));
            }
        });

            Spark.delete("/db", (req, res) -> {
                res.type("application/json");
                try {
                    gameService.clear();
                    authService.clear();
                    userService.clear();
                    res.status(200);
                    return "{ }";
                } catch (Exception e) {
                    res.status(401);
                    return gson.toJson(new ErrorResponse("Error " + e.getMessage()));
                }
            });


        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }


}

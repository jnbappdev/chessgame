package server;
import spark.Spark;
import com.google.gson.Gson;
import service.authService;
import service.gameService;
import service.userService;
import dataAccess.authDAO;
import dataAccess.gameDAO;
import dataAccess.userDAO;
import dataAccess.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

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

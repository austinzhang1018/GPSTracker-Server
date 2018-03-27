package server;

import com.sun.net.httpserver.HttpServer;
import emailer.Emailer;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import handlers.HomeHandler;
import handlers.MapHandler;
import handlers.TrackerHandler;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.HashSet;

public class Server {
    private HttpServer server;
    private Configuration cfg;
    private HashSet<MapHandler> mapHandlers;

    public static void main(String[] args) throws Exception {
        Server s = new Server();
    }

    private Server() throws IOException, InterruptedException {
        //Configuration for the template engine
        cfg = new Configuration(Configuration.VERSION_2_3_27);
        String currentDirectoryName = new File(".").getAbsolutePath().substring(0, new File(".").getAbsolutePath().lastIndexOf("."));
        cfg.setDirectoryForTemplateLoading(new File(currentDirectoryName + "/src/templates"));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);

        //Create the HTTP server
        server = HttpServer.create(new InetSocketAddress(37896), 0);

        //Create the web handlers
        TrackerHandler trackerHandler = new TrackerHandler(this);
        HomeHandler homeHandler = new HomeHandler(this, cfg);
        server.createContext("/trackers", trackerHandler);
        server.createContext("/home", homeHandler);
        server.setExecutor(null); // creates a default executor
        mapHandlers = new HashSet<MapHandler>();
        server.start();
    }

    public void updateTrackedNames(String newName) {
        MapHandler potentialHandler = new MapHandler(cfg, newName);
        if (mapHandlers.add(potentialHandler)) {
            server.createContext("/" + newName + "map", potentialHandler);
        }
    }

    public void triggerGeofenceEvent(String name, String triggeredGeofence) {
        Emailer.sendEmail("austinzhang1018@gmail.com", "Geofence Alert", name + " has left " + triggeredGeofence + ". Track their location on the map. " + "http://24.208.163.239:37896/" + name + "map");
    }

    public HashSet<MapHandler> getMapHandlers() {
        return mapHandlers;
    }
}
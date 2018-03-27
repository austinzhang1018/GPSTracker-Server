package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.Server;
import tools.HttpStringifier;

import java.io.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Austin on 3/20/2018.
 */
public class TrackerHandler implements HttpHandler {
    private Server server;

    public TrackerHandler(Server server) {
        this.server = server;
    }

    public void handle(HttpExchange t) throws IOException {
        // parse request
        Map<String, Object> parameters = new HashMap<String, Object>();
        InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();

        System.out.println(query + " " + LocalTime.now());

        locationUpdateHandler(query);

        String response = "received";
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }


    //Used for saving tracking data, updating tracking list, and sending out geofence alerts
    /** NEED TO MAKE IT PERFORM ACTION (SEND EMAIL) WHEN GEOFENCE TRIGGERED **/
    private void locationUpdateHandler(String query) {
        String[] nameValuePairs = query.split("&");
        String name = "";
        double latitude = 0;
        double longitude = 0;
        float accuracy = 0;
        boolean geofenceEvent = false;
        String triggeredGeofence = "";
        int batteryPercentage = -1;

        //pull tracking info from string and store them
        for (String nameValuePair : nameValuePairs) {
            if (nameValuePair.contains("name")) {
                name = nameValuePair.substring(nameValuePair.indexOf("=") + 1);
            }
            else if (nameValuePair.contains("latitude")) {
                latitude = Double.parseDouble(nameValuePair.substring(nameValuePair.indexOf("=") + 1));
            }
            else if (nameValuePair.contains("longitude")) {
                longitude = Double.parseDouble(nameValuePair.substring(nameValuePair.indexOf("=") + 1));
            }
            else if (nameValuePair.contains("accuracy")) {
                accuracy = Float.parseFloat(nameValuePair.substring(nameValuePair.indexOf("=") + 1));
            }
            else if (query.contains("triggeredgeofence")) {
                triggeredGeofence = nameValuePair.substring(nameValuePair.indexOf("=") + 1);
                geofenceEvent = true;
            }
            else if (query.contains("battery")) {
                batteryPercentage = Integer.parseInt(nameValuePair.substring(nameValuePair.indexOf("=") + 1));
            }
        }

        if (geofenceEvent) {
            server.triggerGeofenceEvent(name, triggeredGeofence);
        }
        else {

            server.updateTrackedNames(name);

            //Build the string to be stored
            StringBuilder locSet = new StringBuilder();
            locSet.append(LocalDateTime.now().toString());
            locSet.append(" ");
            locSet.append(latitude);
            locSet.append(" ");
            locSet.append(longitude);
            locSet.append(" ");
            locSet.append(accuracy);
            locSet.append(" ");
            locSet.append(batteryPercentage);

            try (FileWriter fw = new FileWriter(new File(HttpStringifier.getCurrentDirectory() + "/locationhistories/" + name + "history.txt"), true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.println(locSet.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}

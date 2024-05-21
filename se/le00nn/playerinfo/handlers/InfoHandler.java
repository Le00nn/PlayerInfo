package se.le00nn.playerinfo.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class InfoHandler implements HttpHandler {
   private static int HTTP_OK = 200;
   private static int HTTP_CONTENT_TOO_LARGE = 413;
   private static int HTTP_BAD_REQUEST = 400;
   private static int HTTP_SERVICE_UNAVAILABLE = 503;

   public void handle(HttpExchange exchange) throws IOException {
      OutputStream os;
      try {
         if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.sendResponseHeaders(HTTP_BAD_REQUEST, 0L);
            OutputStream os = exchange.getResponseBody();
            os.write(new byte[0]);
            os.close();
         } else {
            String url = exchange.getRequestURI().toString();
            if (url.getBytes().length > 512) {
               exchange.sendResponseHeaders(HTTP_CONTENT_TOO_LARGE, 0L);
               os = exchange.getResponseBody();
               os.write(new byte[0]);
               os.close();
            } else {
               int currentPlayers;
               String players;
               byte[] response;
               OutputStream os;
               int maxPlayers;
               if (url.matches("^/players\\?format=json$")) {
                  maxPlayers = Bukkit.getMaxPlayers();
                  currentPlayers = Bukkit.getOnlinePlayers().length;
                  players = this.getPlayerNames();
                  JsonObject json = new JsonObject();
                  json.addProperty("status", HTTP_OK);
                  json.addProperty("max_players", maxPlayers);
                  json.addProperty("current_players", currentPlayers);
                  json.addProperty("players", "%info%");
                  if (players == null) {
                     response = (new Gson()).toJson(json).replace("\"%info%\"", "[]").getBytes(StandardCharsets.UTF_8);
                  } else {
                     response = (new Gson()).toJson(json).replace("\"%info%\"", players).getBytes(StandardCharsets.UTF_8);
                  }

                  exchange.getResponseHeaders().set("Content-Type", "application/json;charset=UTF-8");
                  exchange.sendResponseHeaders(HTTP_OK, (long)response.length);
                  os = exchange.getResponseBody();
                  os.write(response);
                  os.close();
               } else if (url.matches("^/players\\?format=xml$")) {
                  maxPlayers = Bukkit.getMaxPlayers();
                  currentPlayers = Bukkit.getOnlinePlayers().length;
                  players = this.getPlayerNames();
                  StringBuilder xml = new StringBuilder();
                  xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                  xml.append("<response>");
                  xml.append("<status>" + HTTP_OK + "</status>");
                  xml.append("<max_players>").append(maxPlayers).append("</max_players>");
                  xml.append("<current_players>").append(currentPlayers).append("</current_players>");
                  xml.append("<players>");
                  if (players == null) {
                     xml.append("[]");
                  } else {
                     xml.append(players.replaceAll("\\[|\\]|\"", ""));
                  }

                  xml.append("</players>");
                  xml.append("</response>");
                  response = xml.toString().getBytes(StandardCharsets.UTF_8);
                  exchange.getResponseHeaders().set("Content-Type", "application/xml;charset=UTF-8");
                  exchange.sendResponseHeaders(HTTP_OK, (long)response.length);
                  os = exchange.getResponseBody();
                  os.write(response);
                  os.close();
               } else {
                  exchange.sendResponseHeaders(HTTP_BAD_REQUEST, 0L);
                  os = exchange.getResponseBody();
                  os.write(new byte[0]);
                  os.close();
               }
            }
         }
      } catch (Exception var9) {
         var9.printStackTrace();
         exchange.sendResponseHeaders(HTTP_SERVICE_UNAVAILABLE, 0L);
         os = exchange.getResponseBody();
         os.write(new byte[0]);
         os.close();
      }
   }

   private String getPlayerNames() {
      ArrayList<String> playerNames = new ArrayList();
      Player[] var5;
      int var4 = (var5 = Bukkit.getOnlinePlayers()).length;

      for(int var3 = 0; var3 < var4; ++var3) {
         Player player = var5[var3];
         playerNames.add("\"" + player.getName() + "\"");
      }

      return playerNames.size() > 0 ? "[" + String.join(",", playerNames) + "]" : null;
   }
}

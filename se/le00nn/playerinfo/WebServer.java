package se.le00nn.playerinfo;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import se.le00nn.playerinfo.handlers.InfoHandler;

public class WebServer {
   private Plugin PlayerInfo;
   private HttpServer www;
   private boolean serverUp = false;

   public WebServer(int port, PlayerInfo playerInfo) {
      try {
         this.www = HttpServer.create(new InetSocketAddress(port), 0);
         this.www.createContext("/players", new InfoHandler());
         this.start();
         this.PlayerInfo = playerInfo;
         Bukkit.getLogger().info("Web server started on port: " + port + ".");
         Bukkit.getLogger().info("You can view it at: http://localhost:" + port);
      } catch (Exception var4) {
         var4.printStackTrace();
         this.serverUp = false;
         Bukkit.getPluginManager().disablePlugin(this.PlayerInfo);
      }

   }

   public void start() {
      if (this.www == null) {
         Bukkit.getLogger().severe("HTTP server not ready!");
      } else if (this.serverUp) {
         Bukkit.getLogger().severe("HTTP server is already running!");
      } else {
         try {
            this.serverUp = true;
            this.www.start();
         } catch (Exception var2) {
            var2.printStackTrace();
            this.serverUp = false;
            Bukkit.getPluginManager().disablePlugin(this.PlayerInfo);
         }
      }

   }

   public void stop() {
      if (this.www == null) {
         Bukkit.getLogger().severe("HTTP server not ready!");
      } else if (!this.serverUp) {
         Bukkit.getLogger().severe("HTTP server is not running!");
      } else {
         try {
            this.serverUp = false;
            this.www.stop(0);
         } catch (Exception var2) {
            var2.printStackTrace();
            this.serverUp = false;
            Bukkit.getPluginManager().disablePlugin(this.PlayerInfo);
         }
      }

   }

   public boolean isServerUp() {
      return this.serverUp;
   }
}

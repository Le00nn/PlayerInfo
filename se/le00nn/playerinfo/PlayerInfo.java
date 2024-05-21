package se.le00nn.playerinfo;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerInfo extends JavaPlugin {
   private WebServer www;

   public void onDisable() {
      Bukkit.getLogger().severe("Plugin has been disabled.");
      this.www.stop();
   }

   public void onEnable() {
      try {
         this.getConfiguration().load();
         String port = this.getConfiguration().getString("www.port");
         int realPort;
         if (port == null) {
            realPort = Bukkit.getServer().getPort() + 2;
            this.getConfiguration().setProperty("www.port", String.valueOf(realPort));
            Bukkit.getLogger().warning("No port defined in config.yml, setting default value to port: " + realPort);
            this.www = new WebServer(realPort, this);
         } else if (port.matches("^(6553[0-5]|655[0-2][0-9]|65[0-4][0-9]{2}|6[0-4][0-9]{3}|[1-5]?[0-9]{1,4})$")) {
            realPort = Integer.valueOf(port);
            this.www = new WebServer(realPort, this);
         } else {
            realPort = Bukkit.getServer().getPort() + 2;
            this.getConfiguration().setProperty("www.port", String.valueOf(realPort));
            Bukkit.getLogger().warning("Invalid port defined in config.yml, setting default value to port: " + realPort);
            this.www = new WebServer(realPort, this);
         }

         this.getConfiguration().save();
      } catch (Exception var3) {
         var3.printStackTrace();
         Bukkit.getPluginManager().disablePlugin(this);
      }

   }
}

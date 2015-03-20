package pl.cba.knest.TabErr;

import java.io.File;
import java.io.IOException;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class TabErr extends Plugin {
	public static TabErr instance;
	public Database db;
	public Configuration c;
	public void onEnable(){
		instance = this;
		reload();
		getProxy().getPluginManager().registerListener(this, new TabErrListener());
		log("Enabled TabErr");

	}
	public static void log(String msg) {
		instance.getLogger().info(msg);
	}
	public void reload(){
		if(!getDataFolder().exists()) getDataFolder().mkdirs();
		File fc = new File(getDataFolder(),"config.yml");
		if(!fc.exists()){
			try {
				fc.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				log("Can't create new config.yml");
				return;
			}
		}
		try {
			c = ConfigurationProvider.getProvider(YamlConfiguration.class).load(fc);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String host = cGetString("db.host","localhost");
		String user = cGetString("db.user","factions");
		String port = cGetString("db.port","3306");	
		String pass = cGetString("db.password","pass");	
		String dbn  = cGetString("db.database","factions");
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(c, fc);
		} catch (IOException e) {
			log("Can't save config.yml");
			return;
		}
		db = new Database(host,user,port,pass,dbn);
		
	}
	public Configuration getConfig(){
		return c;
	}
	
	public String cGetString(String path, String def){
		Object v = getConfig().get(path);
		if(!(v instanceof String)){
			getConfig().set(path, def);
			v = def;
		}
		return (String) v;
	}
	public static TabErr getInstance() {
		return instance;
		
	}
	public Database getDatabase() {
		return db;
		
	}

}

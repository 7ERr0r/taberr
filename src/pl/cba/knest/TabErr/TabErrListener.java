package pl.cba.knest.TabErr;

import java.lang.reflect.Field;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class TabErrListener implements Listener {
	
	@EventHandler
	public void onPostLogin(PostLoginEvent e){
		UserConnection c = (UserConnection) e.getPlayer();
		try {
			Field f = c.getClass().getDeclaredField("tabListHandler");
			f.setAccessible(true);
			f.set(c, new TabErrServerUnique(c));
			
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchFieldException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		};
		//e.getPlayer().setTabList(new TabErrHandler(e.getPlayer()));
	}
	
}

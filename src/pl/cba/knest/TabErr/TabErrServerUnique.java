package pl.cba.knest.TabErr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;







import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.PlayerListItem;
import net.md_5.bungee.protocol.packet.PlayerListItem.Action;
import net.md_5.bungee.tab.ServerUnique;

public class TabErrServerUnique extends ServerUnique implements Runnable {


	
	
	private ProxiedPlayer p;
	private ArrayList<String> players = new ArrayList<String>();
	private boolean compatible;
	private int cc = 0;
	ScheduledTask t;
	public TabErrServerUnique(ProxiedPlayer p) {
		super(p);
		this.p = p;
		this.compatible = p.getPendingConnection().getVersion()<ProtocolConstants.MINECRAFT_1_8;
		if(isCompatible()){
			t = BungeeCord.getInstance().getScheduler().schedule(TabErr.instance, this, 10000L, 10000L, TimeUnit.MILLISECONDS);
		}
		
	}
	private boolean isCompatible(){
		return compatible;
		
	}
    @Override
	public void onUpdate(PlayerListItem playerListItem){
		if(!isCompatible()){
			super.onUpdate(playerListItem);
			return;
		}
		

	}

	
	public void addPlayer(String name){
		if(name.length()>16) name = name.substring(0, 16);
    	PlayerListItem pc = new PlayerListItem();
    	pc.setAction( Action.ADD_PLAYER );
    	PlayerListItem.Item it = new PlayerListItem.Item();
    	it.setUsername(name);
    	it.setDisplayName(name);
    	it.setUuid(UUID.randomUUID());
    	players.add(it.getDisplayName());
    	it.setPing(69);
    	it.setGamemode(0);
    	it.setProperties(new String[][]{});
    	pc.setItems(new PlayerListItem.Item[]{it});
    	player.unsafe().sendPacket(pc);
	}
	
	public void addPlayer(){
		String a = "";
		int b = cc++;
		while(b>0){
			a = ChatColor.values()[b%16]+a;
			b = b/16;
		}
		addPlayer(a);
	}
	public void removePlayer(String name){
    	PlayerListItem pc = new PlayerListItem();
    	pc.setAction( Action.REMOVE_PLAYER );
    	PlayerListItem.Item it = new PlayerListItem.Item();
    	it.setUsername(name);
    	it.setDisplayName(name);
    	pc.setItems(new PlayerListItem.Item[]{it});
    	player.unsafe().sendPacket(pc);
	}
	public void update(){
		cc = 0;
		handleClear();
		handleUpdate();
	}
	public void handleClear(){
		ListIterator<String> i = players.listIterator(players.size());
		while(i.hasPrevious()){
			removePlayer(i.previous());
		}
		players = new ArrayList<String>();
	}
	public void handleUpdate(){
		int online = BungeeCord.getInstance().getOnlineCount();
		String server = p.getServer()==null?"brak":p.getServer().getInfo().getName();
		ResultSet tpr = TabErr.getInstance().getDatabase().query("SELECT `nick`,`rank` FROM `players` ORDER BY `rank` DESC LIMIT 0,20;");
		ArrayList<String> tp = new ArrayList<String>();
		ArrayList<String> tg = new ArrayList<String>();
		int rp = 0;
		try {
			while(tpr.next()){
				tp.add(tpr.getString("nick")+" ");
			}
			ResultSet tgr = TabErr.getInstance().getDatabase().query("SELECT `tag`,(SUM(`rank`)/(COUNT(`rank`)+1)) AS `avg` FROM `players`,`guilds` WHERE `players`.`guild`=`guilds`.`id` GROUP BY `guild` ORDER BY `avg` DESC LIMIT 0,20");


			while(tgr.next()){
				tg.add(ChatColor.BOLD+tgr.getString("tag")+ChatColor.RESET+" "+(tgr.getInt("avg")+1000));
			}
			ResultSet rpr = TabErr.getInstance().getDatabase().query("SELECT `rank` FROM `players` WHERE `nick`='"+p.getName()+"' LIMIT 1;");
			if(rpr.next()){
				rp = rpr.getInt("rank");
			}
		} catch (SQLException e) {
			return;
		}
		for(int y = 0; y<20; y++){
			for(int x = 0; x<3; x++){
    			if(y==1){
    				if(x==0){
    					addPlayer();
    				}else if(x==1){
    					addPlayer("&lNESTERIA".replace('&', ChatColor.COLOR_CHAR));
    				}else if(x==2){
    					addPlayer();
    				}
    			}else if(y==2){
    				if(x==0){
    					addPlayer();
    				}else if(x==1){
    					addPlayer(("&9&lOnline:&f"+online).replace('&', ChatColor.COLOR_CHAR));
    				}else if(x==2){
    					addPlayer();
    				}
    			}else if(y==4){
    				if(x==0){
    					addPlayer("&9&lRanking:".replace('&', ChatColor.COLOR_CHAR));
    				}else if(x==1){
    					addPlayer();
    				}else if(x==2){
    					addPlayer("&9&lTop gildii:".replace('&', ChatColor.COLOR_CHAR));
    				}
    			}else if(y>=5){
       				if(x==0){
       					int i = y-5;
    					if(tp.size()>i) addPlayer(ChatColor.GRAY+tp.get(i));
    					else addPlayer();
    				}else if(x==1){
    					if(y==7){
    						addPlayer("Serwer: "+server);
    					}else if(y==9){
    						addPlayer("Rank: "+(1000+rp));
    					}else if(y==11){
    						addPlayer("Ping: "+p.getPing()+"ms");
    					}else addPlayer();
    				}else if(x==2){
    					int i = y-5;
    					if(tg.size()>i) addPlayer(ChatColor.GRAY+tg.get(i));
    					else addPlayer();
    				}
    			}else{
    				addPlayer();
    			}
    		}
    	}
		
	}
	
    @Override
    public void onServerChange()
    {
    	if(!isCompatible()){
    		super.onServerChange();
    		return;
    	}
    }
    
    public void onConnect()
    {
    }

    public void onDisconnect()
    {
    	if(t!=null){
    		t.cancel();
    		t = null;
    	}
    	players = null;
    	p = null;
    }
	@Override
	public void run() {
		update();
	}
    

}

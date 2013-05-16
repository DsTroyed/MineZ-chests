//to be dited here

public class RandomSpawn{
  private main pl;
  public getItems(main instance) {
		pl = instance;
	}
  public void spawn(){
    getPlaces();
    for (Location loc : list){
      loc.getBlock().setType(Material.chest);
    }
  }
  public void Getplaces(){
    int respawn = pl.c.getConfig().getInt("spawnscheduler")*1200;
    pl.getServer().getScheduler().syncRepeatinTask(main, {
        run(){
          World[] worlds = pl.getServer().getEnabledWorlds();
          for (World world : worlds){
            //...
          }
          //...
          Location[] list;
          return(list)
        }
      },respawn,respawn);
  }

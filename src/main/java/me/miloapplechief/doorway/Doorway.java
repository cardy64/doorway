package me.miloapplechief.doorway;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Doorway extends JavaPlugin {

    private static final Material ROOM1 = Material.SEA_LANTERN;
    private static final Material ROOM2 = Material.GLOWSTONE;

    Material thisRoom = ROOM1;
    Material otherRoom = ROOM2;
    int timer = -1;
    double playerZ;

    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;
        playerZ = player.getEyeLocation().getZ();

        World world = getServer().getWorld("doorway");
        if (world == null) {
            sender.sendMessage("The world \"doorway\" is not loaded");
            return false;
        }
        Material material;
        material = Material.OBSIDIAN;
        for (int x=-2 ; x <= 2 ; x++) {
            for (int y = 0; y <= 4; y++) {
                if (x == -2 || y == 0 || x == 2 || y == 4)
                world.getBlockAt(x, y, 0).setType(material);
            }
        }
        if (timer != -1) {
            getServer().getScheduler().cancelTask(timer);
            timer = -1;
        }
        timer = getServer().getScheduler().scheduleSyncRepeatingTask(this, ()-> {
            if (player.getWorld() != world) {
                return;
            }

            double newPlayerZ = player.getEyeLocation().getZ();
            if ((playerZ > 0.5) != (newPlayerZ > 0.5)) {
                if (insidePortal(player.getEyeLocation().getX(), player.getEyeLocation().getY())) {
                    Material tmp = thisRoom;
                    thisRoom = otherRoom;
                    otherRoom = tmp;
                }
            }
            playerZ = newPlayerZ;

            for (int x=-6 ; x <= 6 ; x++) {
                for (int y = 0; y <= 10; y++) {
                    for (int z = -6; z <= 6; z++) {
                        if ((x == -6 || y == 0 || z == -6 || x == 6 || y == 10 || z == 6) &&
                             !(z == 0 && y == 0 && x >= -2 && x < 2)) {

                            Material material2 = thisRoom;

                            double cx = x + 0.5;
                            double cy = y + 0.5;
                            double cz = z + 0.5;

                            double ex = player.getEyeLocation().getX();
                            double ey = player.getEyeLocation().getY();
                            double ez = player.getEyeLocation().getZ();

                            double vx = cx - ex;
                            double vy = cy - ey;
                            double vz = cz - ez;

                            double length = Math.sqrt(vx * vx + vy * vy + vz * vz);

                            if (length != 0) {


                                vx /= length;
                                vy /= length;
                                vz /= length;

                                if (vz != 0) {
                                    double t = (0.5 - ez) / vz;

                                    if (t > 0) {

                                        double px = ex + vx * t;
                                        double py = ey + vy * t;

                                        if (insidePortal(px, py)) {
                                            material2 = otherRoom;
                                        }
                                    }
                                }
                            }

                            world.getBlockAt(x, y, z).setType(material2);
                        }
                    }
                }
            }

            },0, 1);

        return true;
    }

    private static boolean insidePortal(double x, double y) {
        return x > -1.5 && x < 2.5 && y > 0.5 && y < 4.5;
    }

}

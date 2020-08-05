package io.github.bluelhf.cenchants.utilities;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShapeUtil {
    public static List<Location> wireframe(Location corner1, Location corner2, double sparsity) {
        World w = corner1.getWorld();
        double c1x = corner1.getX();
        double c1y = corner1.getY();
        double c1z = corner1.getZ();
        double c2x = corner2.getX();
        double c2y = corner2.getY();
        double c2z = corner2.getZ();
        Location l000 = new Location(w, c1x, c1y, c1z);
        Location l001 = new Location(w, c1x, c1y, c2z);
        Location l010 = new Location(w, c1x, c2y, c1z);
        Location l011 = new Location(w, c1x, c2y, c2z);
        Location l100 = new Location(w, c2x, c1y, c1z);
        Location l101 = new Location(w, c2x, c1y, c2z);
        Location l110 = new Location(w, c2x, c2y, c1z);
        Location l111 = new Location(w, c2x, c2y, c2z);
        List<Location> wireframe = new ArrayList<>();
        wireframe.addAll(line(l000, l010, sparsity));
        wireframe.addAll(line(l010, l110, sparsity));
        wireframe.addAll(line(l110, l100, sparsity));
        wireframe.addAll(line(l100, l000, sparsity));

        wireframe.addAll(line(l001, l011, sparsity));
        wireframe.addAll(line(l011, l111, sparsity));
        wireframe.addAll(line(l111, l101, sparsity));
        wireframe.addAll(line(l101, l001, sparsity));

        wireframe.addAll(line(l000, l001, sparsity));
        wireframe.addAll(line(l010, l011, sparsity));
        wireframe.addAll(line(l110, l111, sparsity));
        wireframe.addAll(line(l100, l101, sparsity));

        return wireframe;
    }

    public static List<Location> line(Location l1, Location l2, double sparsity) {
        if (sparsity == 0) return new ArrayList<>();
        Vector v = l2.toVector().subtract(l1.toVector());
        v.normalize();
        List<Location> line = new ArrayList<>();
        for(double i = 0; i < l1.distance(l2); i += sparsity) {
            line.add(l1.clone().add(v.clone().multiply(i)));
        }

        return line;
    }
}

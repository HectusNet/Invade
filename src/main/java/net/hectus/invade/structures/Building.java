package net.hectus.invade.structures;

import net.hectus.lang.Translation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public enum Building {
    // CHIA_HONG_CHUA(new int[]{}, new int[]{}, true),
    YUM_YUM(new int[]{ -6264, -6352, -6355, -6219 }, new int[]{ -3580, -3568, -3434, -3453 }, true),
    // AONU(new int[]{}, new int[]{}, true),
    // LEDL(new int[]{}, new int[]{}, true),
    // ONIO(new int[]{}, new int[]{}, true),
    // RICHER_WITCHER(new int[]{}, new int[]{}, true),
    // APOTHEKE(new int[]{}, new int[]{}, true),
    // DRUGGY_MUGGY(new int[]{}, new int[]{}, true),
    // INDIA_SHOP(new int[]{}, new int[]{}, true),
    // CURRYMASTER(new int[]{}, new int[]{}, true),
    // WILLYS_WONKA(new int[]{}, new int[]{}, true),
    // SWEETY_FOX(new int[]{}, new int[]{}, true),
    // WC_DONALDS(new int[]{}, new int[]{}, true),
    BURGER_QUEEN(new int[]{ -6692, -6786, -6686, -6643, -6594, -6674 }, new int[]{ -3943, -4036, -4096, -4096, -3994, -3943 }, true),
    // CHECK_MY_MEAT(new int[]{}, new int[]{}, true),
    // ROCK_N_COOK(new int[]{}, new int[]{}, true),
    // DISCO(new int[]{}, new int[]{}, true),
    // CINEMA(new int[]{}, new int[]{}, true),
    // ANNUAL_ARMOR(new int[]{}, new int[]{}, true),
    // ROCKING_WEAPONS(new int[]{}, new int[]{}, true),
    // OPEN_LIBRARY(new int[]{}, new int[]{}, false),
    // MC_PAPER(new int[]{}, new int[]{}, true),
    EAST_SIDE_SECURITY(new int[]{ -5248, -5308, -5203, -5132, -5131 }, new int[]{ -3708, -3575, -3512, -3635, -3697 }, false),
    // WEST_SIDE_SECURITY(new int[]{}, new int[]{}, false),
    WILLYS_WOOD(new int[]{ -6356, -6356, -6326, -6186, -6076, -6033, -6013, -6003, -5963, -5953, -5943, -5953, -6033, -6153, -6243 },
            new int[]{ -3303, -3406, -3426, -3456, -3506, -3536, -3516, -3496, -3456, -3436, -3423, -3413, -3373, -3333, -3313 }, true),
    // BEAUTY_FLOWERS(new int[]{}, new int[]{}, true),
    // CUTIE_PETS(new int[]{}, new int[]{}, true),
    // FISH_N_FRIENDS(new int[]{}, new int[]{}, true),
    // NEWTON(new int[]{}, new int[]{}, true),
    // HECTAFONE(new int[]{}, new int[]{}, true),
    // A_AND_C(new int[]{}, new int[]{}, true),
    // M_AND_H(new int[]{}, new int[]{}, true),
    // EAST_SIDE_TOILETS(new int[]{}, new int[]{}, false),
    // WEST_SIDE_TOILETS(new int[]{}, new int[]{}, false),
    // MOONBUCKS(new int[]{}, new int[]{}, true),
    // KEBABSHOP(new int[]{}, new int[]{}, true),
    // BUBBLETEA(new int[]{}, new int[]{}, true),
    // BAKED_POTATOES(new int[]{}, new int[]{}, true),
    // PIZZA(new int[]{}, new int[]{}, true),
    // BILLYS_BURGERS(new int[]{}, new int[]{}, true),
    // WOOL(new int[]{}, new int[]{}, true),
    // MUSICAL_LIFE(new int[]{}, new int[]{}, true),
    // WAXY_CANDLES(new int[]{}, new int[]{}, true),
    // CANTEEN(new int[]{}, new int[]{}, true),
    // PLAYFUL_TOYS(new int[]{}, new int[]{}, true),
    // HELO(new int[]{}, new int[]{}, true),
    GENERIC(new int[]{ -6473, -6473, -6663, -6793, -6893, -6913, -6943, -7116, -7116, -7076, -7026, -6976, -6876, -6796, -6636 },
            new int[]{ -4286, -4173, -4127, -4056, -3974, -3933, -3843, -3843, -3896, -3986, -4056, -4106, -4176, -4216, -4266 }, true);
    // DOLLAR_STORE(new int[]{}, new int[]{}, true);

    public final Polygon boundary;
    public final boolean destination;

    Building(int[] xPoints, int[] yPoints, boolean destination) { // The last numeral represents one decimal. So 1234 would mean 123.4. This is needed, as polygons only support integers.
        this.boundary = new Polygon(xPoints, yPoints, xPoints.length);
        this.destination = destination;
    }

    public String getTranslated(Locale locale) {
        return Translation.string(locale, "building." + name().toLowerCase());
    }

    @Contract(" -> new")
    public @NotNull Cord middle() {
        return new Cord(Arrays.stream(boundary.xpoints).sum() / boundary.npoints, Arrays.stream(boundary.ypoints).sum() / boundary.npoints);
    }

    public boolean contains(@NotNull Player player) {
        return boundary.contains(player.getX(), player.getY());
    }

    public static List<Building> destinations() {
        return Arrays.stream(values())
                .filter(building -> building.destination)
                .toList();
    }
}

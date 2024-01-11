package net.hectus.invade;

import net.hectus.Translation;

import java.util.Locale;

@SuppressWarnings("unused")
public enum Building {
    // TODO: Check all these
    MIDDLE(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    NEJ_TON(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    BANG_BING(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    C(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    PETS(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    NATURE_SHOP(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    AONU(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    APOTHEKE(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    V(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    ONIO(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    INDIAN_FOOD(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    U(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    WC1(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    RESTAURANT(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    ARMOR(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    CHINESE_FOOD(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    HECTAFONE(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    HELO(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    MOONBUCKS(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    NO_NAME(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    MC_ONALDS(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    ARP(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    DAIRI_HI(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    WIL(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    DISCO(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    SOP(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    GENIE(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    WC2(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    LIBRARY(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0)),
    STAFF_ONLY(new Cord(0, 0, 0), new Cord(0, 0, 0), new Cord(0, 0, 0));

    public record Cord(int x, int floor, int z) {}

    public final Cord middle;
    public final Cord corner1;
    public final Cord corner2;

    Building(Cord middle, Cord corner1, Cord corner2) {
        this.middle = middle;
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    public String translate(Locale locale) {
        return Translation.string(locale, "building.floor" + (corner1.floor + 1) + "." + name().toLowerCase());
    }
}

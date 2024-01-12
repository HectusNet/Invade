package net.hectus.invade;

import net.hectus.Translation;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("unused")
public enum Building {
    // TODO: Check all these
    NAJOTON(new Cord(0, 0, 0), new Cord(844, 0, 557), new Cord(821, 0, 533), true),
    BANG_BING(new Cord(0, 0, 0), new Cord(850, 0, 557), new Cord(865, 0, 547), true),
    C(new Cord(0, 0, 0), new Cord(867, 0, 530), new Cord(880, 0, 541), true),
    PETS(new Cord(0, 0, 0), new Cord(899, 0, 540), new Cord(883, 0, 530), true),
    NATURE_SHOP(new Cord(0, 0, 0), new Cord(935, 0, 540), new Cord(902, 0, 528), true),
    AONU(new Cord(0, 0, 0), new Cord(820, 0, 600), new Cord(846, 0, 579), true),
    APOTHEKE(new Cord(0, 0, 0), new Cord(845, 0, 604), new Cord(819, 0, 613), true),
    V(new Cord(0, 0, 0), new Cord(824, 0, 626), new Cord(818, 0, 644), true),
    ONIO(new Cord(0, 0, 0), new Cord(827, 0, 625), new Cord(845, 0, 643), true),
    INDIAN_FOOD(new Cord(0, 0, 0), new Cord(843, 0, 652), new Cord(827, 0, 646), true),
    U(new Cord(0, 0, 0), new Cord(865, 0, 578), new Cord(855, 0, 606), true),
    WC1(new Cord(0, 0, 0), new Cord(855, 0, 609), new Cord(865, 0, 615), false),
    RESTAURANT(new Cord(0, 0, 0), new Cord(883, 0, 571), new Cord(914, 0, 559), true),
    ARMOR(new Cord(0, 0, 0), new Cord(897, 0, 596), new Cord(884, 0, 610), true),
    CHINESE_FOOD(new Cord(0, 0, 0), new Cord(913, 0, 597), new Cord(903, 0, 609), true),
    HECTAFONE(new Cord(0, 0, 0), new Cord(898, 0, 628), new Cord(884, 0, 614), true),
    HELO(new Cord(0, 0, 0), new Cord(917, 0, 629), new Cord(901, 0, 613), true),
    MOONBUCKS(new Cord(0, 0, 0), new Cord(949, 0, 587), new Cord(960, 0, 573), true),
    NO_NAME(new Cord(0, 0, 0), new Cord(959, 0, 560), new Cord(948, 0, 570), true),
    MC_ONALDS(new Cord(0, 0, 0), new Cord(944, 0, 596), new Cord(958, 0, 610), true),
    ARP(new Cord(0, 0, 0), new Cord(927, 0, 609), new Cord(0, 941, 596), true),
    DAIRI_HI(new Cord(0, 0, 0), new Cord(926, 0, 612), new Cord(941, 0, 629), true),
    WIL(new Cord(0, 0, 0), new Cord(944, 0, 629), new Cord(959, 0, 613), true),
    DISCO(new Cord(0, 0, 0), new Cord(926, 0, 639), new Cord(904, 0, 655), true),
    SDF(new Cord(0, 0, 0), new Cord(962, 0, 637), new Cord(977, 0, 653), true),
    GENIE(new Cord(0, 0, 0), new Cord(974, 0, 586), new Cord(1000, 0, 559), true),
    WC2(new Cord(0, 0, 0), new Cord(974, 0, 589), new Cord(1000, 0, 601), false),
    LIBRARY(new Cord(0, 0, 0), new Cord(1009, 0, 627), new Cord(1036, 0, 585), true),
    STAFF_ONLY(new Cord(0, 0, 0), new Cord(996, 0, 631), new Cord(1036, 0, 652), false);

    public record Cord(int x, int floor, int z) {}

    public final Cord middle;
    public final Cord corner1;
    public final Cord corner2;
    public final boolean destination;

    Building(Cord middle, Cord corner1, Cord corner2, boolean destination) {
        this.middle = middle;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.destination = destination;
    }

    public String translate(Locale locale) {
        return Translation.string(locale, "building.floor" + (corner1.floor + 1) + "." + name().toLowerCase());
    }

    public static List<Building> destinations() {
        return Arrays.stream(values())
                .filter(building -> building.destination)
                .toList();
    }
}

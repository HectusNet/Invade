package net.hectus.invade.game_events;

import com.marcpg.util.Randomizer;
import net.hectus.invade.Invade;
import net.hectus.invade.structures.Building;
import net.hectus.invade.match.Match;
import net.hectus.lang.Translation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.bukkit.entity.EntityType.*;

public class ChaosEvent extends Event {
    public static final List<EntityType> ENTITY_TYPES = List.of(
            WITHER_SKELETON, STRAY, HUSK, ZOMBIE_VILLAGER, SKELETON_HORSE, ZOMBIE_HORSE, DONKEY, MULE, EVOKER, VEX,
            VINDICATOR, CREEPER, SKELETON, SPIDER, ZOMBIE, SLIME, GHAST, ZOMBIFIED_PIGLIN, ENDERMAN, CAVE_SPIDER,
            SILVERFISH, BLAZE, MAGMA_CUBE, BAT, WITCH, ENDERMITE, GUARDIAN, SHULKER, PIG, SHEEP, COW, CHICKEN, SQUID,
            WOLF, MUSHROOM_COW, SNOWMAN, OCELOT, IRON_GOLEM, HORSE, RABBIT, POLAR_BEAR, LLAMA, PARROT, VILLAGER, TURTLE,
            PHANTOM, DROWNED, CAT, PANDA, PILLAGER, RAVAGER, TRADER_LLAMA, WANDERING_TRADER, FOX, BEE, HOGLIN, PIGLIN,
            STRIDER, ZOGLIN, AXOLOTL, GLOW_SQUID, GOAT, ALLAY, FROG, CAMEL, SNIFFER
    );

    public final List<Entity> entities = new ArrayList<>();
    public final List<EntityType> entityTypes = new ArrayList<>();

    public ChaosEvent(Match match, int mobs) {
        super(match);
        for (int i = 0; i < mobs; i++) {
            entityTypes.add(Randomizer.fromCollection(ENTITY_TYPES));
        }
    }

    public ChaosEvent(Match match, EntityType... mobs) {
        super(match);
        entityTypes.addAll(List.of(mobs));
    }

    @Override
    public void run() {
        for (EntityType type : entityTypes) {
            entities.add(match.world.spawnEntity(Randomizer.fromArray(Building.values()).middle().toLocation(match.world), type));
        }
        Bukkit.getScheduler().runTaskLater(Invade.PLUGIN, this::done, 1200);
    }

    @Override
    public void done() {
        for (Entity entity : entities) {
            if (!entity.isDead()) entity.remove();
        }
    }

    @Override
    public String getTranslated(Locale locale) {
        return Translation.string(locale, "event.chaos.info");
    }
}

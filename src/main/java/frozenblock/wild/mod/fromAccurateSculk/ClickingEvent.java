package frozenblock.wild.mod.fromAccurateSculk;

import net.minecraft.tag.GameEventTags;
import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.world.event.GameEvent;

public class ClickingEvent extends GameEventTags {
    protected static final RequiredTagList<GameEvent> REQUIRED_TAGS = GameEventTags.REQUIRED_TAGS;
    public static final Tag.Identified<GameEvent> CLICKING = ClickingEvent.register("clicking");
    public static final Tag.Identified<GameEvent> DEATH_TAG = ClickingEvent.register("death_tag");

    private static Tag.Identified<GameEvent> register(String string) {
        return REQUIRED_TAGS.add(string);
    }

    public static TagGroup<GameEvent> getTagGroup() {
        return REQUIRED_TAGS.getGroup();
    }
}

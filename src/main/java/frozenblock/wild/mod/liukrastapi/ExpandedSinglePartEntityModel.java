package frozenblock.wild.mod.liukrastapi;

import net.minecraft.client.model.ModelPart;

import java.util.Optional;

public interface ExpandedSinglePartEntityModel {
	Optional<ModelPart> getAnyDescendantWithName(String string);
}

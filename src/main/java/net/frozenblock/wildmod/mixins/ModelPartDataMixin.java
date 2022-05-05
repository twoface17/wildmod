package net.frozenblock.wildmod.mixins;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.frozenblock.wildmod.liukrastapi.ExpandedModelPart;
import net.minecraft.client.model.ModelCuboidData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mixin(ModelPartData.class)
public abstract class ModelPartDataMixin implements ExpandedModelPart {

	@Shadow
	@Final
	private List<ModelCuboidData> cuboidData;
	@Shadow
	@Final
	private Map<String, ModelPartData> children;

	/**
	 * @author FrozenBlock
	 * @reason modelparts
	 */
	/*@Overwrite
	public ModelPart createPart(int textureWidth, int textureHeight) {
		Object2ObjectArrayMap<String, ModelPart> object2ObjectArrayMap = this.children.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (entry) -> {
			return (entry.getValue()).createPart(textureWidth, textureHeight);
		}, (modelPartx, modelPart2) -> {
			return modelPartx;
		}, Object2ObjectArrayMap::new));
		List<ModelPart.Cuboid> list = this.cuboidData.stream().map((modelCuboidData) -> {
			return modelCuboidData.createCuboid(textureWidth, textureHeight);
		}).collect(ImmutableList.toImmutableList());
		ModelPart modelPart = new ModelPart(list, object2ObjectArrayMap);
		modelPart.traverse().forEach(modelPart1 -> ((ExpandedModelPart)modelPart1).resetTransform());
		return modelPart;
	}*/

	public abstract ModelPart traverse();
}

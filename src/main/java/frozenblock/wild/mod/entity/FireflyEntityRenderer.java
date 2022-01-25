package frozenblock.wild.mod.entity;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.WildModClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class FireflyEntityRenderer extends EntityRenderer<FireflyEntity> {

    public FireflyEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(FireflyEntity entity) {
        return new Identifier(WildMod.MOD_ID, "textures/entity/firefly/firefly.png");
    }
}

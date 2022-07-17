package net.frozenblock.wildmod.liukrastapi;

import net.minecraft.client.model.ModelTransform;
import net.minecraft.util.math.Vec3f;

public interface ExpandedModelPart {
    ModelTransform getDefaultTransform();

    void setDefaultTransform(ModelTransform modelTransform);

    void resetTransform();

    boolean hasChild(String string);

    float getXScale();

    float getYScale();

    float getZScale();

    void translate(Vec3f vector3f);

    void rotate(Vec3f vector3f);

    void scale(Vec3f vector3f);

    //ModelPart createPart(int textureWidth, int textureHeight);


    boolean getHidden();

    void setHidden(boolean value);
}

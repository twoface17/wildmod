package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.items.GoatHornItem;
import net.frozenblock.wildmod.items.Instrument;
import net.frozenblock.wildmod.items.InstrumentTags;
import net.frozenblock.wildmod.liukrastapi.WildGoat;
import net.frozenblock.wildmod.registry.RegisterItems;
import net.frozenblock.wildmod.registry.WildRegistry;
import net.frozenblock.wildmod.world.gen.random.WildAbstractRandom;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.gen.random.AbstractRandom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(GoatEntity.class)
public abstract class GoatEntityMixin implements WildGoat {
/*
    @Shadow public abstract boolean isScreaming();

    private static final TrackedData<Boolean> LEFT_HORN = DataTracker.registerData(GoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> RIGHT_HORN = DataTracker.registerData(GoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public ItemStack method_43690() {
        GoatEntity goat = GoatEntity.class.cast(this);
        AbstractRandom abstractRandom = WildAbstractRandom.createAtomic(goat.getUuid().hashCode());
        TagKey<Instrument> tagKey = this.isScreaming() ? InstrumentTags.SCREAMING_GOAT_HORNS : InstrumentTags.REGULAR_GOAT_HORNS;
        RegistryEntryList<Instrument> registryEntryList = WildRegistry.INSTRUMENT.getOrCreateEntryList(tagKey);
        return GoatHornItem.getStackForInstrument(RegisterItems.GOAT_HORN, registryEntryList.getRandom((Random) abstractRandom).get());
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void initDataTracker(CallbackInfo ci) {
        GoatEntity goat = GoatEntity.class.cast(this);
        goat.getDataTracker().startTracking(LEFT_HORN, true);
        goat.getDataTracker().startTracking(RIGHT_HORN, true);
    }

    public boolean hasLeftHorn() {
        GoatEntity goat = GoatEntity.class.cast(this);
        return goat.getDataTracker().get(LEFT_HORN);
    }

    public boolean hasRightHorn() {
        GoatEntity goat = GoatEntity.class.cast(this);
        return goat.getDataTracker().get(RIGHT_HORN);
    }

    public boolean dropHorn() {
        GoatEntity goat = GoatEntity.class.cast(this);
        boolean bl = this.hasLeftHorn();
        boolean bl2 = this.hasRightHorn();
        if (!bl && !bl2) {
            return false;
        } else {
            TrackedData<Boolean> trackedData;
            if (!bl) {
                trackedData = RIGHT_HORN;
            } else if (!bl2) {
                trackedData = LEFT_HORN;
            } else {
                trackedData = goat.getRandom().nextBoolean() ? LEFT_HORN : RIGHT_HORN;
            }

            goat.getDataTracker().set(trackedData, false);
            Vec3d vec3d = goat.getPos();
            ItemStack itemStack = this.method_43690();
            double d = MathHelper.nextBetween(goat.getRandom(), -0.2F, 0.2F);
            double e = MathHelper.nextBetween(goat.getRandom(), 0.3F, 0.7F);
            double f = MathHelper.nextBetween(goat.getRandom(), -0.2F, 0.2F);
            ItemEntity itemEntity = new ItemEntity(goat.world, vec3d.getX(), vec3d.getY(), vec3d.getZ(), itemStack, d, e, f);
            goat.world.spawnEntity(itemEntity);
            return true;
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("HasLeftHorn", this.hasLeftHorn());
        nbt.putBoolean("HasRightHorn", this.hasRightHorn());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        GoatEntity goat = GoatEntity.class.cast(this);
        goat.getDataTracker().set(LEFT_HORN, nbt.getBoolean("HasLeftHorn"));
        goat.getDataTracker().set(RIGHT_HORN, nbt.getBoolean("HasRightHorn"));
    }*/
}

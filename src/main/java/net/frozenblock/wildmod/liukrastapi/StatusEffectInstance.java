package net.frozenblock.wildmod.liukrastapi;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class StatusEffectInstance extends net.minecraft.entity.effect.StatusEffectInstance {
    private Supplier<FactorCalculationData> factorCalculationDataSupplier = () -> {
        return null;
    };
    private static final Logger LOGGER = LogUtils.getLogger();
    private final StatusEffectInstance type;
    int duration;
    private int amplifier;
    private boolean ambient;
    private boolean permanent;
    private boolean showParticles;
    private boolean showIcon;
    @Nullable
    private StatusEffectInstance hiddenEffect;
    private Optional<FactorCalculationData> factorCalculationData;

    //public StatusEffectInstance(StatusEffectInstance statusEffect) {
        //this(statusEffect, 0, 0);
   // }

    public StatusEffectInstance(StatusEffectInstance type, int duration) {
        this(type, duration, 0);
    }

    public StatusEffectInstance(StatusEffectInstance type, int duration, int amplifier) {
        this(type, duration, amplifier, false, true);
    }

    public StatusEffectInstance(StatusEffectInstance type, int duration, int amplifier, boolean ambient, boolean visible) {
        this(type, duration, amplifier, ambient, visible, visible);
    }

    public StatusEffectInstance(StatusEffectInstance type, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon) {
        this(type, duration, amplifier, ambient, showParticles, showIcon, (StatusEffectInstance)null, type.getFactorCalculationDataSupplier());
    }

    public StatusEffectInstance(StatusEffectInstance type, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon, @Nullable StatusEffectInstance hiddenEffect, Optional<FactorCalculationData> factorCalculationData) {
        super(type.getEffectType(), duration, amplifier, ambient, showParticles, showIcon);
        this.type = type;
        this.duration = duration;
        this.amplifier = amplifier;
        this.ambient = ambient;
        this.showParticles = showParticles;
        this.showIcon = showIcon;
        this.hiddenEffect = hiddenEffect;
        this.factorCalculationData = factorCalculationData;
    }

    public StatusEffectInstance(StatusEffectInstance statusEffectInstance) {
        super(statusEffectInstance);
        this.type = statusEffectInstance.type;
        this.factorCalculationData = this.getFactorCalculationDataSupplier();
        this.copyFrom(statusEffectInstance);
    }

    public Optional<FactorCalculationData> getFactorCalculationData() {
        return this.factorCalculationData;
    }

    public Optional<StatusEffectInstance.FactorCalculationData> getFactorCalculationDataSupplier() {
        return Optional.ofNullable((StatusEffectInstance.FactorCalculationData)factorCalculationDataSupplier.get());
    }

    public StatusEffectInstance setFactorCalculationDataSupplier(Supplier<FactorCalculationData> supplier) {
        this.factorCalculationDataSupplier = supplier;
        return this;
    }

    void copyFrom(StatusEffectInstance that) {
        this.duration = that.duration;
        this.amplifier = that.amplifier;
        this.ambient = that.ambient;
        this.showParticles = that.showParticles;
        this.showIcon = that.showIcon;
    }

    public boolean upgrade(StatusEffectInstance that) {
        if (this.type != that.type) {
            LOGGER.warn("This method should only be called for matching effects!");
        }

        int i = this.duration;
        boolean bl = false;
        if (that.amplifier > this.amplifier) {
            if (that.duration < this.duration) {
                StatusEffectInstance statusEffectInstance = this.hiddenEffect;
                this.hiddenEffect = new StatusEffectInstance(this);
                this.hiddenEffect.hiddenEffect = statusEffectInstance;
            }

            this.amplifier = that.amplifier;
            this.duration = that.duration;
            bl = true;
        } else if (that.duration > this.duration) {
            if (that.amplifier == this.amplifier) {
                this.duration = that.duration;
                bl = true;
            } else if (this.hiddenEffect == null) {
                this.hiddenEffect = new StatusEffectInstance(that);
            } else {
                this.hiddenEffect.upgrade(that);
            }
        }

        if (!that.ambient && this.ambient || bl) {
            this.ambient = that.ambient;
            bl = true;
        }

        if (that.showParticles != this.showParticles) {
            this.showParticles = that.showParticles;
            bl = true;
        }

        if (that.showIcon != this.showIcon) {
            this.showIcon = that.showIcon;
            bl = true;
        }

        if (i != this.duration) {
            this.factorCalculationData.ifPresent((factorCalculationData) -> {
                factorCalculationData.effectChangedTimestamp += this.duration - i;
            });
            bl = true;
        }

        return bl;
    }

    //public StatusEffect getEffectType() {
        //return type;
    //}

    public int getDuration() {
        return this.duration;
    }

    public int getAmplifier() {
        return this.amplifier;
    }

    public boolean isAmbient() {
        return this.ambient;
    }

    public boolean shouldShowParticles() {
        return this.showParticles;
    }

    public boolean shouldShowIcon() {
        return this.showIcon;
    }

    private int updateDuration() {
        if (this.hiddenEffect != null) {
            this.hiddenEffect.updateDuration();
        }

        return --this.duration;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (this.duration > 0) {
            this.type.applyUpdateEffect(entity, this.amplifier);
        }

    }

    public String getTranslationKey() {
        return this.type.getTranslationKey();
    }

    public String toString() {
        String var10000;
        String string;
        if (this.amplifier > 0) {
            var10000 = this.getTranslationKey();
            string = var10000 + " x " + (this.amplifier + 1) + ", Duration: " + this.duration;
        } else {
            var10000 = this.getTranslationKey();
            string = var10000 + ", Duration: " + this.duration;
        }

        if (!this.showParticles) {
            string = string + ", Particles: false";
        }

        if (!this.showIcon) {
            string = string + ", Show Icon: false";
        }

        return string;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof StatusEffectInstance statusEffectInstance)) {
            return false;
        } else {
            return this.duration == statusEffectInstance.duration && this.amplifier == statusEffectInstance.amplifier && this.ambient == statusEffectInstance.ambient && this.type.equals(statusEffectInstance.type);
        }
    }

    public int hashCode() {
        int i = this.type.hashCode();
        i = 31 * i + this.duration;
        i = 31 * i + this.amplifier;
        i = 31 * i + (this.ambient ? 1 : 0);
        return i;
    }

    private void writeTypelessNbt(NbtCompound nbt) {
        nbt.putByte("Amplifier", (byte)this.getAmplifier());
        nbt.putInt("Duration", this.getDuration());
        nbt.putBoolean("Ambient", this.isAmbient());
        nbt.putBoolean("ShowParticles", this.shouldShowParticles());
        nbt.putBoolean("ShowIcon", this.shouldShowIcon());
        if (this.hiddenEffect != null) {
            NbtCompound nbtCompound = new NbtCompound();
            nbt.put("HiddenEffect", nbtCompound);
        }

        this.factorCalculationData.ifPresent((factorCalculationData) -> {
            DataResult<NbtElement> var10000 = StatusEffectInstance.FactorCalculationData.CODEC.encodeStart(NbtOps.INSTANCE, factorCalculationData);
            Logger var10001 = LOGGER;
            Objects.requireNonNull(var10001);
            var10000.resultOrPartial(var10001::error).ifPresent((factorCalculationDataNbt) -> {
                nbt.put("FactorCalculationData", factorCalculationDataNbt);
            });
        });
    }

    @Nullable
    public static StatusEffectInstance fromNbt(NbtCompound nbt) {
        int i = nbt.getInt("Id");
        StatusEffect statusEffect = StatusEffect.byRawId(i);
        return statusEffect == null ? null : fromNbt(statusEffect, nbt);
    }

    private static StatusEffectInstance fromNbt(StatusEffect type, NbtCompound nbt) {
        int i = nbt.getByte("Amplifier");
        int j = nbt.getInt("Duration");
        boolean bl = nbt.getBoolean("Ambient");
        boolean bl2 = true;
        if (nbt.contains("ShowParticles", 1)) {
            bl2 = nbt.getBoolean("ShowParticles");
        }

        boolean bl3 = bl2;
        if (nbt.contains("ShowIcon", 1)) {
            bl3 = nbt.getBoolean("ShowIcon");
        }

        StatusEffectInstance statusEffectInstance = null;
        if (nbt.contains("HiddenEffect", 10)) {
            statusEffectInstance = fromNbt(type, nbt.getCompound("HiddenEffect"));
        }

        Optional<?> optional;
        if (nbt.contains("FactorCalculationData", 10)) {
            DataResult<?> var10000 = StatusEffectInstance.FactorCalculationData.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getCompound("FactorCalculationData")));
            Logger var10001 = LOGGER;
            Objects.requireNonNull(var10001);
            optional = var10000.resultOrPartial(var10001::error);
        } else {
            optional = Optional.empty();
        }

        return null;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    public boolean isPermanent() {
        return this.permanent;
    }

    public static class FactorCalculationData {
        public static final Codec<StatusEffectInstance.FactorCalculationData> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                Codecs.NONNEGATIVE_INT.fieldOf("padding_duration").forGetter(data -> data.paddingDuration),
                                Codec.FLOAT.fieldOf("factor_start").orElse(0.0F).forGetter(factorCalculationData -> factorCalculationData.field_39111),
                                Codec.FLOAT.fieldOf("factor_target").orElse(1.0F).forGetter(data -> data.factorTarget),
                                Codec.FLOAT.fieldOf("factor_current").orElse(0.0F).forGetter(data -> data.factorCurrent),
                                Codecs.NONNEGATIVE_INT.fieldOf("effect_changed_timestamp").orElse(0).forGetter(data -> data.effectChangedTimestamp),
                                Codec.FLOAT.fieldOf("factor_previous_frame").orElse(0.0F).forGetter(data -> data.factorPreviousFrame),
                                Codec.BOOL.fieldOf("had_effect_last_tick").orElse(false).forGetter(data -> data.hadEffectLastTick)
                        )
                        .apply(instance, StatusEffectInstance.FactorCalculationData::new)
        );
        private final int paddingDuration;
        private float field_39111;
        private float factorTarget;
        private float factorCurrent;
        int effectChangedTimestamp;
        private float factorPreviousFrame;
        private boolean hadEffectLastTick;

        public FactorCalculationData(int paddingDuration, float factorTarget, float f, float g, int i, float h, boolean bl) {
            this.paddingDuration = paddingDuration;
            this.field_39111 = factorTarget;
            this.factorTarget = f;
            this.factorCurrent = g;
            this.effectChangedTimestamp = i;
            this.factorPreviousFrame = h;
            this.hadEffectLastTick = bl;
        }

        public FactorCalculationData(int paddingDuration) {
            this(paddingDuration, 0.0F, 1.0F, 0.0F, 0, 0.0F, false);
        }

        public void update(StatusEffectInstance instance) {
            this.factorPreviousFrame = this.factorCurrent;
            boolean bl = instance.duration > this.paddingDuration;
            if (this.hadEffectLastTick != bl) {
                this.hadEffectLastTick = bl;
                this.effectChangedTimestamp = instance.duration;
                this.field_39111 = this.factorCurrent;
                this.factorTarget = bl ? 1.0F : 0.0F;
            }

            float f = MathHelper.clamp(((float)this.effectChangedTimestamp - (float)instance.duration) / (float)this.paddingDuration, 0.0F, 1.0F);
            this.factorCurrent = MathHelper.lerp(f, this.field_39111, this.factorTarget);
        }

        public float lerp(float factor) {
            return MathHelper.lerp(factor, this.factorPreviousFrame, this.factorCurrent);
        }
    }
}
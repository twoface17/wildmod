package net.frozenblock.wildmod.misc;

import com.google.common.collect.ComparisonChain;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;

public class WildStatusEffectInstance extends net.minecraft.entity.effect.StatusEffectInstance {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final StatusEffect type;
    int duration;
    private int amplifier;
    private boolean ambient;
    private boolean permanent;
    private boolean showParticles;
    private boolean showIcon;
    @Nullable
    private WildStatusEffectInstance hiddenEffect;
    private final Optional<WildStatusEffectInstance.FactorCalculationData> factorCalculationData;

    public WildStatusEffectInstance(StatusEffect type) {
        this(type, 0, 0);
    }

    public WildStatusEffectInstance(StatusEffect type, int duration) {
        this(type, duration, 0);
    }

    public WildStatusEffectInstance(StatusEffect type, int duration, int amplifier) {
        this(type, duration, amplifier, false, true);
    }

    public WildStatusEffectInstance(StatusEffect type, int duration, int amplifier, boolean ambient, boolean visible) {
        this(type, duration, amplifier, ambient, visible, visible);
    }

    public WildStatusEffectInstance(StatusEffect type, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon) {
        this(type, duration, amplifier, ambient, showParticles, showIcon, null, type.getFactorCalculationDataSupplier());
    }

    public WildStatusEffectInstance(
            StatusEffect type,
            int duration,
            int amplifier,
            boolean ambient,
            boolean showParticles,
            boolean showIcon,
            @Nullable WildStatusEffectInstance hiddenEffect,
            Optional<WildStatusEffectInstance.FactorCalculationData> factorCalculationData
    ) {
        super(type, duration, amplifier, ambient, showParticles, showIcon);
        this.type = type;
        this.duration = duration;
        this.amplifier = amplifier;
        this.ambient = ambient;
        this.showParticles = showParticles;
        this.showIcon = showIcon;
        this.hiddenEffect = hiddenEffect;
        this.factorCalculationData = factorCalculationData;
    }

    public WildStatusEffectInstance(WildStatusEffectInstance instance) {
        super(instance);
        this.type = instance.type;
        this.factorCalculationData = this.type.getFactorCalculationDataSupplier();
        this.copyFrom(instance);
    }

    public Optional<WildStatusEffectInstance.FactorCalculationData> getFactorCalculationData() {
        return this.factorCalculationData;
    }

    void copyFrom(WildStatusEffectInstance that) {
        this.duration = that.duration;
        this.amplifier = that.amplifier;
        this.ambient = that.ambient;
        this.showParticles = that.showParticles;
        this.showIcon = that.showIcon;
    }

    public boolean upgrade(WildStatusEffectInstance that) {
        if (this.type != that.type) {
            LOGGER.warn("This method should only be called for matching effects!");
        }

        int i = this.duration;
        boolean bl = false;
        if (that.amplifier > this.amplifier) {
            if (that.duration < this.duration) {
                WildStatusEffectInstance wildStatusEffectInstance = this.hiddenEffect;
                this.hiddenEffect = new WildStatusEffectInstance(this);
                this.hiddenEffect.hiddenEffect = wildStatusEffectInstance;
            }

            this.amplifier = that.amplifier;
            this.duration = that.duration;
            bl = true;
        } else if (that.duration > this.duration) {
            if (that.amplifier == this.amplifier) {
                this.duration = that.duration;
                bl = true;
            } else if (this.hiddenEffect == null) {
                this.hiddenEffect = new WildStatusEffectInstance(that);
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
            this.factorCalculationData.ifPresent(factorCalculationData -> factorCalculationData.effectChangedTimestamp += this.duration - i);
            bl = true;
        }

        return bl;
    }

    public StatusEffect getEffectType() {
        return this.type;
    }

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

    public boolean update(LivingEntity entity, Runnable overwriteCallback) {
        if (this.duration > 0) {
            if (this.type.canApplyUpdateEffect(this.duration, this.amplifier)) {
                this.applyUpdateEffect(entity);
            }

            this.updateDuration();
            if (this.duration == 0 && this.hiddenEffect != null) {
                this.copyFrom(this.hiddenEffect);
                this.hiddenEffect = this.hiddenEffect.hiddenEffect;
                overwriteCallback.run();
            }
        }

        this.factorCalculationData.ifPresent(factorCalculationData -> factorCalculationData.update(this));
        return this.duration > 0;
    }

    private int updateDuration() {
        if (this.hiddenEffect != null) {
            this.hiddenEffect.updateDuration();
        }

        return --this.duration;
    }

    public void applyUpdateEffect(LivingEntity entity) {
        if (this.duration > 0) {
            this.type.applyUpdateEffect(entity, this.amplifier);
        }

    }

    public String getTranslationKey() {
        return this.type.getTranslationKey();
    }

    public String toString() {
        String string;
        if (this.amplifier > 0) {
            string = this.getTranslationKey() + " x " + (this.amplifier + 1) + ", Duration: " + this.duration;
        } else {
            string = this.getTranslationKey() + ", Duration: " + this.duration;
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
        } else if (!(o instanceof WildStatusEffectInstance wildStatusEffectInstance)) {
            return false;
        } else {
            return this.duration == wildStatusEffectInstance.duration
                    && this.amplifier == wildStatusEffectInstance.amplifier
                    && this.ambient == wildStatusEffectInstance.ambient
                    && this.type.equals(wildStatusEffectInstance.type);
        }
    }

    public int hashCode() {
        int i = this.type.hashCode();
        i = 31 * i + this.duration;
        i = 31 * i + this.amplifier;
        return 31 * i + (this.ambient ? 1 : 0);
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("Id", StatusEffect.getRawId(this.getEffectType()));
        this.writeTypelessNbt(nbt);
        return nbt;
    }

    private void writeTypelessNbt(NbtCompound nbt) {
        nbt.putByte("Amplifier", (byte) this.getAmplifier());
        nbt.putInt("Duration", this.getDuration());
        nbt.putBoolean("Ambient", this.isAmbient());
        nbt.putBoolean("ShowParticles", this.shouldShowParticles());
        nbt.putBoolean("ShowIcon", this.shouldShowIcon());
        if (this.hiddenEffect != null) {
            NbtCompound nbtCompound = new NbtCompound();
            this.hiddenEffect.writeNbt(nbtCompound);
            nbt.put("HiddenEffect", nbtCompound);
        }

        this.factorCalculationData
                .ifPresent(
                        factorCalculationData -> WildStatusEffectInstance.FactorCalculationData.CODEC
                                .encodeStart(NbtOps.INSTANCE, factorCalculationData)
                                .resultOrPartial(LOGGER::error)
                                .ifPresent(factorCalculationDataNbt -> nbt.put("FactorCalculationData", factorCalculationDataNbt))
                );
    }

    @Nullable
    public static WildStatusEffectInstance fromNbt(NbtCompound nbt) {
        int i = nbt.getInt("Id");
        StatusEffect statusEffect = StatusEffect.byRawId(i);
        return statusEffect == null ? null : fromNbt(statusEffect, nbt);
    }

    private static WildStatusEffectInstance fromNbt(StatusEffect type, NbtCompound nbt) {
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

        WildStatusEffectInstance wildStatusEffectInstance = null;
        if (nbt.contains("HiddenEffect", 10)) {
            wildStatusEffectInstance = fromNbt(type, nbt.getCompound("HiddenEffect"));
        }

        Optional<WildStatusEffectInstance.FactorCalculationData> optional;
        if (nbt.contains("FactorCalculationData", 10)) {
            optional = WildStatusEffectInstance.FactorCalculationData.CODEC
                    .parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getCompound("FactorCalculationData")))
                    .resultOrPartial(LOGGER::error);
        } else {
            optional = Optional.empty();
        }

        return new WildStatusEffectInstance(type, j, Math.max(i, 0), bl, bl2, bl3, wildStatusEffectInstance, optional);
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    public boolean isPermanent() {
        return this.permanent;
    }

    public int compareTo(WildStatusEffectInstance wildStatusEffectInstance) {
        int i = 32147;
        return (this.getDuration() <= 32147 || wildStatusEffectInstance.getDuration() <= 32147) && (!this.isAmbient() || !wildStatusEffectInstance.isAmbient())
                ? ComparisonChain.start()
                .compare(this.isAmbient(), wildStatusEffectInstance.isAmbient())
                .compare(this.getDuration(), wildStatusEffectInstance.getDuration())
                .compare(this.getEffectType().getColor(), wildStatusEffectInstance.getEffectType().getColor())
                .result()
                : ComparisonChain.start()
                .compare(this.isAmbient(), wildStatusEffectInstance.isAmbient())
                .compare(this.getEffectType().getColor(), wildStatusEffectInstance.getEffectType().getColor())
                .result();
    }

    public static class FactorCalculationData {
        public static final Codec<WildStatusEffectInstance.FactorCalculationData> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                Codecs.NONNEGATIVE_INT.fieldOf("padding_duration").forGetter(data -> data.paddingDuration),
                                Codec.FLOAT.fieldOf("factor_start").orElse(0.0F).forGetter(factorCalculationData -> factorCalculationData.field_39111),
                                Codec.FLOAT.fieldOf("factor_target").orElse(1.0F).forGetter(data -> data.factorTarget),
                                Codec.FLOAT.fieldOf("factor_current").orElse(0.0F).forGetter(data -> data.factorCurrent),
                                Codecs.NONNEGATIVE_INT.fieldOf("effect_changed_timestamp").orElse(0).forGetter(data -> data.effectChangedTimestamp),
                                Codec.FLOAT.fieldOf("factor_previous_frame").orElse(0.0F).forGetter(data -> data.factorPreviousFrame),
                                Codec.BOOL.fieldOf("had_effect_last_tick").orElse(false).forGetter(data -> data.hadEffectLastTick)
                        )
                        .apply(instance, WildStatusEffectInstance.FactorCalculationData::new)
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
            boolean bl = instance.getDuration() > this.paddingDuration;
            if (this.hadEffectLastTick != bl) {
                this.hadEffectLastTick = bl;
                this.effectChangedTimestamp = instance.getDuration();
                this.field_39111 = this.factorCurrent;
                this.factorTarget = bl ? 1.0F : 0.0F;
            }

            float tickDelta = AdvancedMath.clamp(((float) this.effectChangedTimestamp - (float) instance.getDuration()) / (float) this.paddingDuration, 0.0F, 1.0F);
            this.factorCurrent = AdvancedMath.lerp(tickDelta, this.field_39111, this.factorTarget);
        }

        public float lerp(LivingEntity entity, float factor) {
            if (entity.isRemoved()) {
                this.factorPreviousFrame = this.factorCurrent;
            }
            return AdvancedMath.lerp(factor, this.factorPreviousFrame, this.factorCurrent);
        }
    }
}

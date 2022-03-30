package frozenblock.wild.mod.mixins;

import com.mojang.authlib.GameProfile;
import frozenblock.wild.mod.entity.MangroveBoatEntity;
import frozenblock.wild.mod.entity.chestboat.ChestBoatEntity;
import frozenblock.wild.mod.liukrastapi.MathAddon;
import frozenblock.wild.mod.registry.RegisterItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    private double time;

    @Shadow
    private boolean inSneakingPose;

    @Shadow
    private boolean lastSprinting;

    @Shadow
    private boolean lastSneaking;

    @Shadow
    private double lastX;
    @Shadow
    private double lastBaseY;
    @Shadow
    private double lastZ;
    @Shadow
    private float lastYaw;
    @Shadow
    private float lastPitch;
    @Shadow
    private boolean lastOnGround;
    @Shadow
    private int ticksSinceLastPositionPacketSent;
    @Shadow
    private boolean autoJumpEnabled = true;
    @Shadow
    private int ticksToNextAutojump;

    @Shadow
    @Final
    protected MinecraftClient client;

    @Shadow
    @Final
    public ClientPlayNetworkHandler networkHandler;

    @Shadow
    private boolean riding;

    @Shadow
    public Input input;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow
    protected boolean isCamera() {
        return this.client.getCameraEntity() == this;
    }

    @Shadow
    private boolean isWalking() {
        double d = 0.8D;
        return this.isSubmergedInWater() ? this.input.hasForwardMovement() : (double)this.input.movementForward >= 0.8D;
    }

    @Inject(at = @At("HEAD"), method = "sendMovementPackets")
    private void sendMovementPackets (CallbackInfo ci) {
        boolean bl = this.isSprinting();
        if (bl != this.lastSprinting) {
            ClientCommandC2SPacket.Mode mode = bl ? ClientCommandC2SPacket.Mode.START_SPRINTING : ClientCommandC2SPacket.Mode.STOP_SPRINTING;
            this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, mode));
            this.lastSprinting = bl;
        }

        boolean bl2 = this.isSneaking();
        if (bl2 != this.lastSneaking) {
            ClientCommandC2SPacket.Mode mode2 = bl2 ? ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY : ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY;
            this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, mode2));
            this.lastSneaking = bl2;
        }

        if (this.isCamera()) {
            double d = this.getX() - this.lastX;
            double e = this.getY() - this.lastBaseY;
            double f = this.getZ() - this.lastZ;
            double g = (double)(this.getYaw() - this.lastYaw);
            double h = (double)(this.getPitch() - this.lastPitch);
            ++this.ticksSinceLastPositionPacketSent;
            boolean bl3 = MathAddon.squaredMagnitude(d, e, f) > MathHelper.square(2.0E-4D) || this.ticksSinceLastPositionPacketSent >= 20;
            boolean bl4 = g != 0.0D || h != 0.0D;
            if (this.hasVehicle()) {
                Vec3d vec3d = this.getVelocity();
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(vec3d.x, -999.0D, vec3d.z, this.getYaw(), this.getPitch(), this.onGround));
                bl3 = false;
            } else if (bl3 && bl4) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch(), this.onGround));
            } else if (bl3) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(this.getX(), this.getY(), this.getZ(), this.onGround));
            } else if (bl4) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(this.getYaw(), this.getPitch(), this.onGround));
            } else if (this.lastOnGround != this.onGround) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(this.onGround));
            }

            if (bl3) {
                this.lastX = this.getX();
                this.lastBaseY = this.getY();
                this.lastZ = this.getZ();
                this.ticksSinceLastPositionPacketSent = 0;
            }

            if (bl4) {
                this.lastYaw = this.getYaw();
                this.lastPitch = this.getPitch();
            }

            this.lastOnGround = this.onGround;
            //this.autoJumpEnabled = (Boolean)this.client.options.getAutoJump().getValue();
        }
    }

    @Inject(at = @At("HEAD"), method = "tickMovement")
    public void tickMovement(CallbackInfo ci) {
        boolean bl = this.input.jumping;
        boolean bl2 = this.input.sneaking;
        boolean bl3 = this.isWalking();
        this.inSneakingPose = !this.getAbilities().flying && !this.isSwimming() && this.wouldPoseNotCollide(EntityPose.CROUCHING) && (this.isSneaking() || !this.isSleeping() && !this.wouldPoseNotCollide(EntityPose.STANDING));
        float f = MathHelper.clamp(0.3F + RegisterItems.getSwiftSneakSpeedBoost(this), 0.0F, 1.0F);
    }


    @Inject(at = @At("HEAD"), method = "tickRiding")
    public void tickRiding(CallbackInfo ci) {
        this.riding = false;
        if (this.getVehicle() instanceof MangroveBoatEntity) {
            MangroveBoatEntity boatEntity = (MangroveBoatEntity)this.getVehicle();
            boatEntity.setInputs(this.input.pressingLeft, this.input.pressingRight, this.input.pressingForward, this.input.pressingBack);
            this.riding |= this.input.pressingLeft || this.input.pressingRight || this.input.pressingForward || this.input.pressingBack;
        }
        if (this.getVehicle() instanceof ChestBoatEntity) {
            ChestBoatEntity boatEntity = (ChestBoatEntity)this.getVehicle();
            boatEntity.setInputs(this.input.pressingLeft, this.input.pressingRight, this.input.pressingForward, this.input.pressingBack);
            this.riding |= this.input.pressingLeft || this.input.pressingRight || this.input.pressingForward || this.input.pressingBack;
        }

    }
}

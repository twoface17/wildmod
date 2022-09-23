/*package frozenblock.wild.mod.server;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import frozenblock.wild.mod.worldgen.random.WildRandomUtils;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.effect.WildStatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.NetworkSyncedItem;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.Recipe;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.*;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.gen.random.AbstractRandom;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;

public class ServerPlayerEntity extends net.minecraft.server.network.ServerPlayerEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int field_29769 = 32;
    private static final int field_29770 = 10;
    public ServerPlayNetworkHandler networkHandler;
    public final MinecraftServer server;
    public final ServerPlayerInteractionManager interactionManager;
    private final PlayerAdvancementTracker advancementTracker;
    private final ServerStatHandler statHandler;
    private float lastHealthScore = Float.MIN_VALUE;
    private int lastFoodScore = Integer.MIN_VALUE;
    private int lastAirScore = Integer.MIN_VALUE;
    private int lastArmorScore = Integer.MIN_VALUE;
    private int lastLevelScore = Integer.MIN_VALUE;
    private int lastExperienceScore = Integer.MIN_VALUE;
    private float syncedHealth = -1.0E8F;
    private int syncedFoodLevel = -99999999;
    private boolean syncedSaturationIsZero = true;
    private int syncedExperience = -99999999;
    private int joinInvulnerabilityTicks = 60;
    private ChatVisibility clientChatVisibility = ChatVisibility.FULL;
    private boolean clientChatColorsEnabled = true;
    private long lastActionTime = Util.getMeasuringTimeMs();
    @Nullable
    private Entity cameraEntity;
    private boolean inTeleportationState;
    private boolean seenCredits;
    private final ServerRecipeBook recipeBook = new ServerRecipeBook();
    @Nullable
    private Vec3d levitationStartPos;
    private int levitationStartTick;
    private boolean disconnected;
    @Nullable
    private Vec3d fallStartPos;
    @Nullable
    private Vec3d enteredNetherPos;
    @Nullable
    private Vec3d vehicleInLavaRidingPos;
    private WildChunkSectionPos watchedSection = WildChunkSectionPos.from(0, 0, 0);
    private RegistryKey<World> spawnPointDimension = World.OVERWORLD;
    @Nullable
    private BlockPos spawnPointPosition;
    private boolean spawnForced;
    private float spawnAngle;
    private final TextStream textStream;
    private boolean filterText;
    private boolean allowServerListing = true;
    private final ScreenHandlerSyncHandler screenHandlerSyncHandler = new ScreenHandlerSyncHandler() {
        public void updateState(ScreenHandler handler, DefaultedList<ItemStack> stacks, ItemStack cursorStack, int[] properties) {
            ServerPlayerEntity.this.networkHandler.sendPacket(new InventoryS2CPacket(handler.syncId, handler.nextRevision(), stacks, cursorStack));

            for(int i = 0; i < properties.length; ++i) {
                this.sendPropertyUpdate(handler, i, properties[i]);
            }

        }

        public void updateSlot(ScreenHandler handler, int slot, ItemStack stack) {
            ServerPlayerEntity.this.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(handler.syncId, handler.nextRevision(), slot, stack));
        }

        public void updateCursorStack(ScreenHandler handler, ItemStack stack) {
            ServerPlayerEntity.this.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-1, handler.nextRevision(), -1, stack));
        }

        public void updateProperty(ScreenHandler handler, int property, int value) {
            this.sendPropertyUpdate(handler, property, value);
        }

        private void sendPropertyUpdate(ScreenHandler handler, int property, int value) {
            ServerPlayerEntity.this.networkHandler.sendPacket(new ScreenHandlerPropertyUpdateS2CPacket(handler.syncId, property, value));
        }
    };
    private final ScreenHandlerListener screenHandlerListener = new ScreenHandlerListener() {
        public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
            Slot slot = handler.getSlot(slotId);
            if (!(slot instanceof CraftingResultSlot)) {
                if (slot.inventory == ServerPlayerEntity.this.getInventory()) {
                    Criteria.INVENTORY_CHANGED.trigger(ServerPlayerEntity.this, ServerPlayerEntity.this.getInventory(), stack);
                }

            }
        }

        public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
        }
    };
    private int screenHandlerSyncId;
    public int pingMilliseconds;
    public boolean notInAnyWorld;

    public ServerPlayerEntity(MinecraftServer server, ServerWorld world, GameProfile profile) {
        super(server, world, profile);
        this.textStream = server.createFilterer(this);
        this.interactionManager = server.getPlayerInteractionManager(this);
        this.server = server;
        this.statHandler = server.getPlayerManager().createStatHandler(this);
        this.advancementTracker = server.getPlayerManager().getAdvancementTracker(this);
        this.stepHeight = 1.0F;
        this.moveToSpawn(world);
    }

    private void moveToSpawn(ServerWorld world) {
        BlockPos blockPos = world.getSpawnPos();
        if (world.getDimension().hasSkyLight() && world.getServer().getSaveProperties().getGameMode() != GameMode.ADVENTURE) {
            int i = Math.max(0, this.server.getSpawnRadius(world));
            int j = MathHelper.floor(world.getWorldBorder().getDistanceInsideBorder((double)blockPos.getX(), (double)blockPos.getZ()));
            if (j < i) {
                i = j;
            }

            if (j <= 1) {
                i = 1;
            }

            long l = i * 2L + 1;
            long m = l * l;
            int k = m > 2147483647L ? Integer.MAX_VALUE : (int)m;
            int n = this.calculateSpawnOffsetMultiplier(k);
            int o = WildRandomUtils.createAtomic().nextInt(k);

            for(int p = 0; p < k; ++p) {
                int q = (o + n * p) % k;
                int r = q % (i * 2 + 1);
                int s = q / (i * 2 + 1);
                BlockPos blockPos2 = SpawnLocating.findOverworldSpawn(world, blockPos.getX() + r - i, blockPos.getZ() + s - i);
                if (blockPos2 != null) {
                    this.refreshPositionAndAngles(blockPos2, 0.0F, 0.0F);
                    if (world.isSpaceEmpty(this)) {
                        break;
                    }
                }
            }
        } else {
            this.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);

            while(!world.isSpaceEmpty(this) && this.getY() < (double)(world.getTopY() - 1)) {
                this.setPosition(this.getX(), this.getY() + 1.0, this.getZ());
            }
        }

    }

    private int calculateSpawnOffsetMultiplier(int horizontalSpawnArea) {
        return horizontalSpawnArea <= 16 ? horizontalSpawnArea - 1 : 17;
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("enteredNetherPosition", 10)) {
            NbtCompound nbtCompound = nbt.getCompound("enteredNetherPosition");
            this.enteredNetherPos = new Vec3d(nbtCompound.getDouble("x"), nbtCompound.getDouble("y"), nbtCompound.getDouble("z"));
        }

        this.seenCredits = nbt.getBoolean("seenCredits");
        if (nbt.contains("recipeBook", 10)) {
            this.recipeBook.readNbt(nbt.getCompound("recipeBook"), this.server.getRecipeManager());
        }

        if (this.isSleeping()) {
            this.wakeUp();
        }

        if (nbt.contains("SpawnX", 99) && nbt.contains("SpawnY", 99) && nbt.contains("SpawnZ", 99)) {
            this.spawnPointPosition = new BlockPos(nbt.getInt("SpawnX"), nbt.getInt("SpawnY"), nbt.getInt("SpawnZ"));
            this.spawnForced = nbt.getBoolean("SpawnForced");
            this.spawnAngle = nbt.getFloat("SpawnAngle");
            if (nbt.contains("SpawnDimension")) {
                this.spawnPointDimension = World.CODEC
                        .parse(NbtOps.INSTANCE, nbt.get("SpawnDimension"))
                        .resultOrPartial(LOGGER::error)
                        .orElse(World.OVERWORLD);
            }
        }

    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        this.writeGameModeNbt(nbt);
        nbt.putBoolean("seenCredits", this.seenCredits);
        if (this.enteredNetherPos != null) {
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putDouble("x", this.enteredNetherPos.x);
            nbtCompound.putDouble("y", this.enteredNetherPos.y);
            nbtCompound.putDouble("z", this.enteredNetherPos.z);
            nbt.put("enteredNetherPosition", nbtCompound);
        }

        Entity entity = this.getRootVehicle();
        Entity entity2 = this.getVehicle();
        if (entity2 != null && entity != this && entity.hasPlayerRider()) {
            NbtCompound nbtCompound2 = new NbtCompound();
            NbtCompound nbtCompound3 = new NbtCompound();
            entity.saveNbt(nbtCompound3);
            nbtCompound2.putUuid("Attach", entity2.getUuid());
            nbtCompound2.put("Entity", nbtCompound3);
            nbt.put("RootVehicle", nbtCompound2);
        }

        nbt.put("recipeBook", this.recipeBook.toNbt());
        nbt.putString("Dimension", this.world.getRegistryKey().getValue().toString());
        if (this.spawnPointPosition != null) {
            nbt.putInt("SpawnX", this.spawnPointPosition.getX());
            nbt.putInt("SpawnY", this.spawnPointPosition.getY());
            nbt.putInt("SpawnZ", this.spawnPointPosition.getZ());
            nbt.putBoolean("SpawnForced", this.spawnForced);
            nbt.putFloat("SpawnAngle", this.spawnAngle);
            Identifier.CODEC
                    .encodeStart(NbtOps.INSTANCE, this.spawnPointDimension.getValue())
                    .resultOrPartial(LOGGER::error)
                    .ifPresent(nbtElement -> nbt.put("SpawnDimension", nbtElement));
        }

    }

    public void setExperiencePoints(int points) {
        float f = (float)this.getNextLevelExperience();
        float g = (f - 1.0F) / f;
        this.experienceProgress = MathHelper.clamp((float)points / f, 0.0F, g);
        this.syncedExperience = -1;
    }

    public void setExperienceLevel(int level) {
        this.experienceLevel = level;
        this.syncedExperience = -1;
    }

    public void addExperienceLevels(int levels) {
        super.addExperienceLevels(levels);
        this.syncedExperience = -1;
    }

    public void applyEnchantmentCosts(ItemStack enchantedItem, int experienceLevels) {
        super.applyEnchantmentCosts(enchantedItem, experienceLevels);
        this.syncedExperience = -1;
    }

    private void onScreenHandlerOpened(ScreenHandler screenHandler) {
        screenHandler.addListener(this.screenHandlerListener);
        screenHandler.updateSyncHandler(this.screenHandlerSyncHandler);
    }

    public void onSpawn() {
        this.onScreenHandlerOpened(this.playerScreenHandler);
    }

    public void enterCombat() {
        super.enterCombat();
        this.networkHandler.sendPacket(new EnterCombatS2CPacket());
    }

    public void endCombat() {
        super.endCombat();
        this.networkHandler.sendPacket(new EndCombatS2CPacket(this.getDamageTracker()));
    }

    protected void onBlockCollision(BlockState state) {
        Criteria.ENTER_BLOCK.trigger(this, state);
    }

    protected ItemCooldownManager createCooldownManager() {
        return new ServerItemCooldownManager(this);
    }

    public void tick() {
        this.interactionManager.update();
        --this.joinInvulnerabilityTicks;
        if (this.timeUntilRegen > 0) {
            --this.timeUntilRegen;
        }

        this.currentScreenHandler.sendContentUpdates();
        if (!this.world.isClient && !this.currentScreenHandler.canUse(this)) {
            this.closeHandledScreen();
            this.currentScreenHandler = this.playerScreenHandler;
        }

        Entity entity = this.getCameraEntity();
        if (entity != this) {
            if (entity.isAlive()) {
                this.updatePositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());
                this.getWorld().getChunkManager().updatePosition(this);
                if (this.shouldDismount()) {
                    this.setCameraEntity(this);
                }
            } else {
                this.setCameraEntity(this);
            }
        }

        Criteria.TICK.trigger(this);
        if (this.levitationStartPos != null) {
            Criteria.LEVITATION.trigger(this, this.levitationStartPos, this.age - this.levitationStartTick);
        }

        this.tickFallStartPos();
        this.tickVehicleInLavaRiding();
        this.advancementTracker.sendUpdate(this);
    }

    public void playerTick() {
        try {
            if (!this.isSpectator() || !this.isRegionUnloaded()) {
                super.tick();
            }

            for(int i = 0; i < this.getInventory().size(); ++i) {
                ItemStack itemStack = this.getInventory().getStack(i);
                if (itemStack.getItem().isNetworkSynced()) {
                    Packet<?> packet = ((NetworkSyncedItem)itemStack.getItem()).createSyncPacket(itemStack, this.world, this);
                    if (packet != null) {
                        this.networkHandler.sendPacket(packet);
                    }
                }
            }

            if (this.getHealth() != this.syncedHealth
                    || this.syncedFoodLevel != this.hungerManager.getFoodLevel()
                    || this.hungerManager.getSaturationLevel() == 0.0F != this.syncedSaturationIsZero) {
                this.networkHandler
                        .sendPacket(new HealthUpdateS2CPacket(this.getHealth(), this.hungerManager.getFoodLevel(), this.hungerManager.getSaturationLevel()));
                this.syncedHealth = this.getHealth();
                this.syncedFoodLevel = this.hungerManager.getFoodLevel();
                this.syncedSaturationIsZero = this.hungerManager.getSaturationLevel() == 0.0F;
            }

            if (this.getHealth() + this.getAbsorptionAmount() != this.lastHealthScore) {
                this.lastHealthScore = this.getHealth() + this.getAbsorptionAmount();
                this.updateScores(ScoreboardCriterion.HEALTH, MathHelper.ceil(this.lastHealthScore));
            }

            if (this.hungerManager.getFoodLevel() != this.lastFoodScore) {
                this.lastFoodScore = this.hungerManager.getFoodLevel();
                this.updateScores(ScoreboardCriterion.FOOD, MathHelper.ceil((float)this.lastFoodScore));
            }

            if (this.getAir() != this.lastAirScore) {
                this.lastAirScore = this.getAir();
                this.updateScores(ScoreboardCriterion.AIR, MathHelper.ceil((float)this.lastAirScore));
            }

            if (this.getArmor() != this.lastArmorScore) {
                this.lastArmorScore = this.getArmor();
                this.updateScores(ScoreboardCriterion.ARMOR, MathHelper.ceil((float)this.lastArmorScore));
            }

            if (this.totalExperience != this.lastExperienceScore) {
                this.lastExperienceScore = this.totalExperience;
                this.updateScores(ScoreboardCriterion.XP, MathHelper.ceil((float)this.lastExperienceScore));
            }

            if (this.experienceLevel != this.lastLevelScore) {
                this.lastLevelScore = this.experienceLevel;
                this.updateScores(ScoreboardCriterion.LEVEL, MathHelper.ceil((float)this.lastLevelScore));
            }

            if (this.totalExperience != this.syncedExperience) {
                this.syncedExperience = this.totalExperience;
                this.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(this.experienceProgress, this.totalExperience, this.experienceLevel));
            }

            if (this.age % 20 == 0) {
                Criteria.LOCATION.trigger(this);
            }

        } catch (Throwable var4) {
            CrashReport crashReport = CrashReport.create(var4, "Ticking player");
            CrashReportSection crashReportSection = crashReport.addElement("Player being ticked");
            this.populateCrashReport(crashReportSection);
            throw new CrashException(crashReport);
        }
    }

    public void onLanding() {
        if (this.getHealth() > 0.0F && this.fallStartPos != null) {
            Criteria.FALL_FROM_HEIGHT.trigger(this, this.fallStartPos);
        }

        this.fallStartPos = null;
        super.onLanding();
    }

    public void tickFallStartPos() {
        if (this.fallDistance > 0.0F && this.fallStartPos == null) {
            this.fallStartPos = this.getPos();
        }

    }

    public void tickVehicleInLavaRiding() {
        if (this.getVehicle() != null && this.getVehicle().isInLava()) {
            if (this.vehicleInLavaRidingPos == null) {
                this.vehicleInLavaRidingPos = this.getPos();
            } else {
                Criteria.RIDE_ENTITY_IN_LAVA.trigger(this, this.vehicleInLavaRidingPos);
            }
        }

        if (this.vehicleInLavaRidingPos != null && (this.getVehicle() == null || !this.getVehicle().isInLava())) {
            this.vehicleInLavaRidingPos = null;
        }

    }

    private void updateScores(ScoreboardCriterion criterion, int score) {
        this.getScoreboard().forEachScore(criterion, this.getEntityName(), scorex -> scorex.setScore(score));
    }

    public void onDeath(DamageSource source) {
        this.emitGameEvent(GameEvent.ENTITY_KILLED);
        boolean bl = this.world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES);
        if (bl) {
            Text text = this.getDamageTracker().getDeathMessage();
            this.networkHandler
                    .sendPacket(
                            new DeathMessageS2CPacket(this.getDamageTracker(), text),
                            future -> {
                                if (!future.isSuccess()) {
                                    int i = 256;
                                    String string = text.asTruncatedString(256);
                                    Text text2 = Text.tra("death.attack.message_too_long", new Object[]{Text.literal(string).formatted(Formatting.YELLOW)});
                                    Text text3 = Text.translatable("death.attack.even_more_magic", new Object[]{this.getDisplayName()})
                                            .styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, text2)));
                                    this.networkHandler.sendPacket(new DeathMessageS2CPacket(this.getDamageTracker(), text3));
                                }

                            }
                    );
            AbstractTeam abstractTeam = this.getScoreboardTeam();
            if (abstractTeam == null || abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.ALWAYS) {
                this.server.getPlayerManager().broadcast(text, MessageType.SYSTEM, Util.NIL_UUID);
            } else if (abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDE_FOR_OTHER_TEAMS) {
                this.server.getPlayerManager().sendToTeam(this, text);
            } else if (abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDE_FOR_OWN_TEAM) {
                this.server.getPlayerManager().sendToOtherTeams(this, text);
            }
        } else {
            this.networkHandler.sendPacket(new DeathMessageS2CPacket(this.getDamageTracker(), ScreenTexts.EMPTY));
        }

        this.dropShoulderEntities();
        if (this.world.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS)) {
            this.forgiveMobAnger();
        }

        if (!this.isSpectator()) {
            this.drop(source);
        }

        this.getScoreboard().forEachScore(ScoreboardCriterion.DEATH_COUNT, this.getEntityName(), ScoreboardPlayerScore::incrementScore);
        LivingEntity livingEntity = this.getPrimeAdversary();
        if (livingEntity != null) {
            this.incrementStat(Stats.KILLED_BY.getOrCreateStat(livingEntity.getType()));
            livingEntity.updateKilledAdvancementCriterion(this, this.scoreAmount, source);
            this.onKilledBy(livingEntity);
        }

        this.world.sendEntityStatus(this, (byte)3);
        this.incrementStat(Stats.DEATHS);
        this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_DEATH));
        this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
        this.extinguish();
        this.setFrozenTicks(0);
        this.setOnFire(false);
        this.getDamageTracker().update();
        this.setLastDeathPos(Optional.of(GlobalPos.create(this.world.getRegistryKey(), this.getBlockPos())));
    }

    private void forgiveMobAnger() {
        Box box = new Box(this.getBlockPos()).expand(32.0, 10.0, 32.0);
        this.world
                .getEntitiesByClass(MobEntity.class, box, EntityPredicates.EXCEPT_SPECTATOR)
                .stream()
                .filter(entity -> entity instanceof Angerable)
                .forEach(entity -> ((Angerable)entity).forgive(this));
    }

    public void updateKilledAdvancementCriterion(Entity entityKilled, int score, DamageSource damageSource) {
        if (entityKilled != this) {
            super.updateKilledAdvancementCriterion(entityKilled, score, damageSource);
            this.addScore(score);
            String string = this.getEntityName();
            String string2 = entityKilled.getEntityName();
            this.getScoreboard().forEachScore(ScoreboardCriterion.TOTAL_KILL_COUNT, string, ScoreboardPlayerScore::incrementScore);
            if (entityKilled instanceof PlayerEntity) {
                this.incrementStat(Stats.PLAYER_KILLS);
                this.getScoreboard().forEachScore(ScoreboardCriterion.PLAYER_KILL_COUNT, string, ScoreboardPlayerScore::incrementScore);
            } else {
                this.incrementStat(Stats.MOB_KILLS);
            }

            this.updateScoreboardScore(string, string2, ScoreboardCriterion.TEAM_KILLS);
            this.updateScoreboardScore(string2, string, ScoreboardCriterion.KILLED_BY_TEAMS);
            Criteria.PLAYER_KILLED_ENTITY.trigger(this, entityKilled, damageSource);
        }
    }

    private void updateScoreboardScore(String playerName, String team, ScoreboardCriterion[] criterions) {
        Team team2 = this.getScoreboard().getPlayerTeam(team);
        if (team2 != null) {
            int i = team2.getColor().getColorIndex();
            if (i >= 0 && i < criterions.length) {
                this.getScoreboard().forEachScore(criterions[i], playerName, ScoreboardPlayerScore::incrementScore);
            }
        }

    }

    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            boolean bl = this.server.isDedicated() && this.isPvpEnabled() && "fall".equals(source.name);
            if (!bl && this.joinInvulnerabilityTicks > 0 && source != DamageSource.OUT_OF_WORLD) {
                return false;
            } else {
                if (source instanceof EntityDamageSource) {
                    Entity entity = source.getAttacker();
                    if (entity instanceof PlayerEntity && !this.shouldDamagePlayer((PlayerEntity)entity)) {
                        return false;
                    }

                    if (entity instanceof PersistentProjectileEntity persistentProjectileEntity) {
                        Entity entity2 = persistentProjectileEntity.getOwner();
                        if (entity2 instanceof PlayerEntity && !this.shouldDamagePlayer((PlayerEntity)entity2)) {
                            return false;
                        }
                    }
                }

                return super.damage(source, amount);
            }
        }
    }

    public boolean shouldDamagePlayer(PlayerEntity player) {
        return !this.isPvpEnabled() ? false : super.shouldDamagePlayer(player);
    }

    private boolean isPvpEnabled() {
        return this.server.isPvpEnabled();
    }

    @Nullable
    protected TeleportTarget getTeleportTarget(ServerWorld destination) {
        TeleportTarget teleportTarget = super.getTeleportTarget(destination);
        if (teleportTarget != null && this.world.getRegistryKey() == World.OVERWORLD && destination.getRegistryKey() == World.END) {
            Vec3d vec3d = teleportTarget.position.add(0.0, -1.0, 0.0);
            return new TeleportTarget(vec3d, Vec3d.ZERO, 90.0F, 0.0F);
        } else {
            return teleportTarget;
        }
    }

    @Nullable
    public Entity moveToWorld(ServerWorld destination) {
        this.inTeleportationState = true;
        ServerWorld serverWorld = this.getWorld();
        RegistryKey<World> registryKey = serverWorld.getRegistryKey();
        if (registryKey == World.END && destination.getRegistryKey() == World.OVERWORLD) {
            this.detach();
            this.getWorld().removePlayer(this, RemovalReason.CHANGED_DIMENSION);
            if (!this.notInAnyWorld) {
                this.notInAnyWorld = true;
                this.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.GAME_WON, this.seenCredits ? 0.0F : 1.0F));
                this.seenCredits = true;
            }

            return this;
        } else {
            WorldProperties worldProperties = destination.getLevelProperties();
            this.networkHandler
                    .sendPacket(
                            new PlayerRespawnS2CPacket(
                                    destination.getDimensionEntry(),
                                    destination.getRegistryKey(),
                                    BiomeAccess.hashSeed(destination.getSeed()),
                                    this.interactionManager.getGameMode(),
                                    this.interactionManager.getPreviousGameMode(),
                                    destination.isDebugWorld(),
                                    destination.isFlat(),
                                    true
                            )
                    );
            this.networkHandler.sendPacket(new DifficultyS2CPacket(worldProperties.getDifficulty(), worldProperties.isDifficultyLocked()));
            PlayerManager playerManager = this.server.getPlayerManager();
            playerManager.sendCommandTree(this);
            serverWorld.removePlayer(this, RemovalReason.CHANGED_DIMENSION);
            this.unsetRemoved();
            TeleportTarget teleportTarget = this.getTeleportTarget(destination);
            if (teleportTarget != null) {
                serverWorld.getProfiler().push("moving");
                if (registryKey == World.OVERWORLD && destination.getRegistryKey() == World.NETHER) {
                    this.enteredNetherPos = this.getPos();
                } else if (destination.getRegistryKey() == World.END) {
                    this.createEndSpawnPlatform(destination, new BlockPos(teleportTarget.position));
                }

                serverWorld.getProfiler().pop();
                serverWorld.getProfiler().push("placing");
                this.setWorld(destination);
                destination.onPlayerChangeDimension(this);
                this.setRotation(teleportTarget.yaw, teleportTarget.pitch);
                this.refreshPositionAfterTeleport(teleportTarget.position.x, teleportTarget.position.y, teleportTarget.position.z);
                serverWorld.getProfiler().pop();
                this.worldChanged(serverWorld);
                this.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(this.getAbilities()));
                playerManager.sendWorldInfo(this, destination);
                playerManager.sendPlayerStatus(this);

                for(WildStatusEffectInstance statusEffectInstance : this.getStatusEffects()) {
                    this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getId(), statusEffectInstance));
                }

                this.networkHandler.sendPacket(new WorldEventS2CPacket(1032, BlockPos.ORIGIN, 0, false));
                this.syncedExperience = -1;
                this.syncedHealth = -1.0F;
                this.syncedFoodLevel = -1;
            }

            return this;
        }
    }

    private void createEndSpawnPlatform(ServerWorld world, BlockPos centerPos) {
        BlockPos.Mutable mutable = centerPos.mutableCopy();

        for(int i = -2; i <= 2; ++i) {
            for(int j = -2; j <= 2; ++j) {
                for(int k = -1; k < 3; ++k) {
                    BlockState blockState = k == -1 ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState();
                    world.setBlockState(mutable.set(centerPos).move(j, k, i), blockState);
                }
            }
        }

    }

    protected Optional<BlockLocating.Rectangle> getPortalRect(ServerWorld destWorld, BlockPos destPos, boolean destIsNether, WorldBorder worldBorder) {
        Optional<BlockLocating.Rectangle> optional = super.getPortalRect(destWorld, destPos, destIsNether, worldBorder);
        if (optional.isPresent()) {
            return optional;
        } else {
            Direction.Axis axis = (Direction.Axis)this.world.getBlockState(this.lastNetherPortalPosition).getOrEmpty(NetherPortalBlock.AXIS).orElse(Direction.Axis.X);
            Optional<BlockLocating.Rectangle> optional2 = destWorld.getPortalForcer().createPortal(destPos, axis);
            if (!optional2.isPresent()) {
                LOGGER.error("Unable to create a portal, likely target out of worldborder");
            }

            return optional2;
        }
    }

    private void worldChanged(ServerWorld origin) {
        RegistryKey<World> registryKey = origin.getRegistryKey();
        RegistryKey<World> registryKey2 = this.world.getRegistryKey();
        Criteria.CHANGED_DIMENSION.trigger(this, registryKey, registryKey2);
        if (registryKey == World.NETHER && registryKey2 == World.OVERWORLD && this.enteredNetherPos != null) {
            Criteria.NETHER_TRAVEL.trigger(this, this.enteredNetherPos);
        }

        if (registryKey2 != World.NETHER) {
            this.enteredNetherPos = null;
        }

    }

    public boolean canBeSpectated(ServerPlayerEntity spectator) {
        if (spectator.isSpectator()) {
            return this.getCameraEntity() == this;
        } else {
            return this.isSpectator() ? false : super.canBeSpectated(spectator);
        }
    }

    public void sendPickup(Entity item, int count) {
        super.sendPickup(item, count);
        this.currentScreenHandler.sendContentUpdates();
    }

    public Either<SleepFailureReason, Unit> trySleep(BlockPos pos) {
        Direction direction = (Direction)this.world.getBlockState(pos).get(HorizontalFacingBlock.FACING);
        if (this.isSleeping() || !this.isAlive()) {
            return Either.left(SleepFailureReason.OTHER_PROBLEM);
        } else if (!this.world.getDimension().natural()) {
            return Either.left(SleepFailureReason.NOT_POSSIBLE_HERE);
        } else if (!this.isBedTooFarAway(pos, direction)) {
            return Either.left(SleepFailureReason.TOO_FAR_AWAY);
        } else if (this.isBedObstructed(pos, direction)) {
            return Either.left(SleepFailureReason.OBSTRUCTED);
        } else {
            this.setSpawnPoint(this.world.getRegistryKey(), pos, this.getYaw(), false, true);
            if (this.world.isDay()) {
                return Either.left(SleepFailureReason.NOT_POSSIBLE_NOW);
            } else {
                if (!this.isCreative()) {
                    double d = 8.0;
                    double e = 5.0;
                    Vec3d vec3d = Vec3d.ofBottomCenter(pos);
                    List<HostileEntity> list = this.world
                            .getEntitiesByClass(
                                    HostileEntity.class,
                                    new Box(vec3d.getX() - 8.0, vec3d.getY() - 5.0, vec3d.getZ() - 8.0, vec3d.getX() + 8.0, vec3d.getY() + 5.0, vec3d.getZ() + 8.0),
                                    hostileEntity -> hostileEntity.isAngryAt(this)
                            );
                    if (!list.isEmpty()) {
                        return Either.left(SleepFailureReason.NOT_SAFE);
                    }
                }

                Either<SleepFailureReason, Unit> either = super.trySleep(pos).ifRight(unit -> {
                    this.incrementStat(Stats.SLEEP_IN_BED);
                    Criteria.SLEPT_IN_BED.trigger(this);
                });
                if (!this.getWorld().isSleepingEnabled()) {
                    this.sendMessage(Text.translatable("sleep.not_possible"), true);
                }

                ((ServerWorld)this.world).updateSleepingPlayers();
                return either;
            }
        }
    }

    public void sleep(BlockPos pos) {
        this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
        super.sleep(pos);
    }

    private boolean isBedTooFarAway(BlockPos pos, Direction direction) {
        return this.isBedTooFarAway(pos) || this.isBedTooFarAway(pos.offset(direction.getOpposite()));
    }

    private boolean isBedTooFarAway(BlockPos pos) {
        Vec3d vec3d = Vec3d.ofBottomCenter(pos);
        return Math.abs(this.getX() - vec3d.getX()) <= 3.0 && Math.abs(this.getY() - vec3d.getY()) <= 2.0 && Math.abs(this.getZ() - vec3d.getZ()) <= 3.0;
    }

    private boolean isBedObstructed(BlockPos pos, Direction direction) {
        BlockPos blockPos = pos.up();
        return !this.doesNotSuffocate(blockPos) || !this.doesNotSuffocate(blockPos.offset(direction.getOpposite()));
    }

    public void wakeUp(boolean skipSleepTimer, boolean updateSleepingPlayers) {
        if (this.isSleeping()) {
            this.getWorld().getChunkManager().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(this, 2));
        }

        super.wakeUp(skipSleepTimer, updateSleepingPlayers);
        if (this.networkHandler != null) {
            this.networkHandler.requestTeleport(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
        }

    }

    public boolean startRiding(Entity entity, boolean force) {
        Entity entity2 = this.getVehicle();
        if (!super.startRiding(entity, force)) {
            return false;
        } else {
            Entity entity3 = this.getVehicle();
            if (entity3 != entity2 && this.networkHandler != null) {
                this.networkHandler.requestTeleport(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
            }

            return true;
        }
    }

    public void stopRiding() {
        Entity entity = this.getVehicle();
        super.stopRiding();
        Entity entity2 = this.getVehicle();
        if (entity2 != entity && this.networkHandler != null) {
            this.networkHandler.requestTeleportAndDismount(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
        }

    }

    public void requestTeleportAndDismount(double destX, double destY, double destZ) {
        this.dismountVehicle();
        if (this.networkHandler != null) {
            this.networkHandler.requestTeleportAndDismount(destX, destY, destZ, this.getYaw(), this.getPitch());
        }

    }

    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource) || this.isInTeleportationState() || this.getAbilities().invulnerable && damageSource == DamageSource.WITHER;
    }

    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
    }

    protected void applyMovementEffects(BlockPos pos) {
        if (!this.isSpectator()) {
            super.applyMovementEffects(pos);
        }

    }

    public void handleFall(double heightDifference, boolean onGround) {
        if (!this.isRegionUnloaded()) {
            BlockPos blockPos = this.getLandingPos();
            super.fall(heightDifference, onGround, this.world.getBlockState(blockPos), blockPos);
        }
    }

    public void openEditSignScreen(SignBlockEntity sign) {
        sign.setEditor(this.getUuid());
        this.networkHandler.sendPacket(new BlockUpdateS2CPacket(this.world, sign.getPos()));
        this.networkHandler.sendPacket(new SignEditorOpenS2CPacket(sign.getPos()));
    }

    private void incrementScreenHandlerSyncId() {
        this.screenHandlerSyncId = this.screenHandlerSyncId % 100 + 1;
    }

    public OptionalInt openHandledScreen(@Nullable NamedScreenHandlerFactory factory) {
        if (factory == null) {
            return OptionalInt.empty();
        } else {
            if (this.currentScreenHandler != this.playerScreenHandler) {
                this.closeHandledScreen();
            }

            this.incrementScreenHandlerSyncId();
            ScreenHandler screenHandler = factory.createMenu(this.screenHandlerSyncId, this.getInventory(), this);
            if (screenHandler == null) {
                if (this.isSpectator()) {
                    this.sendMessage(Text.translatable("container.spectatorCantOpen").formatted(Formatting.RED), true);
                }

                return OptionalInt.empty();
            } else {
                this.networkHandler.sendPacket(new OpenScreenS2CPacket(screenHandler.syncId, screenHandler.getType(), factory.getDisplayName()));
                this.onScreenHandlerOpened(screenHandler);
                this.currentScreenHandler = screenHandler;
                return OptionalInt.of(this.screenHandlerSyncId);
            }
        }
    }

    public void sendTradeOffers(int syncId, TradeOfferList offers, int levelProgress, int experience, boolean leveled, boolean refreshable) {
        this.networkHandler.sendPacket(new SetTradeOffersS2CPacket(syncId, offers, levelProgress, experience, leveled, refreshable));
    }

    public void openHorseInventory(AbstractHorseEntity horse, Inventory inventory) {
        if (this.currentScreenHandler != this.playerScreenHandler) {
            this.closeHandledScreen();
        }

        this.incrementScreenHandlerSyncId();
        this.networkHandler.sendPacket(new OpenHorseScreenS2CPacket(this.screenHandlerSyncId, inventory.size(), horse.getId()));
        this.currentScreenHandler = new HorseScreenHandler(this.screenHandlerSyncId, this.getInventory(), inventory, horse);
        this.onScreenHandlerOpened(this.currentScreenHandler);
    }

    public void useBook(ItemStack book, Hand hand) {
        if (book.isOf(Items.WRITTEN_BOOK)) {
            if (WrittenBookItem.resolve(book, this.getCommandSource(), this)) {
                this.currentScreenHandler.sendContentUpdates();
            }

            this.networkHandler.sendPacket(new OpenWrittenBookS2CPacket(hand));
        }

    }

    public void openCommandBlockScreen(CommandBlockBlockEntity commandBlock) {
        this.networkHandler.sendPacket(BlockEntityUpdateS2CPacket.create(commandBlock, BlockEntity::createNbt));
    }

    public void closeHandledScreen() {
        this.networkHandler.sendPacket(new CloseScreenS2CPacket(this.currentScreenHandler.syncId));
        this.closeScreenHandler();
    }

    public void closeScreenHandler() {
        this.currentScreenHandler.close(this);
        this.playerScreenHandler.copySharedSlots(this.currentScreenHandler);
        this.currentScreenHandler = this.playerScreenHandler;
    }

    public void updateInput(float sidewaysSpeed, float forwardSpeed, boolean jumping, boolean sneaking) {
        if (this.hasVehicle()) {
            if (sidewaysSpeed >= -1.0F && sidewaysSpeed <= 1.0F) {
                this.sidewaysSpeed = sidewaysSpeed;
            }

            if (forwardSpeed >= -1.0F && forwardSpeed <= 1.0F) {
                this.forwardSpeed = forwardSpeed;
            }

            this.jumping = jumping;
            this.setSneaking(sneaking);
        }

    }

    public void increaseStat(Stat<?> stat, int amount) {
        this.statHandler.increaseStat(this, stat, amount);
        this.getScoreboard().forEachScore(stat, this.getEntityName(), score -> score.incrementScore(amount));
    }

    public void resetStat(Stat<?> stat) {
        this.statHandler.setStat(this, stat, 0);
        this.getScoreboard().forEachScore(stat, this.getEntityName(), ScoreboardPlayerScore::clearScore);
    }

    public int unlockRecipes(Collection<Recipe<?>> recipes) {
        return this.recipeBook.unlockRecipes(recipes, this);
    }

    public void unlockRecipes(Identifier[] ids) {
        List<Recipe<?>> list = Lists.newArrayList();

        for(Identifier identifier : ids) {
            this.server.getRecipeManager().get(identifier).ifPresent(list::add);
        }

        this.unlockRecipes(list);
    }

    public int lockRecipes(Collection<Recipe<?>> recipes) {
        return this.recipeBook.lockRecipes(recipes, this);
    }

    public void addExperience(int experience) {
        super.addExperience(experience);
        this.syncedExperience = -1;
    }

    public void onDisconnect() {
        this.disconnected = true;
        this.removeAllPassengers();
        if (this.isSleeping()) {
            this.wakeUp(true, false);
        }

    }

    public boolean isDisconnected() {
        return this.disconnected;
    }

    public void markHealthDirty() {
        this.syncedHealth = -1.0E8F;
    }

    public void sendMessage(Text message, boolean actionBar) {
        this.sendMessage(message, actionBar ? MessageType.GAME_INFO : MessageType.CHAT, Util.NIL_UUID);
    }

    protected void consumeItem() {
        if (!this.activeItemStack.isEmpty() && this.isUsingItem()) {
            this.networkHandler.sendPacket(new EntityStatusS2CPacket(this, (byte)9));
            super.consumeItem();
        }

    }

    public void lookAt(EntityAnchorArgumentType.EntityAnchor anchorPoint, Vec3d target) {
        super.lookAt(anchorPoint, target);
        this.networkHandler.sendPacket(new LookAtS2CPacket(anchorPoint, target.x, target.y, target.z));
    }

    public void lookAtEntity(EntityAnchorArgumentType.EntityAnchor anchorPoint, Entity targetEntity, EntityAnchorArgumentType.EntityAnchor targetAnchor) {
        Vec3d vec3d = targetAnchor.positionAt(targetEntity);
        super.lookAt(anchorPoint, vec3d);
        this.networkHandler.sendPacket(new LookAtS2CPacket(anchorPoint, targetEntity, targetAnchor));
    }

    public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive) {
        this.filterText = oldPlayer.filterText;
        this.interactionManager.setGameMode(oldPlayer.interactionManager.getGameMode(), oldPlayer.interactionManager.getPreviousGameMode());
        if (alive) {
            this.getInventory().clone(oldPlayer.getInventory());
            this.setHealth(oldPlayer.getHealth());
            this.hungerManager = oldPlayer.hungerManager;
            this.experienceLevel = oldPlayer.experienceLevel;
            this.totalExperience = oldPlayer.totalExperience;
            this.experienceProgress = oldPlayer.experienceProgress;
            this.setScore(oldPlayer.getScore());
            this.lastNetherPortalPosition = oldPlayer.lastNetherPortalPosition;
        } else if (this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || oldPlayer.isSpectator()) {
            this.getInventory().clone(oldPlayer.getInventory());
            this.experienceLevel = oldPlayer.experienceLevel;
            this.totalExperience = oldPlayer.totalExperience;
            this.experienceProgress = oldPlayer.experienceProgress;
            this.setScore(oldPlayer.getScore());
        }

        this.enchantmentTableSeed = oldPlayer.enchantmentTableSeed;
        this.enderChestInventory = oldPlayer.enderChestInventory;
        this.getDataTracker().set(PLAYER_MODEL_PARTS, (Byte)oldPlayer.getDataTracker().get(PLAYER_MODEL_PARTS));
        this.syncedExperience = -1;
        this.syncedHealth = -1.0F;
        this.syncedFoodLevel = -1;
        this.recipeBook.copyFrom(oldPlayer.recipeBook);
        this.seenCredits = oldPlayer.seenCredits;
        this.enteredNetherPos = oldPlayer.enteredNetherPos;
        this.setShoulderEntityLeft(oldPlayer.getShoulderEntityLeft());
        this.setShoulderEntityRight(oldPlayer.getShoulderEntityRight());
        this.setLastDeathPos(oldPlayer.getLastDeathPos());
    }

    protected void onStatusEffectApplied(WildStatusEffectInstance effect, @Nullable Entity source) {
        super.onStatusEffectApplied(effect, source);
        this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getId(), effect));
        if (effect.getEffectType() == StatusEffects.LEVITATION) {
            this.levitationStartTick = this.age;
            this.levitationStartPos = this.getPos();
        }

        Criteria.EFFECTS_CHANGED.trigger(this, source);
    }

    protected void onStatusEffectUpgraded(WildStatusEffectInstance effect, boolean reapplyEffect, @Nullable Entity source) {
        super.onStatusEffectUpgraded(effect, reapplyEffect, source);
        this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getId(), effect));
        Criteria.EFFECTS_CHANGED.trigger(this, source);
    }

    protected void onStatusEffectRemoved(WildStatusEffectInstance effect) {
        super.onStatusEffectRemoved(effect);
        this.networkHandler.sendPacket(new RemoveEntityStatusEffectS2CPacket(this.getId(), effect.getEffectType()));
        if (effect.getEffectType() == StatusEffects.LEVITATION) {
            this.levitationStartPos = null;
        }

        Criteria.EFFECTS_CHANGED.trigger(this, null);
    }

    public void requestTeleport(double destX, double destY, double destZ) {
        this.networkHandler.requestTeleport(destX, destY, destZ, this.getYaw(), this.getPitch());
    }

    public void refreshPositionAfterTeleport(double x, double y, double z) {
        this.requestTeleport(x, y, z);
        this.networkHandler.syncWithPlayerPosition();
    }

    public void addCritParticles(Entity target) {
        this.getWorld().getChunkManager().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(target, 4));
    }

    public void addEnchantedHitParticles(Entity target) {
        this.getWorld().getChunkManager().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(target, 5));
    }

    public void sendAbilitiesUpdate() {
        if (this.networkHandler != null) {
            this.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(this.getAbilities()));
            this.updatePotionVisibility();
        }
    }

    public ServerWorld getWorld() {
        return (ServerWorld)this.world;
    }

    public boolean changeGameMode(GameMode gameMode) {
        if (!this.interactionManager.changeGameMode(gameMode)) {
            return false;
        } else {
            this.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.GAME_MODE_CHANGED, (float)gameMode.getId()));
            if (gameMode == GameMode.SPECTATOR) {
                this.dropShoulderEntities();
                this.stopRiding();
            } else {
                this.setCameraEntity(this);
            }

            this.sendAbilitiesUpdate();
            this.markEffectsDirty();
            return true;
        }
    }

    public boolean isSpectator() {
        return this.interactionManager.getGameMode() == GameMode.SPECTATOR;
    }

    public boolean isCreative() {
        return this.interactionManager.getGameMode() == GameMode.CREATIVE;
    }

    public void sendSystemMessage(Text message, UUID sender) {
        this.sendMessage(message, MessageType.SYSTEM, sender);
    }

    public void sendMessage(Text message, MessageType type, UUID sender) {
        if (this.acceptsMessage(type)) {
            this.networkHandler
                    .sendPacket(
                            new GameMessageS2CPacket(message, type, sender),
                            future -> {
                                if (!future.isSuccess() && (type == MessageType.GAME_INFO || type == MessageType.SYSTEM) && this.acceptsMessage(MessageType.SYSTEM)) {
                                    int i = 256;
                                    String string = message.asTruncatedString(256);
                                    Text text2 = Text.literal(string).formatted(Formatting.YELLOW);
                                    this.networkHandler
                                            .sendPacket(
                                                    new GameMessageS2CPacket(
                                                            Text.translatable("multiplayer.message_not_delivered", new Object[]{text2}).formatted(Formatting.RED),
                                                            MessageType.SYSTEM,
                                                            sender
                                                    )
                                            );
                                }

                            }
                    );
        }
    }

    public String getIp() {
        String string = this.networkHandler.connection.getAddress().toString();
        string = string.substring(string.indexOf("/") + 1);
        return string.substring(0, string.indexOf(":"));
    }

    public void setClientSettings(ClientSettingsC2SPacket packet) {
        this.clientChatVisibility = packet.chatVisibility();
        this.clientChatColorsEnabled = packet.chatColors();
        this.filterText = packet.filterText();
        this.allowServerListing = packet.allowsListing();
        this.getDataTracker().set(PLAYER_MODEL_PARTS, (byte)packet.playerModelBitMask());
        this.getDataTracker().set(MAIN_ARM, (byte)(packet.mainArm() == Arm.LEFT ? 0 : 1));
    }

    public boolean areClientChatColorsEnabled() {
        return this.clientChatColorsEnabled;
    }

    public ChatVisibility getClientChatVisibility() {
        return this.clientChatVisibility;
    }

    private boolean acceptsMessage(MessageType type) {
        switch(this.clientChatVisibility) {
            case HIDDEN:
                return type == MessageType.GAME_INFO;
            case SYSTEM:
                return type == MessageType.SYSTEM || type == MessageType.GAME_INFO;
            case FULL:
            default:
                return true;
        }
    }

    public void sendResourcePackUrl(String url, String hash, boolean required, @Nullable Text resourcePackPrompt) {
        this.networkHandler.sendPacket(new ResourcePackSendS2CPacket(url, hash, required, resourcePackPrompt));
    }

    protected int getPermissionLevel() {
        return this.server.getPermissionLevel(this.getGameProfile());
    }

    public void updateLastActionTime() {
        this.lastActionTime = Util.getMeasuringTimeMs();
    }

    public ServerStatHandler getStatHandler() {
        return this.statHandler;
    }

    public ServerRecipeBook getRecipeBook() {
        return this.recipeBook;
    }

    protected void updatePotionVisibility() {
        if (this.isSpectator()) {
            this.clearPotionSwirls();
            this.setInvisible(true);
        } else {
            super.updatePotionVisibility();
        }

    }

    public Entity getCameraEntity() {
        return (Entity)(this.cameraEntity == null ? this : this.cameraEntity);
    }

    public void setCameraEntity(@Nullable Entity entity) {
        Entity entity2 = this.getCameraEntity();
        this.cameraEntity = (Entity)(entity == null ? this : entity);
        if (entity2 != this.cameraEntity) {
            this.networkHandler.sendPacket(new SetCameraEntityS2CPacket(this.cameraEntity));
            this.requestTeleport(this.cameraEntity.getX(), this.cameraEntity.getY(), this.cameraEntity.getZ());
        }

    }

    protected void tickNetherPortalCooldown() {
        if (!this.inTeleportationState) {
            super.tickNetherPortalCooldown();
        }

    }

    public void attack(Entity target) {
        if (this.interactionManager.getGameMode() == GameMode.SPECTATOR) {
            this.setCameraEntity(target);
        } else {
            super.attack(target);
        }

    }

    public long getLastActionTime() {
        return this.lastActionTime;
    }

    @Nullable
    public Text getPlayerListName() {
        return null;
    }

    public void swingHand(Hand hand) {
        super.swingHand(hand);
        this.resetLastAttackedTicks();
    }

    public boolean isInTeleportationState() {
        return this.inTeleportationState;
    }

    public void onTeleportationDone() {
        this.inTeleportationState = false;
    }

    public PlayerAdvancementTracker getAdvancementTracker() {
        return this.advancementTracker;
    }

    public void teleport(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch) {
        this.setCameraEntity(this);
        this.stopRiding();
        if (targetWorld == this.world) {
            this.networkHandler.requestTeleport(x, y, z, yaw, pitch);
        } else {
            ServerWorld serverWorld = this.getWorld();
            WorldProperties worldProperties = targetWorld.getLevelProperties();
            this.networkHandler
                    .sendPacket(
                            new PlayerRespawnS2CPacket(
                                    targetWorld.getDimensionEntry(),
                                    targetWorld.getRegistryKey(),
                                    BiomeAccess.hashSeed(targetWorld.getSeed()),
                                    this.interactionManager.getGameMode(),
                                    this.interactionManager.getPreviousGameMode(),
                                    targetWorld.isDebugWorld(),
                                    targetWorld.isFlat(),
                                    true
                            )
                    );
            this.networkHandler.sendPacket(new DifficultyS2CPacket(worldProperties.getDifficulty(), worldProperties.isDifficultyLocked()));
            this.server.getPlayerManager().sendCommandTree(this);
            serverWorld.removePlayer(this, RemovalReason.CHANGED_DIMENSION);
            this.unsetRemoved();
            this.refreshPositionAndAngles(x, y, z, yaw, pitch);
            this.setWorld(targetWorld);
            targetWorld.onPlayerTeleport(this);
            this.worldChanged(serverWorld);
            this.networkHandler.requestTeleport(x, y, z, yaw, pitch);
            this.server.getPlayerManager().sendWorldInfo(this, targetWorld);
            this.server.getPlayerManager().sendPlayerStatus(this);
        }

    }

    @Nullable
    public BlockPos getSpawnPointPosition() {
        return this.spawnPointPosition;
    }

    public float getSpawnAngle() {
        return this.spawnAngle;
    }

    public RegistryKey<World> getSpawnPointDimension() {
        return this.spawnPointDimension;
    }

    public boolean isSpawnForced() {
        return this.spawnForced;
    }

    public void setSpawnPoint(RegistryKey<World> dimension, @Nullable BlockPos pos, float angle, boolean forced, boolean sendMessage) {
        if (pos != null) {
            boolean bl = pos.equals(this.spawnPointPosition) && dimension.equals(this.spawnPointDimension);
            if (sendMessage && !bl) {
                this.sendSystemMessage(Text.translatable("block.minecraft.set_spawn"), Util.NIL_UUID);
            }

            this.spawnPointPosition = pos;
            this.spawnPointDimension = dimension;
            this.spawnAngle = angle;
            this.spawnForced = forced;
        } else {
            this.spawnPointPosition = null;
            this.spawnPointDimension = World.OVERWORLD;
            this.spawnAngle = 0.0F;
            this.spawnForced = false;
        }

    }

    public void sendInitialChunkPackets(ChunkPos chunkPos, Packet<?> chunkDataPacket) {
        this.networkHandler.sendPacket(chunkDataPacket);
    }

    public void sendUnloadChunkPacket(ChunkPos chunkPos) {
        if (this.isAlive()) {
            this.networkHandler.sendPacket(new UnloadChunkS2CPacket(chunkPos.x, chunkPos.z));
        }

    }

    public WildChunkSectionPos getWatchedSection() {
        return this.watchedSection;
    }

    public void setWatchedSection(WildChunkSectionPos section) {
        this.watchedSection = section;
    }

    public void playSound(SoundEvent event, SoundCategory category, float volume, float pitch) {
        this.networkHandler.sendPacket(new PlaySoundS2CPacket(event, category, this.getX(), this.getY(), this.getZ(), volume, pitch, this.random.nextLong()));
    }

    public Packet<?> createSpawnPacket() {
        return new PlayerSpawnS2CPacket(this);
    }

    public ItemEntity dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership) {
        ItemEntity itemEntity = super.dropItem(stack, throwRandomly, retainOwnership);
        if (itemEntity == null) {
            return null;
        } else {
            this.world.spawnEntity(itemEntity);
            ItemStack itemStack = itemEntity.getStack();
            if (retainOwnership) {
                if (!itemStack.isEmpty()) {
                    this.increaseStat(Stats.DROPPED.getOrCreateStat(itemStack.getItem()), stack.getCount());
                }

                this.incrementStat(Stats.DROP);
            }

            return itemEntity;
        }
    }

    public TextStream getTextStream() {
        return this.textStream;
    }

    public void setWorld(ServerWorld world) {
        this.world = world;
        this.interactionManager.setWorld(world);
    }

    @Nullable
    private static GameMode gameModeFromNbt(@Nullable NbtCompound nbt, String key) {
        return nbt != null && nbt.contains(key, 99) ? GameMode.byId(nbt.getInt(key)) : null;
    }

    private GameMode getServerGameMode(@Nullable GameMode backupGameMode) {
        GameMode gameMode = this.server.getForcedGameMode();
        if (gameMode != null) {
            return gameMode;
        } else {
            return backupGameMode != null ? backupGameMode : this.server.getDefaultGameMode();
        }
    }

    public void setGameMode(@Nullable NbtCompound nbt) {
        this.interactionManager.setGameMode(this.getServerGameMode(gameModeFromNbt(nbt, "playerGameType")), gameModeFromNbt(nbt, "previousPlayerGameType"));
    }

    private void writeGameModeNbt(NbtCompound nbt) {
        nbt.putInt("playerGameType", this.interactionManager.getGameMode().getId());
        GameMode gameMode = this.interactionManager.getPreviousGameMode();
        if (gameMode != null) {
            nbt.putInt("previousPlayerGameType", gameMode.getId());
        }

    }

    public boolean shouldFilterText() {
        return this.filterText;
    }

    public boolean shouldFilterMessagesSentTo(ServerPlayerEntity player) {
        if (player == this) {
            return false;
        } else {
            return this.filterText || player.filterText;
        }
    }

    public boolean canModifyAt(World world, BlockPos pos) {
        return super.canModifyAt(world, pos) && world.canPlayerModifyAt(this, pos);
    }

    protected void tickItemStackUsage(ItemStack stack) {
        Criteria.USING_ITEM.trigger(this, stack);
        super.tickItemStackUsage(stack);
    }

    public boolean dropSelectedItem(boolean entireStack) {
        PlayerInventory playerInventory = this.getInventory();
        ItemStack itemStack = playerInventory.dropSelectedItem(entireStack);
        this.currentScreenHandler
                .getSlotIndex(playerInventory, playerInventory.selectedSlot)
                .ifPresent(i -> this.currentScreenHandler.setPreviousTrackedSlot(i, playerInventory.getMainHandStack()));
        return this.dropItem(itemStack, false, true) != null;
    }

    public boolean allowsServerListing() {
        return this.allowServerListing;
    }

    public void triggerItemPickedUpByEntityCriteria(ItemEntity item) {
        super.triggerItemPickedUpByEntityCriteria(item);
        Entity entity = item.getThrower() != null ? this.getWorld().getEntity(item.getThrower()) : null;
        if (entity != null) {
            Criteria.THROWN_ITEM_PICKED_UP_BY_PLAYER.trigger(this, item.getStack(), entity);
        }

    }
}
*/
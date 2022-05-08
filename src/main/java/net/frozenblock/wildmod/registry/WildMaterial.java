package net.frozenblock.wildmod.registry;

import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.piston.PistonBehavior;

public class WildMaterial extends Material {
    private final MapColor color;
    private final PistonBehavior pistonBehavior;
    private final boolean blocksMovement;
    private final boolean burnable;
    private final boolean liquid;
    private final boolean blocksLight;
    private final boolean replaceable;
    private final boolean solid;

    public WildMaterial(MapColor color, boolean liquid, boolean solid, boolean blocksMovement, boolean blocksLight, boolean burnable, boolean replaceable, PistonBehavior pistonBehavior) {
        super(color, liquid, solid, blocksMovement, blocksLight, burnable, replaceable, pistonBehavior);
        this.color = color;
        this.liquid = liquid;
        this.solid = solid;
        this.blocksMovement = blocksMovement;
        this.blocksLight = blocksLight;
        this.burnable = burnable;
        this.replaceable = replaceable;
        this.pistonBehavior = pistonBehavior;
    }

    public boolean isLiquid() {
        return this.liquid;
    }

    public boolean isSolid() {
        return this.solid;
    }

    public boolean blocksMovement() {
        return this.blocksMovement;
    }

    public boolean isBurnable() {
        return this.burnable;
    }

    public boolean isReplaceable() {
        return this.replaceable;
    }

    public boolean blocksLight() {
        return this.blocksLight;
    }

    public PistonBehavior getPistonBehavior() {
        return this.pistonBehavior;
    }

    public MapColor getColor() {
        return this.color;
    }

    public static class WildBuilder extends Builder {
        private PistonBehavior pistonBehavior = PistonBehavior.NORMAL;
        private boolean blocksMovement = true;
        private boolean burnable;
        private boolean liquid;
        private boolean replaceable;
        private boolean solid = true;
        private final MapColor color;
        private boolean blocksLight = true;

        public WildBuilder(MapColor color) {
            super(color);
            this.color = color;
        }

        public WildMaterial.WildBuilder liquid() {
            this.liquid = true;
            return this;
        }

        public WildMaterial.WildBuilder notSolid() {
            this.solid = false;
            return this;
        }

        public WildMaterial.WildBuilder allowsMovement() {
            this.blocksMovement = false;
            return this;
        }

        public WildMaterial.WildBuilder lightPassesThrough() {
            this.blocksLight = false;
            return this;
        }

        protected WildMaterial.WildBuilder burnable() {
            this.burnable = true;
            return this;
        }

        public WildMaterial.WildBuilder replaceable() {
            this.replaceable = true;
            return this;
        }

        protected WildMaterial.WildBuilder destroyedByPiston() {
            this.pistonBehavior = PistonBehavior.DESTROY;
            return this;
        }

        protected WildMaterial.WildBuilder blocksPistons() {
            this.pistonBehavior = PistonBehavior.BLOCK;
            return this;
        }

        public WildMaterial build() {
            return new WildMaterial(
                    this.color, this.liquid, this.solid, this.blocksMovement, this.blocksLight, this.burnable, this.replaceable, this.pistonBehavior
            );
        }
    }
}

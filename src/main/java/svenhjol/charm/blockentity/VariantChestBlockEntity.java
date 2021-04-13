package svenhjol.charm.blockentity;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import svenhjol.charm.block.IVariantChestBlock;
import svenhjol.charm.module.VariantChests;
import svenhjol.charm.base.enums.IVariantMaterial;

import javax.annotation.Nullable;

public class VariantChestBlockEntity extends ChestBlockEntity {
    private IVariantMaterial materialType = null;

    public VariantChestBlockEntity() {
        super(VariantChests.NORMAL_BLOCK_ENTITY);
    }

    public VariantChestBlockEntity(BlockEntityType<?> tile) {
        super(tile);
    }

    @Nullable
    public IVariantMaterial getMaterialType() {
        if (materialType == null && world != null)
            return ((IVariantChestBlock)this.getCachedState().getBlock()).getMaterialType();

        return materialType;
    }

    public void setMaterialType(IVariantMaterial materialType) {
        this.materialType = materialType;
    }
}

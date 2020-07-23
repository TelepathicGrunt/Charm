package svenhjol.charm.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.util.IWorldPosCallable;
import svenhjol.charm.Charm;
import svenhjol.charm.message.ClientOpenInventory;
import svenhjol.meson.Meson;

public class CraftingInventoryContainer extends WorkbenchContainer {
    public CraftingInventoryContainer(int i, PlayerInventory inv, IWorldPosCallable callable) {
        super(i, inv, callable);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);

        if (!playerIn.world.isRemote)
            Meson.getMod(Charm.MOD_ID)
                .getPacketHandler()
                .sendToPlayer(new ClientOpenInventory(), (ServerPlayerEntity)playerIn);
    }
}
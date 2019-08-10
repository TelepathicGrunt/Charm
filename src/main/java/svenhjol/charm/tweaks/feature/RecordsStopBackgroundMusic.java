package svenhjol.charm.tweaks.feature;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.sound.SoundEvent.SoundSourceEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.meson.Feature;
import svenhjol.meson.helper.SoundHelper;

public class RecordsStopBackgroundMusic extends Feature
{
    private int ticks; // delay
    private ISound music = null; // music to stop

    @Override
    public void init()
    {
        super.init();
    }

    @SubscribeEvent
    public void onBlockInteract(RightClickBlock event)
    {
        if (event.getWorld().isRemote) {
            BlockState state = event.getWorld().getBlockState(event.getPos());
            if (event.getEntity() instanceof PlayerEntity
                && event.getItemStack().getItem() instanceof MusicDiscItem
                && state.getBlock() == Blocks.JUKEBOX
                && !state.get(JukeboxBlock.HAS_RECORD)
            ) {
                SoundHelper.getSoundHandler().stop(null, SoundCategory.MUSIC);
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onSoundPlay(SoundSourceEvent event)
    {
        ISound triggered = event.getSound();
        if (triggered.getCategory() == SoundCategory.MUSIC) {
            // check if there are any records playing
            SoundHelper.getPlayingSounds().forEach((category, sound) -> {
                if (category == SoundCategory.RECORDS) {
                    music = triggered;
                }
            });
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onClientTick(ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END
            && music != null
            && ++ticks % 10 == 0
        ) {
            SoundHelper.getSoundHandler().stop(music);
            ticks = 0;
            music = null;
        }
    }

    @Override
    public boolean hasSubscriptions()
    {
        return true;
    }
}
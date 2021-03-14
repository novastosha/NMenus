package dev.nova.menus.menu;

import net.minecraft.server.v1_12_R1.*;

public class MenuAnvil extends ContainerAnvil implements CustomMenuInventory {

    public final EntityPlayer player;

    public MenuAnvil(EntityPlayer player){
        super(player.inventory, player.world, new BlockPosition(0,0,0),player);
        this.checkReachable = false;
        this.player = player;
    }

    @Override
    public void e() {
        super.e();
        this.levelCost = 0;
    }

    @Override
    public void b(EntityHuman entityhuman) {
    }

    @Override
    protected void a(EntityHuman entityhuman, World world, IInventory iinventory) {
    }

    public void open(){
        player.playerConnection.sendPacket(new PacketPlayOutOpenWindow(this.windowId, "minecraft:anvil", new ChatMessage(Blocks.ANVIL.a() + ".name")));
    }
}

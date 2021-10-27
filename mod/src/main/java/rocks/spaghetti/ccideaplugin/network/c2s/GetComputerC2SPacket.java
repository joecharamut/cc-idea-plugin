package rocks.spaghetti.ccideaplugin.network.c2s;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import rocks.spaghetti.ccideaplugin.CCIdeaPlugin;
import rocks.spaghetti.ccideaplugin.network.Packet;
import rocks.spaghetti.ccideaplugin.network.ServerPacketHandler;

public class GetComputerC2SPacket implements Packet<ServerPacketHandler> {
    public static final Identifier ID = new Identifier(CCIdeaPlugin.MOD_ID, "GetComputerC2SPacket".toLowerCase());

    public final ServerPlayerEntity playerEntity;

    public GetComputerC2SPacket() {
        this.playerEntity = null;
    }

    public GetComputerC2SPacket(PacketByteBuf buf, ServerPlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    @Override
    public PacketByteBuf write(PacketByteBuf buf) {
        return buf;
    }

    @Override
    public void apply(ServerPacketHandler handler) {
        handler.onGetComputer(this);
    }

    @Override
    public Identifier getChannel() {
        return ID;
    }

    @Override
    public String toString() {
        return "GetComputerC2SPacket{}";
    }
}

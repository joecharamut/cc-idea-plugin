package rocks.spaghetti.ccideaplugin.network.s2c;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import rocks.spaghetti.ccideaplugin.CCIdeaPlugin;
import rocks.spaghetti.ccideaplugin.network.ClientPacketHandler;
import rocks.spaghetti.ccideaplugin.network.Packet;
import rocks.spaghetti.ccideaplugin.network.ServerPacketHandler;

public class GetComputerS2CPacket implements Packet<ClientPacketHandler> {
    public static final Identifier ID = new Identifier(CCIdeaPlugin.MOD_ID, "GetComputerS2CPacket".toLowerCase());

    public final int computer;

    public GetComputerS2CPacket(int computer) {
        this.computer = computer;
    }

    public GetComputerS2CPacket(PacketByteBuf buf) {
        this.computer = buf.readInt();
    }

    @Override
    public PacketByteBuf write(PacketByteBuf buf) {
        buf.writeInt(this.computer);
        return buf;
    }

    @Override
    public void apply(ClientPacketHandler handler) {
        handler.onGetComputer(this);
    }

    @Override
    public Identifier getChannel() {
        return ID;
    }

    @Override
    public String toString() {
        return "GetComputerS2CPacket{" +
                "computer=" + computer +
                '}';
    }
}

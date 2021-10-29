package rocks.spaghetti.ccideaplugin.network.s2c;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import rocks.spaghetti.ccideaplugin.CCIdeaPlugin;
import rocks.spaghetti.ccideaplugin.network.ClientPacketHandler;
import rocks.spaghetti.ccideaplugin.network.Packet;
import rocks.spaghetti.ccideaplugin.network.ServerPacketHandler;

import java.util.Arrays;

public class GetComputersS2CPacket implements Packet<ClientPacketHandler> {
    public static final Identifier ID = new Identifier(CCIdeaPlugin.MOD_ID, "GetComputersS2CPacket".toLowerCase());

    public final int activeComputer;
    public final int[] accessibleComputers;

    public GetComputersS2CPacket(int activeComputer, int[] accessibleComputers) {
        this.activeComputer = activeComputer;
        this.accessibleComputers = accessibleComputers;
    }

    public GetComputersS2CPacket(PacketByteBuf buf) {
        this.activeComputer = buf.readInt();
        this.accessibleComputers = buf.readIntArray();
    }

    @Override
    public PacketByteBuf write(PacketByteBuf buf) {
        buf.writeInt(this.activeComputer);
        buf.writeIntArray(this.accessibleComputers);
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
                "activeComputer=" + activeComputer +
                ", accessibleComputers=" + Arrays.toString(accessibleComputers) +
                '}';
    }
}

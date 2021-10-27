package rocks.spaghetti.ccideaplugin.network.c2s;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import rocks.spaghetti.ccideaplugin.CCIdeaPlugin;
import rocks.spaghetti.ccideaplugin.network.Packet;
import rocks.spaghetti.ccideaplugin.network.ServerPacketHandler;

public class FileListC2SPacket implements Packet<ServerPacketHandler> {
    public static final Identifier ID = new Identifier(CCIdeaPlugin.MOD_ID, "FileListC2SPacket".toLowerCase());

    public final int computer;
    public final ServerPlayerEntity playerEntity;

    public FileListC2SPacket(int computer) {
        this.computer = computer;
        this.playerEntity = null;
    }

    public FileListC2SPacket(PacketByteBuf buf, ServerPlayerEntity playerEntity) {
        this.computer = buf.readInt();
        this.playerEntity = playerEntity;
    }

    public PacketByteBuf write(PacketByteBuf buf) {
        buf.writeInt(this.computer);
        return buf;
    }

    @Override
    public void apply(ServerPacketHandler handler) {
        handler.onFileList(this);
    }

    @Override
    public Identifier getChannel() {
        return ID;
    }

    @Override
    public String toString() {
        return "FileListC2SPacket{" +
                "computer=" + computer +
                '}';
    }
}

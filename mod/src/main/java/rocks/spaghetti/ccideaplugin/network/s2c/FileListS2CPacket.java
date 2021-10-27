package rocks.spaghetti.ccideaplugin.network.s2c;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import rocks.spaghetti.ccideaplugin.CCIdeaPlugin;
import rocks.spaghetti.ccideaplugin.network.ClientPacketHandler;
import rocks.spaghetti.ccideaplugin.network.Packet;

import java.util.Arrays;

public class FileListS2CPacket implements Packet<ClientPacketHandler> {
    public static final Identifier ID = new Identifier(CCIdeaPlugin.MOD_ID, "FileListS2CPacket".toLowerCase());

    public final int computer;
    public final String[] files;

    public FileListS2CPacket(int computer, String[] files) {
        this.computer = computer;
        this.files = files;
    }

    public FileListS2CPacket(PacketByteBuf buf) {
        this.computer = buf.readInt();
        int entries = buf.readInt();

        files = new String[entries];
        for (int i = 0; i < entries; i++) {
            files[i] = buf.readString();
        }
    }

    public PacketByteBuf write(PacketByteBuf buf) {
        buf.writeInt(this.computer);
        buf.writeInt(this.files.length);
        for (String s : this.files) {
            buf.writeString(s);
        }
        return buf;
    }

    @Override
    public void apply(ClientPacketHandler handler) {
        handler.onFileList(this);
    }

    @Override
    public Identifier getChannel() {
        return ID;
    }

    @Override
    public String toString() {
        return "FileListS2CPacket{" +
                "computer=" + computer +
                ", files=" + Arrays.toString(files) +
                '}';
    }
}

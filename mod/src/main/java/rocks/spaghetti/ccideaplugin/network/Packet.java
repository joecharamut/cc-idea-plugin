package rocks.spaghetti.ccideaplugin.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface Packet<T> {
    PacketByteBuf write(PacketByteBuf buf);
    void apply(T handler);
    Identifier getChannel();
}

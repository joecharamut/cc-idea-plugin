package rocks.spaghetti.ccideaplugin.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import rocks.spaghetti.ccideaplugin.network.s2c.FileListS2CPacket;
import rocks.spaghetti.ccideaplugin.network.s2c.GetComputerS2CPacket;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class ClientPacketHandler {
    private final Queue<Packet<ClientPacketHandler>> receivedPackets = new ConcurrentLinkedQueue<>();

    public void register() {
        registerPacket(FileListS2CPacket.ID, FileListS2CPacket.class);
        registerPacket(GetComputerS2CPacket.ID, GetComputerS2CPacket.class);
    }

    private void registerPacket(Identifier id, Class<? extends Packet<ClientPacketHandler>> packetClass) {
        Constructor<? extends Packet<ClientPacketHandler>> packetCtor;
        try {
            packetCtor = packetClass.getConstructor(PacketByteBuf.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Packet class " + packetClass.getName() + " must implement <init>(PacketByteBuf)");
        }

        ClientPlayNetworking.registerGlobalReceiver(
                id,
                (client, handler, buf, responseSender) -> {
                    try {
                        receivedPackets.add(packetCtor.newInstance(buf));
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void handlePackets(MinecraftClient client) {
        while (!receivedPackets.isEmpty()) {
            Packet<ClientPacketHandler> packet = receivedPackets.poll();
            packet.apply(this);
        }
    }

    public void onGetComputer(GetComputerS2CPacket packet) {
        System.out.println(packet);
        WrappedClientNetworking.receiveActiveComputer(packet.computer);
    }

    public void onFileList(FileListS2CPacket packet) {
        System.out.println(packet);
        WrappedClientNetworking.receiveFileList(packet.files);
    }
}

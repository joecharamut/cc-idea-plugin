package rocks.spaghetti.ccideaplugin.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import rocks.spaghetti.ccideaplugin.network.c2s.FileListC2SPacket;
import rocks.spaghetti.ccideaplugin.network.c2s.GetComputersC2SPacket;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class WrappedClientNetworking {
    private static final Queue<Packet<ServerPacketHandler>> packetsToSend = new ConcurrentLinkedQueue<>();
    private static final int TIMEOUT = 2000;
    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.MILLISECONDS;

    private WrappedClientNetworking() {
        throw new IllegalStateException("Utility Class");
    }

    public static void tick(MinecraftClient client) {
        while (!packetsToSend.isEmpty()) {
            Packet<ServerPacketHandler> packet = packetsToSend.poll();
            ClientPlayNetworking.send(packet.getChannel(), packet.write(PacketByteBufs.create()));
        }
    }

    private static final BlockingQueue<Integer> activeComputer = new LinkedBlockingQueue<>(1);
    public static void receiveActiveComputer(int computer) {
        activeComputer.add(computer);
    }
    public static int getActiveComputer() {
        packetsToSend.add(new GetComputersC2SPacket());

        try {
            Integer val = activeComputer.poll(TIMEOUT, TIMEOUT_UNIT);
            if (val != null) {
                return val;
            }
        } catch (InterruptedException e) {
            // fall through
        }

        return -1;
    }

    private static final BlockingQueue<String[]> fileList = new LinkedBlockingQueue<>(1);
    public static void receiveFileList(String[] list) {
        fileList.add(list);
    }
    public static String[] getFileList(int computer) {
        packetsToSend.add(new FileListC2SPacket(computer));

        try {
            return fileList.poll(TIMEOUT, TIMEOUT_UNIT);
        } catch (InterruptedException e) {
            // fall through
        }

        return new String[0];
    }
}

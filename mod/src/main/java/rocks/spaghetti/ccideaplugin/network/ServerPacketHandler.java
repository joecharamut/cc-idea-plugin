package rocks.spaghetti.ccideaplugin.network;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.core.filesystem.FileSystem;
import dan200.computercraft.core.filesystem.FileSystemException;
import dan200.computercraft.shared.computer.core.IComputer;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.inventory.ContainerComputerBase;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.ArrayUtils;
import rocks.spaghetti.ccideaplugin.network.c2s.FileListC2SPacket;
import rocks.spaghetti.ccideaplugin.network.c2s.GetComputersC2SPacket;
import rocks.spaghetti.ccideaplugin.network.s2c.FileListS2CPacket;
import rocks.spaghetti.ccideaplugin.network.s2c.GetComputersS2CPacket;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerPacketHandler {
    private final Queue<Packet<ServerPacketHandler>> receivedPackets = new ConcurrentLinkedQueue<>();

    public void register() {
        registerPacket(FileListC2SPacket.ID, FileListC2SPacket.class);
        registerPacket(GetComputersC2SPacket.ID, GetComputersC2SPacket.class);
    }

    public void handlePackets(MinecraftServer server) {
        while (!receivedPackets.isEmpty()) {
            Packet<ServerPacketHandler> packet = receivedPackets.poll();
            packet.apply(this);
        }
    }

    private void registerPacket(Identifier id, Class<? extends Packet<ServerPacketHandler>> packetClass) {
        Constructor<? extends Packet<ServerPacketHandler>> packetCtor;
        try {
            packetCtor = packetClass.getConstructor(PacketByteBuf.class, ServerPlayerEntity.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Packet class " + packetClass.getName() + " must implement <init>(PacketByteBuf, ServerPlayerEntity)");
        }

        ServerPlayNetworking.registerGlobalReceiver(
                id,
                (server, player, handler, buf, responseSender) -> {
                    try {
                        receivedPackets.add(packetCtor.newInstance(buf, player));
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
    }

    @Nullable
    public ServerComputer getActiveComputer(ServerPlayerEntity player) {
        if (player == null) return null;
        if (!(player.currentScreenHandler instanceof ContainerComputerBase)) return null;

        IComputer computer = ((ContainerComputerBase) player.currentScreenHandler).getComputer();
        if (computer == null) return null;
        if (!(computer instanceof ServerComputer)) return null;

        return ((ServerComputer) computer);
    }

    public ServerComputer[] getAccessibleComputers(ServerPlayerEntity player) {
        if (player.isCreative() || player.hasPermissionLevel(4)) {
            return ComputerCraft.serverComputerRegistry.getComputers().toArray(new ServerComputer[0]);
        } else {
            ServerComputer computer = getActiveComputer(player);
            if (computer != null) {
                return new ServerComputer[]{computer};
            }
        }

        return new ServerComputer[0];
    }

    public void onGetComputer(GetComputersC2SPacket packet) {
        System.out.println(packet);
        if (packet.playerEntity == null) return;

        ServerComputer computer = getActiveComputer(packet.playerEntity);
        int activeComputer = computer == null ? -1 : computer.getID();
        int[] accessibleComputers = ArrayUtils.toPrimitive(Arrays.stream(getAccessibleComputers(packet.playerEntity)).map(c -> c.getID()).toArray(Integer[]::new));

        ServerPlayNetworking.send(packet.playerEntity,
                GetComputersS2CPacket.ID,
                new GetComputersS2CPacket(activeComputer, accessibleComputers).write(PacketByteBufs.create()));
    }

    public void onFileList(FileListC2SPacket packet) {
        System.out.println(packet);
        if (packet.playerEntity == null) return;

        ServerComputer computer = getActiveComputer(packet.playerEntity);
        if (computer == null) return;
        if (computer.getID() != packet.computer) return;

        FileSystem fs = computer.getAPIEnvironment().getFileSystem();

        List<String> resolvedPaths = new ArrayList<>();
        try {
            List<String> paths = new ArrayList<>(Arrays.asList(fs.list("/")));

            while (!paths.isEmpty()) {
                String p = paths.get(0);
                paths.remove(0);

                if (fs.isDir(p)) {
                    List<String> nextPaths = new ArrayList<>(Arrays.asList(fs.list(p)));

                    if (nextPaths.isEmpty()) {
                        resolvedPaths.add("D;" + p);
                    } else {
                        for (String next : nextPaths) {
                            paths.add(fs.combine(p, next));
                        }
                    }
                } else {
                    resolvedPaths.add("F;" + p);
                }
            }
        } catch (FileSystemException e) {
            e.printStackTrace();
        }

        ServerPlayNetworking.send(packet.playerEntity,
                FileListS2CPacket.ID,
                new FileListS2CPacket(packet.computer, resolvedPaths.toArray(new String[0])).write(PacketByteBufs.create()));
    }
}

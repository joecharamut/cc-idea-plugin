package rocks.spaghetti.ccideaplugin;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import rocks.spaghetti.ccideaplugin.network.ClientPacketHandler;
import rocks.spaghetti.ccideaplugin.network.ServerPacketHandler;
import rocks.spaghetti.ccideaplugin.network.WrappedClientNetworking;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class CCIdeaPlugin implements ModInitializer, ClientModInitializer {
    public static final String MOD_ID = "ccideaplugin";

    @Override
    public void onInitialize() {
        // common init
        ServerPacketHandler handler = new ServerPacketHandler();
        handler.register();
        ServerTickEvents.END_SERVER_TICK.register(handler::handlePackets);
    }

    @Override
    public void onInitializeClient() {
        ClientPacketHandler handler = new ClientPacketHandler();
        handler.register();
        ClientTickEvents.END_CLIENT_TICK.register(handler::handlePackets);
        ClientTickEvents.END_CLIENT_TICK.register(WrappedClientNetworking::tick);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_SCROLL_LOCK)) {
                System.out.println("hi");
//                ClientPlayNetworking.send(GetComputerC2SPacket.ID, new GetComputerC2SPacket().write(PacketByteBufs.create()));
            }
        });

        try {
            // special exception handler for registry creation
            LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            // do nothing, error means registry already exists
        }

        try {
            Naming.rebind(RmiStub.RMI_PATH, IdeaApi.getInstance());
        } catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }
    }
}

package rocks.spaghetti.ccideaplugin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.LiteralText;

public class ClientUtils {
    private ClientUtils() {
        throw new IllegalStateException("Utility Class");
    }

    public static void sendToast(String title, String desc) {
        MinecraftClient.getInstance().getToastManager().add(new SystemToast(null, new LiteralText(title), new LiteralText(desc)));
    }
}

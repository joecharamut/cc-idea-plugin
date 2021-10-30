package rocks.spaghetti.ccideaplugin;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.lua.IComputerSystem;
import dan200.computercraft.shared.computer.upload.FileUpload;
import dan200.computercraft.shared.network.server.UploadFileMessage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import rocks.spaghetti.ccideaplugin.network.WrappedClientNetworking;
import rocks.spaghetti.ccideaplugin.rmi.RmiStub;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class IdeaApi extends UnicastRemoteObject implements RmiStub {
    private final Map<Integer, IComputerSystem> activeComputers = new HashMap<>();

    private static final IdeaApi instance;
    private static Exception cachedException;
    static {
        IdeaApi temp = null;
        try {
            temp = new IdeaApi();
        } catch (RemoteException e) {
            cachedException = e;
        }
        instance = temp;
    }

    private IdeaApi() throws RemoteException {
        super(0);
    }

    public static IdeaApi getInstance() {
        if (instance == null) {
            throw new IllegalStateException(cachedException);
        }
        return instance;
    }

    @Override
    public String getProtocolVersion() throws RemoteException {
        return RmiStub.PROTOCOL_VERSION;
    }

    @Override
    public String rmiTest() throws RemoteException {
        return "hello world from minecraft";
    }

    @Override
    public void sendToast(String title, String desc) throws RemoteException {
        ClientUtils.sendToast(title, desc);
    }

    @Override
    public int getActiveComputer() throws RemoteException {
        return WrappedClientNetworking.getActiveComputer();
    }

    @Override
    public String[] getFiles(int computer) throws RemoteException {
        return WrappedClientNetworking.getFileList(computer);
    }

    @Override
    public OutputStream downloadFile(int computer, String file) throws RemoteException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean uploadFile(int computer, String name, byte[] content) throws RemoteException {
        int instanceID = ComputerCraft.clientComputerRegistry.get(computer).getInstanceID();

        ByteBuffer buf = ByteBuffer.allocate(content.length).put(content);
        byte[] hash = FileUpload.getDigest(buf);
        buf.rewind();

        UploadFileMessage.send(instanceID, List.of(new FileUpload(name, buf, hash)));
        return false;
    }
}

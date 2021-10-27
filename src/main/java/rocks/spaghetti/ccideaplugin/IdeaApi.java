package rocks.spaghetti.ccideaplugin;

import dan200.computercraft.api.lua.IComputerSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import rocks.spaghetti.ccideaplugin.network.WrappedClientNetworking;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
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
    public String getFileContent(int computer, String file) throws RemoteException {
        throw new UnsupportedOperationException();
    }
}

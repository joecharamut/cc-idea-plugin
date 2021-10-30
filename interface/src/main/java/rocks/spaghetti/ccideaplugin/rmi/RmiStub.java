package rocks.spaghetti.ccideaplugin.rmi;

import java.io.OutputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiStub extends Remote {
    String RMI_PATH = "//localhost/CCIdeaAPI";
    String PROTOCOL_VERSION = "v0.0.3";

    String getProtocolVersion() throws RemoteException;
    String rmiTest() throws RemoteException;
    void sendToast(String title, String desc) throws RemoteException;

    int getActiveComputer() throws RemoteException;
    String[] getFiles(int computer) throws RemoteException;

    OutputStream downloadFile(int computer, String file) throws RemoteException;
    boolean uploadFile(int computer, String name, byte[] content) throws RemoteException;
}
package rocks.spaghetti.ccideaplugin;

import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import rocks.spaghetti.ccideaplugin.rmi.RmiStub;

import javax.swing.tree.DefaultMutableTreeNode;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;

public class CCIdeaPlugin {
    public static final Logger LOGGER = LogManager.getLogger(CCIdeaPlugin.class);
    private static CCIdeaPlugin INSTANCE = null;

    private final Project project;

    private RmiStub connection = null;

    private CCIdeaPlugin(Project project) {
        this.project = project;
    }

    public static void createInstance(Project project) {
        if (INSTANCE != null) {
            throw new IllegalStateException();
        }

        INSTANCE = new CCIdeaPlugin(project);
    }

    public static CCIdeaPlugin getInstance() {
        return INSTANCE;
    }

    public boolean connect() {
        try {
            connection = (RmiStub) Naming.lookup(RmiStub.RMI_PATH);

            String ver = connection.getProtocolVersion();
            if (!ver.equals(RmiStub.PROTOCOL_VERSION)) {
                throw new RemoteException(String.format("Invalid Protocol Version (expected %s, got %s)", RmiStub.PROTOCOL_VERSION, ver));
            }

            LOGGER.warn(connection.rmiTest());
            connection.sendToast("Hello", "CCIdea Plugin Connected");
            LOGGER.warn("Connected");
            return true;
        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            Notifications.Bus.notify(new Notification(
                    "CCIdeaPlugin",
                    AllIcons.Toolwindows.ToolWindowCommander,
                    "Could not connect",
                    "An exception occurred",
                    ex.toString(),
                    NotificationType.ERROR,
                    null), project);
            LOGGER.error(ex);
        }
        return false;
    }

    public void test(CCToolWindow window) {
        ProgressManager.getInstance().run(new Task.Backgroundable(
                project,
                "Refreshing computer",
                false,
                PerformInBackgroundOption.ALWAYS_BACKGROUND
        ) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    int computer = connection.getActiveComputer();
                    String[] files = connection.getFiles(computer);
                    LOGGER.warn(Arrays.toString(files));
                    if (files != null) {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            DefaultMutableTreeNode computerNode = new DefaultMutableTreeNode("Computer " + computer);

                            for (String file : files) {
                                DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(file);
                                computerNode.add(fileNode);
                            }

                            window.getTreeModel().insertNodeInto(computerNode, window.getRootNode(), 0);
                            LOGGER.warn(computerNode);

                            VirtualFileManager.getInstance().asyncRefresh(null);
                        });
                    }
                } catch (RemoteException e) {
                    LOGGER.error(e);
                }
            }
        });
    }
}

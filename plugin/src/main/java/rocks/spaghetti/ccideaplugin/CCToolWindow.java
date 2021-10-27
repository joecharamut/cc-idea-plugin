package rocks.spaghetti.ccideaplugin;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.ui.popup.IconButton;
import com.intellij.ui.navigation.Place;
import com.intellij.ui.roots.ToolbarPanel;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import static rocks.spaghetti.ccideaplugin.CCIdeaPlugin.LOGGER;

public class CCToolWindow {
    private JPanel content;
    private JButton button1;
    private ActionToolbar toolbar;
    private JComponent toolbarComponent;
    private Tree tree;

    private boolean connected = false;

    public CCToolWindow() {
        $$$setupUI$$$();
        toolbar.setTargetComponent(content);

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.expandPath(new TreePath(new Object[]{root}));

        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Computer ID 0");
        root.add(node);
        node.add(new DefaultMutableTreeNode("pipis"));
        node.add(new DefaultMutableTreeNode("pipis"));
        node.add(new DefaultMutableTreeNode("pipis"));
        node.add(new DefaultMutableTreeNode("pipis"));

        tree.expandPath(new TreePath(root.getPath()));
    }

    public JPanel getContent() {
        return content;
    }

    public DefaultMutableTreeNode getRootNode() {
        return (DefaultMutableTreeNode) tree.getModel().getRoot();
    }

    private ActionToolbar createToolbar() {
        return ActionManager.getInstance().createActionToolbar("CCToolWindowToolbar",
                new DefaultActionGroup(
                        new AnAction("Connect", "Connect/Disconnect from Game", AllIcons.Nodes.Pluginobsolete) {
                            @Override
                            public void actionPerformed(@NotNull AnActionEvent e) {
                                if (!connected) {
                                    connected = CCIdeaPlugin.getInstance().connect();
                                }
                            }

                            @Override
                            public void update(@NotNull AnActionEvent e) {
                                e.getPresentation().setText(connected ? "Disconnect" : "Connect");
                                e.getPresentation().setIcon(connected ? AllIcons.Nodes.Plugin : AllIcons.Nodes.Pluginobsolete);
                            }
                        },
                        new AnAction("Refresh", null, AllIcons.Actions.Refresh) {
                            @Override
                            public void actionPerformed(@NotNull AnActionEvent e) {
                                LOGGER.warn("test");
                                CCIdeaPlugin.getInstance().test();
                            }
                        }
                ), true);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        content = new JPanel();
        content.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        content.add(toolbarComponent, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        tree = new Tree();
        content.add(tree, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        button1 = new JButton();
        button1.setText("Button");
        content.add(button1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        content.add(spacer1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return content;
    }

    private void createUIComponents() {
        toolbar = createToolbar();
        toolbarComponent = toolbar.getComponent();
    }
}

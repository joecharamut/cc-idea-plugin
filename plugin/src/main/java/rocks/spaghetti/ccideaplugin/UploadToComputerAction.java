package rocks.spaghetti.ccideaplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class UploadToComputerAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        VirtualFile file = e.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE);
        System.out.println(file);
        if (file != null) {
            CCIdeaPlugin.getInstance().test2(file);
        }
    }
}

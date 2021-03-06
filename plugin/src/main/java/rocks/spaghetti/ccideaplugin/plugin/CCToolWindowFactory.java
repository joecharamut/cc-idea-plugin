package rocks.spaghetti.ccideaplugin.plugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import rocks.spaghetti.ccideaplugin.CCIdeaPlugin;
import rocks.spaghetti.ccideaplugin.CCToolWindow;

public class CCToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        CCIdeaPlugin.createInstance(project);
        CCToolWindow window = new CCToolWindow(project);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(window.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
    }
}

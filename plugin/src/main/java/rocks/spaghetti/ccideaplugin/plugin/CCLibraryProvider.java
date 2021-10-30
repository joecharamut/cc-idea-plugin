package rocks.spaghetti.ccideaplugin.plugin;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.AdditionalLibraryRootsProvider;
import com.intellij.openapi.roots.SyntheticLibrary;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.tang.intellij.lua.lang.LuaIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CCLibraryProvider extends AdditionalLibraryRootsProvider {
    @Override
    public @NotNull Collection<SyntheticLibrary> getAdditionalProjectLibraries(@NotNull Project project) {
        URL resource = CCLibraryProvider.class.getClassLoader().getResource("apis");
        if (resource == null) return Collections.emptyList();

        VirtualFile rom = VfsUtil.findFileByURL(resource);
        if (rom == null) return Collections.emptyList();

        return List.of(new CCStdLibrary(rom));
    }

    public static class CCStdLibrary extends SyntheticLibrary implements ItemPresentation {
        private final VirtualFile root;
        private final Collection<VirtualFile> roots;

        private CCStdLibrary(VirtualFile root) {
            this.root = root;
            roots = List.of(root);
        }

        @Override
        public @NotNull Collection<VirtualFile> getSourceRoots() {
            return roots;
        }

        @Override
        public @NlsSafe @Nullable String getLocationString() {
            return "ComputerCraft std library";
        }

        @Override
        public @NlsSafe @Nullable String getPresentableText() {
            return "ComputerCraft";
        }

        @Override
        public @Nullable Icon getIcon(boolean unused) {
            return LuaIcons.FILE;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CCStdLibrary that = (CCStdLibrary) o;
            return root.equals(that.root);
        }

        @Override
        public int hashCode() {
            return root.hashCode();
        }
    }
}

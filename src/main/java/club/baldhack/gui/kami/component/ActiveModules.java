package club.baldhack.gui.kami.component;

import club.baldhack.gui.rgui.component.container.use.Frame;
import club.baldhack.gui.rgui.component.listen.RenderListener;
import club.baldhack.gui.rgui.component.use.Label;
import club.baldhack.gui.rgui.util.ContainerHelper;
import club.baldhack.gui.rgui.util.Docking;

public class ActiveModules extends Label {
//    public HashMap<Module, Integer> slide = new HashMap<>();

    public boolean sort_up = true;

    public ActiveModules() {
        super("");

        addRenderListener(new RenderListener() {
            @Override
            public void onPreRender() {
                Frame parentFrame = ContainerHelper.getFirstParent(Frame.class, ActiveModules.this);
                if (parentFrame == null) return;
                Docking docking = parentFrame.getDocking();
                if (docking.isTop()) sort_up = true;
                if (docking.isBottom()) sort_up = false;
            }

            @Override
            public void onPostRender() {}
        });
    }
};
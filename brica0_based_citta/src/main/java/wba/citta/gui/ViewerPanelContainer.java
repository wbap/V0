package wba.citta.gui;

import java.util.Map;
import java.util.HashMap;
import java.util.WeakHashMap;

import javax.swing.JComponent;
import javax.swing.JFrame;

import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.frontend.DockFrontendPerspective;
import bibliothek.gui.dock.frontend.FrontendDockablePerspective;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.station.split.SplitDockPerspective;
import bibliothek.gui.dock.station.stack.StackDockPerspective;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.support.PlaceholderStrategyListener;
import bibliothek.gui.DockController;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.util.Path;

public class ViewerPanelContainer extends SplitDockStation implements ViewerPanelEventListener {
    private static final long serialVersionUID = 1L;
    final DockFrontend dockFrontend;
    final DockFrontendPerspective dockFrontendPerspective;
    final Map<ViewerPanel, DefaultDockable> dockables = new HashMap<ViewerPanel, DefaultDockable>();

    static class PlaceholderStrategyImpl implements PlaceholderStrategy {
        Map<Dockable, Path> pathMap = new WeakHashMap<Dockable, Path>();

        @Override
        public void addListener(PlaceholderStrategyListener listener) {
        }

        @Override
        public Path getPlaceholderFor(Dockable dockable) {
            Path retval = pathMap.get(dockable);
            if (retval == null) {
                retval = new Path(dockable.getClass().getName());
                pathMap.put(dockable, retval);
            }
            return retval;
        }

        @Override
        public void install(DockStation arg0) {            
        }

        @Override
        public boolean isValidPlaceholder(Path path) {
            for (Path _path: pathMap.values()) {
                if (_path.equals(path)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void removeListener(PlaceholderStrategyListener listener) {
        }

        @Override
        public void uninstall(DockStation arg0) {            
        }
    }
    
    public ViewerPanelContainer(JFrame f) {
        dockFrontend = new DockFrontend(new DockController(), f);
        dockFrontend.getController().getProperties().set(
            PlaceholderStrategy.PLACEHOLDER_STRATEGY,
            new PlaceholderStrategyImpl()
        );
        dockFrontend.addRoot("station", this);
        dockFrontendPerspective = buildPerspective(dockFrontend);
    }

    static DockFrontendPerspective buildPerspective(final DockFrontend dockFrontend) {
        final DockFrontendPerspective perspective = dockFrontend.getPerspective(false);
        final SplitDockPerspective stationPerspective = (SplitDockPerspective)perspective.getRoot("station");
        final PerspectiveDockable agentViewer = new FrontendDockablePerspective("wba.citta.gsa.viewer.AgentViewer");
        final PerspectiveDockable iterationCountViewer = new FrontendDockablePerspective("wba.citta.StepCountViewer");
        final PerspectiveDockable map = new FrontendDockablePerspective("main");
        final PerspectiveDockable sharedMemoryViewer = new FrontendDockablePerspective("wba.citta.gsa.viewer.SharedMemoryViewer");
        final PerspectiveDockable failAgentTreeViewer = new FrontendDockablePerspective("wba.citta.gsa.viewer.TreeViewer");
        final PerspectiveDockable logViewer = new FrontendDockablePerspective("wba.citta.LogViewer");
        final StackDockPerspective infoPanes = new StackDockPerspective(
            new PerspectiveDockable[] { sharedMemoryViewer, failAgentTreeViewer, logViewer },
            sharedMemoryViewer
        );
        stationPerspective.getRoot().setChild(
            new SplitDockPerspective.Node(
                Orientation.HORIZONTAL, 0.2,
                new SplitDockPerspective.Node(
                    Orientation.VERTICAL, 0.75,
                    new SplitDockPerspective.Leaf(agentViewer, null, null, -1),
                    new SplitDockPerspective.Leaf(iterationCountViewer, null, null, -1),
                    null, null, -1
                ),
                new SplitDockPerspective.Node(
                    Orientation.HORIZONTAL, 0.75,
                    new SplitDockPerspective.Leaf(map, null, null, -1),
                    new SplitDockPerspective.Leaf(infoPanes, null, null, -1),
                    null, null, -1
                ),
                null, null, -1
            )
        );
        return perspective;
    }

    public void panelCreated(ViewerPanelEvent evt) {
        final ViewerPanel viewerPanel = evt.getViewerPanel();
        final JComponent panelComponent = viewerPanel.getComponent();
        final DefaultDockable dockable = new DefaultDockable(panelComponent);
        dockable.setTitleText(viewerPanel.getPreferredTitle());
        dockables.put(viewerPanel, dockable);
        String name;
        if (viewerPanel.getViewerPanelRoles().contains("main")) {
            name = "main";
        } else {
            name = viewerPanel.getClass().getName();
        }
        dockFrontend.addDockable(name, dockable);
    }

    @Override
    public void panelPopulated(ViewerPanelEvent evt) {
        dockFrontendPerspective.apply();
    }
}

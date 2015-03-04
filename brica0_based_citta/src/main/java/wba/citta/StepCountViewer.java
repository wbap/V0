package wba.citta;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import wba.citta.gui.ViewerPanel;

public class StepCountViewer extends JPanel implements ViewerPanel, StepEventListener, IterationEventListener {
    private static final long serialVersionUID = 1L;
    private static final Set<String> roles = new HashSet<String>(Arrays.asList("side", "small"));
    private Font font = new Font("Monospaced", Font.PLAIN, 18);
    private JLabel iterationCountLabel;
    private JLabel stepCountLabel;
    private JLabel stepCountSinceLastGoalLabel;

    public StepCountViewer() {
        super();
        initComponents();
    }
 
    void initComponents() {
        SpringLayout lo = new SpringLayout();
        setLayout(lo);
        Spring lastY = Spring.constant(0);
        Spring lastHeight = Spring.constant(0);
        Spring w = lo.getConstraint(SpringLayout.WIDTH, this);
        {
            final JLabel label = new JLabel("Iteration(s)");
            label.setOpaque(true);
            label.setBackground(Color.DARK_GRAY);
            label.setForeground(Color.WHITE);
            add(label,
                new SpringLayout.Constraints(
                    Spring.constant(0),
                    (lastY = Spring.sum(lastY, lastHeight)),
                    w,
                    (lastHeight = Spring.height(label))
                )
            );
        }
        iterationCountLabel = new JLabel("0", SwingConstants.RIGHT);
        {
            iterationCountLabel.setFont(font);
            add(iterationCountLabel,
                new SpringLayout.Constraints(
                    Spring.constant(0),
                    (lastY = Spring.sum(lastY, lastHeight)),
                    w,
                    (lastHeight = Spring.height(iterationCountLabel))
                )
            );
        }
        {
            final JLabel label = new JLabel("Total Step(s)");
            label.setOpaque(true);
            label.setBackground(Color.DARK_GRAY);
            label.setForeground(Color.WHITE);
            add(label,
                new SpringLayout.Constraints(
                    Spring.constant(0),
                    (lastY = Spring.sum(lastY, lastHeight)),
                    w,
                    (lastHeight = Spring.height(label))
                )
            );
        }
        stepCountLabel = new JLabel("0", SwingConstants.RIGHT);
        {
            stepCountLabel.setFont(font);
            add(stepCountLabel,
                new SpringLayout.Constraints(
                    Spring.constant(0),
                    (lastY = Spring.sum(lastY, lastHeight)),
                    w,
                    (lastHeight = Spring.height(stepCountLabel))
                )
            );
        }
        {
            final JLabel label = new JLabel("Step(s)");
            label.setOpaque(true);
            label.setBackground(Color.DARK_GRAY);
            label.setForeground(Color.WHITE);
            add(label,
                new SpringLayout.Constraints(
                    Spring.constant(0),
                    (lastY = Spring.sum(lastY, lastHeight)),
                    w,
                    (lastHeight = Spring.height(stepCountLabel))
                )
            );            
        }
        stepCountSinceLastGoalLabel = new JLabel("0", SwingConstants.RIGHT);
        {
            stepCountSinceLastGoalLabel.setFont(font);
            add(stepCountSinceLastGoalLabel,
                new SpringLayout.Constraints(
                    Spring.constant(0),
                    (lastY = Spring.sum(lastY, lastHeight)),
                    w,
                    (lastHeight = Spring.height(stepCountSinceLastGoalLabel))
                )
            );
        }
        /*
        {
            final SpringLayout.Constraints c = lo.getConstraints(this);
            c.setConstraint(SpringLayout.EAST, Spring.max(Spring.constant(1000), w));
            c.setConstraint(SpringLayout.SOUTH, Spring.sum(lastY, lastHeight));
        }
        */
        
        validate();
    }

    @Override
    public Set<String> getViewerPanelRoles() {
        return roles;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public String getPreferredTitle() {
        return "Iteration";
    }

    @Override
    public void nextStep(final StepEvent evt) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                stepCountLabel.setText(Integer.toString(evt.getStepCount()));
                stepCountSinceLastGoalLabel.setText(Integer.toString(evt.getGoalStepCount()));
            }
        });
    }

    @Override
    public void iterationStarted(final IterationEvent evt) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                iterationCountLabel.setText(String.format("%d / %d", evt.getGoalCount(), evt.getIteration()));
            }
        });
    }

    @Override
    public void iterationEnded(IterationEvent evt) {
        iterationStarted(evt);
    }
}

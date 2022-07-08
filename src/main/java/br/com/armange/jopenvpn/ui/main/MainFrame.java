package br.com.armange.jopenvpn.ui.main;

import br.com.armange.jopenvpn.ui.UiUtils;

import javax.swing.*;
import javax.swing.event.ChangeListener;

public class MainFrame extends JFrame {
    private final SessionsFrame sessionsFrame;
    private final StartStopFrame startStopFrame;
    private final ConfigsCardFrame configsCardFrame;

    private JTabbedPane tabRoot;
    private JPanel pnlRoot;
    private JPanel pnlSessionTab;
    private JPanel pnlStartStopTab;
    private JPanel pnlConfigsTab;

    public MainFrame(final SessionsFrame sessionsFrame,
                     final StartStopFrame startStopFrame,
                     final ConfigsCardFrame configsCardFrame) {
        this.sessionsFrame = sessionsFrame;
        this.startStopFrame = startStopFrame;
        this.configsCardFrame = configsCardFrame;

        setup();
    }

    private void setup() {
        final JPanel pnlSessions = sessionsFrame.getPnlSessions();
        final JPanel pnlStartStop = startStopFrame.getPnlStartStop();
        final JPanel pnlConfigsCard = configsCardFrame.getPnlConfigsCard();

        pnlSessionTab.add(pnlSessions, UiUtils.getCenterGridConstraints());
        pnlStartStopTab.add(pnlStartStop, UiUtils.getCenterGridConstraints());
        pnlConfigsTab.add(pnlConfigsCard, UiUtils.getCenterGridConstraints());

        sessionsFrame.showSessions();
        sessionsFrame.loadSessionPaths();
        startStopFrame.loadProfiles();
        configsCardFrame.getConfigsFrame().loadProfiles();
        addListeners();

        setContentPane(pnlRoot);
        setSize(630, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void addListeners() {
        tabRoot.addChangeListener(buildTabRootChangeListener());
    }

    private ChangeListener buildTabRootChangeListener() {
        return changeEvent -> {
            if (0 == tabRoot.getSelectedIndex()) {
                sessionsFrame.showSessions();
                sessionsFrame.loadSessionPaths();
            } else if (1 == tabRoot.getSelectedIndex()) {
                startStopFrame.loadProfiles();
            } else if (2 == tabRoot.getSelectedIndex()) {
                configsCardFrame.getConfigsFrame().loadProfiles();
            }
        };
    }
}

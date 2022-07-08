package br.com.armange.jopenvpn.ui.main;

import br.com.armange.jopenvpn.be.CommandResult;
import br.com.armange.jopenvpn.be.openvpn.command.OpenVpnCommand;
import br.com.armange.jopenvpn.be.openvpn.dto.OpenVpnSessionDto;
import br.com.armange.jopenvpn.be.openvpn.result.SessionsListCommandResult;
import lombok.Getter;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SessionsFrame implements Serializable {
    private JPanel pnlSessions;
    private JTextArea txtaOutput;
    private JComboBox<String> cbbCleanup;
    private JButton cleanupButton;
    private JScrollPane scrOutput;

    private SessionsListCommandResult commandResult;

    private JTextArea txtaScrolledMessage;
    private JScrollPane scpScrolledMessage;

    private final StartStopFrame startStopFrame;

    public SessionsFrame(final StartStopFrame startStopFrame) {
        this.startStopFrame = startStopFrame;

        setup();
    }

    private void setup() {
        addListeners();
    }

    private void addListeners() {
        cleanupButton.addActionListener(buildCleanupActionListener());
    }

    private ActionListener buildCleanupActionListener() {
        return actionEvent -> {
            if (cbbCleanup.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(
                        pnlSessions,
                        "No session selected.",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                final int confirmation = JOptionPane.showConfirmDialog(
                        pnlSessions,
                        String.format("Path: %s", cbbCleanup.getSelectedItem()),
                        "Do you confirm the disconnection of the session?",
                        JOptionPane.YES_NO_OPTION);

                if (confirmation == JOptionPane.YES_OPTION) {
                    doSessionCleanup();
                }
            }
        };
    }

    private void doSessionCleanup() {
        final CommandResult result = OpenVpnCommand.
                DISCONNECT_SESSION_BY_PATH.run(String.valueOf(cbbCleanup.getSelectedItem()));

        if (result.getResultCode() == 0) {
            JOptionPane.showMessageDialog(
                    pnlSessions,
                    result.getResultMessage(),
                    "Session disconnected",
                    JOptionPane.INFORMATION_MESSAGE);

            startStopFrame.cleanOutput();
        } else {
            JOptionPane.showMessageDialog(
                    pnlSessions,
                    result.getResultMessage(),
                    "Fail",
                    JOptionPane.ERROR_MESSAGE);
        }

        showSessions();
        loadSessionPaths();
    }

    public void showSessions() {
        commandResult = (SessionsListCommandResult) OpenVpnCommand.LIST_SESSIONS.run();

        txtaOutput.setText(commandResult.getResultMessage());
    }

    public void loadSessionPaths() {
        final List<OpenVpnSessionDto> openVpnSessionDtoList = commandResult.extractSessions();

        cbbCleanup.setModel(new DefaultComboBoxModel<>(openVpnSessionDtoList.stream()
                .map(OpenVpnSessionDto::getPath)
                .collect(Collectors.toList())
                .toArray(new String[]{})));
    }
}

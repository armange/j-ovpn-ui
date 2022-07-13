package br.com.armange.jopenvpn.ui.main;

import br.com.armange.jopenvpn.be.CommandResult;
import br.com.armange.jopenvpn.be.dbus.AuthUserPass;
import br.com.armange.jopenvpn.be.dbus.OpenVpnDBusRepository;
import br.com.armange.jopenvpn.be.openvpn.command.OpenVpnCommand;
import br.com.armange.jopenvpn.be.openvpn.dto.OpenVpnConfigDto;
import br.com.armange.jopenvpn.be.openvpn.dto.StatusDto;
import br.com.armange.jopenvpn.be.openvpn.result.ConfigsListCommandResult;
import br.com.armange.jopenvpn.ui.main.model.combobox.ConfigDtoComboItem;
import br.com.armange.jopenvpn.ui.main.model.combobox.OpenVpnConfigDtoModel;
import com.github.hypfvieh.util.StringUtil;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.*;
import java.util.List;
import java.util.prefs.Preferences;

@Getter
public class StartStopFrame implements Serializable {
    public static final String EMPTY = "";
    public static final String WARNING = "Warning";
    private JPanel pnlStartStop;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<ConfigDtoComboItem> cbbProfile;
    private JTextArea txtaOutput;
    private JButton btnStop;
    private JButton btnStart;
    private JLabel lblUsername;
    private JLabel lblProfile;
    private JLabel lblPassword;
    private JPanel pnlButtons;
    private JScrollPane scrOutput;
    private JCheckBox chkPreAuth;
    private JCheckBox chkSavePass;

    private String lastSelectedProfile;

    public StartStopFrame() {
        setup();
    }

    private void setup() {
        addListeners();
    }

    private void addListeners() {
        btnStart.addActionListener(buildStartSessionActionListener());
        btnStop.addActionListener(actionEvent -> stopSession());
        cbbProfile.addActionListener(buildCbbProfileActionListener());
    }

    private ActionListener buildStartSessionActionListener() {
        return actionEvent -> {
            if (!chkPreAuth.isSelected() && txtUsername.getText().isBlank()
                    || (new String(txtPassword.getPassword())).isBlank()) {
                JOptionPane.showMessageDialog(pnlStartStop,
                        "The username and password are required.",
                        WARNING,
                        JOptionPane.WARNING_MESSAGE);
            } else if (!chkPreAuth.isSelected() && cbbProfile.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(pnlStartStop,
                        "No profile selected.",
                        WARNING,
                        JOptionPane.WARNING_MESSAGE);
            } else if (cbbProfile.getSelectedItem() != null
                    && getSelectedProfile().getStatus() == StatusDto.CLIENT_CONNECTED) {
                JOptionPane.showMessageDialog(pnlStartStop,
                        "The selected profile is already connected.",
                        WARNING,
                        JOptionPane.WARNING_MESSAGE);
            } else {
                startSession();
            }
        };
    }

    private ActionListener buildCbbProfileActionListener() {
        return actionEvent -> {
            final ConfigDtoComboItem selectedItem = (ConfigDtoComboItem) cbbProfile
                    .getSelectedItem();

            assert selectedItem != null;

            lastSelectedProfile = selectedItem.getConfigName();
            findPreferences(selectedItem);
            findOvpnConfig(selectedItem);
        };
    }

    public void loadProfiles() {
        final ConfigsListCommandResult commandResult = (ConfigsListCommandResult)
                OpenVpnCommand.LIST_CONFIGS.run();
        final List<OpenVpnConfigDto> profiles = commandResult.extractProfiles();

        Collections.sort(profiles);

        final OpenVpnConfigDtoModel aModel = new OpenVpnConfigDtoModel(profiles);

        cbbProfile.setModel(aModel);

        if (lastSelectedProfile != null) {
            for (OpenVpnConfigDto dto : profiles) {
                if (lastSelectedProfile.equals(dto.getConfigName())) {
                    cbbProfile.setSelectedIndex(profiles.indexOf(dto));
                    break;
                }
            }
        }

        findOvpnConfig((ConfigDtoComboItem) cbbProfile.getSelectedItem());
    }

    public void startSession() {
        final List<String> outputLines = new ArrayList<>();

        if (!chkPreAuth.isSelected()) {
            Collections.addAll(outputLines,
                    Optional.ofNullable(txtUsername.getText()).orElse(EMPTY),
                    new String(txtPassword.getPassword()));
        }

        final ConfigDtoComboItem selectedItem = (ConfigDtoComboItem) cbbProfile.getSelectedItem();
        assert selectedItem != null;
        final CommandResult commandResult = OpenVpnCommand.START_SESSION
                .run(outputLines, selectedItem.getConfigName());

        txtaOutput.setText(commandResult.getResultMessage());
        savePreferencesIfChecked(selectedItem);
        loadProfiles();
    }

    public void stopSession() {
        final ConfigDtoComboItem selectedItem = (ConfigDtoComboItem) cbbProfile.getSelectedItem();
        assert selectedItem != null;
        final CommandResult commandResult = OpenVpnCommand.DISCONNECT_SESSION_BY_CONFIG
                .run(String.valueOf(selectedItem.getConfigName()));

        txtaOutput.setText(commandResult.getResultMessage());
        loadProfiles();
    }

    private void findPreferences(final ConfigDtoComboItem selectedItem) {
        final Preferences node = Preferences.userRoot()
                .node(this.getClass()
                        .getName()
                        .replace(".", "/")
                        .concat(selectedItem.getPath()));


        final boolean savePass = node.getBoolean(chkSavePass.getName(), false);
        final String username = node.get(txtUsername.getName(), null);
        final String password = node.get(txtPassword.getName(), null);

        chkSavePass.setSelected(savePass);
        txtUsername.setText(username);
        txtPassword.setText(password);
    }

    private void findOvpnConfig(final ConfigDtoComboItem selectedItem) {
        if (selectedItem != null) {
            final Optional<AuthUserPass> credentials = OpenVpnDBusRepository
                    .findCredentials(selectedItem.getPath());

            credentials.ifPresentOrElse(authUserPass -> {
                txtUsername.setText(authUserPass.getUsername());
                txtUsername.setEnabled(false);
                txtPassword.setText(authUserPass.getPassword());
                txtPassword.setEnabled(false);
                chkPreAuth.setSelected(true);
                chkSavePass.setEnabled(false);
            }, () -> {
                chkPreAuth.setSelected(false);
                txtUsername.setEnabled(true);
                txtPassword.setEnabled(true);
                chkSavePass.setEnabled(true);
            });
        }
    }

    private void savePreferencesIfChecked(final ConfigDtoComboItem selectedItem) {
        final Preferences node = Preferences.userRoot()
                .node(this.getClass()
                        .getName()
                        .replace(".", "/")
                        .concat(selectedItem.getPath()));

        if (chkSavePass.isSelected()) {
            node.putBoolean(chkSavePass.getName(), chkSavePass.isSelected());
            node.put(txtUsername.getName(), txtUsername.getText());
            node.put(txtPassword.getName(), new String(txtPassword.getPassword()));
        } else {
            node.remove(chkSavePass.getName());
            node.remove(txtUsername.getName());
            node.remove(txtPassword.getName());
        }
    }

    public ConfigDtoComboItem getSelectedProfile() {
        return (ConfigDtoComboItem) cbbProfile.getSelectedItem();
    }

    public void cleanOutput() {
        getTxtaOutput().setText(null);
    }
}

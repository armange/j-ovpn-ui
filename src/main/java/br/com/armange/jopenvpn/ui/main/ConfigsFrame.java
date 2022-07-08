package br.com.armange.jopenvpn.ui.main;

import br.com.armange.jopenvpn.be.openvpn.command.OpenVpnCommand;
import br.com.armange.jopenvpn.be.openvpn.dto.OpenVpnConfigDto;
import br.com.armange.jopenvpn.be.openvpn.result.ConfigsListCommandResult;
import lombok.Getter;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class ConfigsFrame {
    private JPanel pnlConfigs;
    private JTextField txtFile;
    private JComboBox<String> cbbProfile;
    private JTextArea txtaOutput;
    private JButton btnNew;
    private JButton btnRemove;
    private JPanel pnlButtons;
    private JLabel lblProfile;
    private JLabel lblFile;
    private JScrollPane scrOutput;

    private final Map<String, String> profilesMap = new HashMap<>();

    public ConfigsFrame() {
        setup();
    }

    private void setup() {
        addListeners();
    }

    private void addListeners() {
        cbbProfile.addActionListener(buildCbbProfileActionListener());
        btnRemove.addActionListener(buildBtnRemoveActionListener());
    }

    private ActionListener buildCbbProfileActionListener() {
        return actionEvent -> readSelectedProfile();
    }

    private ActionListener buildBtnRemoveActionListener() {
        return actionEvent -> {
            final String message = "Do you want to remove the following path?\n%s";
            final String path = profilesMap.get(String.valueOf(cbbProfile.getSelectedItem()));
            final int option = JOptionPane.showConfirmDialog(
                    pnlConfigs,
                    String.format(message, path),
                    "Removing configuration",
                    JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                OpenVpnCommand.REMOVE_CONFIG_BY_PATH.run(Collections.singletonList("YES"), path);
                loadProfiles();
            }
        };
    }

    private void readSelectedProfile() {
        if (cbbProfile.getSelectedItem() != null) {
            txtFile.setText(
                    profilesMap.get(
                            String.valueOf(cbbProfile.getSelectedItem())));
        }
    }

    public void loadProfiles() {
        final ConfigsListCommandResult commandResult = (ConfigsListCommandResult)
                OpenVpnCommand.LIST_CONFIGS.run();
        final List<OpenVpnConfigDto> profiles = commandResult.extractProfiles();

        profilesMap.clear();
        profilesMap.putAll(profiles.stream().collect(
                Collectors.toMap(OpenVpnConfigDto::getConfigName, OpenVpnConfigDto::getPath)));

        cbbProfile.setModel(new DefaultComboBoxModel<>(
                profilesMap.keySet().toArray(new String[0])));

        txtaOutput.setText(commandResult.getResultMessage());

        readSelectedProfile();
    }
}

package br.com.armange.jopenvpn.ui.main;

import br.com.armange.jopenvpn.be.CommandResult;
import br.com.armange.jopenvpn.be.openvpn.command.OpenVpnCommand;
import br.com.armange.jopenvpn.be.openvpn.dto.OpenVpnConfigDto;
import br.com.armange.jopenvpn.be.openvpn.result.ConfigsListCommandResult;
import br.com.armange.jopenvpn.ui.AbstractUIFrameIJ;
import com.google.common.io.Files;
import lombok.Getter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

@Getter
public class NewConfigFrame extends AbstractUIFrameIJ {
    public static final String OVPN = "ovpn";
    public static final String WARNING = "Warning";
    private JTextField txtFile;
    private JTextField txtProfile;
    private JButton btnFindFile;
    private JTextArea txtaOutput;
    private JButton btnSave;
    private JLabel lblPofile;
    private JLabel lblFile;
    private JPanel pnlNewConfig;
    private JButton btnBack;
    private final JFileChooser fileChooser = new JFileChooser();

    private final ConfigsFrame configsFrame;

    public NewConfigFrame(final ConfigsFrame configsFrame) {
        this.configsFrame = configsFrame;

        setupUI();
        addListeners();
    }

    @Override
    protected void setupUI() {
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setFileFilter(new FileNameExtensionFilter("OVPN files", OVPN));
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setPreferredSize(new Dimension(800, 600));
    }

    @Override
    protected void addListeners() {
        btnFindFile.addActionListener(buildBtnFileActionListener());
        btnSave.addActionListener(actionEvent -> {
            if (validateNewProfile()) return;

            final ConfigsListCommandResult cmdResult = (ConfigsListCommandResult) OpenVpnCommand
                    .LIST_CONFIGS.run();
            final List<OpenVpnConfigDto> openVpnConfigDtos = cmdResult.extractProfiles();
            final String configName = openVpnConfigDtos.stream()
                    .map(OpenVpnConfigDto::getConfigName)
                    .filter(txtProfile.getText()::equals)
                    .findFirst()
                    .orElse(null);

            if (configName != null) {
                JOptionPane.showMessageDialog(
                        pnlNewConfig,
                        String.format("A profile with the name %s already exists.", configName),
                        WARNING,
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            final int confirmation = JOptionPane.showConfirmDialog(
                    pnlNewConfig, "Do you want to save the profile?",
                    "Saving profile", JOptionPane.YES_NO_OPTION);

            if (confirmation == JOptionPane.YES_OPTION) {
                final CommandResult importResult = OpenVpnCommand.IMPORT_CONFIG
                        .run(txtFile.getText(), txtProfile.getText());

                txtaOutput.setText(importResult.getResultMessage());
                configsFrame.loadProfiles();
            }
        });
    }

    private boolean validateNewProfile() {
        if (txtFile.getText() == null || txtFile.getText().isBlank()
                || txtFile.getText().isEmpty()) {
            JOptionPane.showMessageDialog(
                    pnlNewConfig,
                    "No OVPN file selected.",
                    WARNING,
                    JOptionPane.WARNING_MESSAGE);
            return true;
        } else {
            final File file = new File(txtFile.getText());
            final String fileExtension = Files.getFileExtension(txtFile.getText());

            if (!file.exists()) {
                JOptionPane.showMessageDialog(
                        pnlNewConfig,
                        "File not found.",
                        WARNING,
                        JOptionPane.WARNING_MESSAGE);
                return true;
            }

            if (!OVPN.equalsIgnoreCase(fileExtension)) {
                JOptionPane.showMessageDialog(
                        pnlNewConfig,
                        "No OVPN file selected.",
                        WARNING,
                        JOptionPane.WARNING_MESSAGE);
                return true;
            }
        }

        if (txtProfile.getText() == null || txtProfile.getText().isBlank()
                || txtProfile.getText().isEmpty()) {
            JOptionPane.showMessageDialog(
                    pnlNewConfig,
                    "No profile name typed.",
                    WARNING,
                    JOptionPane.WARNING_MESSAGE);
            return true;
        }
        return false;
    }

    private ActionListener buildBtnFileActionListener() {
        return actionEvent -> {
            final int returnVal = fileChooser.showOpenDialog(pnlNewConfig);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                final File file = fileChooser.getSelectedFile();

                txtFile.setText(file.getAbsolutePath());
            }
        };
    }
}

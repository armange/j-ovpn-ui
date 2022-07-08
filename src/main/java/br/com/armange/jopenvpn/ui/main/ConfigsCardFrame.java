package br.com.armange.jopenvpn.ui.main;

import br.com.armange.jopenvpn.ui.AbstractUIFrameIJ;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
public class ConfigsCardFrame extends AbstractUIFrameIJ {
    private final ConfigsFrame configsFrame;
    private final NewConfigFrame newConfigFrame;

    private JPanel pnlConfigsCard;
    private CardLayout cardLayout;

    public ConfigsCardFrame(final ConfigsFrame configsFrame,
                            final NewConfigFrame newConfigFrame) {
        this.configsFrame = configsFrame;
        this.newConfigFrame = newConfigFrame;
        this.cardLayout = (CardLayout) pnlConfigsCard.getLayout();

        setupUI();
        addListeners();
    }

    protected void setupUI() {
        pnlConfigsCard.add(configsFrame.getPnlConfigs(), "1");
        pnlConfigsCard.add(newConfigFrame.getPnlNewConfig(), "2");
    }

    protected void addListeners() {
        configsFrame.getBtnNew().addActionListener(
                actionEvent -> cardLayout.show(pnlConfigsCard, "2"));
        newConfigFrame.getBtnBack().addActionListener(
                actionEvent -> cardLayout.show(pnlConfigsCard, "1"));
    }
}

package br.com.armange;

import br.com.armange.jopenvpn.be.exception.InternalErrorException;
import br.com.armange.jopenvpn.ui.UiUtils;
import br.com.armange.jopenvpn.ui.main.*;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class App
{
    public static void main( String[] args )
    {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (final ClassNotFoundException
                       | InstantiationException
                       | IllegalAccessException
                       | UnsupportedLookAndFeelException e) {
            throw new InternalErrorException(e);
        }

        final StartStopFrame startStopFrame = new StartStopFrame();
        final SessionsFrame sessionsFrame = new SessionsFrame(startStopFrame);
        final ConfigsFrame configsFrame = new ConfigsFrame();
        final NewConfigFrame newConfigFrame = new NewConfigFrame(configsFrame);
        final ConfigsCardFrame configsCardFrame = new ConfigsCardFrame(configsFrame, newConfigFrame);
        final MainFrame mainFrame = new MainFrame(sessionsFrame, startStopFrame, configsCardFrame);
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        final URL image32 = contextClassLoader.getResource("vpn-32.png");

        UiUtils.centerComponent(mainFrame);

        mainFrame.setTitle("OpenVpn3");
        mainFrame.setIconImage(toolkit.getImage(image32));
        mainFrame.setResizable(false);
        mainFrame.setVisible(true);
    }
}

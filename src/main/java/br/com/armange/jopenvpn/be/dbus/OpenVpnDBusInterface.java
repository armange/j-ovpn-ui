package br.com.armange.jopenvpn.be.dbus;

import org.freedesktop.dbus.annotations.DBusInterfaceName;
import org.freedesktop.dbus.interfaces.DBusInterface;

@DBusInterfaceName("net.openvpn.v3.configuration")
public interface OpenVpnDBusInterface extends DBusInterface {

    /**
     * The Fetch method(capitalized) name is defined by OpenVpn CLI.(Third party).
     * @return The OVPN(file) configuration.
     */
    String Fetch();
}

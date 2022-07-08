package br.com.armange.jopenvpn.ui.main.model.combobox;

import br.com.armange.jopenvpn.be.openvpn.dto.OpenVpnConfigDto;
import br.com.armange.jopenvpn.be.openvpn.dto.StatusDto;
import lombok.Getter;

public class ConfigDtoComboItem {

    @Getter
    private final OpenVpnConfigDto openVpnConfigDto;

    public ConfigDtoComboItem(final OpenVpnConfigDto openVpnConfigDto) {
        this.openVpnConfigDto = openVpnConfigDto;
    }

    @Override
    public String toString() {
        return openVpnConfigDto.getStatus() != StatusDto.UNKNOWN
                ? openVpnConfigDto.getConfigName() + "  ("
                + openVpnConfigDto.getStatus().name() + ")"
                : openVpnConfigDto.getConfigName();
    }

    public String getPath() {
        return openVpnConfigDto.getPath();
    }

    public String getConfigName() {
        return openVpnConfigDto.getConfigName();
    }

    public StatusDto getStatus() {
        return openVpnConfigDto.getStatus();
    }
}

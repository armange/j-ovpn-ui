package br.com.armange.jopenvpn.ui.main.model.combobox;

import br.com.armange.jopenvpn.be.openvpn.dto.OpenVpnConfigDto;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

public class OpenVpnConfigDtoModel extends DefaultComboBoxModel<ConfigDtoComboItem> {

    public OpenVpnConfigDtoModel(final List<OpenVpnConfigDto> configs) {
        super(configs.stream()
                .map(ConfigDtoComboItem::new)
                .collect(Collectors.toList())
                .toArray(new ConfigDtoComboItem[]{}));
    }

    @Override
    public ConfigDtoComboItem getSelectedItem() {
        return (ConfigDtoComboItem) super.getSelectedItem();
    }
}

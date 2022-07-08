package br.com.armange.jopenvpn.be.openvpn.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;

@Getter
@Builder
@RequiredArgsConstructor
@EqualsAndHashCode
public class OpenVpnConfigDto implements Comparable<OpenVpnConfigDto> {

    private final String path;
    private final String configName;
    private final StatusDto status;

    @Override
    public int compareTo(final OpenVpnConfigDto openVpnConfigDto) {
        if (getStatus().getOrder() > openVpnConfigDto.getStatus().getOrder()) {
            return 1;
        } else if (getStatus().getOrder() < openVpnConfigDto.getStatus().getOrder()) {
            return -1;
        }

        return getConfigName().compareTo(openVpnConfigDto.getConfigName());
    }
}

package br.com.armange.jopenvpn.be.openvpn.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class OpenVpnSessionDto {

    private final String path;
    private final String configName;
    private final StatusDto status;
}

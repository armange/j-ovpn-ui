package br.com.armange.openvpn.v18b.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OvpnSessionDto extends AbstractCliResult {

    private final String path;
    private final String configName;
    private final String sessionName;
    private final String status;
    private final String cliOutput;
}

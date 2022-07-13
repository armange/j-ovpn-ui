package br.com.armange.openvpn.v18b.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OvpnSessionsListDto extends AbstractCliResult {

    private final List<OvpnSessionDto> sessions;
    private final String cliOutput;
}

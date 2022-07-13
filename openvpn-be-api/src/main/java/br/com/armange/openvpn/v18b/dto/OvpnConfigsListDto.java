package br.com.armange.openvpn.v18b.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OvpnConfigsListDto extends AbstractCliResult {

    private final List<OvpnConfigDto> configs;
    private final String cliOutput;
}

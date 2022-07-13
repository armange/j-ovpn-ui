package br.com.armange.openvpn.v18b.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OvpnCredentialDto extends AbstractCliResult {

    private final String username;
    private final String password;
}

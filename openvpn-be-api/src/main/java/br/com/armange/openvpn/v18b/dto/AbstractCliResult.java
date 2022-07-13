package br.com.armange.openvpn.v18b.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public abstract class AbstractCliResult {

    public final String cliResult;
}

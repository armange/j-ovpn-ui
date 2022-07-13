package br.com.armange.openvpn.v18b.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BooleanResultDto extends AbstractCliResult {
    private final boolean result;
}

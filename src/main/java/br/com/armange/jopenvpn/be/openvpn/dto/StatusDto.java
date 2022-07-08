package br.com.armange.jopenvpn.be.openvpn.dto;

import lombok.Getter;
import lombok.NonNull;

import java.util.stream.Stream;

public enum StatusDto {

    CLIENT_CONNECTED(0, "Connection, Client connected"),
    CLIENT_CONNECTING(1, "Connection, Client connecting"),
    CLIENT_RECONNECT(2, "Connection, Client reconnect"),
    UNKNOWN(3, "unknown")
    ;

    @Getter
    private final int order;

    @Getter
    private final String status;

    StatusDto(final int order,
              final String status) {
        this.order = order;
        this.status = status;
    }

    public static StatusDto valueOfStatus(@NonNull final String status) {
        return Stream.of(StatusDto.values())
                .filter(statusDto -> status.equals(statusDto.getStatus()))
                .findFirst()
                .orElse(UNKNOWN);
    }
}

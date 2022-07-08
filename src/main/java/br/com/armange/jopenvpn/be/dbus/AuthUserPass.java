package br.com.armange.jopenvpn.be.dbus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class AuthUserPass {

    private final String username;
    private final String password;
}

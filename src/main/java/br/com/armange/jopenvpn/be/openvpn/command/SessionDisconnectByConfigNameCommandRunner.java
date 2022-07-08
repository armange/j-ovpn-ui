package br.com.armange.jopenvpn.be.openvpn.command;

import br.com.armange.jopenvpn.be.CommandResult;
import br.com.armange.jopenvpn.be.CommandRunner;
import br.com.armange.jopenvpn.be.PreparedCommandRunner;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
class SessionDisconnectByConfigNameCommandRunner implements PreparedCommandRunner {

    private final CommandRunner commandRunner;
    private final String configName;

    @Override
    public CommandResult run(final String... outputLines) {
        return commandRunner.run(Stream.of(outputLines)
                        .collect(Collectors.toList()),
                "openvpn3", "session-manage", "--config", configName, "--disconnect");
    }
}

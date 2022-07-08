package br.com.armange.jopenvpn.be.openvpn.command;

import br.com.armange.jopenvpn.be.CommandResult;
import br.com.armange.jopenvpn.be.CommandRunner;
import br.com.armange.jopenvpn.be.PreparedCommandRunner;
import br.com.armange.jopenvpn.be.openvpn.result.ConfigsListCommandResult;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
class ConfigsRemoveCommandRunner implements PreparedCommandRunner {

    private final CommandRunner commandRunner;
    private final String path;

    @Override
    public CommandResult run(final String... outputLines) {
        final CommandResult commandResult = commandRunner.run(
                Stream.of(outputLines).collect(Collectors.toList()),
                "openvpn3", "config-remove", "--path", path);

        return new ConfigsListCommandResult(commandResult);
    }
}

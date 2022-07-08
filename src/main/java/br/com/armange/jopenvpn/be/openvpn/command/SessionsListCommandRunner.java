package br.com.armange.jopenvpn.be.openvpn.command;

import br.com.armange.jopenvpn.be.CommandRunner;
import br.com.armange.jopenvpn.be.PreparedCommandRunner;
import br.com.armange.jopenvpn.be.openvpn.result.SessionsListCommandResult;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
class SessionsListCommandRunner implements PreparedCommandRunner {

    private final CommandRunner commandRunner;

    @Override
    public SessionsListCommandResult run(final String... outputLines) {
        final List<String> outputLinesList = Stream.of(outputLines).collect(Collectors.toList());

        return new SessionsListCommandResult(
                commandRunner.run(outputLinesList,"openvpn3", "sessions-list"));
    }
}

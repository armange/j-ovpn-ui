package br.com.armange.jopenvpn.be.openvpn.command;

import br.com.armange.jopenvpn.be.CommandResult;
import br.com.armange.jopenvpn.be.CommandRunner;
import br.com.armange.jopenvpn.be.PreparedCommandRunner;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class ConfigImportCommandRunner implements PreparedCommandRunner {

    private final CommandRunner commandRunner;
    private final String filePath;
    private final String configName;

    @Override
    public CommandResult run(final String... outputLines) {
        return commandRunner.run(
                "openvpn3",
                "config-import",
                "--config",
                filePath,
                "--name",
                configName,
                "--persistent");
    }
}

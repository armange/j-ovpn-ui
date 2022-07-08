package br.com.armange.jopenvpn.be;

import java.util.List;

public interface CommandRunner {

    CommandResult run(String... command);

    CommandResult run(List<String> outputLines, String... command);
}

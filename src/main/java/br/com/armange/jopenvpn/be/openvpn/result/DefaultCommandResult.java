package br.com.armange.jopenvpn.be.openvpn.result;

import br.com.armange.jopenvpn.be.CommandResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@RequiredArgsConstructor
public class DefaultCommandResult implements CommandResult {

    private static final String NEW_LINE = "\n";

    @Getter
    private final int resultCode;

    @Getter
    private final List<String> resultLines;

    @Getter @Setter
    private String additionalMessage = "";

    @Override
    public String getResultMessage() {
        final String message = String.join(NEW_LINE, resultLines);

        return !message.isBlank()
                ? message.concat(NEW_LINE).concat(additionalMessage)
                : additionalMessage;
    }

    @Override
    public void appendAdditionalMessage(final String message) {
        if (additionalMessage != null
                && !additionalMessage.isBlank()) {
            additionalMessage = additionalMessage.concat(NEW_LINE).concat(message);
        } else {
            additionalMessage = message;
        }
    }
}

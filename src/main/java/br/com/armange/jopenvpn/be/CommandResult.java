package br.com.armange.jopenvpn.be;

import java.io.Serializable;
import java.util.List;

public interface CommandResult extends Serializable {

    int getResultCode();

    String getResultMessage();

    List<String> getResultLines();

    String getAdditionalMessage();

    void setAdditionalMessage(String message);

    void appendAdditionalMessage(String message);
}

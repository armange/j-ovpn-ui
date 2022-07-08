package br.com.armange.jopenvpn.ui;

import java.io.Serializable;

public abstract class AbstractUIFrameIJ implements Serializable  {

    protected AbstractUIFrameIJ() {

    }

    protected abstract void setupUI();

    protected abstract void addListeners();
}

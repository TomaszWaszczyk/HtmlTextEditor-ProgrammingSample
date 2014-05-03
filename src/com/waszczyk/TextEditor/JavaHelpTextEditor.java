package com.waszczyk.TextEditor;

import javax.swing.JFrame;

import com.waszczyk.TextEditor.ui.*;

public class JavaHelpTextEditor extends JFrame {

    private static final long    serialVersionUID    = 1L;

    public JavaHelpTextEditor() {
        super("Java Hilfetext Editor");
        setLocationByPlatform(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createUiElements();
        pack();
    }

    private void createUiElements() {
        final MainPanel panel = new MainPanel();
        add(panel);
    }
}

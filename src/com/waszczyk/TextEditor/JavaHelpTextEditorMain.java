package com.waszczyk.TextEditor;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class JavaHelpTextEditorMain {

	public static void main(String[] args) {
		setLookAndFeel();

		final JFrame frame = new JavaHelpTextEditor();
		frame.setVisible(true);
	}

	private static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

}

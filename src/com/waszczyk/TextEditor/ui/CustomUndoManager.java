package com.waszczyk.TextEditor.ui;

import javax.swing.UIManager;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

public class CustomUndoManager extends UndoManager {

	private static final long	serialVersionUID	= 1L;

	private static final String	STYLE_CHANGE_NAME	= UIManager.getString("AbstractDocument.styleChangeText");

	private UndoableEdit		combinedUndoableEdit;

	private UndoableEdit		lastEdit;

	public CustomUndoManager() {
		super();
		// // calling setLimit method from UndoManager not from the
		// // CustomUndoManager, because setLimit is not overridden by
		// // CustomUndoManager ! ! !
		// setLimit(1000);
	}

	@Override
	public synchronized boolean addEdit(final UndoableEdit anEdit) {

		if (STYLE_CHANGE_NAME.equals(anEdit.getPresentationName())) {
			if (combinedUndoableEdit == null) {

				combinedUndoableEdit = new CompoundEdit();
				combinedUndoableEdit.addEdit(lastEdit);

				lastEdit = null;

				combinedUndoableEdit.addEdit(anEdit);
				final boolean returnValue = super.addEdit(combinedUndoableEdit);
				return returnValue;
			}

			return combinedUndoableEdit.addEdit(anEdit);
		}

		closeCombinedUndoableEdit();

		if (lastEdit != null) {
			super.addEdit(lastEdit);
		}

		lastEdit = anEdit;

		return true;
	}

	private void closeCombinedUndoableEdit() {
		if (combinedUndoableEdit != null) {
			((CompoundEdit) combinedUndoableEdit).end();
			combinedUndoableEdit = null;
		}
	}

	public void commitLastEdit() {
		if (lastEdit != null) {
			addEdit(lastEdit);
			lastEdit = null;
		}
	}

	@Override
	public synchronized void undo() throws CannotUndoException {
		closeCombinedUndoableEdit();
		commitLastEdit();

		super.undo();
	}

	@Override
	public synchronized boolean canUndo() {
		closeCombinedUndoableEdit();
		commitLastEdit();

		return super.canUndo();
	}

	@Override
	public synchronized void redo() throws CannotRedoException {
		closeCombinedUndoableEdit();
		commitLastEdit();
		super.redo();
	}

	@Override
	public synchronized boolean canRedo() {
		closeCombinedUndoableEdit();
		commitLastEdit();
		return super.canRedo();
	}

}

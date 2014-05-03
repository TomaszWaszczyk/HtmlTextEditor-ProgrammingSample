package com.waszczyk.TextEditor.ui;

// TODO upgrade redo/undo
// TODO del?    /**
//               * 
//               */
// TODO MainPanel.this ? ? ? 
// FIXME aditional parameter with color to insterTag method

//TODO branch, mongodb
//TODO del?    /**
//             *
//            */
//TODO MainPanel.this ? ? ?
//FIXME aditional parameter with color to insterTag method

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.border.CompoundBorder;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

public class MainPanel extends JPanel implements ClipboardOwner {

     private class ShowButtonAction extends AbstractAction {

             private static final long       serialVersionUID        = 1L;

             public ShowButtonAction() {
                     super();
                     putValue(NAME, "Text anzeigen");
             }

             @Override
             public void actionPerformed(ActionEvent e) {
                     final String text = helpTextPane.getText();
                     displayLabel.setText(text);
             }
     }

     private class UnDoAction extends AbstractAction {

             private static final long       serialVersionUID        = 1L;

             public UnDoAction() {
                     super();
                     putValue(NAME, "Rückgängig");
             }

             @Override
             public void actionPerformed(ActionEvent e) {
                     try {
                             if (um.canUndo()) {
                                     um.undo();
                             }
                     } catch (CannotUndoException evt) {
                             evt.printStackTrace();
                     }
             }
     }

     private class ReDoAction extends AbstractAction {

             private static final long       serialVersionUID        = 1L;

             public ReDoAction() {
                     super();
                     putValue(NAME, "Wiederherstellen");
             }

             @Override
             public void actionPerformed(ActionEvent e) {
                     try {
                             if (um.canRedo()) {
                                     um.redo();
                             }
                     } catch (CannotRedoException evt) {
                             evt.printStackTrace();
                     }
             }
     }

     private class HtmlAction extends AbstractAction {

             private static final long       serialVersionUID        = 1L;

             public HtmlAction() {
                     super();
                     putValue(NAME, "<html>&lt;html&gt; ... &lt;/html&gt;</html>");
             }

             @Override
             public void actionPerformed(ActionEvent e) {

                     insertTag(HtmlTag.HTML.getStartTag(), HtmlTag.HTML.getEndTag(), 7);
             }
     }

     private class BrAction extends AbstractAction {

             private static final long       serialVersionUID        = 1L;

             public BrAction() {
                     super();
                     putValue(NAME, "<br>");
             }

             @Override
             public void actionPerformed(ActionEvent e) {

                     insertTag(HtmlTag.BR.getStartTag(), HtmlTag.BR.getEndTag(), 0);
             }
     }

     private class BAction extends AbstractAction {

             private static final long       serialVersionUID        = 1L;

             public BAction() {
                     super();
                     putValue(NAME, "<b> ... </b>");
             }

             @Override
             public void actionPerformed(ActionEvent e) {

                     insertTag(HtmlTag.BOLD.getStartTag(), HtmlTag.BOLD.getEndTag(), 4);
             }
     }

     private class IAction extends AbstractAction {

             private static final long       serialVersionUID        = 1L;

             public IAction() {
                     super();
                     putValue(NAME, "<i> ... </i>");
             }

             @Override
             public void actionPerformed(ActionEvent e) {

                     insertTag(HtmlTag.ITALIC.getStartTag(), HtmlTag.ITALIC.getEndTag(), 4);
             }
     }

     private class UAction extends AbstractAction {

             private static final long       serialVersionUID        = 1L;

             public UAction() {
                     super();
                     putValue(NAME, "<u> ... </u>");
             }

             @Override
             public void actionPerformed(ActionEvent e) {

                     insertTag(HtmlTag.UNDERLINED.getStartTag(), HtmlTag.UNDERLINED.getEndTag(), 4);
             }
     }

     private class SAction extends AbstractAction {

             private static final long       serialVersionUID        = 1L;

             public SAction() {
                     super();
                     putValue(NAME, "<s> ... </s>");
             }

             @Override
             public void actionPerformed(ActionEvent e) {

                     insertTag(HtmlTag.CROSSEDOUT.getStartTag(), HtmlTag.CROSSEDOUT.getEndTag(), 4);
             }
     }

     private class TTAction extends AbstractAction {

             private static final long       serialVersionUID        = 1L;

             public TTAction() {
                     super();
                     putValue(NAME, "<tt> ... </tt>");
             }

             @Override
             public void actionPerformed(ActionEvent e) {

                     insertTag(HtmlTag.CODEFONT.getStartTag(), HtmlTag.CODEFONT.getEndTag(), 4);
             }
     }

     private class BigAction extends AbstractAction {

             private static final long       serialVersionUID        = 1L;

             public BigAction() {
                     super();
                     putValue(NAME, "<big> ... </big>");
             }

             @Override
             public void actionPerformed(ActionEvent e) {

                     insertTag(HtmlTag.BIGER.getStartTag(), HtmlTag.BIGER.getEndTag(), 4);
             }
     }

     private class SmallAction extends AbstractAction {

             private static final long       serialVersionUID        = 1L;

             public SmallAction() {
                     super();
                     putValue(NAME, "<small> ... </small>");
             }

             @Override
             public void actionPerformed(ActionEvent e) {

                     insertTag(HtmlTag.SMALLER.getStartTag(), HtmlTag.SMALLER.getEndTag(), 4);
             }
     }

     private class SupAction extends AbstractAction {

             private static final long       serialVersionUID        = 1L;

             public SupAction() {
                     super();
                     putValue(NAME, "<sup> ... </sup>");
             }

             @Override
             public void actionPerformed(ActionEvent e) {

                     insertTag(HtmlTag.SUPERSCRIPT.getStartTag(), HtmlTag.SUPERSCRIPT.getEndTag(), 4);
             }
     }

     private class SubAction extends AbstractAction {

             private static final long       serialVersionUID        = 1L;

             public SubAction() {
                     super();
                     putValue(NAME, "<sub> ... </sub>");
             }

             @Override
             public void actionPerformed(ActionEvent e) {

                     insertTag(HtmlTag.SUBSCRIPT.getStartTag(), HtmlTag.SUBSCRIPT.getEndTag(), 4);
             }
     }

     private class H1Action extends AbstractAction {

             private static final long       serialVersionUID        = 1L;

             public H1Action() {
                     super();
                     putValue(NAME, "<h1> ... </h1>");
             }

             @Override
             public void actionPerformed(ActionEvent e) {

                     insertTag(HtmlTag.H1.getStartTag(), HtmlTag.H1.getEndTag(), 4);
             }
     }

     private class H2Action extends AbstractAction {

             private static final long       serialVersionUID        = 1L;

             public H2Action() {
                     super();
                     putValue(NAME, "<h2> ... </h2>");
             }

             @Override
             public void actionPerformed(ActionEvent e) {

                     insertTag(HtmlTag.H2.getStartTag(), HtmlTag.H2.getEndTag(), 4);
             }
     }

     private class H3Action extends AbstractAction {

             private static final long       serialVersionUID        = 1L;

             public H3Action() {
                     super();
                     putValue(NAME, "<h3> ... </h3>");
             }

             @Override
             public void actionPerformed(ActionEvent e) {

                     insertTag(HtmlTag.H3.getStartTag(), HtmlTag.H3.getEndTag(), 4);
             }
     }

     private class H4Action extends AbstractAction {

             private static final long       serialVersionUID        = 1L;

             public H4Action() {
                     super();
                     putValue(NAME, "<h4> ... </h4>");
             }

             @Override
             public void actionPerformed(ActionEvent e) {

                     insertTag(HtmlTag.H4.getStartTag(), HtmlTag.H4.getEndTag(), 4);
             }
     }

     private class H5Action extends AbstractAction {

             private static final long       serialVersionUID        = 1L;

             public H5Action() {
                     super();
                     putValue(NAME, "<h5> ... </h5>");
             }

             @Override
             public void actionPerformed(ActionEvent e) {

                     insertTag(HtmlTag.H5.getStartTag(), HtmlTag.H5.getEndTag(), 4);
             }
     }

     private class H6Action extends AbstractAction {

             private static final long       serialVersionUID        = 1L;

             public H6Action() {
                     super();
                     putValue(NAME, "<h6> ... </h6>");
             }

             @Override
             public void actionPerformed(ActionEvent e) {

                     insertTag(HtmlTag.H6.getStartTag(), HtmlTag.H6.getEndTag(), 4);
             }
     }

     private class PAction extends AbstractAction {

             private static final long       serialVersionUID        = 1L;

             public PAction() {
                     super();
                     putValue(NAME, "<p> ... </p>");
             }

             @Override
             public void actionPerformed(ActionEvent e) {

                     insertTag(HtmlTag.P.getStartTag(), HtmlTag.P.getEndTag(), 4);
             }
     }

     private class CopyAction extends AbstractAction {

             private static final long       serialVersionUID        = 1L;

             public CopyAction() {
                     super();
                     putValue(NAME, "Kopieren");
             }

             @Override
             public void actionPerformed(ActionEvent e) {
                     String text = displayLabel.getText();
                     text = HtmlTag.replaceForExtraction(text);
                     final StringSelection stringSelection = new StringSelection(text);
                     final Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                     cb.setContents(stringSelection, MainPanel.this);
             }

     }

     private static final long               serialVersionUID        = 1L;

     private final JTextPane                 helpTextPane;

     private final JLabel                    displayLabel;

     private final CustomUndoManager um;

     public MainPanel() {
             super(new BorderLayout(20, 20));
             final Action showAction = new ShowButtonAction();
             final Action undoAction = new UnDoAction();
             final Action redoAction = new ReDoAction();
             final Action brAction = new BrAction();
             final Action htmlAction = new HtmlAction();
             final Action bAction = new BAction();
             final Action iAction = new IAction();
             final Action uAction = new UAction();
             final Action sAction = new SAction();
             final Action ttAction = new TTAction();
             final Action bigAction = new BigAction();
             final Action smallAction = new SmallAction();
             final Action supAction = new SupAction();
             final Action subAction = new SubAction();
             final Action h1Action = new H1Action();
             final Action h2Action = new H2Action();
             final Action h3Action = new H3Action();
             final Action h4Action = new H4Action();
             final Action h5Action = new H5Action();
             final Action h6Action = new H6Action();
             final Action pAction = new PAction();

             JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
             toolBar.setMaximumSize(new Dimension(100, 30));

             um = new CustomUndoManager();

             helpTextPane = createTextPane(redoAction, undoAction);

             displayLabel = createLabel();

             JButton html = new JButton("<html>&lt;html&gt; ... &lt;/html&gt;</html>");
             html.addActionListener(htmlAction);
             Dimension d = html.getPreferredSize();
             html.setMinimumSize(d);
             html.setMaximumSize(d);

             final JPanel textPanel = createTextPanel();
             final JPanel labelPanel = createLabelPanel();

             toolBar.add(showAction);
             toolBar.addSeparator();
             toolBar.add(redoAction);
             toolBar.add(undoAction);
             toolBar.addSeparator();
             // toolBar.add(htmlAction);
             toolBar.add(html);
             toolBar.addSeparator();
             toolBar.add(brAction);
             toolBar.addSeparator();
             toolBar.add(bAction);
             toolBar.add(iAction);
             toolBar.add(uAction);
             toolBar.add(sAction);
             toolBar.add(ttAction);
             toolBar.addSeparator();
             toolBar.add(bigAction);
             toolBar.add(smallAction);
             toolBar.addSeparator();
             toolBar.add(supAction);
             toolBar.add(subAction);
             toolBar.addSeparator();
             toolBar.add(h1Action);
             toolBar.add(h2Action);
             toolBar.add(h3Action);
             toolBar.add(h4Action);
             toolBar.add(h5Action);
             toolBar.add(h6Action);
             toolBar.addSeparator();
             toolBar.add(pAction);

             JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, textPanel, labelPanel);
             add(splitPane, BorderLayout.CENTER);
             add(toolBar, BorderLayout.WEST);
     }

     private void insertTag(final String startTag, final String endTag, final int movement) {
             try {

                     int selectionStart;
                     int selectionEnd;

                     if (startTag.equals("<html>")) {
                             selectionStart = 0;
                             selectionEnd = helpTextPane.getDocument().getLength();
                     } else {
                             selectionStart = helpTextPane.getSelectionStart();
                             selectionEnd = helpTextPane.getSelectionEnd();
                     }

                     // no selected text
                     if (selectionStart == selectionEnd && endTag != null) {
                             helpTextPane.getStyledDocument().insertString(selectionStart, startTag + endTag, null);

                             // caret position has changed because of insertion therefore
                             final int caretPos = getCaretPosition();

                             // place caret in between both tags
                             helpTextPane.setCaretPosition(caretPos - endTag.length());

                     } else {
                             // selected text

                             // insert begin-tag at selection start
                             helpTextPane.getStyledDocument().insertString(selectionStart, startTag, null);

                             // correct selection end index
                             selectionEnd += startTag.length();

                             // insert end-tag at selection end
                             helpTextPane.getStyledDocument().insertString(selectionEnd, endTag, null);

                             // correct marking
                             helpTextPane.setSelectionEnd(selectionEnd);
                     }

             } catch (Exception ex) {
                     // nothing else
                     ex.printStackTrace();

             } finally {
                     helpTextPane.requestFocusInWindow();
             }
     }

     private JPanel createLabelPanel() {

             final MouseAdapter ma = new MouseAdapter() {

                     @Override
                     public void mousePressed(MouseEvent e) {
                             if (e.isPopupTrigger()) {
                                     onPopupTrigger(e);
                             }
                     }

                     @Override
                     public void mouseReleased(MouseEvent e) {
                             if (e.isPopupTrigger()) {
                                     onPopupTrigger(e);
                             }
                     }

             };

             final JToolTip tt = new JToolTip();
             final CompoundBorder cb = new CompoundBorder(tt.getBorder(), BorderFactory.createEmptyBorder(2, 2, 2, 2));
             final JPanel innerPanel = new JPanel();

             innerPanel.setBorder(cb);
             innerPanel.setBackground(tt.getBackground());
             innerPanel.add(displayLabel);

             innerPanel.addMouseListener(ma);
             displayLabel.addMouseListener(ma);

             final JScrollPane tooltipscrool = new JScrollPane(innerPanel);
             tooltipscrool.setPreferredSize(new Dimension(100, 120));

             final JPanel outerPanel = new JPanel(new BorderLayout());
             outerPanel.add(tooltipscrool);
             outerPanel.setMinimumSize(new Dimension(200, 200));
             return outerPanel;
     }

     protected void onPopupTrigger(MouseEvent e) {
             final JPopupMenu popup = new JPopupMenu();
             popup.add(new CopyAction());

             popup.show(e.getComponent(), e.getX(), e.getY());
     }

     private JLabel createLabel() {
             final JLabel label = new JLabel();
             return label;
     }

     private JPanel createTextPanel() {

             // wrap the text area in a scroll pane
             final JScrollPane textScrollPane = new JScrollPane(helpTextPane);
             textScrollPane.setPreferredSize(new Dimension(700, 500));

             // assemble the text panel
             final JPanel textPanel = new JPanel();
             textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
             textPanel.add(textScrollPane);

             return textPanel;
     }

     private int getCaretPosition() {

             return helpTextPane.getCaretPosition();
     }

     private JTextPane createTextPane(Action redoAction, Action undoAction) {

             final JTextPane textPane = new JTextPane();
             textPane.setEditorKit(new CustomStyledEditorKit());
             Document doc = textPane.getDocument();
             doc.addUndoableEditListener(um);
             textPane.getActionMap().put("Undo", undoAction);
             textPane.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
             textPane.getActionMap().put("Redo", redoAction);
             textPane.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");

             return textPane;
     }

     @Override
     public void lostOwnership(Clipboard clipboard, Transferable contents) {
             // nothing
     }
}

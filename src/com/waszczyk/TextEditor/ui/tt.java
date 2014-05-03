/*package de.volkswagen.javahelptexteditor.ui;

import java.awt.Color;

public enum HtmlTag {

    HTML("<html>", "</html>", "", "", Color.ORANGE),

    BR("<br>", "", "\n", "", new Color(120, 0, 40)),

    BOLD("<b>", "</b>", "", "", new Color(120, 0, 40)),

    ITALIC("<i>", "</i>", "", "", new Color(120, 0, 40)),

    UNDERLINED("<u>", "</u>", "", "", new Color(120, 0, 40)),

    CROSSEDOUT("<s>", "</s>", "", "", new Color(120, 0, 40)),

    CODEFONT("<tt>", "</tt>", "", "", new Color(120, 0, 40)),

    BIGER("<big>", "</big>", "", "", new Color(120, 0, 40)),

    SMALLER("<small>", "</small>", "", "", new Color(120, 0, 40)),

    SUPERSCRIPT("<sup>", "</sup>", "", "", new Color(120, 0, 40)),

    SUBSCRIPT("<sub>", "</sub>", "", "", new Color(120, 0, 40)),

    H1("<h1>", "</h1>", "\n", "\n", new Color(120, 0, 40)),

    H2("<h2>", "</h2>", "\n", "\n", new Color(120, 0, 40)),

    H3("<h3>", "</h3>", "\n", "\n", new Color(120, 0, 40)),

    H4("<h4>", "</h4>", "\n", "\n", new Color(120, 0, 40)),

    H5("<h5>", "</h5>", "\n", "\n", new Color(120, 0, 40)),

    H6("<h6>", "</h6>", "\n", "\n", new Color(120, 0, 40)),

    P("<p>", "</p>", "\n", "\n", new Color(120, 0, 40)),

    OL("<ol>", "</ol>", "\n", "\n", new Color(120, 0, 40)),

    UL("<ul>", "</ul>", "\n", "\n", new Color(120, 0, 40)),

    LI("<li>", "</li>", "", "\n", new Color(120, 0, 40));

    private static interface ITagReplacementCallback {
        void replace(StringBuilder replaceText, int startIndex);
    }

    private static class OlLiReplacementCallback implements ITagReplacementCallback {
        private int    liCount;

        public OlLiReplacementCallback() {
            super();
            liCount = 0;
        }

        @Override
        public void replace(final StringBuilder replaceText, final int startIndex) {
            final int liStartEndIndex = startIndex + LI.startTag.length();
            liCount++;
            replaceText.replace(startIndex, liStartEndIndex, Integer.toString(liCount) + ". ");
        }

    }

    private static class UlLiReplacementCallback implements ITagReplacementCallback {

        @Override
        public void replace(StringBuilder replaceText, int startIndex) {
            final int liStartEndIndex = startIndex + LI.startTag.length();
            replaceText.replace(startIndex, liStartEndIndex, "• ");
        }

    }

    private final String    startTag;
    private final String    endTag;

    private final String    startTagReplacement;
    private final String    endTagReplacement;

    private final Color     color;
    
    private HtmlTag(String startTag, String endTag, String startTagReplacement, String endTagReplacement, Color color) {
        this.startTag = startTag;
        this.endTag = endTag;

        this.startTagReplacement = startTagReplacement;
        this.endTagReplacement = endTagReplacement;
        
        this.color = color;
    }

    public boolean isThisSpecificTag(String foundTag) {
        return startTag.equals(foundTag) || endTag.equals(foundTag);
    }

    public static HtmlTag getHtmlTag(final String foundTag) {
        // final HtmlTag[] tags = HtmlTag.values();

        for (final HtmlTag tag : HtmlTag.values()) {
            if (tag.isThisSpecificTag(foundTag)) {
                return tag;
            }
        }

        return null;
    }

    private static void replaceAllLists(StringBuilder replacementText, int start) {

        // find the first occurrence of start tag in interval [start, end)
        int firstOlStart = replacementText.indexOf(OL.startTag, start);
        int firstUlStart = replacementText.indexOf(UL.startTag, start);

        // exit when no start tag is found
        if (firstOlStart < 0 && firstUlStart < 0) {
            return;
        }

        // find the NEXT occurrence of the end tag from the position of the
        // first found start tag
        int firstOlEnd = replacementText.indexOf(OL.endTag, start);
        int firstUlEnd = replacementText.indexOf(UL.endTag, start);

        if (firstOlEnd < 0 && firstUlEnd < 0) {
            return;
        }

        int listStart;

        if (firstOlStart >= 0 && firstUlStart >= 0) {
            listStart = Math.min(firstOlStart, firstUlStart);
        } else if (firstOlStart >= 0) {
            listStart = firstOlStart;
        } else {
            listStart = firstUlStart;
        }

        HtmlTag listTag = listStart == firstOlStart ? HtmlTag.OL : HtmlTag.UL;

        while (listStart >= 0) {
            replaceComplexList(replacementText, listStart, listTag);

            firstOlStart = replacementText.indexOf(OL.startTag, start);
            firstUlStart = replacementText.indexOf(UL.startTag, start);

            if (firstOlStart >= 0 && firstUlStart >= 0) {
                listStart = Math.min(firstOlStart, firstUlStart);
            } else if (firstOlStart >= 0) {
                listStart = firstOlStart;
            } else {
                listStart = firstUlStart;
            }

            listTag = listStart == firstOlStart ? HtmlTag.OL : HtmlTag.UL;
        }
    }

    private static void replaceComplexList(StringBuilder replacementText, final int start, final HtmlTag listTag) {

        // find the NEXT occurrence of the end tag from the position of the
        // first found start tag
        int firstOlEnd = replacementText.indexOf(listTag.endTag, start);

        int innerOlStart = replacementText.indexOf(OL.startTag, start + OL.startTag.length());
        int innerUlStart = replacementText.indexOf(UL.startTag, start + UL.startTag.length());
        int innerStartTagIndex = minStartIndex(innerOlStart, innerUlStart);

        while (innerStartTagIndex >= 0 && innerStartTagIndex < firstOlEnd) {
            replaceAllLists(replacementText, innerStartTagIndex);

            innerOlStart = replacementText.indexOf(OL.startTag, start + OL.startTag.length());
            innerUlStart = replacementText.indexOf(UL.startTag, start + UL.startTag.length());

            innerStartTagIndex = minStartIndex(innerOlStart, innerUlStart);

            firstOlEnd = replacementText.indexOf(listTag.endTag, start);
        }

        // at this point only the most outer list remains
        replaceSimpleList(replacementText, start, firstOlEnd, listTag);

    }

    private static int minStartIndex(final int innerOlStart, final int innerUlStart) {
        if (innerOlStart >= 0 && innerUlStart >= 0) {
            return Math.min(innerOlStart, innerUlStart);
        }

        return innerOlStart >= 0 ? innerOlStart : innerUlStart;
    }

    private static void replaceSimpleList(StringBuilder replacedText, int start, int end, HtmlTag listTag) {
        ITagReplacementCallback callBack;

        if (listTag == HtmlTag.OL) {
            callBack = new OlLiReplacementCallback();
        } else {
            callBack = new UlLiReplacementCallback();
        }

        int liStart = replacedText.indexOf(LI.startTag, start);

        // replacing li tags
        while (liStart >= 0 && liStart < end) {

            // the actual replacement
            callBack.replace(replacedText, liStart);

            // find the next liStart
            liStart = replacedText.indexOf(LI.startTag, liStart + LI.startTag.length());

            // adjust checkEnd because it has changed because of the
            // replacement
            end = replacedText.indexOf(listTag.endTag, start);

        }

        // replace all LI ends
        int liEnd = replacedText.indexOf(LI.endTag, start);

        while (liEnd >= 0 && liEnd < end) {
            // the actual replacement

            final int liEndIndex = liEnd + LI.endTag.length();
            replacedText.replace(liEnd, liEndIndex, "");

            // find the next liEnd
            liEnd = replacedText.indexOf(LI.endTag, liEndIndex);

            // adjust checkEnd because it has changed because of the replacement
            end = replacedText.indexOf(listTag.endTag, start);
        }

        // replace the OL start tag
        final int startIndex = start + listTag.startTag.length();
        replacedText.replace(start, startIndex, "");

        // replace the OL end tag
        end = replacedText.indexOf(listTag.endTag, start);
        final int olEndIndex = end + listTag.endTag.length();
        replacedText.replace(end, olEndIndex, "");
    }

    public static String replaceForExtraction(final String text) {
        final StringBuilder replacedText = new StringBuilder(text);

        for (final HtmlTag tag : HtmlTag.values()) {
            tag.replaceHtmlWithExtractionString(replacedText);
        }

        return replacedText.toString();
    }

    private void replaceHtmlWithExtractionString(StringBuilder text) {
        switch (this) {
        case OL:
        case UL:
            replaceAllLists(text, 0);
            break;

        default:
            if (!startTag.isEmpty()) {
                replaceAll(text, startTag, startTagReplacement);
            }

            if (!endTag.isEmpty()) {
                replaceAll(text, endTag, endTagReplacement);
            }
            break;
        }
    }

    private static void replaceAll(final StringBuilder text, final String original, final String replacement) {
        int index = text.indexOf(original, 0);

        while (index >= 0) {
            text.replace(index, index + original.length(), replacement);

            index = text.indexOf(original, index);
        }
    }
    
    public Color getColor(){
        return color;
    }

}









package de.volkswagen.javahelptexteditor.ui;

// TODO upgrade redo/undo
// TODO del?    /**
//               * 
//               */
// TODO MainPanel.this ? ? ? 
// FIXME aditional parameter with color to insterTag method
/*
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
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

public class MainPanel extends JPanel implements ClipboardOwner {

    private class ShowButtonAction extends AbstractAction {

        private static final long    serialVersionUID    = 1L;

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

        private static final long    serialVersionUID    = 1L;

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

        private static final long    serialVersionUID    = 1L;

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

        private static final long    serialVersionUID    = 1L;

        public HtmlAction() {
            super();
            putValue(NAME, "<html>&lt;html&gt; ... &lt;/html&gt;</html>");
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            insertTag("<html>", "</html>", 7);
        }
    }

    private class BrAction extends AbstractAction {

        private static final long    serialVersionUID    = 1L;

        public BrAction() {
            super();
            putValue(NAME, "<br>");
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            insertTag("<br>", null, 0);
        }
    }

    private class BAction extends AbstractAction {

        private static final long    serialVersionUID    = 1L;

        public BAction() {
            super();
            putValue(NAME, "<b> ... </b>");
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            insertTag("<b>", "</b>", 4);
        }
    }

    private class IAction extends AbstractAction {

        private static final long    serialVersionUID    = 1L;

        public IAction() {
            super();
            putValue(NAME, "<i> ... </i>");
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            insertTag("<i>", "</i>", 4);
        }
    }

    private class UAction extends AbstractAction {

        private static final long    serialVersionUID    = 1L;

        public UAction() {
            super();
            putValue(NAME, "<u> ... </u>");
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            insertTag("<u>", "</u>", 4);
        }
    }

    private class SAction extends AbstractAction {

        private static final long    serialVersionUID    = 1L;

        public SAction() {
            super();
            putValue(NAME, "<s> ... </s>");
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            insertTag("<s>", "</s>", 4);
        }
    }
    
    private class TTAction extends AbstractAction {

        private static final long    serialVersionUID    = 1L;

        public TTAction() {
            super();
            putValue(NAME, "<tt> ... </tt>");
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            insertTag("<tt>", "</tt>", 4);
        }
    }
    
    private class BigAction extends AbstractAction {

        private static final long    serialVersionUID    = 1L;

        public BigAction() {
            super();
            putValue(NAME, "<big> ... </big>");
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            insertTag("<big>", "</big>", 4);
        }
    }
    
    private class SmallAction extends AbstractAction {

        private static final long    serialVersionUID    = 1L;

        public SmallAction() {
            super();
            putValue(NAME, "<small> ... </small>");
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            insertTag("<small>", "</small>", 4);
        }
    }
    
    private class SupAction extends AbstractAction {

        private static final long    serialVersionUID    = 1L;

        public SupAction() {
            super();
            putValue(NAME, "<sup> ... </sup>");
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            insertTag("<sup>", "</sup>", 4);
        }
    }
    
    private class SubAction extends AbstractAction {

        private static final long    serialVersionUID    = 1L;

        public SubAction() {
            super();
            putValue(NAME, "<sub> ... </sub>");
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            insertTag("<sub>", "</sub>", 4);
        }
    }
    
    private class H1Action extends AbstractAction {

        private static final long    serialVersionUID    = 1L;

        public H1Action() {
            super();
            putValue(NAME, "<h1> ... </h1>");
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            insertTag("<h1>", "</h1>", 4);
        }
    }
    
    private class H2Action extends AbstractAction {

        private static final long    serialVersionUID    = 1L;

        public H2Action() {
            super();
            putValue(NAME, "<h2> ... </h2>");
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            insertTag("<h2>", "</h2>", 4);
        }
    }
    
    private class H3Action extends AbstractAction {

        private static final long    serialVersionUID    = 1L;

        public H3Action() {
            super();
            putValue(NAME, "<h3> ... </h3>");
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            insertTag("<h3>", "</h3>", 4);
        }
    }
    
    private class H4Action extends AbstractAction {

        private static final long    serialVersionUID    = 1L;

        public H4Action() {
            super();
            putValue(NAME, "<h4> ... </h4>");
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            insertTag("<h4>", "</h4>", 4);
        }
    }
    
    private class H5Action extends AbstractAction {

        private static final long    serialVersionUID    = 1L;

        public H5Action() {
            super();
            putValue(NAME, "<h5> ... </h5>");
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            insertTag("<h5>", "</h5>", 4);
        }
    }
    
    private class H6Action extends AbstractAction {

        private static final long    serialVersionUID    = 1L;

        public H6Action() {
            super();
            putValue(NAME, "<h6> ... </h6>");
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            insertTag("<h6>", "</h6>", 4);
        }
    }
    
    private class PAction extends AbstractAction {

        private static final long    serialVersionUID    = 1L;

        public PAction() {
            super();
            putValue(NAME, "<p> ... </p>");
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            insertTag("<p>", "</p>", 4);
        }
    }
    
    private class CopyAction extends AbstractAction {

        private static final long    serialVersionUID    = 1L;

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

    private static final long        serialVersionUID    = 1L;

    private final JTextPane            helpTextPane;

    private final JLabel            displayLabel;

    private final CustomUndoManager    um;

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
//        toolBar.add(htmlAction);
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















package de.volkswagen.javahelptexteditor.ui;

import java.awt.Color;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledEditorKit;

public class CustomStyledEditorKit extends StyledEditorKit {

    private static class HtmlDocument extends DefaultStyledDocument {

        private static final long    serialVersionUID    = 1L;

        public HtmlDocument() {
            super();
        }

        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            super.insertString(offs, str, a);
            
            highlighter();
        }

        private void highlighter() throws BadLocationException {

            String doc = getText(0, getLength());
            SimpleAttributeSet sas = new SimpleAttributeSet();
            
            
            final StyleContext context = new StyleContext();
            final AttributeSet attrRED = context.addAttribute(context.getEmptySet(), StyleConstants.Foreground, Color.RED);
            final AttributeSet attrBLUE = context.addAttribute(context.getEmptySet(), StyleConstants.Foreground, Color.BLUE);
            
            DefaultStyledDocument dsd = new DefaultStyledDocument(){
                
                
                
            };
            
            StyleConstants.setBold(sas, true);
            
            int a = 0;
            int b = 0;

            // loop through the whole document
            while (a >= 0 && b >= 0) {

                // find the beginning of the tag
                a = doc.indexOf('<', a);

                // when there is a beginning ...
                if (a >= 0) {
                    // ... find the end of the tag
                    b = doc.indexOf('>', a);

                    // when there is also an end ...
                    if (b >= 0) {
                        // correct b, because otherwise the last sign ('>') will
                        // not be bold
                        b += 1;

                        // ... extract the tag
                        final String tag = doc.substring(a, b);
                        
                        // find out if tag is an HTML-Tag
                        HtmlTag foundTag = HtmlTag.getHtmlTag(tag);
                        if (foundTag != null) {
                            // retrieve the attribute set of the tag
                            final AttributeSet attributeSetInText = getCharacterElement(a + ((b - a) / 2)).getAttributes();
                            
                            // when it's not the same attribute set as "sas" is,
                            // then set "sas for the tag
                            if (!isSameAttributeSet(attributeSetInText, sas)) {
                                sas.addAttribute(StyleConstants.Foreground, foundTag.getColor());
                                setCharacterAttributes(a, b - a, sas, true);
                            }
                        }

                        a = b;
                    }
                }
            }
        }

        private boolean isSameAttributeSet(final AttributeSet a1, final AttributeSet a2) {
            final boolean bold1 = StyleConstants.isBold(a1);
            final boolean bold2 = StyleConstants.isBold(a2);

            return bold1 == bold2;
        }
    }

    private static final long    serialVersionUID    = 1L;

    public CustomStyledEditorKit() {

        super();
    }

    @Override
    public MutableAttributeSet getInputAttributes() {

        SimpleAttributeSet sas = new SimpleAttributeSet();
        return sas;
    }

    @Override
    public Document createDefaultDocument() {
        // return super.createDefaultDocument();
        return new HtmlDocument();
    }
}

















*/
package com.waszczyk.TextEditor.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
 
public class CustomStyledEditorKit extends StyledEditorKit {
 
        private static class HtmlDocument extends DefaultStyledDocument {
 
                private static final long       serialVersionUID        = 1L;
 
                public HtmlDocument() {
                        super();
                }
 
                @Override
                public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                        super.insertString(offs, str, a);
 
                        String text = getText(0, getLength());
                        int before = findLastNonWordChar(text, offs);
                        if (before < 0) {
                                before = 0;
                        }
                        int after = findFirstNonWordChar(text, offs + str.length());
 
                        highlighter(before, after, text);
                }
 
                public void remove(int offs, int len) throws BadLocationException {
                        super.remove(offs, len);
 
                        String text = getText(0, getLength());
                        int before = findLastNonWordChar(text, offs);
                        if (before < 0)
                                before = 0;
                        int after = findFirstNonWordChar(text, offs);
 
                        highlighter(before, after, text);
                }
 
                private void highlighter(int start, int end, String text) throws BadLocationException {
 
                        SimpleAttributeSet sas = new SimpleAttributeSet();
                        StyleConstants.setBold(sas, true);
 
                        int a = start;
                        int b = 0;
 
                        // loop through the whole document
                        while (a >= 0 && a < end && b >= 0 && b < end) {
                                // find the beginning of the tag
                                a = text.indexOf('<', a);
 
                                // when there is a beginning ...
                                if (a >= start && a <= end) {
                                        // ... find the end of the tag
                                        b = text.indexOf('>', a);
 
                                        // when there is also an end ...
                                        if (b >= start && b <= end) {
                                                // correct b, because otherwise the last sign ('>') will
                                                // not be bold
                                                b += 1;
 
                                                // ... extract the tag
                                                final String tag = text.substring(a, b);
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
                                                } else {
                                                        setCharacterAttributes(a, b - a, new SimpleAttributeSet(), true);
                                                }
 
                                                // find the beginning of the tag
                                                a = b > end ? end : b;
                                               
                                        } else {
                                                setCharacterAttributes(a, end, new SimpleAttributeSet(), true);
//                                              b = 0;
                                        }
                                } else {
                                        b = text.indexOf('>', start);
                                        if (b >= 0) {
                                                setCharacterAttributes(b+1, end, new SimpleAttributeSet(), true);
                                        }
                                        a = -1;
                                }
                        }
                }
 
                protected int findFirstNonWordChar(String text, int index) {
                        while (index < text.length()) {
                                if (text.charAt(index) == '>') {
                                        break;
                                }
                                index++;
                        }
                        return index;
                }
 
                protected int findLastNonWordChar(String text, int index) {
 
                        while (--index >= 0) {
                                if (text.charAt(index) == '<') {
                                        break;
                                }
                        }
                        return index;
 
                }
 
                private boolean isSameAttributeSet(final AttributeSet a1, final AttributeSet a2) {
                        final boolean bold1 = StyleConstants.isBold(a1);
                        final boolean bold2 = StyleConstants.isBold(a2);
 
                        return bold1 == bold2;
                }
        }
 
        private static final long       serialVersionUID        = 1L;
 
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
 
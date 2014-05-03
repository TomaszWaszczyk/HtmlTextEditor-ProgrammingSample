package com.waszczyk.TextEditor.ui;

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
                private int     liCount;
 
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
 
        private final Color      color;
       
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
       
        public String getStartTag(){
                return startTag;
        }
       
        public String getEndTag(){
                return endTag;
        }
 
}


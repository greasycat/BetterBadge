package com.sadpooh.gauchobadge.network;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HtmlParser {

    public static List<Tag> findAllTagPositions(String content, String tagName) {
        List<Tag> results = new ArrayList<>();
        Pattern forAllPattern = Pattern.compile(String.format("</\\s*?%s.*?>|<\\s*?%s.*?>", tagName, tagName));
        Pattern closingTagPattern = Pattern.compile(String.format("</\\s*?%s.*?>", tagName));

        Matcher matcher = forAllPattern.matcher(content);
        while (matcher.find()) {
            Tag tag = new Tag();
            tag.setStart(matcher.start());
            tag.setEnd(matcher.end());

            if (closingTagPattern.matcher(matcher.group()).find()) {
                tag.setClosingTag(true);
            } else {
                tag.setClosingTag(false);
                tag.attributes = hashMapFromTag(matcher.group());
            }

            results.add(tag);

        }

        return results;
    }

    @SafeVarargs
    public static List<Tag> findAllTagWithAttributeName(String content, String tagName, Pair<String, String>... specificAttributePairs) {
        List<Tag> results = new ArrayList<>();
        if (specificAttributePairs != null) {
            for (Tag tag : findAllTagPositions(content, tagName)) {
                for (Pair<String, String> pair : specificAttributePairs) {
                    if (tag.containAttribute(pair.first)) {
                        if (tag.getAttribute(pair.first).equals(pair.second)) {
                            results.add(tag);
                            break;
                        }
                    }
                }
            }
        }
        return results;
    }

    @SafeVarargs
    public static List<Tag> findAllTagWithAttributeValuePairs(String content, String tagName, Pair<String, String>... attributesValuePair) {
        List<Tag> results = new ArrayList<>();
        if (attributesValuePair != null) {
            for (Tag tag : findAllTagPositions(content, tagName)) {
                Log.d("Gaucho:", "new tag" + tag.attributes.toString());
                boolean containAllAttributeValuePairs = true;
                for (Pair<String, String> pair : attributesValuePair) {
                    if (tag.containAttribute(pair.first)) {
                        containAllAttributeValuePairs &= (tag.getAttribute(pair.first).equals(pair.second));

                    }
                }


                if (containAllAttributeValuePairs)
                    results.add(tag);
            }
        }
        return results;
    }

    public static HashMap<String, String> hashMapFromTag(String rawTag) {
        HashMap<String, String> tagHashMap = new HashMap<>();
        Matcher matcher = Pattern.compile("([\\w-]+)=\"([^\"]*)\"").matcher(rawTag);
        while (matcher.find()) {
            tagHashMap.put(matcher.group(1), matcher.group(2));
        }
        return tagHashMap;
    }

    public String recursiveToString(Node<String> node, String tab) {
        if (node.key == null) {
            return "";
        } else {
            if (node.children == null) {
                return node.key + "\n";
            } else {
                StringBuilder children_text = new StringBuilder();
                for (Node<String> child : node.children) {
                    children_text.append(recursiveToString(child, tab + "  "));
                }
                return tab + node.key + "\n" + children_text.toString();
            }
        }
    }

    public static class Tag {
        public HashMap<String, String> attributes;
        private Integer start;
        private Integer end;
        private Boolean isClosingTag;

        Tag() {

        }

        public Integer getStart() {
            return start;
        }

        public void setStart(Integer start) {
            this.start = start;
        }

        public Integer getEnd() {
            return end;
        }

        public void setEnd(Integer end) {
            this.end = end;
        }

        public Boolean getClosingTag() {
            return isClosingTag;
        }

        public void setClosingTag(Boolean closingTag) {
            isClosingTag = closingTag;
        }

        public void addAttribute(String attribute, String value) {
            if (this.attributes != null)
                this.attributes.put(attribute, value);
        }

        public String getAttribute(String attribute) {
            String result = "";
            if (this.attributes != null) {
                result = this.attributes.get(attribute);
                return result == null ? "" : result;
            }
            return "";
        }

        public boolean containAttribute(String attribute) {
            if (this.attributes != null)
                return this.attributes.containsKey(attribute);
            return false;
        }

    }

    public static class TagBlock {
        private Tag openTag;
        private Tag closeTag;
        private boolean isUnpairedBlock;


        TagBlock() {
        }

        public boolean isUnpairedBlock() {
            return isUnpairedBlock;
        }

        public void setUnpairedBlock(boolean unpairedBlock) {
            isUnpairedBlock = unpairedBlock;
        }

        /**
         * @return The beginning tag
         */
        public Tag getOpenTag() {
            return openTag;
        }

        public void setOpenTag(Tag openTag) {
            this.openTag = openTag;
        }

        public Tag getCloseTag() {
            return closeTag;
        }

        public void setCloseTag(Tag closeTag) {
            this.closeTag = closeTag;
        }
    }

    public static class Node<T> {
        public T key = null;
        public Node<T> parent = null;
        public List<Node<T>> children;

        public Node() {
            children = new ArrayList<>();
        }

        public void setKey(T t) {
            key = t;
        }

        public void setParent(Node<T> parent) {
            this.parent = parent;
        }

        public void addChild(Node<T> child) {
            if (children != null) {
                child.setParent(this);
                children.add(child);
            }
        }
    }

    public static class TagBlockTreeGenerator {

        List<Tag> tagList;

        TagBlockTreeGenerator() {
            tagList = new ArrayList<>();
        }

        public Node<TagBlock> generate(String tagName, String content) {
            Node<TagBlock> base = new Node<>();
            tagList = findAllTagPositions(content, tagName);
            recursivelyGenerateBlockTree(base, 0, 0);

            return base;
        }

        public void recursivelyGenerateBlockTree(Node<TagBlock> base, int i, int unpaired) {
            if (i < tagList.size()) {
                // If the tag is an opening tag
                if (!tagList.get(i).isClosingTag) {

                    Node<TagBlock> child = new Node<>();
                    TagBlock tagBlock = new TagBlock();
                    tagBlock.setOpenTag(tagList.get(i));
                    child.setKey(tagBlock);
                    base.addChild(child);

                    recursivelyGenerateBlockTree(child, i + 1, unpaired + 1);
                } else {
                    if (unpaired > 0) {
                        base.key.setCloseTag(tagList.get(i));
                        recursivelyGenerateBlockTree(base.parent, i + 1, unpaired - 1);
                    } else if (unpaired == 0) {
                        Node<TagBlock> child = new Node<>();
                        child.key.setUnpairedBlock(true);
                        child.key.setCloseTag(tagList.get(i));
                        recursivelyGenerateBlockTree(child, i + 1, unpaired - 1);
                    } else {
                        Node<TagBlock> child = new Node<>();
                        child.key.setUnpairedBlock(true);
                        child.key.setCloseTag(tagList.get(i));
                        base.parent.addChild(child);
                    }
                }
            }
        }
    }

}
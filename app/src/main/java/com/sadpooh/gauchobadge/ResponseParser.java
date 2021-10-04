package com.sadpooh.gauchobadge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResponseParser {

    public static class Node<T> {
        public T key = null;
        public List<Node<T>> children;

        public Node() {
            children = new ArrayList<>();
        }

        public Node(T t, Node<T>... children) {
            this.children = new ArrayList<>();
            setKey(t);
            for (Node<T> child : children) {
                addChild(child);
            }
        }

        public void setKey(T t) {
            key = t;
        }

        public void addChild(Node<T> child) {
            if (children != null)
                children.add(child);
        }
    }

    public static class GeneralUnorderedTree<T> {

        public Node<T> base;

        public GeneralUnorderedTree() {
            base = new Node<>();
        }
    }

    public static String recursiveToString(Node<String> node, String tab) {
        if (node.key == null) {
            return "";
        } else {
            if (node.children == null)
            {
                return node.key + "\n";
            }else{
                StringBuilder children_text = new StringBuilder();
                for (Node<String> child : node.children) {
                    children_text.append(recursiveToString(child, tab+"  "));
                }
                return tab+node.key + "\n" + children_text.toString();
            }
        }
    }

    private String text;

    public ResponseParser(String content) {
        this.text = content;
    }

    public List<HashMap<String, String>> find_all_tag(String tag) {
        List<HashMap<String, String>> result = new ArrayList<>();
        Matcher matcher = Pattern.compile("(<" + tag + ".*?/>)").matcher(text);
        while (matcher.find()) {
            result.add(hashMapFromTag(matcher.group(1)));
        }
        return result;
    }

    public List<HashMap<String, String>> findAllAbsoluteTagWithAttribute(String tag, String attribute, String value) {
        List<HashMap<String, String>> results = new ArrayList<>();
        List<HashMap<String, String>> possible = find_all_tag(tag);
        for (HashMap<String, String> tagHashMap : possible) {
            if (attribute != null && value != null) {
                if (value.equals(tagHashMap.get(attribute))) {
                    results.add(tagHashMap);
                }
            }
        }
        return results;
    }

//    public List<HashMap<String, String>> recursiveFindTagWithAttribute();

//    public String select_tags_with_attribute(String tag, String attribute, String value) {
//
//    }

    public HashMap<String, String> hashMapFromTag(String rawTag) {
        HashMap<String, String> tagHashMap = new HashMap<>();
        Matcher matcher = Pattern.compile("([\\w-]+)=\"([^\"]*)\"").matcher(rawTag);
        while (matcher.find()) {
            tagHashMap.put(matcher.group(1), matcher.group(2));
        }
        return tagHashMap;
    }


}

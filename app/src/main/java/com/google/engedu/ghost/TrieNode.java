/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class TrieNode {
    private HashMap<String, TrieNode> children;
    private boolean isWord;

    public TrieNode() {
        children = new HashMap<>();
        isWord = false;
    }

    public void add(String s) {
        int i = 0;

        if (!(children.containsKey(String.valueOf(s.charAt(i))))) {
            children.put(String.valueOf(s.charAt(i)), new TrieNode());
        }

        TrieNode next = children.get(String.valueOf(s.charAt(i)));
        i++;

        while (i < s.length()) {
            if (next.children.containsKey(String.valueOf(s.charAt(i)))) {
                next = next.children.get(String.valueOf(s.charAt(i)));
                i++;
            } else {
                break;
            }
        }

        while (i < s.length()) {
            next.children.put(String.valueOf(s.charAt(i)), new TrieNode());
            next = next.children.get(String.valueOf(s.charAt(i)));
            i++;
        }

        next.isWord = true;
    }

    public boolean isWord(String s) {
        int i = 0;
        TrieNode next = new TrieNode();

        if (s.isEmpty()) {
            return false;
        } else if (children.containsKey(String.valueOf(s.charAt(i)))) {
            next = children.get(String.valueOf(s.charAt(i)));
            i++;
        } else {
            return false;
        }

        while (i < s.length()) {
            if (next.children.containsKey(String.valueOf(s.charAt(i)))) {
                next = next.children.get(String.valueOf(s.charAt(i)));
                i++;
            } else {
                return false;
            }
        }

        if (next.isWord) {
            return true;
        } else {
            return false;
        }
    }

    public String getAnyWordStartingWith(String s) {
        List<String> keyList = new ArrayList<>(children.keySet());
        String word = new String();
        String letter = new String();
        TrieNode next = new TrieNode();
        int i = 0;

        if (s.isEmpty()) {
            letter = keyList.get(new Random().nextInt(keyList.size()));
        } else {
            letter = String.valueOf(s.charAt(i));
            if (children.containsKey(letter)) {
                i++;
            } else {
                return null;
            }
        }

        word += letter;
        next = children.get(letter);

        while (i < s.length()) {
            letter = String.valueOf(s.charAt(i));
            if (next.children.containsKey(letter)) {
                word += letter;
                next = next.children.get(letter);
                i++;
            } else {
                return null;
            }
        }

        while (!(next.children.isEmpty())) {
            if (next.isWord && (new Random().nextBoolean())) {
                return word;
            }
            keyList = new ArrayList<>(next.children.keySet());
            letter = keyList.get(new Random().nextInt(keyList.size()));
            word += letter;
            next = next.children.get(letter);
        }

        return word;
    }

    public String getGoodWordStartingWith(String s, Boolean whoFirst) {
        List<String> keyList = new ArrayList<>(children.keySet());
        List<String> wordList = new ArrayList<>();
        List<String> oddLengthWord = new ArrayList<>();
        List<String> evenLengthWord = new ArrayList<>();
        String word = new String();
        String letter = new String();
        String selected = new String();
        TrieNode next = new TrieNode();
        int i = 0;

        if (s.isEmpty()) {
            letter = keyList.get(new Random().nextInt(keyList.size()));
        } else {
            letter = String.valueOf(s.charAt(i));
            if (children.containsKey(letter)) {
                i++;
            } else {
                return null;
            }
        }

        word += letter;
        next = children.get(letter);

        while (i < s.length()) {
            letter = String.valueOf(s.charAt(i));
            if (next.children.containsKey(letter)) {
                word += letter;
                next = next.children.get(letter);
                i++;
            } else {
                return null;
            }
        }

        wordList = traverseTrie(next, word);

        for (String w : wordList) {
            if (w.length() % 2 == 0) {
                evenLengthWord.add(w);
            } else {
                oddLengthWord.add(w);
            }
        }

        //true is user's turn, otherwise is computer's turn
        if (whoFirst && !(oddLengthWord.isEmpty())) {
            selected = oddLengthWord.get(new Random().nextInt(oddLengthWord.size()));
        } else if (!(evenLengthWord.isEmpty())) {
            selected = evenLengthWord.get(new Random().nextInt(evenLengthWord.size()));
        } else {
            selected = wordList.get(new Random().nextInt(wordList.size()));
        }

        return selected;
    }

    private List<String> traverseTrie(TrieNode node, String word) {
        List<String> wordList = new ArrayList<>();

        if (node.isWord) {
            wordList.add(word);
        }

        if (node.children.isEmpty()) {
            return wordList;
        }

        for (String letter : node.children.keySet()) {
            wordList.addAll(traverseTrie(node.children.get(letter), word + letter));
        }

        return wordList;
    }
}

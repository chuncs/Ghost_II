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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
              words.add(line.trim());
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {
        if (prefix.isEmpty()) {
            return words.get(new Random().nextInt(words.size()));
        } else {
            int leftIndex = 0;
            int rightIndex = words.size() - 1;
            while (leftIndex <= rightIndex) {
                int middleIndex = (rightIndex - leftIndex) / 2 + leftIndex;
                String searchWord = words.get(middleIndex);
                if (searchWord.startsWith(prefix)) {
                    return searchWord;
                } else if (searchWord.compareTo(prefix) > 0) {
                    rightIndex = middleIndex - 1;
                } else {
                    leftIndex = middleIndex + 1;
                }
            }
        }
        return null;
    }

    @Override
    public String getGoodWordStartingWith(String prefix, Boolean whoFirst) {
        String selected = null;
        ArrayList<String> wordsList = new ArrayList<>();

        if (prefix.isEmpty()) {
            selected = words.get(new Random().nextInt(words.size()));
        } else {
            String target = getAnyWordStartingWith(prefix);
            int targetIndex = words.indexOf(target);

            if (target != null) {
                wordsList.add(target);
                int upperIndex = targetIndex + 1;
                int lowerIndex = targetIndex - 1;
                ArrayList<String> oddLengthWord = new ArrayList<>();
                ArrayList<String> evenLengthWord = new ArrayList<>();

                while (upperIndex < words.size()) {
                    String searchWord = words.get(upperIndex);
                    if (searchWord.startsWith(prefix)) {
                        wordsList.add(searchWord);
                        upperIndex++;
                    } else {
                        break;
                    }
                }

                while (lowerIndex >= 0) {
                    String searchWord = words.get(lowerIndex);
                    if (searchWord.startsWith(prefix)) {
                        wordsList.add(searchWord);
                        lowerIndex--;
                    } else {
                        break;
                    }
                }

                for (String word : wordsList) {
                    if (word.length() % 2 == 0) {
                        evenLengthWord.add(word);
                    } else {
                        oddLengthWord.add(word);
                    }
                }

                //true is user's turn, otherwise is computer's turn
                if (whoFirst && !(oddLengthWord.isEmpty())) {
                    selected = oddLengthWord.get(new Random().nextInt(oddLengthWord.size()));
                } else if (!(evenLengthWord.isEmpty())) {
                    selected = evenLengthWord.get(new Random().nextInt(evenLengthWord.size()));
                } else {
                    selected = wordsList.get(new Random().nextInt(wordsList.size()));
                }
            }
        }

        return selected;
    }
}

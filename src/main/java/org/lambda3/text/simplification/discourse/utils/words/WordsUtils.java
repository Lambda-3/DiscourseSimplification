/*
 * ==========================License-Start=============================
 * DiscourseSimplification : WordsUtils
 *
 * Copyright © 2017 Lambda³
 *
 * GNU General Public License 3
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 * ==========================License-End==============================
 */

package org.lambda3.text.simplification.discourse.utils.words;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.simple.Sentence;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class WordsUtils {

    public static Word lemmatize(Word word) {
        Sentence sentence = new Sentence(word.value());
        return new Word(sentence.lemma(0));
    }

    public static List<Word> splitIntoWords(String sentence) {
        PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(new StringReader(sentence), new CoreLabelTokenFactory(), "");
        List<Word> words = new ArrayList<>();

        while (ptbt.hasNext()) {
            CoreLabel label = ptbt.next();
            words.add(new Word(label));
        }

        return words;
    }

    public static String wordsToString(List<Word> words) {
        return SentenceUtils.listToString(words);
    }

    public static String wordsToProperSentenceString(List<Word> words) {
        return wordsToString(wordsToProperSentence(words));
    }

    private static Word capitalizeWord(Word word) {
        String s = word.value();
        if (s.length() > 0) {
            s = s.substring(0, 1).toUpperCase() + s.substring(1);
        }

        return new Word(s);
    }

    public static Word lowercaseWord(Word word) {
        return new Word(word.value().toLowerCase());
    }

    private static List<Word> wordsToProperSentence(List<Word> words) {
        List<Word> res = new ArrayList<>();
        res.addAll(words);

        // trim '.' and ',' at beginning and the end and remove multiple, consecutive occurrences
        for (String c : Arrays.asList(".", ",")) {
            Word prev = null;
            Iterator<Word> it = res.iterator();
            while (it.hasNext()) {
                Word word = it.next();
                if (word.value().equals(c)) {
                    if (prev == null || prev.value().equals(word.value())) {
                        it.remove();
                    }
                }
                prev = word;
            }
            if ((!res.isEmpty()) && (res.get(res.size() - 1).value().equals(c))) {
                res.remove(res.size() - 1);
            }
        }

        // add a '.' at the end
        res.add(new Word("."));

        // capitalize first word
        if (!res.isEmpty()) {
            res.set(0, capitalizeWord(res.get(0)));
        }

        return res;
    }
}

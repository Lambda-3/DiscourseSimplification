/*
 * ==========================License-Start=============================
 * DiscourseSimplification : ExtractionRule
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

package org.lambda3.text.simplification.discourse.tree.extraction;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeException;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeParser;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class ExtractionRule {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final TregexPattern pattern;

    public ExtractionRule(String pattern) {
        this.pattern = TregexPattern.compile(pattern);
    }

    protected static boolean isInfinitival(Tree clauseParseTree) {
        TregexPattern p = TregexPattern.compile(clauseParseTree.value() + " <<, VP <<, /(T|t)o/");
        TregexMatcher matcher = p.matcher(clauseParseTree);

        return (matcher.findAt(clauseParseTree));
    }

    protected static List<Tree> getSiblings(Tree parseTree, List<String> tags) {
        return parseTree.getChildrenAsList().stream().filter(c -> tags.contains(c.value())).collect(Collectors.toList());
    }

    private static Tense getTense(Tree vp) {
        Tense res = Tense.PRESENT;

        // find past tense
        TregexPattern p = TregexPattern.compile("VBD|VBN");
        TregexMatcher matcher = p.matcher(vp);

        if (matcher.find()) {
            res = Tense.PAST;
        }

        return res;
    }

    private static List<Word> appendWordsFromTree(List<Word> words, Tree tree) {
        List<Word> res = new ArrayList<>();
        res.addAll(words);

        TregexPattern p = TregexPattern.compile(tree.value() + " <<, NNP|NNPS");
        TregexMatcher matcher = p.matcher(tree);

        boolean isFirst = true;
        for (Word word : tree.yieldWords()) {
            if ((isFirst) && (!matcher.findAt(tree))) {
                res.add(WordsUtils.lowercaseWord(word));
            } else {
                res.add(word);
            }
            isFirst = false;
        }

        return res;
    }

    // pp is optional
    protected static List<Word> rephraseIntraSententialAttribution(List<Word> words) {
        try {
            List<Word> res = new ArrayList<>();

            Tree parseTree = ParseTreeParser.parse(WordsUtils.wordsToProperSentenceString(words));

            TregexPattern p = TregexPattern.compile("ROOT <: (S < (NP=np ?$,, PP=pp $.. VP=vp))");
            TregexMatcher matcher = p.matcher(parseTree);
            if (matcher.findAt(parseTree)) {
                Tree pp = matcher.getNode("pp"); // optional
                Tree np = matcher.getNode("np");
                Tree vp = matcher.getNode("vp");

                Tense tense = getTense(vp);
                if (tense.equals(Tense.PRESENT)) {
                    res.add(new Word("This"));
                    res.add(new Word("is"));
                    res.add(new Word("what"));
                } else {
                    res.add(new Word("This"));
                    res.add(new Word("was"));
                    res.add(new Word("what"));
                }
                res = appendWordsFromTree(res, np);
                res = appendWordsFromTree(res, vp);
                if (pp != null) {
                    res = appendWordsFromTree(res, pp);
                }
            }

            return res;
        } catch (ParseTreeException e) {
            return words;
        }
    }

    protected static List<Word> rephraseEnablement(Tree s, Tree vp) {
        List<Word> res = new ArrayList<>();

        Tense tense = getTense(vp);
        if (tense.equals(Tense.PRESENT)) {
            res.add(new Word("This"));
            res.add(new Word("is"));
        } else {
            res.add(new Word("This"));
            res.add(new Word("was"));
        }
        res = appendWordsFromTree(res, s);

        return res;
    }

    public abstract Optional<Extraction> extract(Tree parseTree);

    protected enum Tense {
        PRESENT,
        PAST
    }

}

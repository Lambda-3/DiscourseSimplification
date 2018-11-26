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

package org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction;

import com.typesafe.config.Config;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.classification.CuePhraseClassifier;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.model.Leaf;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeException;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeParser;
import org.lambda3.text.simplification.discourse.utils.pos.POSToken;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class ExtractionRule {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ExtractionRule.class);

    protected enum Tense {
        PRESENT,
        PAST
    }

    protected enum Number {
        SINGULAR,
        PLURAL
    }

    protected CuePhraseClassifier classifer;

    public ExtractionRule() {
    }

    public void setConfig(Config config) {
        this.classifer = new CuePhraseClassifier(config);
    }

    public abstract Optional<Extraction> extract(Leaf leaf) throws ParseTreeException;

    protected static List<Tree> getSiblings(Tree parseTree, List<String> tags) {
        return parseTree.getChildrenAsList().stream().filter(c -> tags.contains(c.value())).collect(Collectors.toList());
    }

    protected static Number getNumber(Tree np) {
        Number res = Number.SINGULAR;

        // find plural forms
        TregexPattern p = TregexPattern.compile("NNS|NNPS");
        TregexMatcher matcher = p.matcher(np);
        if (matcher.find()) {
            res = Number.PLURAL;
        }

        // find and
        p = TregexPattern.compile("CC <<: and");
        matcher = p.matcher(np);
        if (matcher.find()) {
            res = Number.PLURAL;
        }

        return res;
    }

    protected static Tense getTense(Tree vp) {
        Tense res = Tense.PRESENT;

        // find past tense
        TregexPattern p = TregexPattern.compile("VBD|VBN");
        TregexMatcher matcher = p.matcher(vp);

        if (matcher.find()) {
            res = Tense.PAST;
        }

        return res;
    }

    protected static Optional<Word> getHeadVerb(Tree vp) {
        TregexPattern pattern = TregexPattern.compile(vp.value() + " [ <+(VP) (VP=lowestvp !< VP < /V../=v) | ==(VP=lowestvp !< VP < /V../=v) ]");
        TregexMatcher matcher = pattern.matcher(vp);
        while (matcher.findAt(vp)) {
            return Optional.of(ParseTreeExtractionUtils.getContainingWords(matcher.getNode("v")).get(0));
        }
        return Optional.empty();
    }

    private static List<Word> appendWordsFromTree(List<Word> words, Tree tree) {
        List<Word> res = new ArrayList<Word>();
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

            TregexPattern p = TregexPattern.compile("ROOT << (S !> S < (NP=np ?$,, PP=pp $.. VP=vp))");
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

    
    protected static String rephraseApposition(Tree vp, String np) {
        String res = "";

        Tense tense = getTense(vp);
        //Number number = getNumber(np);
        if (tense.equals(Tense.PRESENT)) {
        	if (np.equals("NN") || np.equals("NNP")) {
        		res = " is ";
        	} else {
        		res = " are ";
        	}
        } else {
        	if (np.equals("NN") || np.equals("NNP")) {
        		res = " was ";
        	} else {
        		res = " were ";
        	}
        }
        
        return res;
    }
    
    protected static List<Word> rephraseAppositionNonRes(Tree vp, Tree np, Tree np2) {
        List<Word> res = new ArrayList<>();

        Tense tense = getTense(vp);
        Number number = getNumber(np);
        if (tense.equals(Tense.PRESENT)) {
        	if (number.equals(Number.SINGULAR)) {
        		 res.add(new Word("is"));
        	} else {
        		 res.add(new Word("are"));
        	}
        } else {
        	if (number.equals(Number.SINGULAR)) {
        		 res.add(new Word("was"));
        	} else {
        		 res.add(new Word("were"));
        	}
        }
        res = appendWordsFromTree(res, np2);
        
        return res;
    }


    protected static List<Word> getRephrasedParticipalS(Tree np, Tree vp, Tree s, Tree vbgn) {
        Number number = getNumber(np);
        Tense tense = getTense(vp);

        TregexPattern p = TregexPattern.compile(vbgn.value() + " <<: (having . (been . VBN=vbn))");
        TregexPattern p2 = TregexPattern.compile(vbgn.value() + " <<: (having . VBN=vbn)");
        TregexPattern p3 = TregexPattern.compile(vbgn.value() + " <<: (being . VBN=vbn)");

        TregexMatcher matcher = p.matcher(s);
        if (matcher.findAt(s)) {
            List<Word> res = new ArrayList<>();

            res.add(new Word((number.equals(Number.SINGULAR))? "has" : "have"));
            res.add(new Word("been"));
            List<Word> next = ParseTreeExtractionUtils.getFollowingWords(s, matcher.getNode("vbn"), true);
            if (next.size() > 0) {
                next.set(0, WordsUtils.lowercaseWord(next.get(0)));
            }
            res.addAll(next);

            return res;
        }

        matcher = p2.matcher(s);
        if (matcher.findAt(s)) {
            List<Word> res = new ArrayList<>();

            res.add(new Word((number.equals(Number.SINGULAR))? "has" : "have"));
            List<Word> next = ParseTreeExtractionUtils.getFollowingWords(s, matcher.getNode("vbn"), true);
            if (next.size() > 0) {
                next.set(0, WordsUtils.lowercaseWord(next.get(0)));
            }
            res.addAll(next);

            return res;
        }

        matcher = p3.matcher(s);
        if (matcher.findAt(s)) {
            List<Word> res = new ArrayList<>();
            if (tense.equals(Tense.PRESENT)) {
                res.add(new Word((number.equals(Number.SINGULAR)) ? "is" : "are"));
            } else {
                res.add(new Word((number.equals(Number.SINGULAR)) ? "was" : "were"));
            }
            List<Word> next = ParseTreeExtractionUtils.getFollowingWords(s, matcher.getNode("vbn"), true);
            if (next.size() > 0) {
                next.set(0, WordsUtils.lowercaseWord(next.get(0)));
            }
            res.addAll(next);

            return res;
        }

        // default
        List<Word> res = new ArrayList<>();
        if (tense.equals(Tense.PRESENT)) {
            res.add(new Word((number.equals(Number.SINGULAR)) ? "is" : "are"));
        } else {
            res.add(new Word((number.equals(Number.SINGULAR)) ? "was" : "were"));
        }
        List<Word> next = ParseTreeExtractionUtils.getFollowingWords(s, vbgn, true);
        if (next.size() > 0) {
            next.set(0, WordsUtils.lowercaseWord(next.get(0)));
        }
        res.addAll(next);

        return res;
    }

}

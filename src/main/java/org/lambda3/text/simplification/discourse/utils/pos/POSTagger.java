/*
 * ==========================License-Start=============================
 * DiscourseSimplification : POSTagger
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

package org.lambda3.text.simplification.discourse.utils.pos;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class POSTagger {
    private static final MaxentTagger TAGGER = new MaxentTagger("edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger");

    public static List<POSToken> parse(String text) {
        List<POSToken> tokens = new ArrayList<>();

        String posString = TAGGER.tagString(text);

        String[] posTokens = posString.split(" ");

        int idx = 0;
        for (String posToken : posTokens) {
            int sep_idx = posToken.lastIndexOf("_");

            // create text
            String txt = posToken.substring(0, sep_idx);
            String pos = posToken.substring(sep_idx + 1);
            POSToken token = new POSToken(idx, txt, pos);
            tokens.add(token);

            ++idx;
        }

        return tokens;
    }
}

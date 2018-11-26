/*
 * ==========================License-Start=============================
 * DiscourseSimplification : NERString
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

package org.lambda3.text.simplification.discourse.utils.ner;

import edu.stanford.nlp.ling.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class NERString {
    public static final String NO_CATEGORY = "O";

    protected final List<NERToken> tokens;
    private List<NERTokenGroup> groups;

    public NERString(List<NERToken> tokens) {
        this.tokens = tokens;
        this.createGroups();
    }

    private void createGroups() {
        this.groups = new ArrayList<>();

        String lastCategory = null;
        List<NERToken> currGroupTokens = new ArrayList<>();
        for (NERToken nerToken : this.tokens) {

            if ((lastCategory != null) && (!nerToken.getCategory().equals(lastCategory))) {
                // add
                this.groups.add(new NERTokenGroup(currGroupTokens));
                currGroupTokens = new ArrayList<>();
            }

            currGroupTokens.add(nerToken);
            lastCategory = nerToken.getCategory();
        }

        // add
        this.groups.add(new NERTokenGroup(currGroupTokens));
    }

    public List<NERToken> getTokens() {
        return tokens;
    }

    public List<NERTokenGroup> getGroups() {
        return groups;
    }

    private List<Word> getWords(int fromIndex, int toIndex) {
        return tokens.subList(fromIndex, toIndex).stream().map(t -> new Word(t.getText())).collect(Collectors.toList());
    }

    public List<Word> getWords() {
        return getWords(0, tokens.size());
    }

    @Override
    public String toString() {
        return tokens.stream().map(NERToken::toString).collect(Collectors.joining("\n"));
    }
}

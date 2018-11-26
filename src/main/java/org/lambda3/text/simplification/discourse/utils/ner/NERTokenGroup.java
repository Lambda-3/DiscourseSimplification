/*
 * ==========================License-Start=============================
 * DiscourseSimplification : NERTokenGroup
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

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
class NERTokenGroup {
    private final List<NERToken> tokens;

    public NERTokenGroup(List<NERToken> tokens) {
        this.tokens = tokens;
    }

    public int getFromTokenIndex() {
        return tokens.get(0).index;
    }

    public int getToTokenIndex() {
        return tokens.get(tokens.size() - 1).index;
    }

    public List<NERToken> getTokens() {
        return tokens;
    }

    private String getCategory() {
        return tokens.get(0).getCategory();
    }

    public boolean isNamedEntity() {
        return !getCategory().equals(NERString.NO_CATEGORY);
    }

    public List<Word> getWords() {
        return tokens.stream().map(t -> new Word(t.getText())).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "[\n" + tokens.stream().map(t -> "\t" + t.toString()).collect(Collectors.joining("\n")) + "\n]";
    }
}

/*
 * ==========================License-Start=============================
 * DiscourseSimplification : TNERString
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

package org.lambda3.text.simplification.discourse.utils.ner.tner;

import edu.stanford.nlp.trees.Tree;
import org.lambda3.text.simplification.discourse.utils.ner.NERString;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TNERString extends NERString {
    private final Tree parseTree;

    public TNERString(List<TNERToken> tokens, Tree parseTree) {
        super(new ArrayList<>(tokens));
        this.parseTree = parseTree;
        this.tokens.forEach(t -> ((TNERToken) t).setNerString(this));
    }

    public Tree getParseTree() {
        return parseTree;
    }
}

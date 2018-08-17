/*
 * ==========================License-Start=============================
 * DiscourseSimplification : Element
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

package org.lambda3.text.simplification.discourse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.stanford.nlp.trees.Tree;
import org.lambda3.text.simplification.discourse.utils.IDGenerator;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeException;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeParser;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Element {
    private String id;
    private Tree parseTree;
    private int sentenceIdx;
    private int contextLayer;
    private List<SimpleContext> simpleContexts;
    private List<LinkedContext> linkedContexts;

    // for deserialization
    public Element() {
    }

    public Element(Tree parseTree, int sentenceIdx, int contextLayer) {
        this.id = IDGenerator.generateUUID();
        this.parseTree = parseTree;
        this.sentenceIdx = sentenceIdx;
        this.contextLayer = contextLayer;
        this.simpleContexts = new ArrayList<>();
        this.linkedContexts = new ArrayList<>();
    }

    // not efficient -> prefer to use constructor with tree
    public Element(String text, int sentenceIdx, int contextLayer) throws ParseTreeException {
        this(ParseTreeParser.parse(text), sentenceIdx, contextLayer);
    }

    public void addLinkedContext(LinkedContext context) {
        if (!linkedContexts.contains(context)) {
            linkedContexts.add(context);
        }
    }

    public void addSimpleContext(SimpleContext context) {
        if (!simpleContexts.contains(context)) {
            simpleContexts.add(context);
        }
    }

    public String getId() {
        return id;
    }

    public Tree getParseTree() {
        return parseTree;
    }

    public void setParseTree(Tree parseTree) {
        this.parseTree = parseTree;
    }

    @JsonProperty("text")
    public String getText() {
        return WordsUtils.wordsToString(ParseTreeExtractionUtils.getContainingWords(parseTree));
    }

    public int getSentenceIdx() {
        return sentenceIdx;
    }

    public int getContextLayer() {
        return contextLayer;
    }

    public List<SimpleContext> getSimpleContexts() {
        return simpleContexts;
    }

    public List<LinkedContext> getLinkedContexts() {
        return linkedContexts;
    }

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();
        strb.append(id + "     " + contextLayer + "     " + getText() + "\n");
        getSimpleContexts().forEach(c -> strb.append("\tS:" + c.getRelation() + "    " + c.getText() + "\n"));
        getLinkedContexts().forEach(c -> strb.append("\tL:" + c.getRelation() + "    " + c.getTargetID() + "\n"));
        return strb.toString();
    }
}

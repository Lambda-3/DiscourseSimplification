/*
 * ==========================License-Start=============================
 * DiscourseSimplification : SimplificationContent
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimplificationContent extends Content {
	private List<OutSentence<Element>> sentences;

	// for deserialization
	public SimplificationContent() {
	    this.sentences = new ArrayList<>();
	}

	public void addSentence(OutSentence<Element> sentence) {
	    this.sentences.add(sentence);
    }

    public void addElement(Element element) {
        sentences.get(element.getSentenceIdx()).addElement(element);
    }

    public List<OutSentence<Element>> getSentences() {
        return sentences;
    }

    public Element getElement(String id) {
        for (OutSentence<Element> sentence : sentences) {
            Element e = sentence.getElement(id);
            if (e != null) {
                return e;
            }
        }

        return null;
    }

    public List<Element> getElements() {
        List<Element> res = new ArrayList<>();
        sentences.forEach(s -> res.addAll(s.getElements()));

        return res;
    }

    public String defaultFormat(boolean resolve) {
	    StringBuilder strb = new StringBuilder();
        for (OutSentence<Element> outSentence : getSentences()) {
            strb.append("\n# ").append(outSentence.getOriginalSentence()).append("\n");
            for (Element element : outSentence.getElements()) {
                strb.append("\n").append(element.id).append("\t").append(element.getContextLayer()).append("\t").append(element.getText()).append("\n");
                for (SimpleContext simpleContext : element.getSimpleContexts()) {
                    strb.append("\t" + "S:").append(simpleContext.getRelation()).append("\t").append(simpleContext.getText()).append("\n");
                }
                for (LinkedContext linkedContext : element.getLinkedContexts()) {
                    if (resolve) {
                        strb.append("\t" + "L:").append(linkedContext.getRelation()).append("\t").append(getElement(linkedContext.getTargetID()).getText()).append("\n");
                    } else {
                        strb.append("\t" + "L:").append(linkedContext.getRelation()).append("\t").append(linkedContext.getTargetID()).append("\n");
                    }
                }
            }
        }

        return strb.toString();
    }

    public String flatFormat(boolean resolve) {
        StringBuilder strb = new StringBuilder();
        for (OutSentence<Element> outSentence : getSentences()) {
            for (Element element : outSentence.getElements()) {
                strb.append(outSentence.getOriginalSentence()).append("\t").append(element.id).append("\t").append(element.getContextLayer()).append("\t").append(element.getText());
                for (SimpleContext simpleContext : element.getSimpleContexts()) {
                    strb.append("\t" + "S:").append(simpleContext.getRelation()).append("(").append(simpleContext.getText()).append(")");
                }
                for (LinkedContext linkedContext : element.getLinkedContexts()) {
                    if (resolve) {
                        strb.append("\t" + "L:").append(linkedContext.getRelation()).append("(").append(getElement(linkedContext.getTargetID()).getText()).append(")");
                    } else {
                        strb.append("\t" + "L:").append(linkedContext.getRelation()).append("(").append(linkedContext.getTargetID()).append(")");
                    }
                }
                strb.append("\n");
            }
        }

        return strb.toString();
    }

    @Override
    public String toString() {
        return getSentences().stream().map(OutSentence::toString).collect(Collectors.joining("\n"));
    }
}

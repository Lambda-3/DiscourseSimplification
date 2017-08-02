/*
 * ==========================License-Start=============================
 * DiscourseSimplification : SimpleContext
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

package org.lambda3.text.simplification.discourse.runner.model;

import org.lambda3.text.simplification.discourse.runner.discourse_tree.Relation;
import org.lambda3.text.simplification.discourse.runner.sentence_simplification.classification.ClassificationResult;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleContext {
    private static final Pattern PHRASE_PATTERN = Pattern.compile("^\\W*this\\W+\\w+\\W+(?<phrase>.*\\w+.*)$", Pattern.CASE_INSENSITIVE);

    private String text;
    private String phrase; // optional
    private Relation relation;
    private TimeInformation timeInformation; // optional

    // for deserialization
    public SimpleContext() {
    }

    public SimpleContext(String text) {
        this.text = text;
        extractPhrase();
        this.relation = Relation.UNKNOWN;
        this.timeInformation = null;
    }

    public void setText(String text) {
        this.text = text;
        extractPhrase();
    }

    public String getText() {
        return text;
    }

    private void extractPhrase() {
        Matcher matcher = PHRASE_PATTERN.matcher(text);
        if (matcher.matches()) {
            this.phrase = matcher.group("phrase");
        } else {
            this.phrase = null;
        }
    }

    public Optional<String> getPhrase() {
        return Optional.ofNullable(phrase);
    }

    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    public Relation getRelation() {
        return relation;
    }

    public void setTimeInformation(TimeInformation timeInformation) {
        this.timeInformation = timeInformation;
    }

	public Optional<TimeInformation> getTimeInformation() {
		return Optional.ofNullable(timeInformation);
	}
}

/*
 * ==========================License-Start=============================
 * DiscourseSimplification : TregexUtils
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

package org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.utils;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class TregexUtils {

    public static List<MyMatch> sortedFindAt(Tree parseTree, TregexPattern p, List<String> groupsToOrder) {
        List<MyMatch> res = new ArrayList<>();

        TregexMatcher matcher = p.matcher(parseTree);
        while (matcher.findAt(parseTree)) {
            HashMap<String, Tree> groups = new HashMap<>();
            for (String name : matcher.getNodeNames()) {
                groups.put(name, matcher.getNode(name));
            }
            res.add(new MyMatch(groups));
        }

        // sort groups
        res.sort(new MyMatch.Comparator(parseTree, groupsToOrder));

        return res;
    }

    public static List<MyMatch> sortedFind(Tree parseTree, TregexPattern p, List<String> groupsToOrder) {
        List<MyMatch> res = new ArrayList<>();

        TregexMatcher matcher = p.matcher(parseTree);
        while (matcher.find()) {
            HashMap<String, Tree> groups = new HashMap<>();
            for (String name : matcher.getNodeNames()) {
                groups.put(name, matcher.getNode(name));
            }
            res.add(new MyMatch(groups));
        }

        // sort groups
        res.sort(new MyMatch.Comparator(parseTree, groupsToOrder));

        return res;
    }

    public static class MyMatch {
        private final HashMap<String, Tree> groups;

        public MyMatch(HashMap<String, Tree> groups) {
            this.groups = groups;
        }

        public Tree getNode(String name) {
            if (groups.containsKey(name)) {
                return groups.get(name);
            } else {
                throw new IllegalArgumentException("No discourse_tree for name: '" + name + "'");
            }
        }

        public static class Comparator implements java.util.Comparator<MyMatch> {
            private final Tree anchorTree;
            private final List<String> names;

            public Comparator(Tree anchorTree, List<String> names) {
                this.anchorTree = anchorTree;
                this.names = names;
            }

            @Override
            public int compare(MyMatch myMatch, MyMatch otherMatch) {
                int myMatchValue = 0;
                int otherMatchValue = 0;
                for (String name : names) {
                    myMatchValue += myMatch.getNode(name).nodeNumber(anchorTree);
                    otherMatchValue += otherMatch.getNode(name).nodeNumber(anchorTree);
                }

                return myMatchValue - otherMatchValue;
            }
        }
    }
}

/*
 * ==========================License-Start=============================
 * DiscourseSimplification : POSToken
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

/**
 *
 */
public class POSToken {
    private final int index;
    private final String text;
    private final String pos;

    public POSToken(int index, String text, String pos) {
        this.index = index;
        this.text = text;
        this.pos = pos;
    }

    public int getIndex() {
        return index;
    }

    public String getText() {
        return text;
    }

    public String getPos() {
        return pos;
    }

    public String toString() {
        return "(" + index + ": " + pos + ", '" + text + "')";
    }
}

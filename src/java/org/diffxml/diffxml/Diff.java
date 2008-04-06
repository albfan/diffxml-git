/*
Program to difference two XML files

Copyright (C) 2002-2004  Adrian Mouat

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

Author: Adrian Mouat
email: amouat@postmaster.co.uk
*/

package org.diffxml.diffxml;

import java.io.File;

/**
 * Diff is the base class for differencing algorithms.
 *
 * The class defines two diff methods for handling File and String input.
 * Algorithms output result to standard out.
 *
 * @author    Adrian Mouat
 */

public abstract class Diff
{

    /**
     * Differences two files.
     *
     * Result is sent to standard out.
     *
     * @return    True if differences are found.
     *            False if files are identical.
     *
     * @param f1  Original file
     * @param f2  Modified file
     */

    public abstract boolean diff(final File f1, final File f2);


    /**
     * Differences two files.
     *
     * Result is sent to standard out.
     *
     * @return    True if differences are found.
     *            False if files are identical.
     *
     * @param f1  String with path to original file
     * @param f2  String with path to modified file
     */

    public abstract boolean diff(final String f1, final String f2);
}

/*
Program to difference two XML files

Copyright (C) 2002-2005  Adrian Mouat

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

package org.diffxml.diffxml.fmes;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.xerces.dom.NodeImpl;

/**
 * Class to handle general diffxml operations on Nodes.
 *
 * Methods in this class should be general usefulness.
 */

final class NodeOps
{
    /**
     * Do not allow instantiation.
     */

    private NodeOps() { }

    /**
     * Inserts a given node as numbered child of a parent node.
     *
     * If childnum doesn't exist the node is simply appended.
     *
     * Due to general applicability should be moved to helper class.
     *
     * @param childNum  the position to add the node to
     * @param parent    the node that is to be the parent
     * @param insNode   the node to be inserted
     */

    public static void insertAsChild(final int childNum, final Node parent,
           final Node insNode)
        {
        NodeList kids = parent.getChildNodes();

        if (kids.item(childNum) != null)
            parent.insertBefore(insNode, kids.item(childNum));
        else
            parent.appendChild(insNode);
        }

    /**
     * Mark the node as being "inorder".
     *
     * @param n the node to mark as "inorder"
     */

    public static void setInOrder(final Node n)
        {
        ((NodeImpl) n).setUserData("inorder", "true", null);
        }

    /**
     * Mark the node as not being "inorder".
     *
     * @param n the node to mark as not "inorder"
     */

    public static void setOutOfOrder(final Node n)
        {
        ((NodeImpl) n).setUserData("inorder", "false", null);
        }

    /**
     * Mark the node as being "matched".
     *
     * @param n the node to mark as "matched"
     */

    public static void setMatched(final Node n)
        {
        ((NodeImpl) n).setUserData("matched", "true", null);
        }

    /**
     * Mark the node as not being "matched".
     *
     * @param n the node to mark as not being "matched"
     */

    public static void setNotMatched(final Node n)
        {
        ((NodeImpl) n).setUserData("matched", "false", null);
        }

    /**
     * Mark a pair of nodes as matched.
     *
     * @param nodeA  The unmatched partner of nodeB
     * @param nodeB  The unmatched partner of nodeA
     */

    public static void setMatched(final Node nodeA, final Node nodeB)
        {
        setMatched(nodeA);
        setMatched(nodeB);
        }

    /**
     * Check if node is marked "inorder".
     *
     * TODO: Check we can use "equals" in this way
     *
     * @param n node to check
     * @return true if marked "inorder", false otherwise
     */

    public static boolean isInOrder(final Node n)
        {
        return ((NodeImpl) n).getUserData("inorder").equals("true");
        }

    /**
     * Check if node is marked "matched".
     *
     * @param n node to check
     * @return true if marked "matched", false otherwise
     */

    public static boolean isMatched(final Node n)
        {
        return ((NodeImpl) n).getUserData("matched").equals("true");
        }

    /**
     * Check if nodes are the same.
     *
     * Does not test if data equivalent, but if same node in same doc.
     * TODO: Test this method!
     *
     * @param x first node to check
     * @param y second node to check
     * @return true if same node, false otherwise
     */

    public static boolean checkIfSameNode(final Node x, final Node y)
        {
        return (((NodeImpl) x).isSameNode(((NodeImpl) y)));
        }
}

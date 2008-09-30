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

package org.diffxml.diffxml.fmes;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.bcel.internal.generic.ARETURN;

/**
 * Class to hold and calculate DOM and XPath child numbers of node.
 */
public final class ChildNumber {
    
    /** DOM child number. */
    private int mDOMChildNo;

    /** XPath child number. */
    private int mXPathChildNo;

    /** XPath char position. */
    private int mXPathCharPos;

    /** In-order DOM child number. */
    private int mInOrderDOMChildNo;

    /** In-order XPath child number. */
    private int mInOrderXPathChildNo;
    
    /** In-order XPath text position. */
    private int mInOrderXPathCharPos;
    
    /** The node we are doing the calcs on. */
    private final Node mNode;
    
    /** The siblings of the node and the node itself. */
    private NodeList mSiblings;
    
    
    /**
     * Default constructor.
     * 
     * @param n
     *            Node to find the child numbers of
     */
    public ChildNumber(final Node n) {
        
        if (n == null) {
            throw new IllegalArgumentException("Node cannot be null");
        }
        if (n.getParentNode() == null) {
            throw new IllegalArgumentException("Node must have parent");
        }
        
        mNode = n;
        mSiblings = mNode.getParentNode().getChildNodes();
        setChildNumbers();
    }

    /**
     * Get the DOM child number.
     * 
     * @return DOM child number of associated node.
     */
    public int getDOM() {
        
        return mDOMChildNo;
    }

    /**
     * Get the XPath child number.
     * 
     * @return XPath child number of associated node.
     */
    public int getXPathCharPos() {
        
        return mXPathCharPos;
    }

    /**
     * Get the XPath child number.
     * 
     * @return XPath child number of associated node.
     */
    public int getInOrderXPathCharPos() {
        
        return mInOrderXPathCharPos;
    }

    
    /**
     * Get the XPath child number.
     * 
     * @return XPath child number of associated node.
     */
    public int getXPath() {
        
        return mXPathChildNo;
    }

    /**
     * Get the in-order XPath child number.
     * 
     * Only counts nodes marked in-order.
     * 
     * @return In-order XPath child number of associated node.
     */
    public int getInOrderXPath() {
        
        return mInOrderXPathChildNo;
    }

    /**
     * Get the in-order DOM child number.
     * 
     * Only counts nodes marked in-order.
     * 
     * @return In-order DOM child number of associated node.
     */
    public int getInOrderDOM() {
        
        return mInOrderDOMChildNo;
    }
    
    /**
     * Determines whether XPath index should be incremented.
     * 
     * Handles differences between DOM index and XPath index
     * 
     * @param i
     *            the current position in siblings
     * @return true if index should be incremented
     */
    private boolean incIndex(final int i) {

        boolean inc = true;
 
        // Handle non-coalescing of text nodes
        if ((i > 0) && nodesAreTextNodes(
                mSiblings.item(i), mSiblings.item(i - 1))) {
            inc = false;
        }

        return inc;
    }
    
    /**
     * Determines whether the given Nodes are all text nodes or not.
     * 
     * @param nodes The Nodes to checks.
     * @return true if all the given Nodes are text nodes
     */
    private static boolean nodesAreTextNodes(final Node... nodes) {

        boolean areText = true;

        for (Node n : nodes) {
            if ((n == null) || (n.getNodeType() != Node.TEXT_NODE)) {
                areText = false;
                break;
            }

        }
        return areText;
    }

    /**
     * Calculate child numbers of node.
     * 
     */
    private void setChildNumbers() {

        calculateXPathChildNumberAndPosition();
        calculateDOMChildNumber();
        calculateInOrderDOMChildNumber();
        calculateInOrderXPathChildNumberAndPosition();
    }

    /**
     * Calculates the DOM index of the node.
     * 
     */
    private void calculateDOMChildNumber() {
        
        int cn;
        
        for (cn = 0; cn < mSiblings.getLength(); cn++) {
            if (NodeOps.checkIfSameNode(mSiblings.item(cn), mNode)) {
                break;
            }
        }
        
        mDOMChildNo = cn;
    }

    /**
     * Calculates the "in order" DOM child number of the node.
     * 
     */
    private void calculateInOrderDOMChildNumber() {

        mInOrderDOMChildNo = 0;

        for (int i = 0; i < mSiblings.getLength(); i++) {
            if (NodeOps.checkIfSameNode(mSiblings.item(i), mNode)) {
                break;
            }
            if (NodeOps.isInOrder(mSiblings.item(i))) {
                mInOrderDOMChildNo++;
            }
        }
    }

    /**
     * Sets the XPath child number and text position.
     */
    private void calculateXPathChildNumberAndPosition() {
        
        int domIndex = calculateXPathChildNumber();
        calculateXPathTextPosition(domIndex);   
    }

    /**
     * Sets the XPath child number and text position.
     */
    private void calculateInOrderXPathChildNumberAndPosition() {
        
        int domIndex = calculateInOrderXPathChildNumber();
        calculateInOrderXPathTextPosition(domIndex);   
    }
    
    /**
     * Calculate the character position of the node.
     * 
     * @param domIndex The DOM index of the node in its siblings.
     */
    private void calculateXPathTextPosition(final int domIndex) {
        
        mXPathCharPos = 1;
        for (int i = (domIndex - 1); i >= 0; i--) {
            if (mSiblings.item(i).getNodeType() == Node.TEXT_NODE) {
                mXPathCharPos = mXPathCharPos 
                    + mSiblings.item(i).getTextContent().length();
            } else {
                break;
            }
        }
    }

    /**
     * Set the XPath child number of the node.
     * 
     * @return The DOM index of the node in its siblings
     */
    private int calculateXPathChildNumber() {
        
        int childNo = 1;

        int domIndex;
        for (domIndex = 0; domIndex < mSiblings.getLength(); domIndex++) {
            
            if (NodeOps.checkIfSameNode(mSiblings.item(domIndex), mNode)) {
                if (!incIndex(domIndex)) {
                    childNo--;
                }
                break;
            }
            if (incIndex(domIndex)) {
                childNo++;
            }

        }
        
        mXPathChildNo = childNo;
        return domIndex;
    }

    /**
     * Set the in-order XPath child number of the node.
     * 
     * @return The DOM index of the node in its siblings
     */
    private int calculateInOrderXPathChildNumber() {

        int childNo = 0;
        int domIndex;
        Node lastInOrderNode = null;
        Node currNode;
        for (domIndex = 0; domIndex < mSiblings.getLength(); domIndex++) {
            currNode = mSiblings.item(domIndex);
            if (NodeOps.isInOrder(currNode)
                    && !nodesAreTextNodes(currNode, lastInOrderNode)) {
                childNo++;
            }
            if (NodeOps.checkIfSameNode(currNode, mNode)) {
                break;
            }
            if (NodeOps.isInOrder(currNode)) {
                lastInOrderNode = currNode;
            }
        }
   
        //For cases where the node is out of order and there are no 
        //inorder nodes
        if (childNo == 0) {
            childNo = 1;
        }
   
        mInOrderXPathChildNo = childNo;
        return domIndex;
        
    }
    
    /**
     * Calculate the character position of the node.
     * 
     * @param domIndex The DOM index of the node in its siblings.
     */
    private void calculateInOrderXPathTextPosition(final int domIndex) {
        
        mInOrderXPathCharPos = 1;
        for (int i = (domIndex - 1); i >= 0; i--) {
            if (mSiblings.item(i).getNodeType() == Node.TEXT_NODE) {
                if (NodeOps.isInOrder(mSiblings.item(i))) {
                    mInOrderXPathCharPos = mInOrderXPathCharPos 
                        + mSiblings.item(i).getTextContent().length();
                }
            } else {
                break;
            }
        }
    }

}

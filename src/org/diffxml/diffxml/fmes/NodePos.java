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

import org.diffxml.diffxml.DiffXML;
import org.diffxml.diffxml.DiffFactory;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.apache.xerces.dom.NodeImpl;

/**
 * Class to deal with finding positions of nodes within document.
 */

public class NodePos
{

    private static int getDOMChildNumber(Node n)
        {
        return getDOMChildNumber(n, n.getChildNodes());
        }

    private static int getDOMChildNumber(Node n, NodeList kids)
        {
        int cn;

        for (cn = 0; cn < kids.getLength(); cn++)
            {
            if (NodeOps.checkIfSameNode(n, kids.item(cn)))
                break;
            }
        return cn;
        }

    public static int getCharpos(NodeList kids, int DOMChildNumber)
        {
        int charpos = 1;
        for (int y = (DOMChildNumber - 1); y >= 0; y--)
            {
            if (kids.item(y).getNodeType() == Node.TEXT_NODE)
                {
                charpos = charpos + kids.item(y).getNodeValue().length();

                DiffXML.log.finer(kids.item(y).getNodeValue()
                        + " charpos " + charpos);
                }
            else
                break;
            }
        return charpos;
        }

    /**
     * Get the character position of a node.
     *
     * Finds the character offset at which a node starts.
     *
     * @param n the node to find the position of
     * @return int the character offset of the node, starting at 1
     */

    public static int getCharpos(Node n)
        {
        NodeList kids = n.getParentNode().getChildNodes();
        int cn = getDOMChildNumber(n, kids);

        return getCharpos(kids, cn);
        }	

    //Returns XPath of given node in form needed for DUL, 
    //also gives charpos and length if needed
    public static Pos get(Node n)
        {
        Pos n_pos=new Pos(); 
        n_pos.path="null";
        n_pos.charpos=-1;
        n_pos.length=-1;
        String xpath="";

        //Keep boolean so we know if at top
        boolean top=true;

        //Have to init x to something so can be used in final comp
        NodeImpl x = (NodeImpl) n;

        NodeImpl root = (NodeImpl) n.getOwnerDocument().getDocumentElement();

        if (n == null)
            return n_pos;

        do 
            {
            DiffXML.log.fine("Entered XPath");
            DiffXML.log.finer(n.getNodeName());
            Node t=n.getParentNode();

            int index=0;	
            NodeList kids = t.getChildNodes();
            for(int i=0;i<kids.getLength();i++)
                {
                String tag=n.getNodeName();
                x = (NodeImpl) kids.item(i);
                //Increment index, unless text node following text node

                if (DiffFactory.TAGNAMES)
                    {
                    if (tag.equals(x.getNodeName()))
                        index++;

                    //Exception to rule if text node
                    //What about comments???
                    if (x.getNodeType()==Node.TEXT_NODE && i>0
                            && kids.item(i-1).getNodeType()==Node.TEXT_NODE)
                        index--;
                    }
                else
                    {
                    index++;
                    if (x.getNodeType()==Node.TEXT_NODE && i>0 
                            && kids.item(i-1).getNodeType()==Node.TEXT_NODE)
                        index--;
                    }

                if (x.isSameNode(n))
                    {
                    xpath="[" + index + "]" + xpath;
                    if (DiffFactory.TAGNAMES)
                        {
                        //special case for top node
                        if (top)
                            {
                            switch (x.getNodeType())
                                {
                                case Node.TEXT_NODE:
                                    xpath="/text()"+xpath;
                                    break;
                                case Node.COMMENT_NODE:
                                    xpath="/comment()"+xpath;
                                    break;
                                default:
                                    xpath="/" + x.getNodeName()+xpath;
                                }
                            }
                        else
                            xpath="/" + x.getNodeName() + xpath;
                        }
                    else 
                        xpath = "/node()" + xpath;

                    //Continue with next level
                    if (top)
                        {
                        top=false;

                        //Add charpos if prev or next node text
                        DiffXML.log.finer("In top i=" + i + " x.nodevalue=" + x.getNodeValue());
                        DiffXML.log.finer("Path=" + xpath);

                        if ((i>0 && kids.item(i-1).getNodeType()==Node.TEXT_NODE) || ( (i+1) < kids.getLength() && kids.item(i+1).getNodeType()==Node.TEXT_NODE))
                            {
                            //Loop backwards through nodes getting lengths if text
                            //Not sure if charpos will be 1 out..... (by XPath standard)
                            n_pos.charpos=1;
                            for (int y=(i-1); y>=0; y--)
                                {
                                if (kids.item(y).getNodeType()==Node.TEXT_NODE)
                                    {
                                    DiffXML.log.finer("Extra Text value: " + kids.item(y).getNodeValue() + "END");
                                    n_pos.charpos=n_pos.charpos+kids.item(y).getNodeValue().length();
                                    DiffXML.log.finer(kids.item(y).getNodeValue()+ " charpos "+n_pos.charpos);	
                                    }
                                else
                                    break;
                                }
                            //Need length if current node text node
                            if (n.getNodeType()==Node.TEXT_NODE)
                                n_pos.length=n.getNodeValue().length();

                            }
                        }
                    break;
                    }	
                }
            n=n.getParentNode();
            }
        while (!root.isSameNode(x));
        n_pos.path=xpath;			
        return n_pos;
        }
}


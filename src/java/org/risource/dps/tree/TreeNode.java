// TreeNode.java
// $Id: TreeNode.java,v 1.2 1999-04-17 01:19:47 steve Exp $

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/


package org.risource.dps.tree;

import java.io.*;
import org.w3c.dom.*;
import org.risource.dps.active.*;
import org.risource.dps.*;
import org.risource.dps.input.FromNodeList;

/**
 * Abstract base class for all Nodes used to build parse trees in the
 *	Document Processing System.  <p>
 *
 *	Implements the Node interface of the w3c's Document Object Model, and
 *	the additional methods of the ActiveNode interface.  Blythely assumes
 *	that all nodes in the parse tree are subclasses of ParseTreeNode,
 *	and throws a runtime exception when this assumption is violated.  <p>
 *
 * @see org.w3c.dom.Node
 * @see org.risource.dps.active.ActiveNode
 */
public class TreeNode implements ActiveNode, Serializable {

  public static int nodesCreated = 0;

  /************************************************************************
  ** Constructors:
  ************************************************************************/

  public TreeNode() {
    parent = null;
    head   = null;
    tail   = null;
    next   = null;
    prev   = null;
    handler= null;
    action = null;
    nodesCreated ++;
  }

  public TreeNode(short type, String name) {
    nodesCreated ++;
    nodeType = type;
    nodeName = name; // === set from type if null == 
  }

  public TreeNode(short type, String name, Handler h) {
    this(type, name);
    setHandler(h);
  }

  public TreeNode(TreeNode n, boolean copyChildren) {
    handler = n.handler;
    action = n.action;
    owner = n.owner;
    nodeType = n.nodeType;
    nodeName = n.nodeName;
    if (copyChildren) copyChildren(n);
  }

  public TreeNode(ActiveNode n, boolean copyChildren) {
    handler = (Handler)n.getSyntax();
    action  = n.getAction();
    owner = (TreeDocument)n.getOwnerDocument();
    nodeType = n.getNodeType();
    nodeName = n.getNodeName();
    if (copyChildren) copyChildren(n);
  }

  /************************************************************************
  ** Node interface:
  ************************************************************************/

  public short getNodeType() 	{ return nodeType; }

  public String getNodeName() 	{ return nodeName; }

  public String getNodeValue() 	{ return null;  }

  public void setNodeValue(String v) {
    throw new DPSException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
  }


  /** Returns the parent of the given Node instance. If this node is the root
   * 	of the document object tree, null is returned.
   */
  public Node     getParentNode()	{ return getParent(); }

  /** Returns a NodeList object containing the children of this node. 
   *
   *	If there are no children, null is returned. The content of the
   *	returned NodeList is "live" in the sense that changes to the children
   *	of the Node object that it was created from will be immediately
   *	reflected in the set of Nodes the NodeList contains; it is not a
   *	static snapshot of the content of the Node. Similarly, changes made to
   *	the NodeList will be immediately reflected in the set of children of
   *	the Node that the NodeList was created from.
   */
  public NodeList getChildNodes() {
    return (getHead() == null) ? null : new TreeChildList( this ); 
  }

  /** Returns true if the node has any children, false if the node has no
   *	children at all.
   */
  public boolean  hasChildNodes()  	{ return  getHead() != null; }

  public Node     getFirstChild()	{ return getHead(); }
  public Node	  getLastChild() 	{ return getTail(); }

  public Node     getPreviousSibling()	{ return getPrev(); }
  public Node     getNextSibling()	{ return getNext(); }

  public NamedNodeMap getAttributes() 	{ return null; }
  public ActiveAttrList getAttrList()	{ return null; }

  public Document getOwnerDocument() 	{ return owner; }

  /** Inserts a child node (newChildbefore the existing child node refChild. 
   *
   *	If refChild is null, insert newChild at the end of the list of
   *	children. If refChild is not a child of the Node that insertBefore is
   *	being invoked on, a NotMyChildException is thrown.
   */
  public synchronized Node insertBefore(Node newChild, Node refChild)
       throws DOMException 
  {
    TreeNode nc = null;
    if( newChild == null ) return null;

    if( refChild != null && (refChild.getParentNode() != this) )
      throw new DPSException(DOMException.NOT_FOUND_ERR);
    else if( refChild != null && !(refChild instanceof TreeNode) )
      throw new DPSException(DPSException.NOT_ACTIVE_NODE_ERR);
    else if( !(newChild instanceof TreeNode) )
      throw new DPSException(DPSException.NOT_ACTIVE_NODE_ERR);
    else nc = (TreeNode)newChild;

    doInsert(nc, (TreeNode)refChild);
    return newChild;
  }

  /** 
   * Replaces the child node oldChild with newChild in the set of children
   *	of the given node, and return the oldChild node. 
   *
   *	If oldChild was not already a child of the node that the replaceChild
   *	method is being invoked on, a NotMyChildException is thrown.
   */
  public synchronized Node replaceChild(Node oldChild, Node newChild)
       throws DOMException
  {
    TreeNode nc = null;
    if( newChild == null || oldChild == null ) return null;

    if( oldChild.getParentNode() != this )
      throw new DPSException(DOMException.NOT_FOUND_ERR);
    else if( !(newChild instanceof TreeNode) )
      throw new DPSException(DPSException.NOT_ACTIVE_NODE_ERR);
    else nc = (TreeNode)newChild;

    return replaceChild((TreeNode) oldChild, nc); 
  }

  /**
   * Removes the child node indicated by oldChild from the list of children and
   *	returns it.  <p>
   *
   *	If oldChild was not a child of the given node, a NotMyChildException is
   *	thrown. 
   */
  public synchronized Node removeChild(Node oldChild)
       throws DOMException  {
    if( oldChild == null ) return null;

    if( oldChild.getParentNode() != this )
      throw new DPSException(DOMException.NOT_FOUND_ERR);
    else
      return removeChild((TreeNode)oldChild);
  }

  /************************************************************************
  ** ActiveNode interface:
  ************************************************************************/

  
  public ActiveNode getPrevActive()	{ return prev; }
  public ActiveNode getNextActive()	{ return next; }
  public ActiveNode getActiveParent()	{ return parent; }
  public ActiveNode getFirstActive()	{ return head; }
  public ActiveNode getLastActive()	{ return tail; }

  public ActiveNodeList getValueNodes(Context cxt) {
    return getContent();
  }
  
  public void setValueNodes(Context cxt, ActiveNodeList v) {
    throw new DPSException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
  }

  public ActiveNodeList getContent() {
    return hasChildNodes() ? new TreeChildList(this) : null;
  }

  public Input fromContent() {
    // === fromContent needs to return an Input that uses children. ===
    return hasChildNodes()
      ? new FromNodeList(new TreeChildList(this))
      : null;
  }

  public Input fromValue(Context cxt) {
    return new FromNodeList(getValueNodes(cxt));
  }


  ////////////////////////////////////////////////////////////////////////
  // Syntax:

  public Syntax getSyntax() 		{ return handler; }
  public Action getAction() 		{ return action; }
  public Handler getHandler() 		{ return handler; }
  public void setAction(Action newAction) { action = newAction; }

  public void setHandler(Handler newHandler) {
    handler = newHandler;
    action  = handler;
  }

  ////////////////////////////////////////////////////////////////////////
  // Conversion convenience functions:
  // 	At most one of the following conversion functions 
  // 	must be overridden to return <code>this</code>:

  public ActiveElement	 asElement() 	{ return null; }
  public ActiveAttr	 asAttribute() 	{ return null; }
  public ActiveEntity 	 asEntity() 	{ return null; }
  public Namespace	 asNamespace()  { return null; }

  ////////////////////////////////////////////////////////////////////////
  // Copying:

  /** Return a shallow copy of this Node.  Attributes, if any, are
   *	copied, but children are not.
   */
  public ActiveNode shallowCopy() {
    return new TreeNode(this, false);
  }

  /** Return a deep copy of this Node.  Attributes and children are copied.
   */
  public ActiveNode deepCopy() {
    TreeNode node = (TreeNode)shallowCopy();
    for (TreeNode child = getHead();
	 child != null;
	 child = child.getNext()) {
      TreeNode newChild = (TreeNode)child.deepCopy();
      node.doInsert(newChild, null);
    }
    return node;
  }

  public Node cloneNode(boolean deep) {
    return deep? deepCopy() : shallowCopy();
  }

  ////////////////////////////////////////////////////////////////////////
  // Construction:

  /** Append a new child.
   *	Can be more efficient than <code>insertBefore()</code>
   */
  public void addChild(ActiveNode newChild) {
    if (newChild == null) return;
    if (newChild.getParentNode() == this) return;
    if (newChild.getParentNode() != null) newChild = newChild.deepCopy();
    doInsert((TreeNode)newChild, null);
  }

  /** Append a new child.
   *	Can be more efficient than <code>insertBefore()</code>
   */
  public Node appendChild(Node newChild) {
    if (newChild == null) return null;
    if (newChild.getParentNode() == this) return newChild;
    if (newChild.getParentNode() != null) newChild = newChild.cloneNode(true);
    doInsert((TreeNode)newChild, null);
    return newChild;
  }

  ////////////////////////////////////////////////////////////////////////
  // Presentation:

  /**
   * Convert the node and its content to a String.
   */
  public String toString(){
    return startString() + contentString() + endString();
  }

  /** Return SGML/XML comment left bracket; that is "<!--".
   *  Subclasses are suppose to override this function
   *  to return appropriate start tag.
   */
  public String startString(){
    return "<!--";
  }
  
  /** Return the String equivalent of this node type's content or data.
   *  Subclasses are suppose to override this function
   *  to return appropriate content string.
   */
  public String contentString(){
    return Integer.toString( getNodeType() );
  }

  /** Return SGML/XML comment right bracket; that is "-->".
   *  Subclasses are suppose to override this function
   *  to return appropriate end tag.
   *	
   */
  public String endString(){
    return "-->";
  }

  /************************************************************************
  ** Link Management:
  ************************************************************************/

  ////////////////////////////////////////////////////////////////////////
  // Access:

  protected final void setParent(TreeNode parent){ this.parent = parent; }
  protected final void setPrev(TreeNode prev){ this.prev = prev; }
  protected final void setNext(TreeNode next){ this.next = next; }
  protected final void setHead(TreeNode head){ this.head = head; }
  protected final void setTail(TreeNode tail){ this.tail = tail; }
  
  protected final TreeNode getPrev(){ return prev; }
  protected final TreeNode getNext(){ return next; }
  protected final TreeNode getParent(){ return parent; }
  protected final TreeNode getHead(){ return head; }
  protected final TreeNode getTail(){ return tail; }

  protected final int  getChildCount(){ return childCount; }
  protected final void setChildCount(int count){ childCount = count; }
  protected final void incChildCount(){ childCount++; }
  protected final void decChildCount(){ childCount--; }

  ////////////////////////////////////////////////////////////////////////
  // List Processing:

  /** Simple implementation of insertBefore assuming all synchronization
   *	and error-checking have been done.  Overriding this method will
   *	take care of <em>all</em> operations that add children.
   */
  protected void doInsert(TreeNode newChild, TreeNode refChild)
  {
    if      (refChild == null) 		doInsertAtEnd(newChild); 
    else if (refChild == getHead())	doInsertAtStart(newChild);
    else				doInsertBefore(newChild, refChild);
  }

  /** Simple implementation of insertBefore assuming all synchronization
   *	and error-checking have been done, which cannot be overridden. Used
   *	when the caller can guarantee that all other setup has been done.
   */
  protected final void justInsert(TreeNode newChild,
				  TreeNode refChild)
  {
    if      (refChild == null) 		doInsertAtEnd(newChild); 
    else if (refChild == getHead())	doInsertAtStart(newChild);
    else				doInsertBefore(newChild, refChild);
  }

  /** Insert at the end of the list.  Handles empty list.
   */
  protected final void doInsertAtEnd(TreeNode newChild) {
    incChildCount();
    newChild.setParent( this );
    if( !hasChildNodes() ){ 
      newChild.setPrev( null );
      newChild.setNext( null );
      setHead( newChild );
      setTail( newChild );
    } else {
      TreeNode last = getTail();
    
      newChild.setPrev( last );
      newChild.setNext( last.getNext() );
    
      last.setNext( newChild );
    
      setTail( newChild );
    }
  }


  /**
   * Insert a new child at the start.  Handles empty list.
   */
  protected final void doInsertAtStart(TreeNode newChild){
    incChildCount();
    newChild.setParent( this );
    newChild.setPrev( null );
    newChild.setNext( head );

    if (getHead() != null) {
      getHead().setPrev( newChild );
    }

    setHead( newChild );
    if (getTail() == null) setTail(newChild);
  }

  
  /**
   * Normal case insert somewhere in the middle
   */
  protected final void doInsertBefore(TreeNode newChild,
				      TreeNode refChild) {
    incChildCount();
    newChild.setParent( this );

    TreeNode temp = refChild.getPrev();
    
    newChild.setPrev( temp );
    newChild.setNext( refChild );
    
    refChild.setPrev( newChild );
    
    temp.setNext( newChild );
  }


  /**
   * remove a child, assuming that all error checking has been done.
   */
  protected synchronized Node removeChild( TreeNode p )
  {
    if( p == null ) return null;
    
    if( getHead() == getTail() && hasChildNodes() ){ // only one child
      //Report.debug("Removing only one child...");
      setHead( null );
      setTail( null );
    }else if( getTail() == p ){
      // remove the last child, should adjust header's next reference
      //Report.debug("Removing last child...");
      p.getPrev().setNext( p.getNext() );
      setTail( p.getPrev() ); 
    }else if( getHead() == p ){ // the first item, but more than one child
      //Report.debug("Removing first child...");
      p.getNext().setPrev( null );
      setHead( p.getNext() );
    }else{
      //Report.debug("Removing somewhere in the middle...");
      p.getPrev().setNext( p.getNext() );
      p.getNext().setPrev( p.getPrev() );
    }
    p.setPrev( null );
    p.setNext( null );
    p.setParent( null );

    decChildCount();
    return p;
  }

  /**
   * replace a child, assuming that all error checking has been done.
   */
  protected Node replaceChild(TreeNode oldChild, TreeNode newChild)
  {
    //Report.debug(this, "do replace child...");
    if( oldChild == null || newChild == null ) return null;
    
    TreeNode previous = oldChild.getPrev();
    TreeNode next = oldChild.getNext();
    
    newChild.setPrev( previous );
    newChild.setNext( next );
    newChild.setParent( this );

    if ( getHead() == getTail() && hasChildNodes() ){ // only one item
      //Report.debug("Replacing only one item...");
      setHead( newChild );
      setTail( newChild );
    }else if( oldChild == getHead() ){ // sub the first guy, adjust header
      //Report.debug("Replacing the first child...");
      next.setPrev( newChild );
      setHead( newChild );
    }else if ( oldChild == getTail() ){ // replacing the last item
      //Report.debug("Replacing the last child...");
      previous.setNext( newChild );
      setTail( newChild );
    }else{
      //Report.debug("Replacing somewhere in the middle...");
      previous.setNext( newChild );
      previous.setPrev( newChild );
    }

    oldChild.setPrev (null );
    oldChild.setNext ( null );
    oldChild.setParent( null );

    //Report.debug("Leaving replaceChild.");
    return oldChild;
  }

  /**
   * Copy all the children of a Node. 
   * 
   */
  protected void copyChildren(TreeNode nodeB){
    if (nodeB == null) return;
    TreeNode elem = null;
    for (elem = nodeB.getHead(); elem != null; elem = elem.getNext()) {
      TreeNode clone = (TreeNode)elem.deepCopy();
      doInsert( clone, null );
    }
  }

  protected void copyChildren(ActiveNode nodeB) {
    if (nodeB == null) return;
    ActiveNode elem = null;
    for (elem = nodeB.getFirstActive();
	 elem != null;
	 elem = elem.getNextActive()) {
      TreeNode clone = (TreeNode)elem.deepCopy();
      doInsert( clone, null );
    }
  }


  /************************************************************************
  ** Node state:
  ************************************************************************/

  protected short nodeType;
  protected String nodeName;

  /************************************************************************
  ** ActiveNode state:
  ************************************************************************/

  protected Handler handler = null;
  protected Action  action  = null;

  /************************************************************************
  ** Document Tree Links:
  ************************************************************************/

  /**
   * The parent 
   */
  protected TreeNode parent;

  /**
   * Left sibling
   */
  protected TreeNode prev;

  /**
   * Right sibling 
   */
  protected TreeNode next;

  /**
   * Reference to first child
   */
  protected TreeNode head;

  /**
   * Reference to last child
   */
  protected TreeNode tail;

  /**
   * How many children do I have
   */
  protected int childCount = 0;

  /** 
   * Reference to owner document.
   */
  protected TreeDocument owner;
}

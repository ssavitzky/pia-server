////// Mailnow.java:  Handler for <mailnow>
//	$Id: mailnowHandler.java,v 1.1 2000-01-21 23:11:56 bill Exp $

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


package org.risource.dps.handle;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;
import org.risource.dps.tree.TreeText;

/* The classes imported below must be downloaded separately from Sun in the
   JavaMail package (mail.jar located in
   penguin.crc.ricoh.com/jdk1.2.2/lib/mail.jar , and the JavaBeans classes
   activation.jar located in
   penguin.crc.ricoh.com/jdk1.2.2/lib/activation.jar).  Be sure to add these
   to the classpath. */


import java.io.*;
import java.net.InetAddress;
import java.util.Properties;
import java.util.Date;            
import javax.mail.*; 
import javax.mail.internet.*; 
import javax.activation.*;

/** Handler class for &lt;mailnow&gt tag 
 */
public class mailnowHandler extends GenericHandler {
 
  public void action(Input in, Context aContext, Output out) {

    ActiveAttrList atts = Expand.getExpandedAttrs(in, aContext);
    String text = Expand.getProcessedContentString(in, aContext);

    String from = null, url = null;
    String mailhost = null;
    String mailer = "msgsend";
    String protocol = null, host = null, user = null, password = null;
    String record = null;   // name of folder in which to record mail
    boolean debug = false;
     
    String to = atts.getAttribute("to");
    String subject = atts.getAttribute("subject");
    String bcc = atts.getAttribute("bcc");
    String cc = atts.getAttribute("cc");

    if (to == null || to == ""){
	String errMsg = "Mail (subject " + subject + 
	    ") cannot be sent without 'to' attribute.";
	reportError(in, aContext, errMsg);
	out.putNode(new TreeText( errMsg ) );
    }


    try {

	// XXX - concatenate all remaining arguments

	Properties props = System.getProperties();
	// XXX - could use Session.getTransport() and Transport.connect()
	// XXX - assume we're using SMTP
	if (mailhost != null)
	    props.put("mail.smtp.host", mailhost);

	// Get a Session object
	Session session = Session.getDefaultInstance(props, null);
	if (debug)
	    session.setDebug(true);

	// construct the message
	Message msg = new MimeMessage(session);
	if (from != null)
	    msg.setFrom(new InternetAddress(from));
	else
	    msg.setFrom();

	msg.setRecipients(Message.RecipientType.TO,
			  InternetAddress.parse(to, false));
	if (cc != null)
	    msg.setRecipients(Message.RecipientType.CC,
			      InternetAddress.parse(cc, false));
	if (bcc != null)
	    msg.setRecipients(Message.RecipientType.BCC,
			      InternetAddress.parse(bcc, false));

	msg.setSubject(subject);

        msg.setText( text );
                                                     
	msg.setHeader("X-Mailer", mailer);
	msg.setSentDate(new Date() );

	// send the thing off
	Transport.send(msg);

	/*
	// Keep a copy, if requested.

	if (record != null) {
	    // Get a Store object
	    Store store = null;
	    if (url != null) {
		URLName urln = new URLName(url);
		store = session.getStore(urln);
		store.connect();
	    } else {
		if (protocol != null)
		    store = session.getStore(protocol);
		else
		    store = session.getStore();

		// Connect
		if (host != null || user != null || password != null)
		    store.connect(host, user, password);
		else
		    store.connect();
	    }

	    // Get record Folder.  Create if it does not exist.
	    Folder folder = store.getFolder(record);
	    if (folder == null) {
		System.err.println("Can't get record folder.");
		System.exit(1);
	    }
	    if (!folder.exists())
		folder.create(Folder.HOLDS_MESSAGES);

	    Message[] msgs = new Message[1];
	    msgs[0] = msg;
	    folder.appendMessages(msgs);

	    System.out.println("Mail was recorded successfully.");
	}
	*/

    } catch (Exception e) {
	e.printStackTrace();
    }

    String notify = "Mail regarding " + subject + " to " + to + " was sent.";
    out.putNode(new TreeText(notify) );
    System.out.println( notify );
  }

    /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
    public mailnowHandler() {
	/* Expansion control: */
	expandContent = true;	// false	Expand content?
	textContent = false;	// true		extract text from content?

	/* Syntax: */
	parseElementsInContent = true;	// false	recognize tags?
	parseEntitiesInContent = true;	// false	recognize entities?
	syntaxCode = QUOTED;  		// EMPTY, QUOTED, 0 (check)
    }

  mailnowHandler(ActiveElement e) {
    this();
    // customize for element.
    if (e.hasTrueAttribute("result")) syntaxCode=NORMAL;
  }
}


////// signHandler.java: <sign> Handler implementation


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

import org.risource.site.*;
import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;
import org.risource.util.Utilities;

import java.util.Hashtable;
import java.io.*;


import java.security.*;

// The KeyTool class is used to generate new keys
// Calling this directly is not a good idea, but there is no standard
// implementation for creating keys and certificates.  At least 
// this is available to most JDK implementations...
import  sun.security.tools.KeyTool;

/**
 * Handler for digital signatures using &lt;sign&gt;....&lt;/&gt;  
 *
 * In addition to providing digital signatures, this class maintains
 * a storage of public and private keys.  Note that the keys are 
 * stored encrypted on disc, but care should still be taken to prevent 
 * those files from being compromised. <p>      
 * Currently uses Sun's KeyStore class for gnerating/storing keys --
 * The KeyTool class is used to generate key pairs and self signed certificates * (private keys cannot be stored in the key store without a corresponding 
 * and no standard certificate is available so we use Sun's implementation
 * this affects only the generateKeys method --
 * if you have another method of generating keys and storing them into 
 * the key store then you should not need the Sun classes and can 
 * remove the generateKeys method.)
 *
 * @version $Id: signHandler.java,v 1.1 2000-01-21 23:11:57 bill Exp $
 * @author wolff@rsv.ricoh.com
 */

public final class signHandler extends GenericHandler {


  /************************************************************************
  ** Key storage:
  ************************************************************************/

 /**
   * the key storage -- includes protected private keys and corresponding certificates
   * for the public keys
   */
  private KeyStore keys = null;

  /**
   * cache for key storage (in case multiple key files are being used)
   */
  private static Hashtable keysCache = new Hashtable();

  /**
   * cache for key store passwords (to protect cached keys stores)
   */
  private static Hashtable keysPassCache = new Hashtable();
  

  /**
   * default validity period for new keys 
   */
  private static String defaultValidity = "3660"; //10 years
  

  /************************************************************************
  ** Utility functions:
  ************************************************************************/
  /**
   * utility function for managing file associated with keys
   */


  /**
   * cache files -- key is filename 
   */
  private void cacheKeys(String file, String pass)
     {
      keysCache.put( file, keys); 
      keysPassCache.put( file, pass);
    }


  /**
   * check for cached files -- key is filename
   */
    private boolean isFileCached(String file)
      {
        return keysCache.containsKey( file );
      }
    

  /**
   * load cached files -- key is filename  checks password
   */
  private void loadCachedKeys(String file, String pass)
     {
       String cachePass = (String) keysPassCache.get( file );
       if( pass.equals( cachePass ) ){
           keys = (KeyStore)keysCache.get( file );  
       }
    }
 
 /**
   * load keys from cache or files -- 
   * 
   */
 private void loadKeys(String file, String pass) throws IOException, NoSuchAlgorithmException, java.security.cert.CertificateException, KeyStoreException{
    keys = null;

    if(file == null) {
       return;
    }

    if( isFileCached( file )){
      loadCachedKeys( file, pass);
      if( keys != null ) return;
    }

    // else load from file
    
    // JKS is the type for the sun provided key store
    // change for your platform -- 
    // see http://java.sun.com/products/jdk/1.3/docs/guide/security/CryptoSpec.html#AppA


    keys = KeyStore.getInstance("JKS");

    InputStream is = new FileInputStream( file );

    keys.load( is, pass.toCharArray() );
    is.close();
    // store in cache 
    cacheKeys( file, pass);
 }


  /**
   * Key generation and storage is done by an outside class.
   * This uses an external class to create the keys and generate a self signed
   * certificate.  This method can be removed if key generation is external 
   * to your application.  
   * Here we use Sun's KeyTool class.
   * Be careful to void the cache when generating new Keys.  
   * NOTE: if another tool is used to manage
   * keys while this system is running, then the caching code above 
   * should be augmented to 
   * check for updates to the file.
   * <p>
   * if a key already exist for this user it will be retained with alias
   * user-old-date
   * <p>
   * @param user specifies the alias for the new key
   * @param distinguishedName specifies the values that should be put into 
   * (self signed) certificate -- should be of the form
   * "CN=Common Name, OU=Organization Unit, O=Organization, L=Location, S=State, C=Country"
   * @param keypass is the password for the private key
   * @param file is the name of the file
   * @param storepass is the password for the key storage
   * @param validity is number of days key should be valid
   */
 
    private void createKeyPair(String user, String distinguishedName, String keypass, String file, String storepass, String validity){

	if(user==null || distinguishedName == null || keypass == null || file == null || storepass == null) return;
    
	String[] bar = new String[13];
	bar[0] = "-genkey";
	bar[1] = "-dname";
	bar[2] = distinguishedName;
	bar[3] = "-alias";
	bar[4] =  user;
	bar[5] = "-keystore";
	bar[6] = file;
	bar[7] = "-storepass";
	bar[8] = storepass;
	bar[9] = "-keypass"; 
	bar[10] = keypass;
	bar[11] = "-validity";
	bar[12] = validity;
      

	//stop anyone from using the keys while we are generating a new key pair
	synchronized( keysCache ){
	    // load the keys
	    try{
		loadKeys( file, storepass);
		if( keys.containsAlias( user )) {
		    // save the existing public key for this user
		    java.security.cert.Certificate cert = 
			keys.getCertificate( user );
		    int i=0;
		    while(keys.containsAlias( user + "-" + i)){
			i++;
		    }
		    keys.setCertificateEntry( user + "-" + i, cert);
		}
		keys.deleteEntry( user );
		OutputStream os = new FileOutputStream( file );
		char[] passchars = storepass.toCharArray();
		keys.store(os, passchars);
		os.close();
	    }
	    catch(Exception e){
		System.out.println("could not delete key for " + user);
	    }
	    // remove this keystore from memory
	    keysCache.remove(file);
	    keys = null;

	    // generate new key pair
	    try{
		KeyTool.main(bar);
	    }
	    catch(Exception e){
		System.out.println("could not generate key for " + user);
	    }
	}
	// at this point we could reload the keys
	//  loadKeys(file,storepass);
    }
  


    /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;sign&gt; node. 
   * <sign user=user [[password=password [create]] | [verify=signature] | [exists]] [keyfile=filename [keypass=keypass]]>content</sign>
   */
  public void action(Input in, Context cxt, Output out, 
                     ActiveAttrList atts, ActiveNodeList content) {
 
      String file = atts.getAttribute("keyfile");
      String filepass = atts.getAttribute("filepass");
      String user = atts.getAttribute("user");
      if(file == null || user == null) {
	  reportError(in,cxt, "No file for the keys or no user specified");
	  //      putText(out,cxt,"ERROR No file for the keys or no user specified");

	  return;
      }
      if (filepass == null){
	  reportError( in, cxt, "filepass required to open keystore" );
	  return;
      }

      try{
	  loadKeys( file, filepass);      
      } catch(Exception e){
	  reportError( in, cxt, "Failed to load keys from " + file + " exception "+e);
	  e.printStackTrace();
	  if(atts.getAttribute("create") == null) return;  //output needed?  
	  // no output implies failure to complete operation
      }

      // catch keyStore exceptions for all cases    
      try{
      
	  //  check if user exists 
	  if(atts.getAttribute("exists") != null){
	      if(keys.containsAlias(user)){
		  putText(out,cxt,"True");
	      }
	      return;
	  }
     
   
	  // Create new key pair
	  if(atts.getAttribute("createKey") !=null || atts.getAttribute("create") != null){
	      String dname = atts.getAttribute("dname");
	      String keypass = atts.getAttribute("password");
	      if(dname == null || keypass == null) {
		  reportError(in,cxt, "DNAME null or password null");
		  return ;
	      }
	      String validity = atts.getAttribute("validity");
	      if(validity == null) {
		  validity = defaultValidity;
	      }
     
	      createKeyPair(user, dname, keypass, file, filepass, validity); 
	      putText(out,cxt,"KeyPair generated for " + user);
	      return;
	  }
     
    
	  // verify signature
	  if(atts.getAttribute("verify") !=null){
	      String toSign = content.toString();
	      String sigString=atts.getAttribute("verify");
	      java.security.cert.Certificate cert = keys.getCertificate(user);
	      if(cert == null){
		  putText(out,cxt,"False (no key for "+user+")");
		  return;
	      }
	      PublicKey pk = cert.getPublicKey();
	      Signature dsa = Signature.getInstance("SHA/DSA"); 
	      dsa.initVerify(pk);
	      byte[] sig = Utilities.decodeBase64(sigString); 
	      dsa.update(toSign.getBytes());

       
	      if(dsa.verify(sig)){
		  putText(out,cxt,"True");
	      } else {
		  //test old keys here
		  int i=0;
		  while(keys.containsAlias( user + "-" + i)){
		      pk = cert.getPublicKey();
		      dsa = Signature.getInstance("SHA/DSA"); 
		      dsa.initVerify(pk);
		      sig = Utilities.decodeBase64( sigString ); 
		      dsa.update( toSign.getBytes() );
		      if(dsa.verify(sig)){
			  putText(out,cxt,"True (old key))");
			  return;
		      }
		      i++;
		  }
		  putText(out,cxt,"Signature not Verified");      
	      }
	      return;
	  }

	  // default action is to sign content
	  String toSign = content.toString();
	  String keypass = atts.getAttribute("password");
	  char[] passchars = keypass.toCharArray();
	  PrivateKey key = (PrivateKey) keys.getKey(user, passchars);
	  Signature dsa = Signature.getInstance("SHA/DSA"); 
	  dsa.initSign(key);
	  dsa.update(toSign.getBytes());
	  byte hash[] = dsa.sign();
	  putText(out,cxt,Utilities.encodeBase64(hash));
      } catch(KeyStoreException e){
	  reportError(in,cxt, "Problem loading keys" + e);
	  return; //output error text??
      } catch(SignatureException e){
	  reportError(in,cxt, "Badly coded signature" + e);
	  return; 
      } catch(UnrecoverableKeyException e){
	  reportError(in,cxt, "Invalid password for user " + user +"..." + e);
	  putText(out,cxt,"Invalid password for user " + user );
	  return;
      }
      catch(Exception e){
	  reportError(in,cxt, "Problem with signing"+e);
	  putText(out,cxt,"ERROR "+e);
	  e.printStackTrace();
      
	  return;
      }
    
  }
  

 
    /************************************************************************
     ** Constructor:
     ************************************************************************/

  /** Constructor must set instance variables. */
  public signHandler() {
    /* Expansion control: */
    expandContent = true;       // false        Expand content?
    textContent = false;        // true         extract text from content?

    /* Syntax: */
    parseElementsInContent = true;      // false        recognize tags?
    parseEntitiesInContent = true;      // false        recognize entities?
    syntaxCode = NORMAL;                // EMPTY, QUOTED, 0 (check)
  }

  signHandler(ActiveElement e) {
    this();
    // customize for element.
  }
}


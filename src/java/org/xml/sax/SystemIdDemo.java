// SAX demonstration for parsing from a system identifier (URI).
// No warranty; no copyright -- use this as you will.
// $Id: SystemIdDemo.java,v 1.4 1998/05/01 21:00:35 david Exp $

import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.ParserFactory;

import java.net.URL;


/**
  * Demonstrate parsing from a system identifier (URI).
  *
  * <p>Usage: java -Dorg.xml.sax.parser=<var>classname</var> SystemIdDemo
  * <var>systemId</var></p>
  *
  * <p>The SAX parser will open a connection to the URI itself.</p>
  *
  * @see DemoHandler
  */
public class SystemIdDemo {

  /**
    * Main entry point.
    */
  public static void main (String args[])
    throws Exception
  {
    Parser parser;
    DemoHandler handler;

				// Check the command-line usage.
    if (args.length != 1) {
      System.err.println("Usage: java -Dorg.xml.sax.parser=<classname> " +
			 "SystemIdDemo <document>");
      System.exit(2);
    }

				// Make the parser, using the value
				// provided in the org.xml.sax.parser property.
    parser = ParserFactory.makeParser();

				// Create an event handler, and register
				// it with the SAX parser.
    handler = new DemoHandler();
    parser.setEntityResolver(handler);
    parser.setDTDHandler(handler);
    parser.setDocumentHandler(handler);
    parser.setErrorHandler(handler);

				// Parse the document.
    parser.parse(makeAbsoluteURL(args[0]));
  }


  /**
    * If a URL is relative, make it absolute against the current directory.
    */
  private static String makeAbsoluteURL (String url)
    throws java.net.MalformedURLException
  {
    URL baseURL;

    String currentDirectory = System.getProperty("user.dir");
    String fileSep = System.getProperty("file.separator");
    String file = currentDirectory.replace(fileSep.charAt(0), '/') + '/';

    if (file.charAt(0) != '/') {
      file = "/" + file;
    }
    baseURL = new URL("file", null, file);

    return new URL(baseURL, url).toString();
  }

}

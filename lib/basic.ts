<!DOCTYPE tagset SYSTEM "tagset.dtd">
<!-- ====================================================================== -->
<!-- The contents of this file are subject to the Ricoh Source Code Public  -->
<!-- License Version 1.0 (the "License"); you may not use this file except  -->
<!-- in compliance with the License.  You may obtain a copy of the License  -->
<!-- at http://www.risource.org/RPL                                         -->
<!--                                                                        -->
<!-- Software distributed under the License is distributed on an "AS IS"    -->
<!-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.  See   -->
<!-- the License for the specific language governing rights and limitations -->
<!-- under the License.                                                     -->
<!--                                                                        -->
<!-- This code was initially developed by Ricoh Silicon Valley, Inc.        -->
<!-- Portions created by Ricoh Silicon Valley, Inc. are                     -->
<!-- Copyright (C) 1995-1999.  All Rights Reserved.                         -->
<!--                                                                        -->
<!-- Contributor(s): steve@rii.ricoh.com pgage@rii.ricoh.com                -->
<!-- ====================================================================== -->

<tagset name="basic" tagset="tagset">
<title>Basic Tagset</title>
<cvs-id>$Id: basic.ts,v 1.9 2001-01-11 23:37:00 steve Exp $</cvs-id>

<doc>
This file contains the XML definition for the Basic tagset.  It is essentially
an XML representation of a DTD, with extensions for describing the semantics
of the objects being defined, and intermixed documentation in HTML.  The best
reference on how to <em>read</em> such a representation is <a
href="tagset.html"><code>tagset.html</code></a>, the HTML version of the <a
href="tagset.ts"><code>tagset</code></a> tagset.
</doc>

<doc> This tagset consists of the primitive operations <em>only</em>.	It is
      mainly intended as a base for more extensive tagsets, which in turn are
      usually layered on top of HTML.
</doc>

<dl>
  <dt> <b>Note:</b> </dt>
  <dd> It is already possible to process a tagset file into HTML using the
       <a href="tsdoc.html">tsdoc</a> tagset. It will eventually be possible
       to process tagset files into DTD's as well.  It's conceivable that we
       could even process them into Java or C, perhaps using embedded
       <code>&lt;code&gt;</code> tags with a <code>language</code> attribute.
       </dd>
  <dt> <b>Note:</b> </dt>
  <dd> Also observe the SGML requirement that the element named in the
       &lt;!DOCTYPE...&gt; declaration must be the outermost element in the
       document. 
       </dd>
  <dt> <b>Note:</b> </dt>
  <dd> Finally, observe that this tagset includes documentation in HTML
       (actually its XML-ized version XHTML), in spite of the fact that HTML
       is not a superset of the tagset being defined.  The syntax of the
       document that defines a tagset and the syntax defined <em>by</em> the
       tagset are, potentially, completely disjoint.  A few HTML constructs
       remain (e.g., not all attributes have values), but these will
       eventually be fixed.
  </dd>
</dl>

<h2>Definition Elements</h2>

<doc><em>
  These must already be defined for bootstrapping, but they will not be in the
  tagset we are defining unless we put them there.  This is a feature, not a
  bug.  Note that because the tagset is not recursive, we cannot use its tags
  as actions.  This is also a feature.  We may have to relax it if some
  actions turn out to be non-primitive.
</em></doc>

<h3>Define and its components</h3>

<define element="define" handler no-text="no-text">
  <doc> Defines an element, attribute, entity, or word.  It is meaningful for
	for <tag>define</tag> to occur outside of a <tag>namespace</tag> or
	<tag>tagset</tag> element because there is always a "current''
	namespace and tagset in effect.

	<p>A <tag>define</tag> element that contains neither a
	<tag>value</tag> nor an <tag>action</tag> subelement defines only
	syntax.  The defined construct is simply passed through to the output
	by the processor, with its contents and attributes (if any) also being
	processed in turn. </p>

	<p>Note that a <tag>define</tag> can contain anything at all in its
	content; everything but the <tag>value</tag>, <tag>action</tag>, and
	possibly <tag>doc</tag> elements are thrown away.  This means that a
	definition can contain arbitrary decorative markup, and that arbitrary
	computation can be done in the course of processing a definition.</p>

	<p>A construct can be "defined'' more than once; the attributes
	are effectively merged; the value and/or action are replaced.  The
	main use of this is to associate a new value with a construct, and to
	associate an action with a construct that has already been defined. </p>
  </doc>

  <h4>Construct Specification Attributes:</h4>

  <doc> <p>The following attributes specify the type of construct being
	defined, and its name (expressed as the value of the attribute).  It
	is an error for more than one of these attributes to be present in a
	single <tag>define</tag> tag.</p>
  </doc>

  <define attribute="element" optional="optional">
    <doc> Specifies that an element (tag) is being defined.  The value of the
	  attribute is the tagname of the element being defined.  If the
	  <code>handler</code> attribute or the <code><tag>action</tag></code>
	  subelement is present, the element will be active.  If a
	  <code>value</code> attribute or subelement is present, the element
	  will be passively replaced by its value when the document is
	  processed.
    </doc>
  </define>
  <define attribute="attribute" optional="optional"><!-- unimplemented -->
    <doc> Specifies that an attribute is being defined.    The value of the
	  attribute is the name of the attribute being defined.  If the
	  <code>handler</code> attribute or the <code><tag>action</tag></code>
	  subelement is present, the attribute will be active.  If a
	  <code>value</code> attribute or subelement is present, the
	  attribute will be passively replaced by its value unless a value is
	  explicitly specified in the tag where the attribute occurs.

	  <p>It is usual for attributes to be defined <em>inside</em> an
	  element's definition.</p>

	  <note><tag>define attribute</tag> is presently used only for
	  documentation.
	  </note>
    </doc>
  </define>
  <define attribute="entity" optional="optional">
    <doc> Specifies that an entity is being defined.    The value of the
	  attribute is the name of the entity being defined.  If the
	  <code>handler</code> attribute or the <code><tag>action</tag></code>
	  subelement is present, the entity will be active.  If a
	  <code>value</code> attribute or subelement is present, the entity
	  will be passively replaced by its value when it is referenced in a
	  document.
    </doc>
  </define>
  <define attribute="notation" optional="optional"><!-- unimplemented -->
    <doc> Specifies that a notation is being defined.    The value of the
	  attribute is the name of the notation being defined.  The associated
	  value, if any, should be the MIME type of the data.

	  <note><tag>define attribute</tag> is presently used only for
	  documentation.
	  </note>
    </doc>
  </define>

  <h4>General modifiers:</h4>

  <define attribute="handler" optional="optional">
    <doc> Specifies the action handler class for the node being defined.

	  <p> If no value is specified, the handler class name is assumed to
	  be the same as the element's tag, possibly with <code>Handler</code>
	  appended.  If the value contains periods, it is assumed to be a
	  complete Java class name and is used unmodified.</p>

	  <p>If the node being defined is an
	  <em>attribute</em>, things get a little complicated.  What we would
	  really like to happen is that the parser selects a handler for the
	  containing <em>element</em> based on the presence of a "handled''
	  attribute.  Only one such attribute is permitted.</p>
    </doc>
  </define>

  <h4>Modifiers for <code><tag>define element</tag></code>:</h4>
  <doc> <p>The following attributes are meaningful only when defining an
	element.  It is impossible to represent this constraint in SGML, and
	there is no provision for it in tagsets.</p>
  </doc>

  <define attribute="syntax" optional="optional">
    <doc> Specifies the syntax for the element's content.  May be one of the
	  following keywords, which are also available as shortcut attribute
	  names:  <code>quoted</code>, <code>literal</code>,
	  <code>text</code>, <code>no-text</code>, <code>empty</code>.
    </doc>
  </define>

  <define attribute="quoted" optional="optional">
    <doc> Indicates that the content of the element being defined should be
	  parsed, but not expanded before invoking the action.  Note that
	  <code>quoted</code> is not exactly a syntax; it has semantic
	  implications as well.
    </doc>
  </define>
  <define attribute="empty" optional="optional">
    <doc> Indicates that the element being defined cannot have any
	  content.  In XML an empty element may have an end tag or be
	  terminated with "<code>/&gt;</code>''; in HTML and generic SGML the
	  end tag is simply omitted.
    </doc>
  </define>

  <define attribute="implicitly-ends" optional="optional">
    <doc> Indicates that the element being defined cannot occur within any of
	  the space-separated list of elements specified in the value.
	  <code>implicitly-ends</code> can be used to express most, if not
	  all, of the cases where the parser needs to implicitly insert end
	  tags in HTML.
    </doc>
  </define>
  <define attribute="parent" optional="optional">
    <doc> Indicates that the element being defined must be a child of one of
	  the space-separated list of elements specified in the value.  This
	  is not particularly useful except for obligatory components of
	  active elements such as <tag>if</tag>; otherwise it can prevent the
	  element being defined from occurring inside such structures.
    </doc>
  </define>

  <define attribute="literal" optional="optional">
    <doc> Indicates that the content of the element is not parsed (#CDATA).
    </doc>
    <note> <font color="red"> this attribute is currently ignored by the
	   parser, because true XML parsers are supposed to ignore it.  In the
	   future, it will be implemented. </font>
    </note>
  </define>
  <define attribute="text" optional="optional">
    <doc> Indicates that the content of the element is text (including entity
	  expansions), but not markup.
    </doc>
    <note> <font color="red"> this attribute is currently significant only for
	   tags implemented via handlers.  In the future it will be
	   significant for defined tags as well. </font>
    </note>
  </define>
  <define attribute="no-text" optional="optional">
    <doc> Indicates that the content of the element consists entirely of
	  markup (i.e. that all text in the content, if any, is enclosed in
	  markup elements).  Whitespace is permitted in the content, and is
	  marked as ignorable.  Non-whitespace text is wrapped in a comment.
    </doc>
    <note> <font color="red"> this attribute is currently significant only for
	   tags implemented via handlers.  In the future it will be
	   significant for defined tags as well. </font>
    </note>
  </define>

  <h4>Modifiers for <code><tag>define attribute</tag></code>:</h4>

<doc>
	The following are modifiers for the define attribute.
</doc>

  <define attribute="optional" optional="optional">
    <doc> Only meaningful for attributes.  Specifies that the attribute is
	  implied (optional).
    </doc>
    <note author="steve"> === I'm not certain of the semantics of SGML
	  "implied'' attributes, and in any case "optional'' is unambiguous.
    </note>
  </define>
  <define attribute="required" optional="optional">
    <doc> Only meaningful for attributes.  Specifies that the attribute is
	  required.
    </doc>
  </define>
  <define attribute="fixed" optional="optional">
    <doc> Only meaningful for attributes.  Specifies that the attribute's
	  value is invariant (and must be given in its definition).
    </doc>
  </define>

  <h4>Modifiers for <code><tag>define entity</tag></code>:</h4>

  <define attribute="system" optional="optional">
    <doc> The value of this attribute is the "system identifier" (URI) of
	  an "external entity".  Typically it is a filename relative to
	  the document containing the definition being processed.
    </doc>
  </define>
  <define attribute="public" optional="optional">
    <doc> The value of this attribute is the "public identifier" of an
	  external entity.  In a DTD, an alternative system identifier must be
	  provided; that should be specified via the <code>system</code>
	  attribute. 
    </doc>
  </define>
  <define attribute="mode" optional="optional">
    <doc> This specifies the read-write status of an external entity: one of
	  <code>read</code> (default), <code>write</code>, 
	  <code>create</code>, <code>update</code>, or <code>append</code>.
    </doc>
  </define>
  <define attribute="method" optional="optional">
    <doc> This specifies the HTTP request method to make when requesting an
	  external entity from a remote server.  Note that the PUT and POST
	  requests use the specified <tag>value</tag> as their content.
    </doc>
  </define>
  <define attribute="NDATA" optional="optional"><!-- unimplemented -->
    <doc> This specifies that the entity contains non-parsed data; the value
	  specifies the name of the data's <em>notation</em>.
	  <note>This attribute is currently only for documentation.</note>
    </doc>
  </define>
  <define attribute="tagset" optional="optional">
    <doc> The tagset with which to process the entity.  The default is the
	  current tagset. 
    </doc>
  </define>
  <define attribute="retain" optional="optional">
    <doc> This specifies that the a reference to the entity should be replaced
	  by its value when conversion to a string is desired, but otherwise
	  should be retained in the tree and passed through to the output.
	  This is used, e.g., for the predefined character entities.
    </doc>
  </define>
  <define attribute="parameter" optional="optional"><!-- unimplemented -->
    <doc> This specifies that the entity is a "parameter entity," of the
	  sort prefixed by "<code>%</code>" in DTD's.  Unlike ordinary
	  entities, parameter entities are expanded <em>while defining a
	  tagset</em> or DTD. 
	  <note>This attribute is currently only for documentation.</note>
    </doc>
  </define>

</define>

<dl>
  <dt> Note:</dt>
  <dd> The <code>no-text</code> attribute specifies that the element, in this
       case <tag>define</tag>, does not contain text.  All whitespace in its
       content is marked ignorable. 
  </dd>
</dl>

<h4>Subelements of <tag>define</tag></h4>
<dl>
  <dt> Note:</dt>
  <dd> The use of <code>parent=</code> specifies that these elements
       <em>only</em> occur inside the given parent element; in this case,
       <code>define</code>.  The value of the <code>parent</code> attribute is
       actually a list which is appended to with each use, allowing the DTD to
       be incrementally extended.

       <p>The use of <code>parent</code> greatly simplifies the construction of
       content models and the parser.  An element with a parent implicitly
       terminates any unclosed elements between it and its innermost
       parent. </p> 
  </dd>
</dl>

<ul>
  <li> <define element="value" parent="define" handler>
         <doc> The <tag>value</tag> subelement defines a value for the node
	       being defined.  The node is replaced by its value whenever
	       it appears in a document being processed.

       	       <p>The contents of the <tag>value</tag> element are processed
       	       at the point in the document where the <tag>define</tag> occurs
       	       in the document unless the <code>quoted</code> attribute is
       	       present.  They are <em>not</em> processed again during
       	       expansion unless the <code>active</code> attribute is
       	       present. </p>
         </doc>
       	 <define attribute="active" optional="optional">
           <doc> If present, causes the defined value to be reprocessed
		 at the point where it is expanded.  Unlike an
		 <tag>action</tag>, however, no local namespace is created
		 during the expansion.
           </doc>
         </define>
       	 <define attribute="quoted" optional="optional">
           <doc> If present, suppresses processing of the defined value
		 at the point where it is defined.  
           </doc>
         </define>
       	 <define attribute="passive" optional="optional">
           <doc> If present, suppresses replacement of the defined construct
		 with its value during normal processing.  The construct,
		 usually an entity, is passed through all the way to the
		 output.  Passive values are normally used for things like
		 character entities (<code>&amp;amp;</code> and so on).  They
		 <em>are</em> expanded when converting an object to a string.
           </doc>
         </define>
       </define>
  </li>       
  <li> <define element="action" parent="define AGENT" syntax="quoted" handler>
         <doc> The <tag>action</tag> subelement defines an action for the
	       node being defined.  Note that it is possible for a node to
	       have both an action and a value.

       	       <p>The contents of the <tag>action</tag> element are processed
       	       at the point in the document where the defined construct is
       	       expanded. They are <em>not</em> processed in the definition.</p>

       	       <p>Expanding an action associated with an element or attribute
       	       implicitly defines a local <tag>namespace</tag> containing the
       	       following entities:</p>

       		<dl>
		  <dt> <code>&amp;content;</code></dt>
		  <dd> The content of the element being expanded.</dd>
		  <dt> <code>&amp;element;</code></dt>
		  <dd> The "start tag" of the element being expanded: the
		       element without its content.</dd>
		  <dt> <code>&amp;attributes;</code></dt>
		  <dd> The attribute list of the element being expanded.</dd>
		  <dt> <code>&amp;value;</code></dt>
		  <dd> The defined <tag>value</tag> associated with the
		       definition being expanded.</dd>
		</dl>
       	       <p>Expanding an action associated with an Entity or Word
       	       implicitly defines a local <tag>namespace</tag> containing the
       	       following entities:</p>

       		<dl>
		  <dt> <code>&amp;content;</code></dt>
		  <dd> The children of the node being expanded, if any.</dd>
		  <dt> <code>&amp;node;</code></dt>
		  <dd> The Text or EntityReference node being expanded</dd>
		  <dt> <code>&amp;value;</code></dt>
		  <dd> The defined <tag>value</tag> associated with the
		       definition being expanded.</dd>
		</dl>
       </doc>
       <define attribute="mode">
	  <doc>	Defines exactly how the <tag>action</tag> element is to be
		expanded.  One of the following values:
		
       		<dl>
		  <dt> <code>replace-element</code> or <code>element</code></dt>
		  <dd> The default: replace the entire invocation element.</dd>
		  <dt> <code>replace-content</code> or <code>content</code>
		  <dd> Replace only the content, passing the start tag through
		       unchanged.</dd>
		  <dt> <code>append-content</code> or <code>append</code>
		  <dd> Append the expansion to the content. </dd>
		  <dt> <code>defer-content</code> or <code>defer</code>
		  <dd> Defer expansion of the content until
		       <code>&amp;content;</code> actually occurrs in the
		       content.  This provides a <em>correct</em> form of lazy
		       evaluation.  The content is expanded, if necessary,
		       <em>in the context of the tag being expanded</em>.  It
		       is necessary for the action to expand the content
		       <em>exactly once!</em>
		  </dd>
		  <dt> <code>before-content</code> or <code>before</code>
		  <dd> Defer expansion of the content until after the action
		       has been expanded.  This provides a crude form of lazy
		       evaluation.  The content is expanded <em>in the context
		       of the tag being expanded</em>.

		       <p> There are two control variables the action may set:
		           <code>wrapper</code> and <code>content</code>.
		           <code>content</code> is expanded <em>after</em> the
		           invoking node's content.   <code>wrapper</code> is
		           either the tagname of an element wrapped
		           <em>around</em> the expanded content, or one of the
		           two special strings <code>#skip</code> or
		           <code>#hide</code>. 
		       </p>
		  </dd>
		  <dt> <code>silent</code></dt>
		  <dd> The action is invoked only for side effects: the
		       invoking element is passed unchanged to the output, but
		       any output generated in the expansion is ignored. </dd>
		  <dt> <code>delete</code></dt>
		  <dd> Like <code>silent</code> except that the invoking
		       element is ignored as well.</dd>
		</dl>

	  </doc>		
       </define>
     </define>
  </li>		
</ul>

<h3>Tagset and Namespace</h3>

<doc>
  The <tag>tagset</tag> and <tag>namespace</tag> elements provide the context
  in which <tag>define</tag> operates, i.e., in which elements, entities, and
  so on are defined.

  <p>It is, however, meaningful for <tag>define</tag> to occur outside of
  a <tag>namespace</tag> or <tag>tagset</tag> element because there is always
  a "current" namespace and tagset in effect.</p>
</doc>

<define element="tagset" handler>
  <doc> This element defines a "<em>tagset</em>".  A tagset is roughly equivalent
	to a DTD or database schema.  Having an XML representation allows
	tagsets to be processed using "normal" methods, rather than devising
	special machinery for parsing and processing DTD's.
  </doc>
  <note author="steve">
	Eventually it will be possible to process XML tagsets into DTD's.
  </note>
  <note author="steve">
	Eventually we must make it possible to "include" one tagset
	inside another by using <tag>extract</tag>.
  </note>

  <define attribute="name" required >
	<doc>The tagset name.
	</doc>
  </define>
  <define attribute="parent" optional="optional">
    <doc> The <code>parent</code> attribute specifies the tagset in which
	  names not defined in the current tagset are looked up.  It is
	  effectively <em>included</em> in the tagset being defined.
    </doc>
  </define>
  <define attribute="include" optional="optional">
    <doc> The <code>include</code> attribute specifies a list of tagsets,
	  by name, whose contents are to be <em>copied into</em> the current
	  <tag>tagset</tag>.  This is different from <tag>context</tag>, which
	  effectively includes by reference.
    </doc>
  </define>
  <define attribute="tagset" optional="optional">
    <doc> The <code>tagset</code> attribute specifies the tagset to be used
	  when <em>parsing</em> the tagset being defined.  It specifies the
	  syntax of documentation elements and the operations permitted in
	  <tag>value</tag> and <tag>action</tag> elements.
    </doc>
  </define>
  <define attribute="recursive" optional="optional">
    <doc> If present, this attribute indicates that elements defined in the
	  tagset can be used in the <tag>action</tag> and <tag>value</tag>
	  definitions of other elements.  The default is to restrict
	  definitions to be in terms of elements and entities defined in the
	  <em>enclosing document's</em> tagset.  This allows the language used
	  to define tagsets to differ from the language being defined.

	  <p><strong>Note</strong> that it may be almost impossible (totally
	  impossible, in some cases) to come up with a DTD that 
	  accurately describes a recursive tagset definition file.  It is
	  reducible to a valid DTD, however, so that the documents
	  <em>it describes</em> are valid SGML.</p>
    </doc>
  </define>
  <define attribute="parser" optional="optional">
    <doc> The <code>tagset</code> attribute specifies the class of the parser
	  to be used with this tagset.  If not specified, an XML/HTML parser
	  (<code>BasicParser</code>) is used.  Parser parameters are usually
	  passed in the attributes of the <tag>tagset</tag> tag; the
	  attributes used by <code>BasicParser</code> are defined below.
    </doc>
  </define>
  <define attribute="nonstrictEndTags" optional="optional">
    <doc> If present, this attribute indicates that the parser should not
	  complain about missing end tags.  It is used for markup languages
	  such as HTML in which end tags may be omitted in certain cases.
    </doc>
  </define>
</define>

<define element="namespace" handler>
  <doc> This defines a namespace for entities.  The content is expanded in a
	context in which the namespace in question is the local one.  
  </doc>
  <define attribute="name" optional="optional">
    <doc> Note that the <code>name</code> attribute is optional; it is
	  perfectly meaningful to have an anonymous <tag>namespace</tag>.
    </doc>
  </define>
  <define attribute="hide" optional="optional">
    <doc> If present, the namespace is not returned as a result.  The contents
	  are simply expanded for their side-effects.
    </doc>
  </define>
  <define attribute="pass" optional="optional">
    <doc> If present, the namespace is not returned as a result, but
	  everything in the content is passed through to the output.
    </doc>
  </define>
</define>


<define element="properties" handler="propertyBuilder">
  <doc> This defines a ``property list'' -- effectively a namespace for
	elements.  The content is expanded in a context in which the namespace
	in question is the local one.  Elements in the content are put into
	the property list, with the ``name'' being the element's tagname and
	the ``value'' being its content.
  </doc>
  <define attribute="name" optional="optional">
    <doc> Note that the <code>name</code> attribute is optional; it is
	  perfectly meaningful to have an anonymous <tag>properties</tag>.
    </doc>
  </define>
  <define attribute="hide" optional="optional">
    <doc> If present, the namespace is not returned as a result.  The contents
	  are simply expanded for their side-effects.
    </doc>
  </define>
  <define attribute="pass" optional="optional">
    <doc> If present, the namespace is not returned as a result, but
	  everything in the content is passed through to the output.
    </doc>
  </define>
</define>

<define element="stringmap" handler="stringmapBuilder">
  <doc> This defines a ``property list'' that maps names into strings. 
  </doc>
  <define attribute="name" optional="optional">
    <doc> Note that the <code>name</code> attribute is optional; it is
	  perfectly meaningful to have an anonymous <tag>propstrings</tag>.
    </doc>
  </define>
  <define attribute="hide" optional="optional">
    <doc> If present, the namespace is not returned as a result.  The contents
	  are simply expanded for their side-effects.
    </doc>
  </define>
  <define attribute="pass" optional="optional">
    <doc> If present, the namespace is not returned as a result, but
	  everything in the content is passed through to the output.
    </doc>
  </define>
</define>


<h3>Documentation Elements</h3>

<define element="doc" syntax="quoted">
  <doc> Contains documentation, e.g. for the node being defined.  It can be
	either retained or stripped out depending on how the enclosing
	namespace is being processed.  Since it lacks a defined parent, it can
	be used in any context.
  </doc>
  <action> </action>
</define>

<define element="note" syntax="quoted">
  <doc> This subelement contains attributed annotation for the node being
	defined.  Whether it is retained or stripped depends on how the
	enclosing namespace is processed.  Since it lacks a defined parent, it
	can be used in any context.
  </doc>
  <define attribute="author" optional="optional">
     <doc> The value of this attribute identifies the author, typically by
	   initials, login name, or e-mail address.
     </doc>
   </define>
  <action> </action>
</define>

<h3>Name-Binding Elements</h3>

<define element="bind" handler syntax="quoted">
  <doc> <p>This binds a name (typically an entity) in a Namespace.  The
	contents are <em>not</em> expanded.  <tag>bind</tag> is primarily
	intended for use in initialization.</p>
  </doc>
  <define attribute="name" required>
    <doc> Specifies the name to be bound.
    </doc>
  </define>
</define>


<define element="let" handler syntax="quoted">
  <doc> <p>This binds a name (typically an entity) in a Namespace.  The
	contents <em>are</em> expanded.  <tag>let</tag> differs from
	<tag>set</tag> in that the name is bound in the <em>most-local</em>
	namespace.</p>
  </doc>
  <define attribute="name" required>
    <doc> Specifies the name to be bound.
    </doc>
  </define>
</define>


<h3>Get and Set</h3>

<define element="get" handler syntax="quoted">
  <doc> <p>Get the value associated with a name.  If the name is undefined,
	expand the content of the <tag>get</tag> element and return the
	result.</p>

	<p>The main advantages of <tag>get</tag> over the equivalent entity
	reference are that the name can be computed by entity substitution, it
	is not limited to strict entity syntax, and that a default value can
	be provided.</p>

	<p>A further advantage is that <tag>get</tag> works in situations
	(e.g. ECMAscript and other, mainly client-side 
	DOM Level 1 applications) where entities are expanded automatically
	before the document processor can see them.</p>
  </doc>
  <define attribute="name" required>
    <doc> Specifies the name of the entity to be retrieved.
    </doc>
  </define>
</define>

<define element="set" handler >
  <doc> <p>The main advantages of <tag>set</tag> over the equivalent
	<tag>extract</tag> are compactness and simplicity.  These also
	translate into a considerable increase in run-time efficiency.</p>
  </doc>
  <define attribute="name" required>
    <doc> Specifies the name of the entity to be set.
    </doc>
  </define>
</define>


<h2>Element Construction</h2>

<doc>
  The ability to construct an element with a given attribute list and content
  is essential to the language, because most XML parsers do not pass entity
  references to the application, but substitute them immediately. 
</doc>

<define element="do" quoted="yes" handler="nodeBuilder">
  <doc> Constructs a new node, typically an Element, then processes it.  The
	attributes of the element being constructed are specified using
	<tag>let</tag> or <tag>bind</tag> elements, exactly as if the
	attribute list was a Namespace (which in fact it is).  The content is
	specified using the pseudo-attribute ``<code>...</code>''.  The
	tagname is specified using either the <code>name</code> attribute or
	by binding the pseudo-attribute ``<code>.</code>''.  Note that if you
	use <tag>let&nbsp;name="..."</tag> to set the content, the content
	will end up being expanded <em>twice</em>.

	<p>The <tag>do</tag> tag allows the values of attributes to be
	computed dynamically.  This is necessary because most XML parsers
	substitute entities <em>before</em> passing an attribute list to the
	application, and in fact the XML specification <em>requires</em> this
	behavior.  It also allows the tagnames of elements to be computed;
	this may be useful under peculiar circumstances. </p>

	<p>If the <code>...</code> (content) pseudo-attribute is not bound,
	the entire result of expanding the content of the <tag>do</tag>
	element is used as the content of the new node, but with leading and
	trailing whitespace trimmed off.  This is by far the most common way
	of defining the content of a new node. </p>
  </doc>
  <action><!-- dummy --></action>
  <define attribute="name">
    <doc> Specifies the tag name of the element being constructed.  If the
	  name begins with a <code>#</code> character it is followed by a
	  node type, and a node of the given type is constructed instead of an
	  element.  Popular node types for this treatment are
	  <code>#cdata</code> and <code>#comment</code>.

	  <p>It is important not to confuse the <code>name</code> attribute of
	  the <tag>element</tag> tag with the <code>name</code> attribute of
	  the element under construction.  The latter must be specified using
	  <tag>bind</tag> or <tag>set</tag> in the usual way.  For
	  example,</p>

	  <pre>&lt;do name="get"&gt;&lt;set name="name"&gt;name-to-get&lt;/set&gt;&lt;/do&gt;</pre>

	  <p>... is equivalent to </p>
	  <pre>&lt;get name="name-to-get" /&gt;</pre>
    </doc>
  </define>
  <define attribute="type" optional="optional">
    <doc> Specifies the node type of the element being constructed.  If
	  omitted, the default is <code>element</code>.
    </doc>
  </define>
</define>

<define element="make" quoted="yes"  handler="nodeBuilder">
  <doc> A version of <tag>do</tag> that simply builds a node but does not
	process it.
  </doc>
  <define attribute="name">
    <doc> Specifies the tag name or node name of the element or node being
	  constructed.  May specify <var>#type</var> for constructing nodes
	  that do not have a specific node name (e.g. <code>comment</code>).
    </doc>
  </define>
  <define attribute="type" optional="optional">
    <doc> Specifies the node type of the element being constructed.  If
	  omitted, the default is <code>element</code>.
    </doc>
  </define>
</define>

<h2>Control Structure Elements</h2>

<doc><em>
  Control structure elements modify the control flow of an expansion, by
  selectively including, skipping, or repeating some content.  The control
  structure elements are <tag>if</tag> and <tag>repeat</tag>.

The control structure elements are summarized here:

</em></doc>

<h3>If and its Components</h3>
<define element="if" handler PCDATA syntax="quoted">
  <doc> If any non-whitespace text, or any defined entity, is present before
	the first "official" child element, the <em>condition</em> of the
	<tag>if</tag> is considered to be <code>true</code>.  Otherwise it is
	false.  (This implies that comments are ignored.)

	Any element is allowed as a child of <tag>if</tag>.
  </doc>
</define>
<dl>
  <dt> Note: </dt>
  <dd> === need a way to specify that anything can be a child of
       <tag>if</tag>, not just the specified children.  PCDATA?  ANY?</dd>
  <dt> Note: </dt>
  <dd> We could have specified <tag>test</tag> as the default child, but the
       implementation is cleaner and more efficient without doing so.</dd>
  <dt> Note: </dt>
  <dd> <tag>else-if</tag> is not strictly necessary: an <tag>if</tag> may have
       multiple tests, each with a corresponding <tag>then</tag> element.</dd>
</dl>

<h4>Subelements of <tag>if</tag></h4>
<ul>
  <li> <define element="then" parent="if else-if elif elsf" handler syntax="quoted">
         <doc> The <tag>then</tag> component is expanded if its parent's
	       condition is <code>true</code>.
         </doc>
       </define>
  </li>
  <li> <define element="else" parent="if" handler syntax="quoted">
         <doc> The <tag>else</tag> component is expanded if its parent's
	       condition is <code>false</code>.
         </doc>
       </define>
  </li>       
  <li> <define element="else-if" parent="if" handler="elsf" syntax="quoted">
         <doc> The <tag>else-if</tag> component is expanded if its parent's
	       condition is <code>false</code>.  Its contents consist of a
	       <em>condition</em> and a <tag>then</tag> element which is
	       expanded if the condition is <code>true</code>.  If the
	       condition is <code>false</code>, expansion resumes with the
	       following <tag>else-if</tag> or <tag>else</tag> in the
	       surrounding element.
         </doc>
       </define>
  </li>
  <li> <define element="elsf" parent="if" handler="elsf" syntax="quoted">
         <doc> This is a compact synonym for <tag>else-if</tag>, chosen because
	       it is the same length as <tag>then</tag> and <tag>else</tag>.
         </doc>
       </define>
  </li>
  <li> <define element="elif" parent="if" handler="elsf" syntax="quoted">
         <doc> This is a compact synonym for <tag>else-if</tag>, chosen because
	       it is the same length as <tag>then</tag> and <tag>else</tag>.
	       Note that <tag>elif</tag> is the keyword used for this purpose
	       in Python. 
         </doc>
       </define>
  </li>
</ul>

<h3>Repeat and its components</h3>
<define element="repeat" handler syntax="quoted" PCDATA>
  <doc> The content of this element is repeatedly expanded until one of the
	defined subelements reaches its specified "stop" condition.  An
	implicit local namespace is created in which the iteration variables
	are defined.
  </doc>
  <define attribute="list" optional="optional">
    <doc> If present, this results in a simplified
	  <code><tag>repeat</tag></code> with an implied
	  <code><tag>foreach</tag></code> subelement.
    </doc>
  </define>
  <define attribute="start" optional="optional">
    <doc> If present, this results in a simplified
	  <code><tag>repeat</tag></code> with an implied
	  <code><tag>for</tag></code> subelement.
    </doc>
  </define>
  <define attribute="stop" optional="optional">
    <doc> If present, this results in a simplified
	  <code><tag>repeat</tag></code> with an implied
	  <code><tag>for</tag></code> subelement.
    </doc>
  </define>
  <define attribute="step" optional="optional">
    <doc> If present, this results in a simplified
	  <code><tag>repeat</tag></code> with an implied
	  <code><tag>for</tag></code> subelement.
    </doc>
  </define>
  <define attribute="entity" optional="optional">
    <doc> If present, this specifies the name of the iteration variable for an
	  implied <code><tag>foreach</tag></code> or
	  <code><tag>for</tag></code> subelement.
    </doc>
  </define>
</define>

<h4>Subelements of <tag>repeat</tag></h4>
<doc>
The contents of a <tag>repeat</tag> is repeatedly expanded.  All of the
following subelements are effectively iterating in parallel, which makes it
easy to go through multiple lists and number the corresponding elements. 
</doc>

<ul>
  <li> <define element="foreach" parent="repeat" handler>
         <doc> The contents of this subelement are a list; at each iteration
	       the specified entity (default <code>&amp;li;</code>) is
	       replaced by an item from the list.  The list
	       is contained in the entity
	       <code>&amp;<em>name</em>-list;</code> and the current position
	       in <code>&amp;<em>name</em>-index;</code>
         </doc>
         <define attribute="entity" optional="optional">
           <doc> If present, this specifies the name of the iteration variable
		 used by the <tag>foreach</tag> subelement.  
    	   </doc>
         </define>
       </define>
  </li>       
  <li> <define element="for" parent="repeat" handler>
         <doc> The iteration variable (default <code>&amp;n;</code>) is
	       incremented from the starting value (default 1) by the step
	       value (default 1) until it reaches the final value (no
	       default).  Iteration is stopped when the iteration variable
	       exceeds the final value.
         </doc>
         <define attribute="entity" optional="optional">
           <doc> If present, this specifies the name of the iteration variable
		 used by the <tag>for</tag> subelement.  
    	   </doc>
         </define>
         <define attribute="start" optional="optional">
           <doc> If present, this specifies the starting value for the
		 iteration variable, replacing a <tag>start</tag> subelement.  
    	   </doc>
         </define>
         <define attribute="stop" optional="optional">
           <doc> If present, this specifies the final value for the
		 iteration variable, replacing a <tag>stop</tag> subelement.  
    	   </doc>
         </define>
         <define attribute="step" optional="optional">
           <doc> If present, this specifies the step value for the
		 iteration variable, replacing a <tag>step</tag> subelement.  
    	   </doc>
         </define>
       </define>
       <ul>
	 <li> <define element="start" parent="for">
	        <doc> The content must evaluate to a number, which is used as
		      the starting value for the iteration variable.
	        </doc>
	      </define>
         </li>
	 <li> <define element="stop" parent="for">
	        <doc> The content must evaluate to a number, which is used as
		      the final value for the iteration variable.
	        </doc>
	      </define>
         </li>
	 <li> <define element="step" parent="for">
	        <doc> The content must evaluate to a number, which is used as
		      the step value for the iteration variable.
	        </doc>
	      </define>
         </li>
       </ul>
  </li>		
  <li> <define element="while" parent="repeat" handler>
         <doc> At each iteration the content is evaluated as a
	       <em>condition</em> as in <tag>if</tag>; if the condition is
	       <code>false</code> the <tag>repeat</tag> terminates.
         </doc>
       </define>
  </li>		
  <li> <define element="until" parent="repeat" handler>
         <doc> At each iteration the content is evaluated as a
	       <em>condition</em> as in <tag>if</tag>; if the condition is
	       <code>true</code> the <tag>repeat</tag> terminates.
         </doc>
       </define>
  </li>		
  <li> <define element="first" parent="repeat" handler>
         <doc> The contents are expanded exactly once, during the first
	       iteration. 
         </doc>
       </define>
  </li>		
  <li> <define element="finally" parent="repeat" handler>
         <doc> The contents are expanded exactly once, after the last
	       iteration. 
         </doc>
       </define>
  </li>		
</ul>

<h3>Logical Elements</h3>

<doc>
The logical elements are <tag>logical</tag> and <tag>test</tag>.
</doc>

<define element="logical" handler syntax="quoted">
  <doc> This element is essentially a convenient shorthand for a nested set of
	<tag>if</tag> elements.  It performs functions that are equivalent to
	the LISP functions <code>AND</code> and <code>OR</code>.

	<p>With no attributes, this simply returns the value of every
	component that has a true value.  This can be used, for example, to
	remove whitespace from the content.</p>
  </doc>
  <define attribute="op" optional="optional">
    <doc> If present, this specifies the operation to be performed.  Values
	  accepted are <code>and</code> and <code>or</code>.  Using this
	  attribute instead of "and" or "or" will make XML code look better. 
    </doc>
  </define>
  <define attribute="and" optional="optional">
    <doc> If present, this specifies that a "logical AND" operation is
	  performed.  Each child of the <tag>logical</tag> element is expanded
	  in turn.  Declarations, comments, processing instructions, and
	  whitespace are ignored.  If <em>every</em> other child expands to
	  something <tag>if</tag> would consider a "<code>true</code>"
	  condition, the expansion of the <em>last</em> such child is passed
	  to the output.  Otherwise no output is generated.
    </doc>
  </define>
  <define attribute="or" optional="optional">
    <doc> If present, this specifies that a "logical OR operation is
	  performed.  Each child of the <tag>logical</tag> element is expanded
	  in turn.  Declarations, comments, processing instructions, and
	  whitespace are ignored.  If <em>any</em> other child expands to
	  something <tag>if</tag> would consider a "<code>true</code>"
	  condition, the expansion of the <em>that</em> child is passed
	  to the output, and expansion stops at that point.  Otherwise no
	  output is generated. 
    </doc>
  </define>
</define>

<h3>Test</h3>
<define element="test" handler>
  <doc> This element performs a test on its content.  If no attributes are
	specified, the test is the same as that performed by <tag>if</tag>.
	If the tested condition is <code>true</code>, the <tag>test</tag>
	element expands to "<code>1</code>", otherwise it "expands" to
	nothing at all.

	<p><tag>test</tag> is not, strictly speaking, a control-flow
	operation, but it is used almost exclusively inside control-flow
	operations for computing conditions.</p>

	<p>Because the expansion of a successful <tag>test</tag> is
	"<code>1</code>", <tag>test</tag> can be used for counting the
	number of items in a list that satisfy some condition. </p> 
  </doc>
  <define attribute="text" optional="optional">
    <doc> Performs the indicated test on the text portion of the content,
	  discarding the markup.  In some cases, this has no effect.
    </doc>
  </define>
  <define attribute="not" optional="optional">
    <doc> Inverts the sense of the indicated test.
    </doc>
  </define>
  <define attribute="op" optional="optional">
    <doc> Specifies the test to be performed on the content.  See individual
	  ``shortcut'' attributes for details.  Note that the tests that
	  compare the content with the value of an attribute cannot be
	  specified using <code>op</code>.
    </doc>
  </define>

  <h4>sub-tests of <code>op</code></h4>
  <define attribute="positive" optional="optional">
    <doc> Tests for the content being numerically greater than zero.
    </doc>
  </define>
  <define attribute="negative" optional="optional">
    <doc> Tests for the content being numerically less than zero.
    </doc>
  </define>
  <define attribute="zero" optional="optional">
    <doc> Tests for the content being numerically equal to zero.  Whitespace
	  is considered to be zero, but nonblank text that cannot be converted
	  to a number is not.
    </doc>
  </define>
  <define attribute="numeric" optional="optional">
    <doc> Tests for the content being convertable to a number.  Although
	  whitespace is considered to be equal to ``zero'' for many purposes,
	  it is <em>not</em> considered to be ``numeric''.  Hence
	  &lt;test op="zero"&gt; &lt;/test&gt; returns true, but 
	  &lt;test op="numeric"&gt; &lt;/test&gt; returns false. 
    </doc>
  </define>
  <define attribute="null" optional="optional">
    <doc> Tests for the content being totally empty, even of whitespace.
    </doc>
  </define>
  <define attribute="markup" optional="optional">
    <doc> Tests whether the content contains any markup, defined as anything
	  except text or CDATA sections (which are considered to be text).
    </doc>
  </define>
  <define attribute="sorted" optional="optional">
    <doc> Tests for the content being a sorted list.  Modified by the
	  <code>text</code>, <code>numeric</code>, <code>reverse</code>,
	  <code>case</code>, and of course <code>not</code> attributes.
    </doc>
  </define>
  <define attribute="numeric" optional="optional">
    <doc> If <code>sorted</code> is present, performs numeric tests for
	  sortedness rather than lexical.  Otherwise, tests for the content
	  being numeric.
    </doc>
  </define>

  <h4>other tests</h4>

  <define attribute="equals" optional="optional">
    <doc> Tests for the content being numerically equal to the value of the
          attribute "equal."  Whitespace and other non-numeric text fail
           equality.
    </doc>
  </define>
  <define attribute="greater" optional="optional">
    <doc> Tests for the content being numerically greater than the value of
	  the attribute "greater." Whitespace and other non-numeric text fail.
    </doc>
  </define>
  <define attribute="less" optional="optional">
    <doc> Tests for the content being numerically less than the value of
	  the attribute "less." Whitespace and other non-numeric text fail.
    </doc>
  </define>
  <define attribute="match" optional="optional">
    <doc> The value of the attribute is a regular expression which is matched
	  against the content, converted to a string.   
    </doc>
  </define>
  <define attribute="exact" optional="optional">
    <doc> With the <code>match</code> attribute, performs an exact match. 
    </doc>
  </define>
  <define attribute="case" optional="optional">
    <doc> With the <code>match</code> attribute, performs a case-sensitive
	  match.  
    </doc>
  </define>
  <define attribute="reverse" optional="optional">
    <doc> Tests for sortedness in reverse (largest element first) order.
    </doc>
  </define>
</define>


<h2>Document Structure Elements</h2>

<doc>
  Document structure elements extract nodes or sets of nodes from a parse tree,
  and perform structural modifications on trees.  The tree being
  operated on need not be part of the document being processed.  It might be a
  namespace or the value of an entity.  
</doc>

<h3>Extract and its Components</h3>
<define element="extract" handler>
  <doc> Extract, and optionally replace, a set of nodes.  Subelements in the
	content are processed in order of occurrence.  At any time in this
	sequence of operations, there is a <em>current set</em> of nodes that
	have been extracted.  This set is accessible as the value of the entity
	<code>&amp;extract:list;</code>.

	<p>The current set of nodes is initially empty unless the
	<tag>extract</tag> is enclosed in some outer <tag>extract</tag>, in
	which case it is that extract's current set.<!-- unimplemented --></p>
        <note>This feature is currently unimplemented.</note>

	<p>Each stage in the extraction process results in "extracting" some
	set of nodes (possibly a subset of the current set, but also possibly
	some or all of their children, siblings, etc.) and replacing the
	current set of nodes with the set of extracted nodes.  This is done by
	simply expanding each subelement and using its expansion as the next
	current set.  If the current set ever becomes empty, the process
	terminates. </p>

	<p>The result of expanding a extract is the current set of nodes
	extracted at the end of the expansion.</p>

	<p>The <tag>extract</tag> operation is intended to have a superset of
	the power of <a href="http://www.w3.org/TR/WD-xptr">XPointer</a>
	expressions.</p>
  </doc>
  <define attribute="sep" optional="optional">
    <doc> The separator to use between extracted items in the output.  The default
	 is whitespace. 
    </doc>
  </define>
  <define attribute="all" optional="optional"><!-- unimplemented -->
    <doc> Modifies the operation of <tag>extract</tag> so that each stage in
	  the extraction process uses the same current set of nodes, and
	  appends extracted nodes to the output instead of replacing the
	  current set with them.

	  <note>This attribute is currently unimplemented.</note>
    </doc>
  </define>
</define>

<h4>Subelements of <tag>extract</tag>: Starting Points</h4>
<ul>
  <li> <define element="from" parent="extract sort" handler>
         <doc> Contains a sequence of nodes.  These are the currently
		selected set expanded to its content.  Text nodes are 
		split on whitespace.
         </doc>
       </define>
  </li>
  <li> <define element="in" parent="extract sort" handler>
         <doc> Elements in the content are simply added to the initial
	       extraction, as in <tag>from</tag>.  Text is split into
	       identifiers which are looked up as entities, and the
	       corresponding entity bindings are extracted.  This permits their
	       names and values to be manipulated.
         </doc>
       </define>
  </li>       
  <li> <define element="id" parent="extract sort" text handler>
         <doc> Contains an identifier.  The element with the given
	       <code>name</code> or <code>id</code> attribute (it is supposed
	       to be unique) is extracted.

       	       <p>If the <tag>id</tag> element is the first element in the
	       <tag>extract</tag>, the element with the given ID in the entire
	       input document is <em>(supposed to be)</em> extracted.
	       <b><em>This feature is currently unimplemented!</em></b></p>
         </doc>
         <define attribute="case" optional="optional">
           <doc> Causes the name matching to be case-sensitive, even if the
		 nodes are part of a namespace that is not case-sensitive.
           </doc>
         </define>
         <define attribute="recursive" optional="optional">
           <doc> Causes the matching to descend recursively into the children
		 (content) of the current set. 
           </doc>
         </define>
         <define attribute="all" optional="optional">
           <doc> Causes all elements that match to be returned.  Even though
		 identifiers are supposed to be unique, they may not be.
           </doc>
         </define>
       </define>
  </li>		
</ul>

<h4>Subelements of <tag>extract</tag>: Extraction</h4>
<ul>
  <li> <code>text</code> can occur inside a <tag>extract</tag> element.  Text
       is split on whitespace and interpreted as follows:
       <doc>
       <ol>
	 <li> If the text is a number <em>N</em>, it extracts the
	      <em>N<sup>th</sup></em> node in the current set.  The first node
	      is zero, and negative numbers are counted from the last node.
	      </li>
	 <li> If the text starts with a pound sign (<code>#</code>), it is
	      matched as a node type.  The list of node types is defined in
	      the <a href="http://www.w3.org/TR/1998/WD-xptr-19980303">XPointer
	      specification</a>, plus locally-defined types.  In addition,
	      <code>#all</code> is defined, matching <em>any</em> node.  Type
	      matching is case-insensitive.
	      </li>
	 <li> Otherwise, it is matched as a "<em>name</em>".  The name of an
	      entity or attribute is the name it is defined to have; the name
	      of an element is its tag name.
	      </li>
       </ol>

       Text items are applied <em>sequentially</em>, so that, for example,
       <code>... li -1</code> extracts the last &lt;li&gt; element in the
       current set.
       </doc>
  </li>

  <li> <define element="name" parent="extract sort" text handler>
         <doc> Contains a name (identifier).  All nodes in the current set
	       that have the given name are extracted.  Attributes and entities
	       are matched by name; elements are matched by their tagname.
	       Text and comments are ignored.  If the name starts with a
	       pound sign (<code>#</code>) it represents a node type.

       	       <p> If the content is empty, the name of each node in the
		   extraction becomes extracted.</p>
         </doc>
         <define attribute="case" optional="optional">
           <doc> A false value (<code>no</code>, <code>false</code>,
		 <code>0</code>, or <code>""</code>) causes matching to be
		 case-insensitive; any other value (or unspecified) causes it
		 to be case-sensitive.  (Node type matching is always
		 case-insensitive.)
           </doc>
         </define>
         <define attribute="recursive" optional="optional">
           <doc> Causes the matching to descend recursively into the children
		 (i.e. the content) of the current set.  If a node (e.g. a
		 list) is extracted, its content is not further examined.
           </doc>
         </define>
         <define attribute="all" optional="optional">
           <doc> Causes the matching to descend recursively into the children
		 (content) of the current set.  Unlike <code>recursive</code>,
		 content even of extracted nodes will be further examined; this
		 will result in extracting <em>all</em> elements with the
		 matching tag.
           </doc>
         </define>
       </define>
  </li>


       
  <li> <define element="tm_like" parent="extract" text handler>
         <doc> Contains a node list (template).  All nodes in the current set
	       that contain the structure in the template are extracted.
               At present the structure template should consist of elements and
               text content.  

         </doc>
         <define attribute="nokeep" optional="optional">
           <doc> The (empty) default means nodes matching the current template
                 will be extracted; if <code>nokeep</code> is set, all nodes
                 <em>except</em> matching nodes will be extracted.
           </doc>
         </define>

         <define element="any-tag" optional="optional">
           <doc> The presence of this tag in the template-tree content of
		 tm_like requires the extraction match some tag at that level;
		 it's like  a wildcard-tag. M nested any-tag tags is equivalent
		 to insisting on the presence of any M nested tags in the list
		 to be extracted.
            </doc>
         </define>

         
        
       </define>
  </li>

  <li> <define element="sort" parent="extract sort" quoted="quoted" handler>
         <doc> Contains a name (identifier).  All nodes in the current set
	       that have the given name are sorted according to the contents
	       of nodes having that name (for instance,
	       <code>&lt;sort&gt;start-time&lt;/sort&gt;</code> would sort the
	       current list by the values in the start-time tags.
         </doc>
       <define attribute="numeric" optional="optional">
           <doc>
	       Sorts based on the "numeric" value of the tags' contents,
	       which still uses text but interprets numbers as numbers
	       rather than as character strings.
           </doc>
         </define>
       <define attribute="floatonly" optional="optional">
           <doc>Sort based on the floating-point interpretation
	        of the tags' contents.
           </doc>
         </define>
       <define attribute="intonly" optional="optional">
           <doc>Sort based on the integral interpretation
	        of the tags' contents if possible.
           </doc>
         </define>
         <define attribute="reverse" optional="optional">
           <doc>Sorts in reverse order.
           </doc>
         </define>
	      
       </define>
  </li>

	      
  <li> <define element="key" parent="extract sort" text handler>
         <doc> Contains a "key" (string) which is matched against nodes in the
	       current set.  Attributes and entities are matched when their
	       values match the content of the key; elements are matched when
	       the text in their content matches the content of the key.  Text
	       nodes are matched when their content matches the content of the
	       key.
         </doc>
         <define attribute="case" optional="optional">
           <doc> Causes the name matching to be case-sensitive, even if the
		 nodes are part of a namespace that is not case-sensitive.
           </doc>
         </define>
         <define attribute="sep" optional="optional">
           <doc> Contains a delimiter string that marks the end of the key
		 in a node's text.  If specified, nodes that lack the
		 delimiter are ignored.
           </doc>
         </define>
         <define attribute="recursive" optional="optional">
           <doc> Causes the matching to descend recursively into the children
		 (content) of the current set.  Note that if a node (e.g. a
		 list) is extracted, its content is not examined further.
           </doc>
         </define>
         <define attribute="all" optional="optional">
           <doc> Causes the matching to descend recursively into the children
		 (content) of the current set.  Unlike <code>recursive</code>,
		 content even of extracted nodes is examined further.  The
		 result is that <em>all</em> elements with the matching tag
		are extracted.
           </doc>
         </define>
       </define>
  </li>       
  <li> <define element="match" parent="extract sort" handler>
         <doc> Contains a <em>regular expression</em> which is matched against
	       nodes in the current set.  Attributes and entities are matched
	       by name; elements and text are converted to strings.
         </doc>
         <define attribute="case" optional="optional">
         </define>
         <define attribute="text" optional="optional"><!-- unimplemented -->
           <doc> If present, elements are matched using only the text in their
		 content.

	         <note>This attribute is currently unimplemented.</note>
           </doc>
         </define>
       </define>
  </li>       
  <li> <define element="child" parent="extract sort" handler>
         <doc> The content is a list of strings that are treated like the text
	       items in <tag>extract</tag>.  Each designated item is extracted
	       from the children of each node in the current set.  Thus,
	       <tag>child</tag>0 -1<tag>/child</tag> extracts the first and
	       last children of each node in the current set.

       	       <p>Note that <tag>child</tag> only contains text terms, with
       	       none of the subelements permitted in <tag>extract</tag>.  This
       	       is to allow the text terms to be computed (for example,
       	       computing a name or index), which is more useful.  The more
       	       general operation can be done using an embedded
       	       <tag>extract&nbsp;all</tag>.  </p>
         </doc>
         <note author="steve"> XPointer uses
           <code>child(<em>n, tag, attr, value</em>)</code> <em>n</em> can be
           <code>all</code>; <em>tag</em> can be <code>#<em>type</em></code>.
           Xpointer also has <code>descendent</code>, <code>ancestor</code>,
           <code>string</code>, and <code>span</code>.
         </note>
       </define>
  </li>       
  <li> <define element="attr" parent="extract sort" handler>
         <doc> Contains an attribute name; extracts the named attribute of each
	       Element in the current set, and every Attribute node in the
	       current set matching the given name.  This tag will evaluate to
	       a name-value pair ('att-name="att-value"'); if you want just
	       the value, follow it by the tag <tag>eval</tag> tag.
         </doc>
         <define attribute="case" optional="optional">
         </define>
       </define>
  </li>       
  <li> <define element="has-attr" parent="extract sort" handler="hasAttrHandler">
         <doc> Contains an attribute name; extracts every Element in the
	       current set for which that attribute has a non-null value.
         </doc>
         <define attribute="value" optional="optional">
           <doc> If present, selects only elements for which the named
		 attribute is an exact match for the given value.
           </doc>
         </define>
       </define>
  </li>       
  <li> <define element="nodes" parent="extract sort" handler>
         <doc> The content evaluates to a list of strings that are treated
	       like the text items in <tag>extract</tag>.  Each designated
	       item is extracted from the current set.  Thus, <tag>nodes</tag>0
	       -1<tag>/nodes</tag> extracts the first and last nodes in the
	       current set.
         </doc>
       </define>
  </li>       
  <li> <undefine element="xptr" parent="extract sort" handler><!-- unimplemented -->
         <doc> Contains an XPointer expression and extracts the corresponding
	       nodes. See <a
	       href="http://www.w3.org/TR/WD-xptr">www.w3.org/TR/WD-xptr</a>
         </doc>
       </undefine>
  </li>       
  <li> <define element="eval" parent="extract sort" syntax="empty" handler>
         <doc> Evaluates each extracted node, i.e. replaces it by its value.
	       (We use <code>eval</code> for this because <code>value</code>
	       is already defined with a different syntax.)  Text and passive
	       markup evaluate to themselves.

       	       <p>We expect that <tag>dt</tag> nodes and the like will know
       	       how to find their corresponding values.</p>
         </doc>
       </define>
  </li>       
  <li> <define element="content" parent="extract sort" syntax="empty" handler>
         <doc> Replaces each extracted node with its content.
         </doc>
       </define>
  </li>       
  <li> <define element="parent" parent="extract sort" syntax="empty" handler>
         <doc> Extracts the parent of each node in the current set.
         </doc>
       </define>
  </li>       
  <li> <define element="next" parent="extract sort" syntax="empty" handler>
         <doc> Extracts the next node after each node in the current set.
	       Ignorable whitespace is skipped.
         </doc>
       </define>
  </li>       
  <li> <define element="prev" parent="extract sort" syntax="empty" handler>
         <doc>Extracts the node previous to each node in the current set.
	       Ignorable whitespace is skipped.
         </doc>
       </define>
  </li>       
</ul>

<note author="steve">
  Wanted:
  <ol>
    <li> A way of extracting on the basis of an arbitrary test.
	 <tag>repeat</tag> might do for that, too, but it's clumsy.
	 Possibly &lt;extract-each&gt;
    </li>       
    <li> A way of performing <em>multiple</em> extractions, i.e. extracting
	 everything that matches A <em>or</em> B, or of extracting the first
	 three children of each node.  Possibly &lt;extract-all&gt;
    </li>       
  </ol>
</note>

<h4>Subelements of <tag>extract</tag>: Replacement</h4>
<ul>
  <li> <define element="replace" parent="extract sort" handler>
         <doc> Contains a list of nodes.  The default action is for the entire
	       list to replace the current content of each extracted element,
	       and the value of each extracted entity or attribute.  (In most
	       cases only one node is extracted.)
         </doc>
         <define attribute="name" optional="optional">
           <doc> The content of the <tag>replace</tag> element replaces the
		 value of the named attribute of each extracted element, and
		 the value of each extracted attribute or entity with a
		 matching name.

             <p> <b>Note</b> that there is no way to change the <em>name</em>
		 of a node; this is deliberate.  Named nodes are used in hash
		 tables, which would be rendered unusable if nodes could be
		 renamed. </p>
           </doc>
         </define>
         <define attribute="case" optional="optional">
           <doc> Name matching is done on a case-sensitive basis.
           </doc>
         </define>
         <define attribute="node" optional="optional">
           <doc> The whole node is replaced rather than the contents. 
           </doc>
         </define>
         <undefine attribute="each" optional="optional"><!-- unimplemented -->
           <doc> The content of the <tag>replace</tag> element is split into a
		 list of nodes, each of which replaces a corresponding
		 extracted item.
           </doc>
         </undefine>
       </define>
  </li>       
  <li> <define element="append" parent="extract sort" handler>
         <doc> Appends the contents to the list of extracted nodes.  The
	       content nodes become the right siblings of the last extracted
	       node. 
         </doc>
         <define attribute="children" optional="optional">
           <doc> Appends to the children (content) of <em>each</em> extracted
		 node. 
           </doc>
         </define>
         <note author="steve"> This will not work for most cases in the current
         	 system; it will become possible only after named nodes become
         	 the children of their respective namespaces, and values
         	 become the children of their containers.
         </note>
       </define>
  </li>       
  <li> <define element="insert" parent="extract sort" handler>
         <doc> Inserts its content into the content of every selected element.
         </doc>
         <define attribute="child" optional="optional">
           <doc> Designates the child to insert <em>before</em>.  -1 (the
		 default), specifies inserting at the end (appending); -2
		 specifies inserting before the last child; 0 specifies
		 inserting before the first child.
           </doc>
         </define>
       </define>
  </li>       
  <li> <define element="remove" parent="extract sort" syntax="empty" handler>
         <doc> Removes each extracted node from its parent.
         </doc>
         <note author="steve"> This will not work for most cases in the current
         	 system; it will become possible only after named nodes become
         	 the children of their respective namespaces, and values
         	 become the children of their containers.
         </note>
       </define>
  </li>       
  <li> <define element="unique" parent="extract sort" syntax="empty" handler>
         <doc> Removes duplicate text nodes from the extracted set.
         </doc>
         <note author="pgage"> Probably doesn't work.
         </note>
       </define>
  </li>       
</ul>

<h2>Expansion Control Elements</h2>

<doc>
  Expansion control elements modify the processing of their contents, but are
  not conditional in the same way that control-structure operations are.  No
  tests are performed.
</doc>

<h3>Expand</h3>
<define element="expand" handler>
  <doc> The content is actually expanded (processed) <em>twice</em>.  This is
	done in order to compute some active content and then expand it as if
	it had been in the input document.
  </doc>
  <define attribute="hide" optional="optional">
    <doc> The content is processed as usual, but the results are not passed to
	the output.  The effect is to process the content for side-effects
	only. 
    </doc>
  </define>
</define>

<h3>Protect</h3>
<define element="protect" handler syntax="quoted">
  <doc> The content is not expanded (unless the <code>result</code> attribute
	is present), but passed to the output as-is. 
  </doc>
  <define attribute="result" optional="optional">
    <doc> The content is expanded (once); this makes <tag>protect</tag> a
	  no-op unless the <code>markup</code> attribute is present.
    </doc>
  </define>
  <define attribute="markup" optional="optional">
    <doc> Markup in the content is "protected" from further expansion by
	  converting it to text, replacing markup-specific characters with the
	  corresponding entity references.
    </doc>
  </define>
</define>

<h3>Hide</h3>
<define element="hide" handler>
  <doc> The content is processed as usual, but the results are not passed to
	the output.  The effect is to process the content for side-effects
	only. 
  </doc>
  <define attribute="markup" optional="optional">
    <doc> Only markup in the content is "hidden" -- text is passed through
	  to the output.  References to passive entities (including character
	  entities) are expanded.
    </doc>
  </define>
  <define attribute="text" optional="optional">
    <doc> Only text in the content is "hidden" -- markup is passed through
	  to the output.  References to passive entities (including character
	  entities) are expanded.
    </doc>
  </define>
</define>

<h3>Debug</h3>
<define element="debug" handler>
  <doc> Prints all children of the current node as an indented tree.
  </doc>
</define>


<h2>Data Manipulation Elements</h2>

<doc>
  Data manipulation elements perform operations on data, typically text, that
  depend on some non-structural features of its content (e.g. its value as a
  number).
</doc>

<h3>Numeric operations</h3>
<define element="numeric" handler>
  <doc> All passive markup in the content is ignored in most cases; the
	content is taken to be a sequence of numbers <em>n<sub>0</sub>,
	n<sub>1</sub>, ...</em> separated by whitespace.  Elements are
	represented by their first non-whitespace text.  If no operation is
	specified in the attributes, this sequence is returned as a
	space-separated list of text nodes.  The <code>base</code>,
	<code>modulus</code>, and <code>digits</code> attributes are
	recognized. 
  </doc>
  <define attribute="op" optional="optional">
    <doc> Specifies the operation to be performed.  This reads better in XML
	  than using the name of the operation as an attribute.  The
	  operations supported are:
	  <ol>
	    <li> <code>sum</code>	</li>
	    <li> <code>diff</code> or <code>difference</code>	</li>
	    <li> <code>prod</code> or <code>product</code>	</li>
	    <li> <code>quot</code> or <code>quotient</code>	</li>
	    <li> <code>rem</code> or <code>remainder</code>	</li>
	    <li> <code>sort</code>	</li>
	  </ol>
    </doc>
  </define>
  <define attribute="sum" optional="optional">
    <doc> The numbers in the content are added.
    </doc>
  </define>
  <define attribute="difference" optional="optional" handler>
    <doc> The difference  <em>n<sub>0</sub> - n<sub>1</sub> - ...</em> is
	  computed. 
    </doc>
  </define>
  <define attribute="product" optional="optional" handler>
    <doc> The numbers in the content are multiplied.
    </doc>
  </define>
  <define attribute="quotient" optional="optional" handler>
    <doc> The quotient <em>n<sub>0</sub> / n<sub>1</sub> / ...</em> is
	  computed. 
    </doc>
  </define>
  <define attribute="remainder" optional="optional" handler>
    <doc> The remainder <em>n<sub>0</sub> % n<sub>1</sub> % ...</em> is
	  computed.  
    </doc>
  </define>
  <define attribute="power" optional="optional" handler>
    <doc> The power <em>n<sub>0</sub> ^ n<sub>1</sub> ^ ...</em> is
	  computed. 
    </doc>
  </define>
  <define attribute="sort" optional="optional" handler>
    <doc> The content is taken to be a list of items each of which must
	  contain a numeric value in its text. 
    </doc>
  </define>
  <define attribute="reverse" optional="optional">
    <doc> Causes a sort to be done in reverse order.
    </doc>
  </define>
  <define attribute="pairs" optional="optional">
    <doc> Sorts a list of key and value pairs according to the keys.
	  Typically this is used for the contents of <code>&lt;dl&gt;</code>
	  lists, in which the <code>&lt;dt&gt;</code> and
	  <code>&lt;dd&gt;</code> elements follow one another rather than
	  being hierarchical as they are in a table.
    </doc>
  </define>
  <define attribute="sep" optional="optional">
    <doc> Specifies the separator to be used between sorted or split (no
	  operation specified) numbers.
    </doc>
  </define>
  <define attribute="digits" optional="optional">
    <doc> The value is the number of digits to the right of the decimal point
	  to preserve in the output.  The default is zero if the result is an
	  integer, the maximum possible otherwise.
    </doc>
  </define>
  <define attribute="base" optional="optional">
    <doc> The value is the base (radix) by which the result is converted.
	  Only applies to integer operations.  Default is 10.
    </doc>
  </define>
  <define attribute="pad" optional="optional">
    <doc> The value of the attribute is a width.  If the result of the
	  computation is an integer, it is padded on the left with zeros to at
	  least that width.
    </doc>
  </define>
  <define attribute="integer" optional="optional">
    <doc> All computation is done with (64-bit, signed) integer arithmetic.
	  Results of division operations are truncated.
    </doc>
  </define>
  <define attribute="extended" optional="optional">
    <doc> All computation is done with extended-precision integer arithmetic.  
    </doc>
  </define>
  <define attribute="modulus" optional="optional">
    <doc> All computation is done with modular arithmetic with the specified
	  modulus.  Combined with extended-precision, this can be used for
	  cryptographic calculations.
    </doc>
  </define>
</define>

<h3>Text operations</h3>
<define element="text" handler>
  <doc> <p>Passive markup in the content is ignored in most cases, but preserved
	in the output when possible.  For sorting purposes, the content is
	taken to be a sequence of text nodes separated by whitespace.
	Elements are represented by their first text content.  If no operation
	is specified in the attributes, this sequence is returned as a
	space-separated list of Text nodes.</p>
  </doc>
  <define attribute="op" optional="optional">
    <doc> Specifies the operation to be performed.  One of the following:
	  <code>sort</code>, <code>trim</code>, <code>pad</code>,
	  <code>split</code>, <code>join</code>, <code>encode</code>,
	  <code>decode</code>. 
    </doc>
  </define>
  <define attribute="pad" optional="optional" handler>
    <doc> Pad the text to the specified <code>width</code> with the specified
	  <code>align</code>ment (default is <code>left</code>).
    </doc>
  </define>
  <define attribute="trim" optional="optional" handler>
    <doc> Trim the text to the specified <code>width</code> with the specified
	  <code>align</code>ment.   If no <code>align</code>ment or 
	  <code>width</code> is specified, leading and trailing whitespace are
	  trimmed.  (It is possible that <code>align</code> and
	  <code>width</code> may not be implemented or may be implemented only
	  for pure text; they are difficult to specify in the presence of
	  markup.) 
    </doc>
  </define>
  <define attribute="width" optional="optional">
    <doc> Specifies a width to pad or trim to.  If neither <code>pad</code>
	  nor <code>trim</code> is specified, padding or trimming is done as
	  needed. 
    </doc>
  </define>
  <define attribute="align" optional="optional">
    <doc> Specifies alignment when padding.  Permissible values are
	  <code>left</code>, <code>right</code>, <code>center</code>
    </doc>
  </define>
  <define attribute="sort" optional="optional" handler>
    <doc> The list of items in the content is sorted according to their text
	  content.  Markup is ignored for sorting, but retained for output
	  unless the <code>text</code> attribute is also present.
    </doc>
  </define>
  <define attribute="reverse" optional="optional">
    <doc> Causes a sort to be done in reverse order.
    </doc>
  </define>
  <define attribute="pairs" optional="optional">
    <doc> Sorts a list of key and value pairs according to the keys.
	  Typically this is used for the contents of <code>&lt;dl&gt;</code>
	  lists, in which the <code>&lt;dt&gt;</code> and
	  <code>&lt;dd&gt;</code> elements follow one another rather than
	  being hierarchical as they are in a table.
    </doc>
  </define>
  <define attribute="sep" optional="optional">
    <doc> Specifies the separator to be inserted between items in the result
	  of a <code>sort</code> or <code>split</code>.  If not specified, no
	  separator is inserted and the items are simply jammed together, but
	  remain separate nodes.  This is usually a bad idea unless the
	  <tag>text</tag> element is enclosed in something else, like a
	  <tag>foreach</tag> or <tag>extract</tag>.
    </doc>
  </define>
  <define attribute="split" handler optional="optional">
    <doc> Splits the text into "tokens" (words) on whitespace and element
	  boundaries.  With the <code>sep</code> attribute present, the
	  separator is inserted between each token in the result.  The tokens
	  and the separators remain distinct Text nodes unless the
	  <code>join</code> attribute is also present.
    </doc>
  </define>
  <define attribute="join" handler optional="optional">
    <doc> Joins items in the content by merging adjacent Text nodes.
    </doc>
  </define>
  <define attribute="encode" handler optional="optional">
    <doc>  Encodes text as one of url, base64, or entity. To add a different encoding, see <a href='../../../API/javadoc/org.risource.dps.handle.text_encode.html'><em>How to extend the encode handler</em></a>.
    </doc>
  </define>
  <define attribute="decode" handler optional="optional">
    <doc>  Decodes text that has been encoded as one of url, base64, or entity. To add different decoding, see <a href='../../../API/javadoc/org.risource.dps.handle.text_decode.html'><em>How to extend the decode handler</em></a>.
    </doc>
  </define>

</define>

<note author="steve">
  At some point we'll want to deal with <code>measure</code> and font
  metrics.
</note>

<h3>Subst</h3>
<define element="subst" handler>
  <doc> Performs a text substitution on the content (after first removing any
	<tag>match</tag> and <tag>result</tag> elements). 
  </doc>
  <define attribute="match" optional="optional">
    <doc> Contains the regular expression to match.
    </doc>
  </define>
  <define attribute="result" optional="optional">
    <doc> Contains the replacement text.
    </doc>
  </define>
</define>

<!--
<h4>Subelements of <tag>subst</tag></h4>
<ul>
  <li> <undefine element="match" parent="subst" handler>
         <doc> Contains the regular expression to match against.
         </doc>
       </undefine>
  </li>       
  <li> <undefine element="result" parent="subst" handler>
         <doc> Contains the replacement text.
         </doc>
       </undefine>
  </li>       
</ul>
-->

<h3>Parse</h3>
<define element="parse" handler>
  <doc> Converts the content to a single string.  Character and other
	entities are expanded.  Runs of consecutive whitespace characters are
	converted to single spaces, and leading and trailing whitespace are
	trimmed. 

	<p>If no other operation is specified in an attribute, the text is
	parsed using the current tagset.</p>

	<p>In general the result of parsing the text is a (possibly empty)
	sequence of name="value" pairs, plus a (possibly empty) "remainder left
	over.  (For example, the <code>entities</code> format returns no
	pairs, and the <code>query</code> format returns no remainder.)</p>

	<p>In some tagsets it might be reasonable to rename this operation as
	<tag>decode</tag>, or to make it an attribute of <tag>text</tag>.</p>

	<p>The set of attributes used in <tag>parse</tag> and
	<tag>to-text</tag> is open-ended; tagset authors are free to define
	new ones as needed. </p>
  </doc>
  <define attribute="usenet" optional="optional">
    <doc> Markup is added according to the conventions of Usenet mail and news
	  articles:  <code>_<i>italics</i>_</code>,
	  <code>*<b>bold</b>*</code>, and so on, similar to the legacy
	  <code>&lt;add-markup&gt;</code> tag.  Runs of multiple uppercase
	  letters are converted to lower case and set monospaced.  Values
	  after an equals sign are italicized.  Markup elements in the
	  original content are ignored.
    </doc>
  </define>
  <undefine attribute="header" optional="optional">
    <doc> The input text is parsed as a mail or HTTP (MIME) header. 
    </doc>
  </undefine>
  <undefine attribute="entities" optional="optional">
    <doc> Special characters are replaced by their corresponding entities, but
	  no other parsing is done.
    </doc>
  </undefine>
  <undefine attribute="url" optional="optional">
    <doc> The text is parsed as a URL.  Characters escaped by
	  <code>%<em>nn</em></code> are decoded. 
    </doc>
  </undefine>
  <define attribute="query" optional="optional">
    <doc> The text is parsed as a query string.  Characters escaped by
	  <code>%<em>nn</em></code> are decoded. 
    </doc>
  </define>

  <h4>Format specifiers</h4>
  <doc> The various format specifier attributes specify the output format of
	the <tag>parse</tag> element, and the corresponding input format of
	the <tag>to-text</tag> element.  The latter, however, is usually able
	to guess the input format.
  </doc>

  <define attribute="element" optional="optional">
    <doc> The result is produced as a single element, with attribute-value
	  pairs in its attributes.  The content of the resulting element is
	  any component of the input "left over" after the parsing
	  process, i.e. the body of a message parsed with the
	  <code>header</code> format.  The value of this attribute is the
	  tagname of the element. 
    </doc>
  </define>
  <define attribute="elements" optional="optional">
    <doc> The result is produced as a sequence of elements, with attribute-value
	  pairs in its attributes.  The value of this attribute is the tagname
	  of the element to be used; the name is the value of the
	  <code>name</code> attribute  unless the <code>attr</code> attribute
	  is present.
    </doc>
  </define>
  <define attribute="attr" optional="optional">
    <doc> Specifies the name of the attribute to be used for names, if the
	  <code>elements</code> format is specified.
    </doc>
  </define>
  <define attribute="pairs" optional="optional">
    <doc> The value of this attribute is a space-separated list of 
	  two element tagnames.  The first is the element that is used
	  for names, the second the element that is used for values.  The
	  default value is <code>"dt dd"</code>.
    </doc>
  </define>
</define>

<h3>To-text</h3>
<undefine element="to-text" handler>
  <doc> Converts the marked-up content into one or more Text nodes (strings).
	If no other operation is specified in an attribute, the marked-up
	content is simply converted to its external representation.

	<p>In some tagsets it might be reasonable to rename this operation as
	<tag>encode</tag>, or to make it an attribute of <tag>text</tag>.</p>
  </doc>
  <define attribute="entities" optional="optional">
    <doc> <em>All</em> defined entities in the content are expanded.
    </doc>
  </define>
  <define attribute="url" optional="optional">
    <doc> The content is rendered as a URL (including URL-encoding and entity
	  encoding).
    </doc>
  </define>
  <define attribute="query" optional="optional">
    <doc> The text is rendered as a query string, including URL-encoding.
    </doc>
  </define>
</undefine>

<h2>External Resources</h2>

<doc>
  External "resources" include both documents local to the system on which
  the document processor resides (i.e. files), and remote resources (specified
  with complete URLs).
</doc>

<h3>Include</h3>
<define element="include" handler syntax="quoted">
  <doc> Request a remote or local resource, and insert its content into the
	input stream just as if it had been defined as an external entity and
	then referenced.  Essentially a convenience function that replaces a
	<tag>define entity</tag> followed by an entity reference enclosed in a
	<tag>process</tag> element.

	<p>If the requested resource does not exist, expand the contents of
	the <tag>include</tag> element and insert the result.</p>
  </doc>
  <define attribute="src" required>
    <doc> Specifies the URL (or path relative to the current document or its
	  server's document root) of the document to be included.
    </doc>
  </define>
  <define attribute="tagset" optional="optional">
    <doc> The tagset with which to process the result of the request.  The
	  default is the current tagset.  An <em>empty</em> value results in
	  the document being included as a single Text node.
    </doc>
  </define>
  <define attribute="entity" optional="optional">
    <doc> Specifies the name of an entity to be defined, effectively caching
	  the resource.  If not specified, no entity is defined and the
	  resource is not cached.  If the entity is already defined, that
	  definition is used.
    </doc>
  </define>
  <define attribute="quoted" boolean optional="optional">
    <doc> If present, specifies that the included document is parsed but
	  not processed.  This is useful if, for example, the included
	  document needs to access entities in the main document's namespace.
    </doc>
  </define>
</define>

<h3>Output</h3>
<define element="output" handler >
  <doc> Output the content to a remote or local resource.  Essentially a
	convenience function that replaces a suitable <tag>connect</tag>,
	except that the resulting document, if any, is not returned.  This
	makes it more suitable for use with files than with URLs.
  </doc>
  <define attribute="dst" required>
    <doc> Specifies the URL (or path relative to the current document or its
	  server's document root) of the document to be output to.
    </doc>
  </define>
  <define attribute="append" optional="optional">
    <doc> If true, the document is appended to (using a POST request if it is
	  a URL).
    </doc>
  </define>
  <define attribute="directory" boolean optional="optional">
    <doc> If present, the <code>dst</code> attribute names a directory which
	  is created (along with any missing parents) if it does not exist.
    </doc>
  </define>
</define>

<h3>Connect</h3>
<define element="connect" handler >
  <doc> Perform an HTTP request to connect to a remote or local resource.  The
	content of the element becomes the data content of a <code>PUT</code>
	(write) or <code>POST</code> (append) request.  The result is the
	document returned from the request, <em>as a <tag>document</tag>
	element</em>.

	<p>If a <tag>URL</tag> and/or <tag>headers</tag> element appear before
	any nonblank content, they are used for the connection.</p>
  </doc>
  <define attribute="method" optional="optional">
    <doc> The request "method".  Default is <code>GET</code>.  Any valid HTTP
	  request method is allowed.  When operating on a local (file)
	  resource, <code>POST</code> specifies an "append" operation, while
	  <code>PUT</code> specifies a "write".
    </doc>
  </define>
  <define attribute="src" optional="optional">
    <doc> The URL of the resource to which the connection is being made.
    </doc>
  </define>
  <define attribute="mode" optional="optional">
    <doc> This specifies the read-write status of an external entity: one of
	  <code>read</code> (default), <code>write</code>, 
	  <code>create</code>, <code>update</code>, or <code>append</code>.
    </doc>
  </define>
  <define attribute="tagset" optional="optional">
    <doc> The tagset with which to process the result of the request.  The
	  default is the current tagset.  An <em>empty</em> value results in
	  the entire document being read as a single text node.
    </doc>
  </define>
  <define attribute="entity" optional="optional">
    <doc> Specifies the name of an entity to be defined, effectively caching
	  the resource.  If not specified, no entity is defined and the
	  resource is not cached.  If the entity has already been defined
	  as an external entity, the <code>src</code> attribute may be omitted.
    </doc>
  </define>
  <define attribute="result" optional="optional">
    <doc> Specifies the result to be returned:
	  <dl>
	    <dt> <code>content</code> (default)</dt>
	    <dd> The content of the document is returned.</dd> 
	    <dt> <code>status</code></dt>
	    <dd> an attribute list identical to that returned by the
		 <tag>status</tag> operation.  A connection is established, if
		 possible.  The content of the connected resource can be found
		 in the entity specified by the <code>entity</code>
		 attribute. </dd>
	    <dt> <code>document</code></dt>
	    <dd> The entire document is returned as a <tag>DOCUMENT</tag>
		 element. 
	    <dt> <code>none</code></dt>
	    <dd> Nothing is returned. </dd>
	  </dl> 
    </doc>
  </define>
</define>


<h3>Status</h3>
<define element="status" handler syntax="quoted">
  <doc> Query the status of a connection or resource.  The result is an
	<em>attribute list</em>.  It can be assigned to an entity, which can
	then be used as a namespace.  For example,
	<pre>
	  &lt;set name="foo"&gt;&lt;status src="bar.html" /&gt;&lt;/&gt;
	  &amp;foo:exists;
	</pre>
	A partial list of attributes includes: <code>exists, path, host-name,
	last-modified, content-type, ...</code> In addition, files may have
	<code>readable, writeable, directory</code> and directories have
	<code>files</code>. 
  </doc>
  <define attribute="src" optional="optional">
    <doc> The URL of the resource being queried.  If the resource is local,
	  the file system is queried.  If remote, only information that can be
	  obtained from the URL is returned -- getting more information
	  requires a connection.
    </doc>
  </define>
  <define attribute="entity" optional="optional">
    <doc> Specifies the name of an entity that has been either defined as an
	  external entity, or created as a connection handle by a
	  <tag>connect</tag> or <tag>include</tag> tag.
    </doc>
  </define>
  <define attribute="item" optional="optional">
    <doc> If present, only the value of the specified name in the status
	  namespace is returned. 
    </doc>
  </define>
</define>


<h2>Data Structure Elements</h2>

<doc>
  Data structure elements perform no operations.  They represent
  common forms of complex structured data.  Strictly speaking,
  <code><tag>tagset</tag></code> and <code><tag>namespace</tag></code> are
  data structure elements.  Often a data structure element has a
  representation that is a <em>subclass</em> of the representation of an
  ordinary element.  (Currently
  <code>org.risource.dps.active.ParseTreeElement</code>). 
</doc>

<h3>DOCUMENT and its Components</h3>
<define element="DOCUMENT">
  <doc> Corresponds to a DOM <code>Document</code> object.  The attributes
	correspond to the data present in the first line of the response
	returned from an HTTP request.  The headers are the first element in
	the content. 
  </doc>
  <define attribute="protocol" optional="optional">
    <doc> The protocol is typically HTTP.  If the document
	  corresponds to a file, is <code>file</code>.
    </doc>
  </define>
  <define attribute="version" optional="optional">
    <doc> The protocol version.
    </doc>
  </define>
  <define attribute="code" optional="optional">
    <doc> The result code returned from the HTTP request. 
    </doc>
  </define>
  <define attribute="message" optional="optional">
    <doc> The message corresponding to the result code.
    </doc>
  </define>
</define>


<h3>HEADERS and its Components</h3>
<define element="HEADERS" handler="headersHandler">
  <doc> Corresponds to a standard set of e-mail headers consisting of
	<code><em>name</em>: <em>value</em></code> pairs.  Each pair in its
	content is a separate <tag>header</tag> element.  Lines can be
	extracted using the <tag>id</tag> subelement of <tag>extract</tag>.

	<p>This is an active element.  If the content is initially a text node
	in <code><em>name</em>: <em>value</em><b>\n</b></code> format, it is
	converted.</p>
  </doc>
  <define attribute="element" optional="optional">
    <doc> If present, the element is left as an element; otherwise it
	  is converted to text when output or converted to a string.
    </doc>
  </define>
</define>

<define element="header" parent="headers">
  <doc> Corresponds to a standard e-mail header line containing a
	<code><em>name</em>:<em>value</em></code> pair.  The name is contained
	in the <code>name</code> attribute; the value is the content.
  </doc>
  <define attribute="name" required>
    <doc> The value of this attribute is the name (key) portion of the header
	  line. 
    </doc>
  </define>

</define>


<h3>Query</h3>
<define element="QUERY" handler="queryHandler">
  <doc> Corresponds to a URL-converted query, consisting of
	<code><em>name</em>=<em>value</em></code> pairs.  Each pair in its
	content is a separate Attribute node.

	<p>This is an active element.  If the content is initially a text node
	in <code><em>name</em>=<em>value</em><b>\n</b></code> format, a form,
	or a description list, it is converted.</p>
  </doc>
  <define attribute="element" optional="optional">
    <doc> If present, the query is left as an element; otherwise it
	  is converted to text when output or converted to a string.
    </doc>
  </define>
</define>

<h3>URL and its Components</h3>

<define element="URL" handler="urlHandler">
  <doc> Represents a URL or, more generally, a URI.  When expanded, its
	content and attributes are "synchronized" so that all attributes
	corresponding to portions of the complete URL are set correctly, and
	the content is replaced by a text node containing the external
	representation of the complete URL.

	<p>URL decoding is done when setting the attributes, and encoding is
	done when setting the content, so that <code>%</code>-escaped
	characters in the content of the <tag>URL</tag> element are
	represented in the attributes by their corresponding actual
	characters.</p>
  </doc>
  <define attribute="protocol" optional="optional">
    <doc> The protocol (scheme) portion of the URL.
    </doc>
  </define>
  <define attribute="host" optional="optional">
    <doc> The host name portion of the URL.  Note that not all protocols have
	  a host portion (for example, <code>mailto:</code> and
	  <code>file:</code>).
    </doc>
  </define>
  <define attribute="port" optional="optional">
    <doc> The port name of the URL.
    </doc>
  </define>
  <define attribute="path" optional="optional">
    <doc> The path portion of the URL.
    </doc>
  </define>
  <define attribute="reference" optional="optional">
    <doc> The reference (fragment) portion of the URL.
    </doc>
  </define>
  <define attribute="query" optional="optional">
    <doc> The query portion of the URL.
    </doc>
  </define>
</define>


<!-- template 
<h3>Xxx and its components</h3>
<define element="xxx" >
  <doc>
  </doc>
  <define attribute= optional="optional">
    <doc> 
    </doc>
  </define>
</define>

<h4>Subelements of <tag>xxx</tag></h4>
<ul>
  <li> <define element="yyy" parent="xxx" handler>
         <doc>
         </doc>
       </define>
  </li>       
</ul>
</template -->

</tagset>



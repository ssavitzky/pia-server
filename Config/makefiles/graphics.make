### graphics.make
# $Id: graphics.make,v 1.4 2001-01-11 23:36:42 steve Exp $

############################################################################## 
 # The contents of this file are subject to the Ricoh Source Code Public
 # License Version 1.0 (the "License"); you may not use this file except in
 # compliance with the License.  You may obtain a copy of the License at
 # http://www.risource.org/RPL
 #
 # Software distributed under the License is distributed on an "AS IS" basis,
 # WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 # for the specific language governing rights and limitations under the
 # License.
 #
 # This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 # created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 # Rights Reserved.
 #
 # Contributor(s): steve@rii.ricoh.com
 #
############################################################################## 

########################################################################
#
# This makefile contains rules for graphics format conversion.
#
########################################################################

.SUFFIXES: .tex .dvi .html .idx .ps .xbm .fig .eps .gif .mif .fm .ppm


### .fig (xfig) to various output formats.
#	Works even with GNU ghostscript, which doesn't have GIF drivers.

.fig.gif:	 
	fig2dev -Lps -P $< \
	| gs -q -dNOPAUSE -sDEVICE=ppmraw -sOutputFile=- - \
	| pnmcrop | ppmtogif -transparent white > $@

.fig.ps:	 
	fig2dev -Lps -P $< > $@

.fig.eps:	 
	fig2dev -Lps $<  > $@

### Other image format conversions 

.ps.gif:	 
	gs -q -dNOPAUSE -sDEVICE=ppmraw -sOutputFile=- $< </dev/null\
	| pnmcrop | ppmtogif -transparent white > $@

.ppm.gif:	 
	ppmtogif -transparent white $< > $@

.gif.eps:
	giftopnm $< | pnmtops -noturn -rle -nocenter > $@


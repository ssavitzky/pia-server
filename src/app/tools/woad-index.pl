#!/usr/bin/perl
#	$Id: woad-index.pl,v 1.18 2000-11-22 22:58:25 steve Exp $
# Create WOAD index files.
#

### Notes: 
#
# See /usr/local/lxr for how to tie a database file to a hash:
# 	reading: /usr/local/lxr/http/lib/LXR/Common.pm
#	writing: /usr/local/lxr/bin/genxref
#
# 	This would be used to reduce memory and disk requirements for the
#	identifier usage cross-ref.  The wrapper in woad-index.ts would have
#	to invoke it, of course.  Simply map identifier -> space-separated
#	list of path#line's.
#
# Internally, using an array of ref's for use would save much space.

### usage() -- print usage summary

sub usage {
    print "$0 [options] [parameters] [<dir>]\n";
    print "  options:\n";
    print "	-l		local (no recursion)\n";
    print "	-v		Print version string and exit\n";
    print "	-q		Quiet\n";
    print "	-n		index only the notes (AllNotesByTime)\n";
    print "	-x		also produce cross-reference index\n";
    print "	-y		xref definitions only (faster)\n";
    print "	-root <dir>	Woad annotations (default ~/.woad)\n";
    print "  parameters: \n";
    print "	source=<dir>	source root\n";
    print "	root=<dir>	annotation root (same as -root <dir>)\n";
    print "	offset=<path>	document offset from root\n";
    print "	project=<name>	project identifier\n";
    print "	prune=<regexp>	directories not to index (default [0-9]+)\n";
}

### Parameters: 

$source 	= "";
$offset 	= "";
$root 		= "$ENV{HOME}/.woad";
$recursive 	= 1 ;
$project	= "";

$notesOnly	= 0;
$xref		= 0;
$yref		= 0;
$quiet		= 0;

$sourcePrefix	= ".source";
$sourceSuffix	= ".notes";
$wordPrefix	= ".words";
$words		= "/$wordPrefix";

### Patterns 

$prune		= '[0-9]+';	        # pattern for directories to prune
$xids		= '[-_0-9A-Za-z]';      # start chars for XML identifiers
$nxids		= '[^-_0-9A-Za-z]';     # non-start chars for XML identifiers
$xidc		= '[-.:_0-9A-Za-z]';    # interior chars for XML identifiers
$xid		= "($xids($xidc*$xids)?)";	# pattern for XML identifiers
$ids		= '[_A-Za-z]';		# start chars for C identifiers
$nids		= '[^_A-Za-z]';		# non-start chars for C identifiers
$idc		= '[._0-9A-Za-z]';     # interior chars for C identifiers
$id		= "($ids($idc*$ids)?)"; # pattern for C identifiers

### Global state:

$pass		= 0;		# pass 0: look for definitions
				# pass 1: look for references

@roots		= ();		# absolute paths of root directories
				# ( used for link circularity checking )

### Statistics:

$nSourceFiles	= 0;
$nSourceDirs	= 0;
$nNotPruned	= 0;
$nNoteFiles	= 0;

### Filename and extension classifiers:
#	Each maps the extension or name onto a type description.
# ===	really ought to just have a single map, and get MIME type
#	as well as Woad type and type description.

%noIndexDirs 	= ( "CVS"	  => "CVS control directory",
		    $sourceSuffix => "WOAD source annotations" );

%noIndexNotes 	= ( "CVS"	=> "CVS control directory",
		    "logs"	=> "PIA logs",
		    "DATA" 	=> "PIA data directory" );

%noIndexExt	= ( "log"	=> "log",
		    "bak"	=> "backup",
		    "old"	=> "old",
		);

%markupExt	= ( "html"	=> "HTML",
		    "shtml"	=> "HTML with server-side includes",
		    "htm"	=> "HTML",
		    "php"	=> "PHP",
		    "php3"	=> "PHP3",
		    "xml"	=> "XML",
		    "xh"	=> "PIA XHTML",
		    "inc"	=> "PIA XHTML parsed entity (include) file",
		    "xx"	=> "PIA XXTML",
		    "ts"	=> "PIA tagset",
		    "xcf"	=> "PIA config",
		    "xci"	=> "PIA config  parsed entity (include) file",
		);

%textFileNames	= ( "README"	=> "Read Me",
		    "HEADER"	=> "listing header",
		    "FOOTER"	=> "listing footer",
		);

%textFileExt	= ( "txt"	=> "text",
		    
		);

%codeFileNames	= ( "Makefile"	=> "Makefile",
		    "makefile"	=> "Makefile",
		);

%codeFileExt  	= ( "java"	=> "Java",
		    "c"		=> "C",
		    "h"		=> "C header",
		    "cpp"	=> "C++",
		    "cxx"	=> "C++",
		    "c++"	=> "C++",
		    "C" 	=> "C++",
		    "hpp"	=> "C++ header",
		    "hxx"	=> "C++ header",
		    "h++"	=> "C++ header",
		    "H" 	=> "C++ header",
		    "pl"	=> "PERL",
		    "pm"	=> "PERL module",
		    "py"	=> "Python",
		    "sh"	=> "shell",
		    "bat"	=> "DOS shell",
		);

%binFileExt	= ( "class"	=> "java class",
		    "o"		=> "object code (Unix)",
		    "obj"	=> "object code (DOS)",
		    "exe"	=> "executable (DOS)",
		    "com"	=> "executable (DOS)",
		    "dvi"	=> "TeX DeVice Independent",
		);

%archiveFileExt	= ( "tar"	=> "tar archive",
		    "zip"	=> "zip archive",
		    "gz"	=> "gzip compressed file",
		    "tgz"	=> "gzipped tar archive",
		    "tar.gz"	=> "gzipped tar archive",
		    "jar"	=> "Java archive",
		    "a" 	=> "library (Unix)",
		    "so"	=> "shared object (Unix)",
		    "dll"	=> "dynamic link lib (DOS)",
		);

%imageFileExt	= ( "gif"	=> "gif",
		    "jpeg"	=> "jpeg",
		    "jpg"	=> "jpeg",
		    "png"	=> "png",
		    "xbm"	=> "X bitmap",
		    "xpm"	=> "X pixmap",
		    "pbm"	=> "portable bitmap",
		    "pgm"	=> "portable greymap",
		    "ppm"	=> "portable pixmap",
		    "ps"	=> "postscript",
		    "eps"	=> "encapsulated postscript",
		    "pdf"	=> "Acrobat Portable Document Format",
		    "fig"	=> "xfig figure",
		);

### Global Indices:
#
#   Terminology:
#
#	path		a path >> from the source directory <<
#			  All paths start with "/". 
#	ref		a "reference" to a word, consisting of an 
#			  anonymous hash containing the reference data.

#   Context maps: (=== we probably won't use this ===)
#
#	Each context map is a hash table that maps keywords (or something)
#	in some specific context onto arrays of refs.

%keywords 	= ();		# maps words in text
%tags 		= ();		# maps tag names
%fragments	= ();		# maps HTML fragment identifiers
%identifiers	= ();		# maps identifiers in various languages
%notes		= ();
%notesByTime	= ();

#   Keyword context maps:
#
#	Each maps a context name into a big string, which becomes the content
#	of the appropriate index file. 

%defs		= ();		# context map for word definitions
%docs		= ();		# context map for word documentation
%uses		= ();		# context map for word usage

#   Word cross-reference map:
#
#	This maps words onto a string containing all references to that word.

%xrefs		= ();

#   File Index:
#
#	Each file index is a hash table that maps file paths into 
#	anonymous hashes.  Each hash contains the following items:
#
#		title		title, from <title>...</title>
#		dscr		description, derived heuristically if no title
#		tdscr		description derived from type
#		ext		extension
#		dext		"derived extension" for files without one.
#		type		markup / code / text / image / archive / bin
#		mdate		last-modified date
#		size		size in bytes

%pathIndex	= ();		# maps paths into hashes


###### Analyze Command Line #############################################

if (@ARGV == 0) { usage(); exit; }

for ($i = 0; $i < @ARGV; ++$i) {
    $arg = $ARGV[$i];

    if ($arg eq "-l") {			# Handle "-*" options:
	$recursive = 0;
    } elsif ($arg eq '-v') {
	print version() . "\n";  
	exit(0);
    } elsif ($arg eq '-root') {
	$root = $ARGV[++$i];
    } elsif ($arg eq '-n') {
	$notesOnly = 1;
    } elsif ($arg eq '-x') {
	$xref = 1;
	$yref = 1;
    } elsif ($arg eq '-y') {
	$yref = 1;
    } elsif ($arg eq '-q') {
	$quiet += 1;
    } elsif ($arg =~ /^-/) {		 # unrecognized switch
	usage();
	exit(1);
    } elsif ($arg =~ /^source\=(\S+)/) { # Handle "n=v" parameters
	$source = $1;
    } elsif ($arg =~ /^prefix\=(\S*)/) { # \S* because prefix might be null
	$sourcePrefix = $1;
    } elsif ($arg =~ /^offset\=(\S+)/) {
	$offset = $1;
    } elsif ($arg =~ /^project\=(\S+)/) {
	$project = $1;
    } elsif ($arg =~ /^root\=(\S+)/) {
	$root = $1;
    } elsif ($arg =~ /^prune\=(\S+)/) {
	$prune = $1;
    } elsif ($arg =~ /\=/) {
	print STDERR "Unrecognized parameter $arg ignored\n";
    } else {				# handle file
	if ($source ne "") { die "$0 can only index one source root\n"; }
	$source = $arg;
    }

}

if (! $notesOnly) {
    if ($source eq "")	{ die "No source directory specified."; }
    if (! -d $source) 	{ die "Source must be a directory."; }

    $roots[0] = getRealDirPath($source);
}

if ($project ne "" && $project !~ /^\//) {
    $project = "/" . $project;
}

$rec = $recursive? "/..." : "";

print STDERR "indexing $source$rec -> $root$project \n" unless ($quiet);
print STDERR "    abs: " . $roots[0] . "\n" unless ($quiet);

###### Do the Work ######################################################

## Index the source tree
if (! $notesOnly) {
    open (PATHINDEX, ">$root$project/sourcePathIndex.wi");
    indexDir($source, "/");
    close (PATHINDEX);
}

## Index the WOAD tree, mainly looking for notes.
indexWoadDir("$root$project", "/");

## Now output the global definition and documentation indices
#	Afterwards we throw most of the information away to save space.
globalIndices() unless ($notesOnly);
listNotesByTime();

## Increment $pass and re-index the sources looking for references
if ($xref) {
    @roots = ();		# === do we need to clobber @roots here? 
    $roots[0] = getRealDirPath($source);
    $pass ++;
    indexDir($source, "/");
}

## Put out the cross-references.  -y means only the definitions.
if ($yref) {
    makeCrossReference();
}

## Finally, output the statistics.
if ($quiet > 1) { exit(0); }	# if not being very quiet, of course.

$nPruned = $nsourceFiles - $nNotPruned;
print STDERR "roots: @roots \n";
print STDERR "files: $nSourceFiles, "
	.    "($nSourceDirs dirs, $nPruned pruned), "
	.    "notes: $nNoteFiles, " 
    	.    "defs: $nDefs\n";
print STDERR "xrefs: $nWords\n" if ($yref);

exit(0);


###### Index a Directory ################################################

### indexDir(directoryName, pathFromSources) 
#	index a directory.
#
sub indexDir {
    my ($d, $path) = (@_);

    # Open and read the directory.
    if (! opendir(DIR, "$d")) {
	print STDERR "cannot open directory $d\n";
	return;
    }
    my @files = sort(readdir(DIR));
    my @subdirs = ();

    ++ $nSourceDirs unless ($pass);

    # Open the corresponding index file for output
    my $xd = "$root$project/$sourcePrefix$path";
    $xd =~ s@//@/@g;
    -d $xd || mkdir($xd, 0777) || die "cannot create directory $xd/\n";

    # === eventually compare dates on directory and dirIndex.wi
    if (!$pass) { 
	print STDERR "indexing $d -> $xd\n" unless ($quiet);
	open (DIRINDEX, ">$xd/dirIndex.wi")
	    || die "cannot create $xd/dirIndex.wi";
    } else {
	print STDERR "reindexing $d -- " unless ($quiet);
    }

    my $n = 0; my $s = 0;
    for (my $i= 0; $i < @files; ++$i, ++$n) {
	if ($files[$i] eq "." || $files[$i] eq "..") { next; }
	if (indexFile($files[$i], $path)) {
	    ++ $s;
	    push (@subdirs, $files[$i]); 
	}
    }

    if (!$pass) {
	close (DIRINDEX);
    } else {
	print STDERR " $n files, $s dirs\n" unless ($quiet);
    }

    # now do the subdirectories

    if ($recursive) {
	for (my $i = 0; $i < @subdirs; ++$i) { 
	    my $dd = $subdirs[$i];
	    indexDir("$d/$dd", "$path/$dd" );
	}
    }
}

###### Index a File #####################################################

### indexFile(filename, pathFromSources) 
#	index a file OR directory.  
#	Returns 1 if this is a directory that needs to be indexed,
#	        0 otherwise. 
#
sub indexFile {
    my ($f, $path) = (@_);
    ++ $nSourceFiles unless ($pass);

    if ($f eq "." || $f eq "..") { return 0; }
    if ($f =~ /^\./) { return 0; } # don't index any dot files (questionable)
    if ($f =~ /\~$/) { return 0; } # don't index any backup files
    if ($f =~ /^\#.*\#$/) { return 0; }	# don't index #...# files

    my $indexme = 0;
    my $type = '';		# type category
    my $tdscr = '';		# type description
    my $ftype = '';		# type from `file`

    $f =~ /\.([^.]*)$/;		# extract extension.  
    my $ext = $1;
    my $basename = $f; 
    $basename =~ s/\.([^.]*)$//;

    my $absPath = "$source$path/$f";
    $absPath =~ s@//@/@g;

    my $woadPath = "$path/$f";
    $woadPath =~ s@//@/@g;

    my ($dev, $ino, $mode, $nlink, $uid, $gid, $rdev, $size, $atime,
	$mtime, $ctime, $blksiz, $blks) = stat $absPath;

    # file index entry
    my %entry = ("name" => $f, "path" => $woadPath,
		 "size" => $size, "mtime" => $mtime);

    if (-d $absPath) {		# Fill out entry for directory
	$type = "dir";
	$tdscr = $noIndexDirs{$f};
	if ($prune ne '' && $f =~ /^$prune$/) { 
	    $indexme = 0;		# prune as specified
	    $tdscr = "directory (not indexed)";
	    if (-f "$woadPath/dirIndex.wi") {
		unlink("$woadPath/dirIndex.wi");
		print STDERR "removed dirIndex.wi in pruned $woadPath";
	    }
	}
	if (-l $absPath) {	# if this is a symbolic link...
	    # check: is the real path underneath a known source root?
	    #        if not, we follow the link, and add it to the list
	    #        of known source roots (for more circularity checking)
	    my $realPath = getRealDirPath($absPath);

	    $tdscr = "symlink to directory $realPath";
	    $indexme = 1;
	    for (my $i = 0; $i < @roots; ++$i) {
		if ($realPath =~ /^$roots[$i]/) { $indexme = 0; last; }
	    }
	    if ($indexme) {
		push(@roots, $realPath);
	    } else {
		$tdscr = "CIRCULAR " . $tdscr;
	    }
	}
	if (! $tdscr) {
	    $indexme = 1;
	    $tdscr = "directory";
	}
    } else {			# Fill out entry for ordinary file
	$entry{"ext"} = $ext;
	if ($noIndexExt{$f} ne '') { return 0; }

	if (($tdscr = $textFileNames{$f}) ne '' ||
	    ($tdscr = $textFileExt{$ext}) ne '') {
	    $type = "text";
	    indexTextFile($absPath, \%entry);

	} elsif (($tdscr = $codeFileNames{$f}) ne '' ||
		 ($tdscr = $codeFileExt{$ext}) ne '') {
	    $type = "code";
	    indexCodeFile($absPath, \%entry);

	} elsif (($tdscr = $markupExt{$ext}) ne '') {
	    $type = "markup";
	    indexMarkupFile($absPath, \%entry);

	} elsif (($tdscr = $noIndexExt{$ext}) ne '') {
	    return 0;		# don't even look at it.

	} elsif (($tdscr = $binFileExt{$ext}) ne '') {
	    $type = "binary";

	} elsif (($tdscr = $archiveFileExt{$ext}) ne '') {
	    $type = "archive";

	} elsif (($tdscr = $imageFileExt{$ext}) ne '') {
	    $type = "image";
	    $ftype = `file -b $absPath`;
	    $entry{"dscr"} = $ftype;
	    $ftype =~ /([0-9]+)\s*x\s*([0-9]+)/;
	    $entry{"width"} = $1;
	    $entry{"height"} = $2;

	} else {
	    $type = "unknown";	# call `file` to identify the file
	    $ftype = `file -b $absPath` unless ($pass);
	    $entry{"dscr"} = "(?) $ftype" unless ($pass);

	    # At this point we can decide whether to index as text or code
	    # actually, `file` isn't always specific enough.

	    if ($ftype =~ /script/ || $ftype =~ /commands/) {
		indexCodeFile($absPath, \%entry);
	    } elsif ($ftype =~ /text/) {
		indexMarkupFile($absPath, \%entry);
	    }
	}
    }

    ### At this point we're done if we're not re-indexing ###
    return $indexme if ($pass);

    # add type and type description to entry
    $entry{"type"} = $type;
    if ($tdscr ne '') { $entry{"tdscr"} = $tdscr; }

    # instead of $tdscr in indices, use complete dscription if available.
    my $dscr = ($tdscr ne '')? $tdscr : $entry{"dscr"};

    indexDef($basename, 'file', $woadPath, 0, '', $dscr);

    # === indexDef is wrong: it wordifies the path, which loses.
    # === There's no reason to have a path index here anyway; it's in .source
    # indexDef($woadPath, 'path', $woadPath, 0, '', $dscr);

    # Insert entry into file index table.  We may possibly be needing it.
    $pathIndex{$absPath} = \%entry;
    
    # Convert entry to xml format:
    $ent = "<File";
    for (my @keys = keys(%entry), my $k = 0; $k < @keys; ++$k) {
	$ent .= " " . $keys[$k] . '="' . stringify($entry{$keys[$k]}) . '"';
    }
    $ent .= ">$woadPath</File>";
    print PATHINDEX "    $ent\n";
    print DIRINDEX  "    $ent\n";

    ++ $nNotPruned;
    return $indexme;
}


###### Index various types of file ######################################

### Each index*File routine takes the path and a reference to the 
#	pathIndex entry as arguments.

sub indexMarkupFile {
    my ($pf, $entry) = (@_);
    # note that we will use $$entry{"key"} to access entry items.

    my $title = '';
    my $line  = 0;
    my $element = '';

    my $txt = '';		# text of a line + continuations
    my $start = 0;		# start of a multi-line construct

    my $path = $$entry{"path"};

    if ($pass) {
	reindexFile($pf, $entry);
	return;
    }

    $summary = '';		# return note summary in a global.

    open (FILE, $pf);
    while (<FILE>) {
	++$line;
	# This really ought to use an HTML/XML parser.  Punt for now.
	# === worry about documents with multiple <title> elements. ===
	if ($title eq '' && /\<title\>(.*)\<\/title\>/i) { 
	    $title = compactify($1);
	    indexDef($title, 'title', $path, $line, '', $title);
	} elsif ($title eq '' && /\<title\>(.*)$/i) { 
	    # === _really_ want a parser here -- this is a kludge.
	    $txt = $_;
	    $start = $line;
	    while (<FILE>) {
		$txt .= $_;
		++$line;
		last if (/\<\/title\>/i);
	    }
	    $txt =~ /\<title\>(.*)\<\/title\>/is;
	    $title = $1;
	    indexDef($title, 'title', $path, $start, '', $title);
	} elsif ($title eq '' && /\<tagset.+name=["']($xid)['"]/) {
	    $title = "Tagset \"$1\"";
	    indexDef($1, 'tagset', $path, $line, $1, $_);
	}

	# Here we go looking for declarations embedded in web pages.
	#   This category includes not only PIA tag definitions but
	#   PHP function and class declarations.  It's easily extended
	#   to other embedded-language active-page schemes.
	#
	if (/\<define\s+element=["']($xid)['"]/) {
	    indexDef($1, 'tag', $path, $line, "$1", $_);
	    $element = "$1";
	} elsif (/\<define\s+attribute=["']($xid)['"]/) {
	    # === not entirely clear how to treat attribute defs.
	    indexDef($1, 'tag', $path, $line, "$element.$1", $_);
	} elsif (/\s+function $id/) {	# PHP function
	    indexDef($1, 'func', $path, $line, $1, $_);
	}

	if (/\<summary\>/) {
	    $txt = $_;
	    $start = $line;
	    while (<FILE>) {
		$txt .= $_;
		++$line;
		last if (/\<\/summary\>/i);
	    }
	    $txt =~ /\<summary\>(.*)\<\/summary\>/is;
	    $summary = $1;
	}

	# === ought to be able to identify javadoc/tsdoc documentation ===
    }
    close FILE;

    if ($title ne '') { $$entry{"title"} = $title; }
}

sub indexTextFile {
    my ($pf, $entry) = (@_);
    my $path = $$entry{"path"};

    my $title = '';
    my $line  = 0;
    
    if ($pass) {
	reindexFile($pf, $entry);
	return;
    }

}

sub indexCodeFile {
    my ($pf, $entry) = (@_);
    my $path = $$entry{"path"};

    my $title = '';
    my $line  = 0;

    if ($pass) {
	reindexFile($pf, $entry);
	return;
    }

    open (FILE, $pf);
    while (<FILE>) {
	++$line;

	# The following is a very sorry excuse for parsing -- note that
	# we don't know what the programming language is at this point.
	# Amazingly enough, we don't really care!  This "parse" is loose
	# enough to catch the majority of interesting cases with  
	# surprisingly small false-positive and false-negative rates
	# It will almost certainly fail on obfuscated C code.
	
	if (/^\s*\/\/|^\s*\#|^\s*\/\*.+\*\/\s+$/) { # C/Perl comment
	    # Only skip comment if it occupies the entire line.
	} elsif (/\s+sub $id/) {	# PERL function
	    indexDef($1, 'func', $path, $line, $1, $_);
	} elsif (/\s+function $id\s*[^a-zA-Z\s]/) {# PHP or shell function
	    indexDef($1, 'func', $path, $line, $1, $_);
	} elsif (/\s+def $id/) {	# Python function
	    indexDef($1, 'func', $path, $line, $1, $_);
	} elsif (/^\s*class\s+($id)/) {	# C++/Java/Python class decl.
	    indexDef($1, 'class', $path, $line, "$1", $_);
	} elsif (/\s+class\s+($id)/) {  # C++/Java/Python class decl
	    indexDef($1, 'class', $path, $line, "$1", $_);
	} elsif (/^\s*interface\s+($id)/) { # Java interface decl.
	    indexDef($1, 'class', $path, $line, "$1", $_);
	} elsif (/\s+interface\s+($id)/) {  # Java interface decl
	    indexDef($1, 'class', $path, $line, "$1", $_);
	} elsif (/$id\s*\(/) {	# possible C/C++/Java function decl.
	    # may not eliminate uninitialized function-valued variables
	    my $def = $1;
	    if (/^\s*($id[*&+:\s]+)+\(\s*\*\s*$id\s*\)\s*\(/) {
		/\(\s*\*\s*($id)\s*\)/;	# We just matched (*fptr)(...)
		$def = $1;		# so ID being defined is "fptr"
		if (! /\)\s*[=]/) {     # reject initialized variables
		    indexDef($def, 'func', $path, $line, $def, $_);
		}
	    } elsif ($def =~ /^(if|for|else|elsif|while|until|void)$/) {
		# certain keywords can be followed by "("
	    } elsif (/^\s*(case|if|elsif|else|return)/) {
		# some other keywords can be followed by "$id ("
		# if qualifies in php, for example, where parens aren't needed
	    } elsif (/^\s*($id[*&+:\s]+)+$id\s*\(/) {
		# Ought to be in class or top level, but it seems
		# amazingly reliable nevertheless.

		# === drag in the rest of the arglist if multi-line ===
		# === hard because there may be nested parens. ===
		indexDef($def, 'func', $path, $line, $def, $_);
	    }
	} elsif (/\#define\s+$id/) { # CPP macro definition (#define)
	    indexDef($1, 'macro', $path, $line, $1, $_);
	}
    }
    close FILE;
}

### reindexFile(path, entry)
#	Go through the file on the second pass looking for references
#	to words that are already defined.
#
sub reindexFile {
    my ($pf, $entry) = (@_);
    my $path = $$entry{"path"};

    my $title = '';
    my $line  = 0;

    open (FILE, $pf);
    while (<FILE>) {
	++$line;

	while (/$id/) {		# gobble words in the line
	    my $word = $1;
	    s/$nids*$id//;
	    if ($xrefs{lc($word)} ne '') {	# If the word has a definition,
					# index its use here.
		indexUse($1, '', $path, $line);
	    }
	}	
    }
    close FILE;
}


###### Index WOAD Annotation ##########################################

### indexWoadDir(directoryName, pathFromNotes) 
#	index a WOAD annotation directory.  This is easy because we're 
#	only looking for annotation (.ww) files.
#
sub indexWoadDir {
    my ($d, $path) = (@_);

    # Open and read the directory.
    if (! opendir(DIR, "$d")) {
	print STDERR "cannot open notes directory $d\n";
	return;
    }
    my @files = sort(readdir(DIR));
    my @subdirs = ();

    # === eventually compare dates on directory and dirIndex.wi
    print STDERR "indexing notes in $d \n" unless ($quiet);

    for (my $i= 0; $i < @files; ++$i) {
	my $f = $files[$i];
	if ($f eq "." || $f eq "..") { next; }
	if ($noIndexNotes{$f}) { next; }
	if ($f =~ /\.wi$/) { next; }
	if (-l "$d/$f") { next; }
	if (-d "$d/$f") { push (@subdirs, $f); }
	elsif ($f =~ /\.ww$/) { indexWoadNote($f, $path); }
    }

    # now do the subdirectories

    if ($recursive) {
	for (my $i = 0; $i < @subdirs; ++$i) { 
	    my $dd = $subdirs[$i];
	    if (-d "$d/$dd") { indexWoadDir("$d/$dd", "$path/$dd" ); }
	}
    }
}

sub indexWoadNote {
    my ($f, $path) = (@_);

    ++ $nNoteFiles;

    my $indexme = 0;
    my $type = '';		# type category
    my $tdscr = '';		# type description
    my $ftype = '';		# type from `file`

    $f =~ /\.([^.]*)$/;		# extract extension.  
    my $ext = $1;
    my $basename = $f; 
    $basename =~ s/\.([^.]*)$//;

    my $absPath = "$root$project$path/$f";
    $absPath =~ s@//@/@g;

    my $woadPath = "$path/$f";
    $woadPath =~ s@//@/@g;

    if (-d $absPath) { return 1; } # index directories
    if ($f !~ /\.ww$/) { return 0; } # otherwise, only look at notes

    my ($dev, $ino, $mode, $nlink, $uid, $gid, $rdev, $size, $atime,
	$mtime, $ctime, $blksiz, $blks) = stat $absPath;

    # file index entry
    my %entry = ("name" => $f, "path" => $woadPath,
		 "size" => $size, "mtime" => $mtime, "type" => "note");

    $type = "markup";

    # === indexMarkupFile is probably wrong for notes.
    indexMarkupFile($absPath, \%entry) unless ($notesOnly);

    # Convert entry to xml format:	=== this is probably wrong
    my $ent = "<Wfile";
    for (my @keys = keys(%entry), my $k = 0; $k < @keys; ++$k) {
	$ent .= " " . $keys[$k] . '="' . stringify($entry{$keys[$k]}) . '"';
    }
    $ent .= "> $summary </Wfile>\n";

    $notesByTime{"m$mtime"} .= $ent;
    # [debug] print STDERR $ent;

    return 0;
}


###### Create word index entry ########################################

### indexDef(word, context, file, lineNr, name, shortDef)
#	index a word definition.  The "name" parameter is the HTML fragment
#	name that we expect the word to have in a listing.
#
sub indexDef {
    my ($word, $context, $path, $line, $name, $def) = (@_);
    $def = stringify($def);
    $ident= stringify(wordify($word));
    $word = stringify(compactify($word));

    # We have to distinguish the original word from the wordified XML id,
    # but only waste the space (word=, id=) if they're different.

    my $entry = "<Def word=\"$word\" path=\"$path\" "
	      . ($line?  "line=\"$line\" " : '')
	      . (($ident eq $word)? '' : "id=\"$ident\" ")
	      . "context=\"$context\" "
	      . "name=\"$name\">$def</Def>\n\n";

    $defs{$context} .= $entry;

    if ($ident eq $word) {
	$xrefs{lc($word)} .= $entry;
    }
    # === Append to $context/$word/defs.wi as well ===
    # === it's possible, even likely, that a database _would_ be better ===
}

### indexDoc(word, context, file, lineNr, name, shortDef)
#	index documentation for a word (e.g., a javadoc or tsdoc listing).
#
sub indexDoc {
    my ($word, $context, $path, $line, $name, $def) = (@_);
    $def = stringify($def);

    $entry = "<Doc word=\"$word\" path=\"$path\" line=\"$line\" "
	   . "context=\"$context\" "
	   . "name=\"$name\">$def</Def>\n\n";

    $docs{$context} .= $entry;
    $xrefs{lc($word)} .= $entry;
}

### indexUse(word, context, file, lineNr)
#	index a word use.  This is done in a second pass so that we know
#	which words have already been defined. 
#
sub indexUse {
    my ($word, $context, $path, $line) = (@_);

    my $entry = "<Ref word=\"$word\" path=\"$path\" line=\"$line\" "
	      . ($context ? "context=\"$context\" " : "") . " />\n\n";

    if ($context ne '') {
	$uses{$context} .= $entry;
    }

    # lowercase the word for the cross-reference key.
    $xrefs{lc($word)} .= $entry;
}


###### Output the global indices ########################################

### globalIndices()
#	output the global indices.
#
sub globalIndices {
    my $context;
    my $word;
    my @entries;
    my ($entry, $i, $c, $d);
    my @keys;
    my $k;

    # === LOSES HUGELY if there are empty lines in entries! ===

    mkdir ("$root$project$words", 0777);
    for (@keys = sort(keys(%defs)), $k = 0; $k < @keys; ++$k) {
	$context = $keys[$k];
	$dir = "$root$project$words/$context";
	print STDERR "defs for $context -> $dir/defs.wi\n" unless ($quiet);
	mkdir ($dir, 0777);
	open (INDEX, ">$dir/defs.wi");
	@entries = sort(split /\n\n/, $defs{$context});
	print INDEX join ("\n", @entries);
	close (INDEX);
	for ($i = 0; $i < 27; ++$i) {
	    $c = substr('0ABCDEFGHIJKLMNOPQRSTUVWXYZ', $i, 1);
	    if (-f "$dir/defs-$c-.wi") { unlink "$dir/defs-$c-.wi"; }
	}
	$c = '';
	for ($i = 0; $i < @entries; ++$i) {
	    $entries[$i] =~ /word\=\"(.)/; # " fix WOAD string parsing.
	    $c = uc($1);
	    if ($c !~ /[a-zA-Z]/) { $c = '0'; }
	    ++ $nDefs;

	    # Should really take advantage of sortedness to avoid extra opens
	    open (INDEX, ">>$dir/defs-$c-.wi");
	    print INDEX $entries[$i] . "\n";
	    close (INDEX);
	}
    }
    %defs = ();

    for (@keys = sort(keys(%docs)), $k = 0; $k < @keys; ++$k) {
	$context = $keys[$k];
	$dir = "$root$project$words/$context";
	print STDERR "docs for $context -> $dir/docs.wi\n" unless ($quiet);
	mkdir ($dir, 0777);
	open (INDEX, ">$dir/docs.wi");
	print INDEX $docs{$context};
	close (INDEX);
    }
    %docs = ();
}

### listNotesByTime()
#	output the chronological list of notes
#
sub listNotesByTime {
    # Here we do the chronological notes index
    open (INDEX, ">$root$project/AllNotesByTime.wi");
    @keys = sort(keys(%notesByTime));
    for ($k = 0; $k < @keys; ++$k) {
	print INDEX $notesByTime{$keys[$k]};
    }
    close (INDEX);
}

### makeCrossReference()
#	output the global cross-reference index.
#
#	Unlike the other contexts, which have an index file for each initial
#	letter, the xref directory has a subdirectory for each letter, and
#	a file for each word.  It is hoped that this will keep individual
#	directories at least somewhat manageable.  
#	
sub makeCrossReference {
    my $context;
    my $word;
    my @entries;
    my ($entry, $i, $c, $d);
    my @keys;
    my $k;

    # First put out the context-specific cross-references. 

    for (@keys = sort(keys(%uses)), $k = 0; $k < @keys; ++$k) {
	$context = $keys[$k];
	$dir = "$root$project$words/$context";
	print STDERR "uses for $context -> $dir/uses.wi\n" unless ($quiet);
	mkdir ($dir, 0777);
	# Don't even bother filling out uses.wi -- it will normally be too big.
	# Instead, make a dummy as a header.
	open (INDEX, ">>$dir/uses.wi");
	print INDEX "See breakout files uses-*-.wi\n";
	close (INDEX);
	@entries = sort(split /\n\n/, $uses{$context});
	for ($i = 0; $i < 27; ++$i) {
	    $c = substr('0ABCDEFGHIJKLMNOPQRSTUVWXYZ', $i, 1);
	    if (-f "$dir/uses-$c-.wi") { unlink "$dir/uses-$c-.wi"; }
	}
	$c = '';
	for ($i = 0; $i < @entries; ++$i) {
	    $entries[$i] =~ /word\=\"(.)/;  # " fix WOAD string parsing.
	    $c = uc($1);
	    if ($c !~ /[a-zA-Z]/) { $c = '0'; }

	    # Should really take advantage of sortedness to avoid extra opens
	    open (INDEX, ">>$dir/uses-$c-.wi");
	    print INDEX $entries[$i] . "\n";
	    close (INDEX);
	}
    }
    %uses = ();

    # Now do the actual cross-references

    $context = "xref";
    $dir = "$root$project$words/$context";
    mkdir ($dir, 0775);
    print STDERR "xrefs -> $root$project/xrefs.wi\n" unless ($quiet);
    open (XREFS, ">$root$project/xrefs.wi");
    $d = '';
    my $n = 0;
    for (@keys = sort(keys(%xrefs)), $k = 0; $k < @keys; ++$k, ++$n) {
	my $word = $keys[$k];
	++ $nWords;

	# === This would be a good place to check for stopwords.

	$c = substr($word, 0, 1);
	$c = uc($c);
	if ($c !~ /[a-zA-Z]/) { $c = '0'; }

	if ($c ne $d) {		# If we're starting a new letter,
	    mkdir ("$dir/-$c-", 0775); # ... make a directory.
	    if ($d ne '') { print STDERR " $d($n)" unless ($quiet); }
	    $n = 0;
	    $d = $c;
	}

	# Write the entry for the top-level cross-reference namespace.
	# The parser will fill in the prefix and the word.
	my $xfile = "$dir/-$c-/$word.wi";
	my $xlink = "-$c-/";	# was "$words/$context/-$c-/$word.wi";
	print XREFS "<xref name=\"$word\">$xlink</xref>\n";
	
	# write the cross-reference file entry for the word
	# We don't bother to sort the entries, which saves a little time.
	# As we go, clear out entries in %xrefs to save both time and space

	open (INDEX, ">$xfile");
	print INDEX $xrefs{$word}; # we don't really have to sort.
	## @entries = sort(split (/\n\n/, $xrefs{$word}));
	$xrefs{$word} = '';	# save memory
	##print INDEX join ("\n", @entries);
	close (INDEX);
    }
    close (XREFS);
    print STDERR "\n" unless ($quiet);
}


###### Utilities ########################################################

### getRealDirPath(path)
#	Return the real, absolute path to the given directory.
#
sub getRealDirPath {
    my ($d) = (@_);
    my $p = `cd $d; /bin/pwd`;
    $p =~ /(.*)$/;
    return $1;
}

### compactify(string)
#	Trim excess spaces and trailing periods from "string"
#
sub compactify {
    my ($s) = (@_);
    $s =~ s/^\s*//;		# remove leading spaces
    $s =~ s/\s*$//;		# remove trailing spaces
    $s =~ s/\.+$//;		# remove trailing periods
    $s =~ s/\s+/ /g;		# squash multiple spaces

    return($s);
}

### wordify(string)
#	Turn an arbitrary phrase "string" into a WOAD-indexable ``word''
#
sub wordify {
    my ($s) = (@_);
    $s =~ s/^\s*//s;		# remove leading spaces
    $s =~ s/\s*$//s;		# remove trailing spaces
    $s =~ s/\.+$//s;		# remove trailing periods
    $s =~ s/\&/ and /gs;	# & -> and
    $s =~ s/\@/ at /gs;		# @ => at
    $s =~ s/\%/ percent /gs;	# % => percent
    $s =~ s/\s+/ /gs;		# squash multiple spaces
    $s =~ s/[^A-Za-z0-9.-]/_/gs;	# non-letters -> _

    return($s);
}

### caseSep(string)
#	Eliminate spaces or underscores between letters of different case.
#	toProduce_aPhraseLikeThis.
#
sub caseSep {
    my ($s) = (@_);

    $s =~ s/\s+/ /gs;			# squash multiple spaces
    $s =~ s/[^A-Za-z0-9.-]/_/gs;	# non-letters -> _
    $s =~ s/([a-z])\_([A-Z])/$1$2/gs;
    $s =~ s/([A-Z])\_([a-z])/$1$2/gs;

    return($s);
}

### stringify(string)
#	entity-encode "string"
#
sub stringify {
    my ($s) = (@_);
    $s =~ s/^\s*//s;
    $s =~ s/\s*$//s;
    $s =~ s/\&/\&amp\;/gs;
    $s =~ s/\</\&lt\;/gs;
    $s =~ s/\>/\&gt\;/gs;
    $s =~ s/\"/\&quot\;/gs; # " fix WOAD string parsing.
    return($s);
}

sub version {
    return q'$Id: woad-index.pl,v 1.18 2000-11-22 22:58:25 steve Exp $ ';
    # put this last because the $'s confuse emacs.
}


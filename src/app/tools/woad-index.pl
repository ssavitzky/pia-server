#!/usr/bin/perl
#	$Id: woad-index.pl,v 1.6 2000-08-10 17:31:02 steve Exp $
# Create WOAD index files.
#

### usage() -- print usage summary

sub usage {
    print "$0 [options] [parameters] [<dir>]\n";
    print "  options:\n";
    print "	-l		local (no recursion)\n";
    print "	-root <dir>	Woad annotations (default ~/.woad)\n";
    print "	-v		Print version string and exit\n";
    print "  parameters: \n";
    print "	sources=<dir>	source root\n";
    print "	root=<dir>	annotation root (same as -root <dir>)\n";
    print "	offset=<path>	document offset from root\n";
    print "	project=<name>	project identifier\n";
    print "	prune=<regexp>	directories not to index (default [0-9]+)\n";
}

### Parameters: 

$sources 	= "";
$offset 	= "";
$root 		= "$ENV{HOME}/.woad";
$recursive 	= 1 ;
$project	= "";

$sourcePrefix	= ".source";
$sourceSuffix	= ".notes";
$wordPrefix	= ".words";
$words		= "/$wordPrefix";

$prune		= '[0-9]+';	         # pattern for directories to prune
$ids		= '[-_0-9A-Za-z]';       # start chars for XML identifiers
$idc		= '[-.:_0-9A-Za-z]';     # interior chars for XML identifiers
$id		= "$ids($idc*$ids)?";    # pattern for XML identifiers

### Filename and extension classifiers:
#	Each maps the extension or name onto a type description.
# ===	really ought to just have a single map, and get MIME type
#	as well as Woad type and type description.

%noIndexDirs 	= ( "CVS"	  => "CVS control directory",
		    $sourceSuffix => "WOAD source annotations" );

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
		    "obj"	=> "object (DOS)",
		    "exe"	=> "executable (DOS)",
		    "com"	=> "executable (DOS)",
		    "dvi"	=> "TeX DeVice Independent",
		);

%archiveFileExt	= ( "tar"	=> "tar",
		    "zip"	=> "zip",
		    "gz"	=> "gzip",
		    "tgz"	=> "gzipped tar",
		    "tar.gz"	=> "gzipped tar",
		    "jar"	=> "Java archive",
		    "ar"	=> "library (Unix)",
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

#   Keyword context maps:
#
#	Each maps a context name into a big string, which becomes the content
#	of the appropriate index file. 

%defs		= ();		# context map for word definitions
%docs		= ();		# context map for word documentation
%uses		= ();		# context map for word usage

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

    if ($arg eq "-l") {			# Handle options:
	$recursive = 0;
    } elsif ($arg eq '-v') {
	print version() . "\n";  
	exit(0);
    } elsif ($arg eq '-root') {
	$root = $ARGV[++$i];
    } elsif ($arg =~ /^-/) {
	usage();
	exit(1);
    } elsif ($arg =~ /^sources\=(\S+)/) { # Handle parameters
	$sources = $1;
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
	if ($sources ne "") { die "$0 can only index one source root\n"; }
	$sources = $arg;
    }

}

if ($sources eq "")	{ die "No sources directory specified."; }
if (! -d $sources) 	{ die "Sources must be a directory."; }

if ($project ne "" && $project !~ /^\//) {
    $project = "/" . $project;
}

$rec = $recursive? "/..." : "";

print STDERR "indexing $sources$rec -> $root$project \n";


###### Do the Work ######################################################

open (PATHINDEX, ">$root$project/sourcePathIndex.wi");
indexDir($sources, "/");
close (PATHINDEX);

globalIndices();
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

    # Open the corresponding index file for output
    my $xd = "$root$project/$sourcePrefix$path";
    $xd =~ s@//@/@g;
    -d $xd || mkdir($xd, 0777) || die "cannot create directory $xd/\n";

    # === eventually compare dates on directory and dirIndex.wi
    open (DIRINDEX, ">$xd/dirIndex.wi") || die "cannot create $xd/dirIndex.wi";
    print STDERR "indexing $d -> $xd\n";

    for (my $i= 0; $i < @files; ++$i) {
	if ($files[$i] eq "." || $files[$i] eq "..") { next; }
	if (indexFile($files[$i], $path)) {
	    push (@subdirs, $files[$i]); 
	}
    }

    close (DIRINDEX);

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
    if ($f eq "." || $f eq "..") { return 0; }
    if ($f =~ /^\./) { return 0; } # don't index any dot files (questionable)
    if ($f =~ /\~$/) { return 0; } # don't index any backup files

    my $indexme = 0;
    my $type = '';		# type category
    my $tdscr = '';		# type description
    my $ftype = '';		# type from `file`

    $f =~ /\.([^.]*)$/;		# extract extension.  
    my $ext = $1;

    my $absPath = "$sources$path/$f";
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
	    $type = "unknown";	# could actually call `file` here.
	    $ftype = `file -b $absPath`;
	    $entry{"dscr"} = "(?) $ftype";
	}
    }

    # add type and type description to entry
    $entry{"type"} = $type;
    if ($tdscr ne '') { $entry{"tdscr"} = $tdscr; }

    # Insert entry into file index table.  We may possibly be needing it.
    $pathIndex{$absPath} = \%entry;

    # Convert entry to xml format:
    $ent = "<File";
    for (my @keys = keys(%entry), my $k = 0; $k < @keys; ++$k) {
	$ent .= " " . $keys[$k] . '="' . $entry{$keys[$k]} . '"';
    }
    $ent .= ">$woadPath</File>";
    print PATHINDEX "    $ent\n";
    print DIRINDEX  "    $ent\n";
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

    my $path = $$entry{"path"};

    open (FILE, $pf);
    while (<FILE>) {
	++$line;
	# This really ought to use an HTML/XML parser.  Punt for now.
	if ($title eq '' && /\<title\>(.*)\<\/title\>/i) { 
	    $title = $1;
	} elsif ($title eq '' && /\<title\>(.*)$/i) { 
	    # === _really_ want a parser here -- this is a kludge.
	    $title = $1;
	} elsif ($title eq '' && /\<tagset.+name=["']($id)['"]/) {
	    $title = "Tagset \"$1\"";
	}
	if (/\<define\s+element=["']($id)['"]/) {
	    indexDef($1, 'tags', $path, $line, "$1", $_);
	    $element = "$1";
	} elsif (/\<define\s+attribute=["']($id)['"]/) {
	    indexDef($1, 'tags', $path, $line, "$element.$1", $_);
	}
    }
    close FILE;

    if ($title ne '') { $$entry{"title"} = $title; }
}

sub indexTextFile {
    my ($pf, $entry) = (@_);
    
}

sub indexCodeFile {
    my ($pf, $entry) = (@_);

    open (FILE, $pf);
    while (<FILE>) {
    
    }
    close FILE;
}

###### Create word index entry ########################################

### indexDef(word, context, file, lineNr, name, shortDef)
#	index a word definition.  The "name" parameter is the HTML fragment
#	name that we expect the word to have in a listing.
#
sub indexDef {
    my ($word, $context, $path, $line, $name, $def) = (@_);
    $def = stringify($def);
    my $entry = "<Def word='$word' path='$path' line='$line' "
	      . "name='$name'>$def</Def>\n";
    $defs{$context} .= $entry;
    # === Append to $context/$word/defs.wi as well ===
    # === it's possible, even likely, that a database _would_ be better ===
}

### indexDoc(word, context, file, lineNr, name, shortDef)
#	index documentation for a word (e.g., a javadoc or tsdoc listing).
#
sub indexDoc {
    my ($word, $context, $path, $line, $name, $def) = (@_);
    $def = stringify($def);
    $docs{$context} .= "<Doc word='$word' path='$path' line='$line' "
	             . "name='$name'>$def</Def>\n";
}

### indexUse(word, context, file, lineNr, name)
#	index a word use. 
#   === Presently not done: don't know where to put it.  BIG. ===
#
sub indexUse {
    my ($word, $context, $path, $line, $name) = (@_);
    $uses{$context} .= "<Ref word='$word' path='$path' line='$line' "
	             . "name='$name' />\n";
    
}


###### Output the global indices ########################################

### globalIndices()
#	output the global indices.
#
sub globalIndices {
    my ($context);

    for (my @keys = sort(keys(%defs)), my $k = 0; $k < @keys; ++$k) {
	$context = $keys[$k];
	$dir = "$root$project$words/$context";
	print STDERR "defs for $context -> $dir/defs.wi\n";
	mkdir ($dir, 0777);
	open (INDEX, ">$dir/defs.wi");
	print INDEX join ("\n", sort(split /\n/, $defs{$context}));
	close (INDEX);
    }
    for (my @keys = sort(keys(%docs)), my $k = 0; $k < @keys; ++$k) {
	$context = $keys[$k];
	$dir = "$root$project$words/$context";
	print STDERR "docs for $context -> $dir/docs.wi\n";
	mkdir ($dir, 0777);
	open (INDEX, ">$dir/docs.wi");
	print INDEX $docs{$context};
	close (INDEX);
    }
    for (my @keys = sort(keys(%uses)), my $k = 0; $k < @keys; ++$k) {
	$context = $keys[$k];
	$dir = "$root$project$words/$context";
	print STDERR "uses for $context -> $dir/uses.wi\n";
	mkdir ($dir, 0777);
	open (INDEX, ">$dir/uses.wi");
	print INDEX $uses{$context};
	close (INDEX);
    }
    
}


###### Utilities ########################################################

### stringify(string)
#	entity-encode "string"
#
sub stringify {
    my ($s) = (@_);
    $s =~ s/^\s*//;
    $s =~ s/\s*$//;
    $s =~ s/\&/\&amp\;/g;
    $s =~ s/\</\&lt\;/g;
    $s =~ s/\>/\&gt\;/g;

    return($s);
}

sub version {
    return q'$Id: woad-index.pl,v 1.6 2000-08-10 17:31:02 steve Exp $ ';		# put this last because the $'s confuse emacs.
}


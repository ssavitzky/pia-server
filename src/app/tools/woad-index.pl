#!/usr/bin/perl
#	$Id: woad-index.pl,v 1.1 2000-06-21 01:14:23 steve Exp $
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
    print "	offset=<path>	document offset from root\n";
    print "	project=<name>	project identifier\n";
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

#   Context maps:
#
#	Each context map is a hash table that maps keywords (or something)
#	in some specific context onto arrays of refs.

%keywords 	= ();		# maps words in text
%tags 		= ();		# maps tag names
%fragments	= ();		# maps HTML fragment identifiers
%identifiers	= ();		# maps identifiers in various languages
%notes		= ();

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

    } elsif ($arg =~ /^-/) {
	usage();
	exit(1);
    } elsif ($arg =~ /^sources\=(\S+)/) { # Handle parameters
	$sources = $1;
    } elsif ($arg =~ /^offset\=(\S+)/) {
	$offset = $1;
    } elsif ($arg =~ /^project\=(\S+)/) {
	$project = $1;
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

print PATHINDEX '<namespace name="sourcePathIndex">', "\n";
indexDir($sources, "/");
print PATHINDEX '</namespace>', "\n";

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
    opendir(DIR, "$d") || die "can't open directory $d";
    my @files = readdir(DIR);
    my @subdirs = ();

    # Open the corresponding index file for output
    my $xd = "$root$project/$sourcePrefix$path";
    $xd =~ s@//@/@g;
    -d $xd || mkdir($xd, 0777) || die "cannot create directory $xd\n";

    open (DIRINDEX, ">$xd/dirIndex.wi") || die "cannot open $xd/dirIndex.wi";
    #print DIRINDEX '<namespace name="dirIndex">', "\n";

    print STDERR "indexing $d -> $xd\n";

    for (my $i= 0; $i < @files; ++$i) {
	if ($files[$i] eq "." || $files[$i] eq "..") { next; }
	if (indexFile($files[$i], $path)) {
	    push (@subdirs, $files[$i]); 
	}
    }
    print DIRINDEX '</namespace>', "\n";
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

    my $pf = "$sources$path/$f";
    $pf =~ s@//@/@g;

    my ($dev, $ino, $mode, $nlink, $uid, $gid, $rdev, $size, $atime,
	$mtime, $ctime, $blksiz, $blks) = stat $pf;

    # file index entry
    my %entry = ("name" => $f, "size" => $size, "mtime" => $mtime);

    if (-d $pf) {		# Fill out entry for directory
	$type = "dir";
	$tdscr = $noIndexDirs{$f};
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
	    indexTextFile($pf, \%entry);

	} elsif (($tdscr = $codeFileNames{$f}) ne '' ||
		 ($tdscr = $codeFileExt{$ext}) ne '') {
	    $type = "code";
	    indexCodeFile($pf, \%entry);

	} elsif (($tdscr = $markupExt{$ext}) ne '') {
	    $type = "markup";
	    indexMarkupFile($pf, \%entry);

	} elsif (($tdscr = $noIndexExt{$ext}) ne '') {
	    return 0;		# don't even look at it.

	} elsif (($tdscr = $binFileExt{$ext}) ne '') {
	    $type = "binary";

	} elsif (($tdscr = $archiveFileExt{$ext}) ne '') {
	    $type = "archive";

	} elsif (($tdscr = $imageFileExt{$ext}) ne '') {
	    $type = "image";
	    $ftype = `file -b $pf`;
	    $entry{"dscr"} = $ftype;
	    $ftype =~ /([0-9]+)\s*x\s*([0-9]+)/;
	    $entry{"width"} = $1;
	    $entry{"height"} = $2;

	} else {
	    $type = "unknown";	# could actually call `file` here.
	}
    }

    # add type and type description to entry
    $entry{"type"} = $type;
    if ($tdscr ne '') { $entry{"tdscr"} = $tdscr; }

    # Insert entry into file index table.  We may possibly be needing it.
    $pathIndex{$pf} = \%entry;

    # Convert entry to xml format:
    $ent = "<File";
    for (my @keys = keys(%entry), my $k = 0; $k < @keys; ++$k) {
	$ent .= " " . $keys[$k] . '="' . $entry{$keys[$k]} . '"';
    }
    $ent .= ' />';
    print PATHINDEX "    <bind name=\"$pf\">$ent</bind>\n";
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

    open (FILE, $pf);
    while (<FILE>) {
	# This really ought to use an HTML/XML parser.  Punt for now.
	if ($title eq '' && /\<title\>(.*)\<\/title\>/i) { $title = $1; }

    }
    close FILE;

    if ($title) { $$entry{"title"} = $title; }
}

sub indexTextFile {
    my ($pf, $entry) = (@_);
    
}

sub indexCodeFile {
    my ($pf, $entry) = (@_);
    
}

###### Output the global indices ########################################

### globalIndices()
#	output the global indices.
#
sub globalIndices {

}


###### Utilities ########################################################

sub version {
    return q'$Id: woad-index.pl,v 1.1 2000-06-21 01:14:23 steve Exp $ ';		# put this last because the $'s confuse emacs.
}

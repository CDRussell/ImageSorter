ImageSorter
===========

NOT YET READY FOR PUBLIC CONSUMPTION!! USE AT YOUR OWN RISK!

Setup your input directory (where your loose, unstructured images live) and the output directory in the config.properties file.

Does not currently recursively navigate your input directory and file support is currently whitelisted into the code.

Sorts images into directories based on the EXIF date taken if available. 
Failing that, uses the date if it is in the filename (yyyy-MM-dd format only at the moment).
Failing that, uses the system last modified date instead.



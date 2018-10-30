Pantry - This project is no longer actively maintained. 
======
[![Build Status](https://travis-ci.org/Comcast/pantry.svg)](https://travis-ci.org/Comcast/pantry)

[http://comcast.github.io/pantry/](http://comcast.github.io/pantry/)


##Summary
Pantry is a collection of helpful java utilities for use in multi-threaded applications.  They are loosely organized by package.  You can find a general description of 
what each package does in the Packages section of this document.  For specific details, please see the javadocs

![Pantry](http://comcast.github.io/pantry/images/pantry-shield.png)

## Packages

### cleanup
This package contains interfaces which should be implemented by components who wish to be cleaned up by processes which do not have specfic shutdown hooks.

### io
This package contains useful circular buffer/queue implementations as well as other utilities which are helpful in dealing with i/o.

### process
This package contains utilities to assist with forking java processes and logging things appropriately on those forked processes.

### run
This package contains utilities to help with running multithreaded processes safely.

### test
This package contains utilities which simplify the process of doing data driven unit tests and wiring dependencies into classes under test

##Submitting Issues
Please file a github issue for any problems or feature requests (or better yet, submit a pull request!)

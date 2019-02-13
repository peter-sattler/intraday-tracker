# Aliya Financial Coding Challenge 2019

## My Design Decisions:

* At a high-level, the _intraday-tracker_ project is broken down into the following components:
    * Tracking service interface
    * Tracking service in-memory implementation
    * Simple intraday security implementation
    * Intraday tracking command-line client
* The separation of the interface from the implementation allows:
    * Clearly documented interface contract details.
    * Unit tests to be written in a straight-forward manner.
    * Facilitates the addition of new implementations in the future.
    * Allows the client to inject the dependent service implementation of choice.
* The intraday security implementation stores the symbol, low price, high price, list of prices and sum of all prices from which the average price is calculated. Since this is a financial calculation, the rounding mode is exposed to the client to guarantee required behavior.
* In order to avoid gold-plating, all classes implement the minimum required methods including hashCode(), equals() and toString() where appropriate.
* Critical inputs are validated and runtime exceptions are thrown (if necessary). Otherwise, reasonable defaults are used.
* All exceptions are documented as part of the standard java doc.
* Test coverage includes both the intraday security and tracking service implementations.
* __Note:__ Includes addition of trade date from February 13, 2019 coding review session.

## Getting Started

These instructions will get you a copy of this project up and running on your local machine. Please make sure your __JAVA_HOME__ environment variable is set to a valid JDK installation (JRE will not work). 

`unzip intraday-tracker.zip`  
`cd intraday-tracker`  
`gradlew run`


## Given Requirements:

Hello!

Thanks for your interest in Aliya. We are excited to find out more about how you approach software development. We find that the quickest way to understand how someone builds software is to look at a concrete example. To that end we'd like you to work on a short programming assignment for us. It shouldn't take more than an hour or two of your time, and will allow us to have a much more focused interview process further down the line.

At Aliya we mostly work in Java and Javascript, but feel free to implement your solution in whichever tech stack you'd prefer. Please do NOT post your submission online - you can just send us a zip or tarball.

### Assessment Criteria

We will be assessing your submission based on the following criteria:

* __Correct__ - Does your program work correct and meet the specification?
* __Understandable__ - We spend much more time reading code than writing it. Is your submission easy for us to understand? Are assumptions and design decisions documented?
* __Maintainable__ - Does your program lend itself to future enhancement. Does it have test coverage to protect against regressions.
* __Pragmatic__ - We don't spend time on features that aren't needed today. Over-engineering and "gold-plating" are red flags for us.

### The Problem

You will be building a command-line tool which tracks intraday data for an arbitrary set of securities. The tool receives security price updates in a specific format via STDIN. Every time a new line of input is received the tool will output updated intraday data, in the form of High, Low and Average. High and Low are the highest and lowest values seen for a given security, and Average is the mean of all prices seen so far for a given security.

Input is a space-delimited line of the form {TRADE DATE} {SYMBOL} {PRICE}, where {TRADE DATE} is the date on which the security was traded in _YYYY-MM-DD_ format, {SYMBOL} is the symbol for a security and price is the current price for that security.

Output is set of space-delimited lines, one per security, of the form {TRADE DATE} {SYMBOL} {HIGH} {LOW} {AVERAGE}.

Here is an example sequence of updates from the command-line tool:

`> 2019-02-12 APPL 178.44`  
`< 2019-02-12 APPL 178.44 178.44 178.44`  

`> 2019-02-11 GOOG 1149.49`  
`< 2019-02-12 APPL 178.44 178.44 178.44`  
`< 2019-02-11 GOOG 1149.49 1149.49 1149.49`  

`> 2019-02-12 APPL 178.50`  
`< 2019-02-12 APPL 178.50 178.44 178.47`  
`< 2019-02-11 GOOG 1149.49 1149.49 1149.49`  

`> 2019-02-11 GOOG 1148.10`  
`< 2019-02-12 APPL 178.50 178.44 178.47`  
`< 2019-02-11 GOOG 1149.49 1148.10 1148.80`  

`> 2019-02-12 APPL 178.45`  
`< 2019-02-12 APPL 178.50 178.44 178.46`  
`< 2019-02-11 GOOG 1149.49 1148.10 1148.80`  

`> 2019-02-12 FB 184.19`  
`< 2019-02-12 APPL 178.50 178.44 178.46`  
`< 2019-02-12 FB 184.19 184.19 184.19`  
`< 2019-02-11 GOOG 1149.49 1148.10 1148.80`  


Pete Sattler   
12 February 2019  
_peter@sattler22.net_  

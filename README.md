# prob2-plugin
[![Build Status](https://travis-ci.org/bendisposto/prob2-plugin.svg?branch=develop)](https://travis-ci.org/bendisposto/prob2-plugin)

This project was extracted from the https://github.com/bendisposto/prob2 repository. It contains the generic Eclipse and the Rodin Integration plug-ins, i.e., the mapping of Eclipse views to ProB 2.0 components are defined here. The content of the views is defined in https://github.com/bendisposto/prob2.

## Installation
The most recent build can be installed using this update site: http://www3.hhu.de/stups/rodin/prob2/nightly/

Released versions can be found here: http://nightly.cobra.cs.uni-duesseldorf.de/prob2/updates/releases/

The released versions were tested a bit more thoroughly.

## Building
Maven 3 is required to build the project:
  <pre>
  cd master
  mvn clean verify
  </pre>  

This will produce the update site for the plug-in in the folder `de.prob2.updatesite/target`

## Bugs
Because of a bug in Eclipse, Rodin with ProB 2.0 crashes on some Linux systems after using it for about 3 minutes. This issue is fixed in Rodin 3.1


(c) 2014 Jens Bendisposto et.al. , all rights reserved

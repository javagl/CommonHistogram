# common-histogram

Simple histogram Swing components.

This library is only intended for internal use. Many details of this library 
are intentionally not specified, and may change arbitrarily in the future.

![Histograms01.png](/screenshots/Histograms01.png)

These classes are basically only a thin wrapper around 
[JFreeChart](http://www.jfree.org/jfreechart/) that allow to conveniently 
create histograms with basic mouse interaction from arbitrary data sets.

Versions:

- 0.0.2 (2020-06-09)
  - Exposed `NumberHistogram` interface with `set/getBinCount`
  - Used Sturges rule for initial number of bins
  - Improved formatting options for dates, empty ranges, and large numbers

- 0.0.1:
  - Initial release


    
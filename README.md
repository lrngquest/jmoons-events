# jmoons-events

A GUI simulation of the Galilean moons of Jupiter, a Clojure learning
exercise.


## Credits

The event search; Earth, Jupiter, and Galilean moons models are
translated or (freely) paraphrased from Java code in SkyviewCafe
which all carry the following notice:

    ```
    Copyright (C) 2000-2007 by Kerry Shetline, kerry@shetline.com.

    This code is free for public use in any non-commercial application. All
    other uses are restricted without prior consent of the author, Kerry
    Shetline. The author assumes no liability for the suitability of this
    code in any application.

    2007 MAR 31   Initial release as Sky View Cafe 4.0.36.
    ```

The Swing GUI code is inspired by:
   https://github.com/stuarthalloway/programming-clojure/blob/master/src/examples/atom_snake.clj
See also notice in  core.clj.


## Usage

Normal GUI operation  (from dir containing  src/... )

    ```
    $ lein run                     # start at 00:00 UTC current date
    $ lein run  [year month day ]  # start at 00:00 UTC indicated date
    ```
Text output from CLI

    `$ lein run  [year month day N ]  # list N days' events at indicated date

Developed originally with Leiningen but nowadays one can use:

    `$ clojure -M -m jmoons-events.core


## Examples
   ```
   jmoons-events EdV$ lein run 2022 2 8 1
2022-02-08   UT
[15:14 55  I. Oc.D]
[17:58 55  I. Ec.R]
   ```

### Bugs
Any discrepancies from SykviewCafe results are my responsibility.

## License

Copyright © 2018   L. E. Vandergriff

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

"Freely you have received, freely give." Mt. 10:8


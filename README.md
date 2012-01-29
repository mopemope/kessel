# kessel

Kessel is a more clojure-fied version of (clarsec)[https://github.com/mmikulicic/clarsec], which itself is a port of Haskell's Parsec to Clojure.

Rather than rely on an external monad library, kessel relies on `clojure.algo.monads`. This has the advantage that it can be dropped into other monadic code in Clojure should that be necessary.

## Usage

There are two example parsers in the `kessel.examples`. One parses dates, and the other parses a small subset of JSON. The JSON example serves as an example of building parsers with recursive grammars.

## Caveats

Forward references for recursive rules require some hackery. Instead of using `delay` as clarsec does, kessel doesn't solve the problem for you. The JSON example uses `var` and `alter-root-var` to get around the issue, which is currently the recommended way to go.

Left recursive grammars should be avoided as they will eventually explode. There's some smart memoization that can be done to avoid this, but I haven't done it yet.

## The Future

1. Solve the explosion problem for left recursive grammars.
2. Make a better solution for forward references in recursive grammars
3. Should probably write some tests. :)

## Installation

lein jar

## License

Since this was originally a fork of clarsec and it used the Apache License version 2.0, so too does Kessel, though this is not necessarily desired.

http://www.apache.org/licenses/LICENSE-2.0.html

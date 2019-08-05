[![Build Status](https://travis-ci.org/Samourai-Wallet/boltzmann-java.svg?branch=develop)](https://travis-ci.org/Samourai-Wallet/boltzmann-java)
[![](https://jitpack.io/v/Samourai-Wallet/boltzmann-java.svg)](https://jitpack.io/#Samourai-Wallet/boltzmann-java)

# Boltzmann-Java

A Java library computing the entropy of Bitcoin transactions and the linkability of their inputs and outputs.

For a description of the metrics :

- Bitcoin Transactions & Priva
cy (part 1) : https://gist.github.com/LaurentMT/e758767ca4038ac40aaf

- Bitcoin Transactions & Privacy (part 2) : https://gist.github.com/LaurentMT/d361bca6dc52868573a2

- Bitcoin Transactions & Privacy (part 3) : https://gist.github.com/LaurentMT/e8644d5bc903f02613c6


## Usage
### Run from command-line
```
java -jar target/boltzmann-java-develop-SNAPSHOT-run.jar <txid> [maxCjIntrafeesRatio]
```

### Developers
See [src/test/java/com/samourai/boltzmann/Example.java](src/test/java/com/samourai/boltzmann/Example.java)


## Requirements
Java >= 6 or Android API level >= 21


## Build instructions
Build with maven:

```
cd boltzmann-java
mvn clean install -Dmaven.test.skip=true
```

Or retrieve from [JitPack](https://jitpack.io/#Samourai-Wallet/boltzmann-java) repository


## Resources
Boltzmann is also available for Python: https://github.com/Samourai-Wallet/boltzmann
#!/bin/bash

# The script is used to run the application. It is a simple bash script that sets the classpath and runs the application. 
# This script pass all the arguments to the application.

dir=$(dirname $0)/dist/home
java -cp "$dir/choco.jar:$dir/approximation-0.1.0.jar:$dir/argparse4j-0.9.0.jar" fr.univartois.cril.approximation.Main $@
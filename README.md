# Approximation Solver

## Overview

**Approximation** is a solver designed to tackle hard combinatorial problems by combining **relaxation and restoration** techniques with a **complete solving approach**. This methodology enhances traditional constraint solvers by introducing constraint removal strategies to facilitate partial solutions, which are then progressively restored to obtain a complete solution.

This project is developed in collaboration with:
- **Université du Luxembourg**
- **SNT (Interdisciplinary Centre for Security, Reliability, and Trust)**
- **CRIL, Univ Artois & CNRS**

## Motivation

Combinatorial Constraint Satisfaction Problems (CSP) and Constraint Optimization Problems (COP) are efficiently solved in practice using high-performance constraint solvers. However, certain instances remain **challenging**, requiring significant computation time. This is not acceptable in **industrial applications**, where time efficiency is crucial.

Alternative approaches using **incomplete techniques** (e.g., local search) lack the ability to prove **unsatisfiability** or **optimality**. Our proposed method **combines completeness and approximation**, providing faster solutions while maintaining correctness.

## Features

- Supports **normal resolution** for solving standard CSP/COP problems
- Implements **relaxation-based solving**, progressively removing constraints to simplify the problem
- Provides **constraint restoration techniques**, ensuring the problem is ultimately solved completely
- Includes multiple **constraint removal and restoration strategies** to fine-tune the approximation process
- Allows direct integration with **Choco solver**

## How It Works

The solving process follows four main steps:

1. **Solve** the problem with a limited number of steps (e.g., failure limits, iteration counts).
2. If the problem is not solved, **relax** (remove) some constraints and solve the simplified problem again.
3. If a partial solution is found, **restore** previously removed constraints and continue solving.
4. If necessary, **repeat relaxation and restoration** until a complete solution is found.

## Command-Line Interface (CLI)

The `CLI` class provides a structured way to define command-line arguments for configuring the solver. 

- **General settings**: Instance file, verbosity, timeout, output formatting
- **Normal resolution parameters**: Solving the original problem without approximation
- **Relaxation parameters**: Defining how constraints are removed and reintroduced
- **Additional solver parameters**: Arguments passed directly to the Choco solver

### Build the program 

For building the program you need and java 17 and running this command: 

```sh
./gradlew approx
```

You obtain the built jars in `dist/home/`. 


### Usage Example

To run the solver with an approximation strategy:
```sh
./irreducible.sh --approx -i instance.xml.lzma [OPTIONS]
```

To use a portfolio solver configuration using a configuration file like [here](./src/test/resources/):
```sh
./irreducible.sh --portfolio --portfolio-configuration config.txt -i instance.xml.lzma [OPTIONS] 
```

To use directly the internal solver: 
```sh
./irreducible.sh --default -- [OPTIONS] 
```

For printing the help message of the command line interface: 

```sh
./irreducible.sh --help 
```



## Relaxation and Restoration Strategies

### Relaxation Strategies

To determine which constraints should be removed, the solver supports multiple strategies:

- **Slow relaxation**: Removes **one constraint at a time** for a gradual approximation.
- **Grouped relaxation**: Removes a **syntactically related group** of constraints.
- **Block relaxation**: Removes a **semantically related block** of constraints.

### Constraint Selection Metrics

To decide which constraint(s) to remove, the solver implements different heuristic metrics:

- `neff`: Measures the number of times a constraint is effective during filtering.
- `nback`: Counts how often a constraint causes backtracking.
- `wdeg`: Uses the well-known **weighted degree heuristic**.

### Restoration Strategies

To reintroduce removed constraints, the solver offers:

- **Aggressive Restoration** (`AGGRESSIVE_RESTORE`): Restores all removed constraints once a subproblem solution is found.
- **Progressive Restoration** (`PROGRESSIVE_RESTORE`): Gradually reintroduces the most recently removed constraints first.

## License

This project is licensed under the **GNU Lesser General Public License (LGPL)**, allowing redistribution and modification under the terms of the LGPLv3 license or later versions.

For full license details, see: [GNU LGPL](http://www.gnu.org/licenses/lgpl.html)

## Authors

- **Thibault Falque** (Université du Luxembourg, SNT)
- **Pierre Talbot** (Université du Luxembourg)
- **Romain Wallon** (CRIL, Univ Artois & CNRS)

## Contact

For questions or contributions, please open an issue or submit a pull request on the **GitHub repository**.


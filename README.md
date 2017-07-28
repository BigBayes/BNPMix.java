# MCMC for Bayesian Non-Parametric Mixture Models 

## What's this about

This package has been written by [Yee Whye Teh](https://www.stats.ox.ac.uk/~teh) for the articles *MCMC for Normalized Random Measure Mixture Models* and *On a class of σ-stable Poisson–Kingman models and an effective marginalized sampler*.
The folder `src/nrmix` is related to the first article and `src/qmix`to the second article.
This package has been cleaned and uploaded by [Emile Mathieu](http://emilemathieu.fr).

Do not hesitate to create pull requests for enhancements or to open issues.

## Installation and requirements

Requirements:

* Java JDK `[1.8.x]` (Can be found on [Oracle website](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html))

In a terminal:

```bash
git clone https://github.com/emilemathieu/NRMMM.git
```

## Algorithms implemented

* Marginalized Samplers:
  * Neal’s Algorithm 8 generalized
  * The Reuse algorithm
* Conditional Slice Sampler

## Example

You can run the Reuse conditional sampler on the galaxy dataset modelled as a mixtures of Gaussian with a normalized generalized Gamma prior:

```bash
cd out/artifacts/BNPMix_jar
java -cp BNPMix.jar nrmix.galaxy
```

## Compile project
With IntelliJ:
* File > New > Project from Existing Sources... > Navigate to the NRMMM folder > Open
* File > Project Structure > Project Settings > Artifacts > Click green plus sign > Jar > From modules with dependencies... > Extract to the target Jar > Ok
* Build > Build Artifact...


## Reference

*  [Favaro, Teh, *MCMC for Normalized Random Measure Mixture Models*, 2013](https://www.stats.ox.ac.uk/~teh/research/npbayes/FavTeh2013a.pdf)
*  [Favaro, Lomeli, Teh, *On a class of σ-stable Poisson–Kingman models and an effective marginalized sampler*, 2015](https://link.springer.com/article/10.1007/s11222-014-9499-4)

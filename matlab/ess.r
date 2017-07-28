library(coda)

ess <- function(f,dims) {
  t <- read.table(paste(f,".parameters",sep=""))
  a <- mcmc(t[,dims])
  return(effectiveSize(a))
}



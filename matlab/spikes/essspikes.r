
library(coda)

ess <- function(f) {
  t <- read.table(paste(f,".numclusters",sep=""))
  a <- mcmc(t[,1]);
  s <- effectiveSize(a);
  write(s,file=paste(f,".ess",sep=""));
}

ess("spikesdata/spikesslice"); 
ess("spikesdata/spikesneal81"); 
ess("spikesdata/spikesneal82"); 
ess("spikesdata/spikesneal83"); 
ess("spikesdata/spikesneal84"); 
ess("spikesdata/spikesneal85"); 
ess("spikesdata/spikesneal8r1"); 
ess("spikesdata/spikesneal8r2"); 
ess("spikesdata/spikesneal8r3"); 
ess("spikesdata/spikesneal8r4"); 
ess("spikesdata/spikesneal8r5"); 
ess("spikesdata/spikesneal8r10"); 
ess("spikesdata/spikesneal8r20"); 






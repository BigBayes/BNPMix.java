
library(coda)

ess <- function(f) {
  t <- read.table(paste(f,".parameters",sep=""))
  a <- mcmc(t[,1:3]);
  s <- effectiveSize(a);
  write(s,file=paste(f,".ess",sep=""));
}

ess("data/nrmix.geyser.ns.slice");
ess("data/nrmix.geyser.ns.neal81");
ess("data/nrmix.geyser.ns.neal82");
ess("data/nrmix.geyser.ns.neal83");
ess("data/nrmix.geyser.ns.neal84");
ess("data/nrmix.geyser.ns.neal85");
ess("data/nrmix.geyser.ns.neal8r1");
ess("data/nrmix.geyser.ns.neal8r2");
ess("data/nrmix.geyser.ns.neal8r3");
ess("data/nrmix.geyser.ns.neal8r4");
ess("data/nrmix.geyser.ns.neal8r5");

ess("spikesdata/s10x1000.slice"); 
ess("spikesdata/s10x1000.neal81"); 
ess("spikesdata/s10x1000.neal82"); 
ess("spikesdata/s10x1000.neal83"); 
ess("spikesdata/s10x1000.neal84"); 
ess("spikesdata/s10x1000.neal85"); 
ess("spikesdata/s10x1000.neal8r1"); 
ess("spikesdata/s10x1000.neal8r2"); 
ess("spikesdata/s10x1000.neal8r3"); 
ess("spikesdata/s10x1000.neal8r4"); 
ess("spikesdata/s10x1000.neal8r5"); 





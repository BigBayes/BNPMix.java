
library(coda)

ess <- function(f) {
  t <- read.table(paste(f,".parameters",sep=""))
  a <- mcmc(t[,1:90]);
  s <- effectiveSize(a);
  write(s,file=paste(f,".ess",sep=""));
}

ess("data/nrmix.galaxy.cm.slice");
ess("data/nrmix.galaxy.cs.slice");
ess("data/nrmix.galaxy.ns.slice");
ess("data/nrmix.galaxy.cm.neal81");
ess("data/nrmix.galaxy.cm.neal8r1");
ess("data/nrmix.galaxy.cs.neal81");
ess("data/nrmix.galaxy.cs.neal82");
ess("data/nrmix.galaxy.cs.neal83");
ess("data/nrmix.galaxy.cs.neal84");
ess("data/nrmix.galaxy.cs.neal85");
ess("data/nrmix.galaxy.cs.neal8r1");
ess("data/nrmix.galaxy.cs.neal8r2");
ess("data/nrmix.galaxy.cs.neal8r3");
ess("data/nrmix.galaxy.cs.neal8r4");
ess("data/nrmix.galaxy.cs.neal8r5");
ess("data/nrmix.galaxy.ns.neal81");
ess("data/nrmix.galaxy.ns.neal82");
ess("data/nrmix.galaxy.ns.neal83");
ess("data/nrmix.galaxy.ns.neal84");
ess("data/nrmix.galaxy.ns.neal85");
ess("data/nrmix.galaxy.ns.neal8r1");
ess("data/nrmix.galaxy.ns.neal8r2");
ess("data/nrmix.galaxy.ns.neal8r3");
ess("data/nrmix.galaxy.ns.neal8r4");
ess("data/nrmix.galaxy.ns.neal8r5");

ess("data/nrmix.logacid.cm.slice");
ess("data/nrmix.logacid.cs.slice");
ess("data/nrmix.logacid.ns.slice");
ess("data/nrmix.logacid.cm.neal81");
ess("data/nrmix.logacid.cm.neal8r1");
ess("data/nrmix.logacid.cs.neal81");
ess("data/nrmix.logacid.cs.neal82");
ess("data/nrmix.logacid.cs.neal83");
ess("data/nrmix.logacid.cs.neal84");
ess("data/nrmix.logacid.cs.neal85");
ess("data/nrmix.logacid.cs.neal8r1");
ess("data/nrmix.logacid.cs.neal8r2");
ess("data/nrmix.logacid.cs.neal8r3");
ess("data/nrmix.logacid.cs.neal8r4");
ess("data/nrmix.logacid.cs.neal8r5");
ess("data/nrmix.logacid.ns.neal81");
ess("data/nrmix.logacid.ns.neal82");
ess("data/nrmix.logacid.ns.neal83");
ess("data/nrmix.logacid.ns.neal84");
ess("data/nrmix.logacid.ns.neal85");
ess("data/nrmix.logacid.ns.neal8r1");
ess("data/nrmix.logacid.ns.neal8r2");
ess("data/nrmix.logacid.ns.neal8r3");
ess("data/nrmix.logacid.ns.neal8r4");
ess("data/nrmix.logacid.ns.neal8r5");

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





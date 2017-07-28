

tu   = readqmixdata(1,'statcomp/galaxy_qggp_tu');
vz   = readqmixdata(1,'statcomp/galaxy_qggp_vz');
marg = readqmixdata(1,'statcomp/galaxy_qggp_marg');
logn = readqmixdata(1,'statcomp/galaxy_qggp_lognormal');

%tu = vispartition(tu); pause
%vz = vispartition(vz); pause
marg = vispartition(marg,labels); 
logn = vispartition(logn,labels); 

plotqmix1d(galaxy,tu,'statcomp/galaxy_tu_');
plotqmix1d(galaxy,logn,'statcomp/galaxy_logn_');



tu   = readqmixdata(6,'statcomp/oliveoil_tu');
vz   = readqmixdata(6,'statcomp/oliveoil_vz');
marg = readqmixdata(6,'statcomp/oliveoil_marg');
logn = readqmixdata(6,'statcomp/oliveoil_lognormal');

%tu = vispartition(tu); pause
%vz = vispartition(vz); pause
marg = vispartition(marg,labels); 
logn = vispartition(logn,labels); 



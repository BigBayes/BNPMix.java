%load HPLCforweb
%fdata = HPLCforweb.data;

load oliveoil

numdim = 6;
numdata = 120;

data = Y(1:numdata,1:numdim);
fname = ['oliveoil/s' num2str(numdim) 'x' num2str(numdata)];

if 0
pitmanyor = runqmix('Reuse','PitmanYor',data,false,'pitmanyor',10000,10000,100,100);
constant = runqmix('Reuse','Constant',data,false,'constant',10000,10000,100,100);
lognormal = runqmix('Reuse','LogNormal',data,false,'lognormal',10000,10000,100,100);
end

pitmanyortu = runqmix('Reuse','PitmanYor-TU',data,false,'olive',10000,10000,10,10);
pitmanyorvz = runqmix('Reuse','PitmanYor-VZ',data,false,'olive',10000,10000,10,10);
pitmanyormarg = runqmix('Reuse','PitmanYor-marg',data,false,'olive',10000,10000,10,10);
constant = runqmix('Reuse','Constant',data,false,'olive',10000,10000,10,10);
lognormal = runqmix('Reuse','LogNormal',data,false,'olive',10000,10000,10,10);

return
figure(4); vispartition(constant2);
figure(5); vispartition(lognormal2);
figure(6); vispartition(pitmanyor2);

nrmixfixed = runnrmix_fixed('reuse',false,true,data,true,'olive',1000,1000,10,10);
nrmix = runnrmix('reuse',false,true,data,true,'olive',10000,10000,10,10);

nrmix = runnrmix('reuse',false,true,data,false,'olive',10000,10000,10,10);

nrmix2012 =runnrmix2012('reuse',false,true,data,true,'olive',10000,10000,10,10);


vis
oilneal.cocluster = cocluster(oilneal.assignments);

oilneal.tree = linkage(1-oilneal.cocluster);

plotdendrogram(oilneal.tree);

graphpartition(fdata,oilneal,.9,labels);


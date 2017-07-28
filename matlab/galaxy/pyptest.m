load -ascii data/galaxy.data
galaxy = galaxy/1e4;

%nrmgalaxy = runnrmix('neal8r',false,true,galaxy,false,'data/galaxy.nrm',100,100,20,5);
pypgalaxy = runpymix('neal8r',false,true,galaxy,false,'output/galaxy.pyp',10000,10000,20,5);


%save data/pyptest.mat nrmgalaxy pypgalaxy

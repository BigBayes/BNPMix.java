load -ascii galaxy.data
galaxy = galaxy/1e4;

galaxyslicecm = runnrmix('slice',true,false,galaxy,false,'data/galaxy.cm',10000,10000,20,1e6);
galaxyslicecs = runnrmix('slice',true,true,galaxy,false,'data/galaxy.cs',10000,10000,20,1e6);
galaxyslicens = runnrmix('slice',false,true,galaxy,false,'data/galaxy.ns',10000,10000,20,1e6);

galaxyneal81cm = runnrmix('neal8',true,false,galaxy,false,'data/galaxy.cm',10000,10000,20,1);
galaxyneal81cs = runnrmix('neal8',true,true,galaxy,false,'data/galaxy.cs',10000,10000,20,1);
galaxyneal82cs = runnrmix('neal8',true,true,galaxy,false,'data/galaxy.cs',10000,10000,20,2);
galaxyneal83cs = runnrmix('neal8',true,true,galaxy,false,'data/galaxy.cs',10000,10000,20,3);
galaxyneal84cs = runnrmix('neal8',true,true,galaxy,false,'data/galaxy.cs',10000,10000,20,4);
galaxyneal85cs = runnrmix('neal8',true,true,galaxy,false,'data/galaxy.cs',10000,10000,20,5);
galaxyneal81ns = runnrmix('neal8',false,true,galaxy,false,'data/galaxy.ns',10000,10000,20,1);
galaxyneal82ns = runnrmix('neal8',false,true,galaxy,false,'data/galaxy.ns',10000,10000,20,2);
galaxyneal83ns = runnrmix('neal8',false,true,galaxy,false,'data/galaxy.ns',10000,10000,20,3);
galaxyneal84ns = runnrmix('neal8',false,true,galaxy,false,'data/galaxy.ns',10000,10000,20,4);
galaxyneal85ns = runnrmix('neal8',false,true,galaxy,false,'data/galaxy.ns',10000,10000,20,5);

galaxyneal8r1cm = runnrmix('neal8r',true,false,galaxy,false,'data/galaxy.cm',10000,10000,20,1);
galaxyneal8r1cs = runnrmix('neal8r',true,true,galaxy,false,'data/galaxy.cs',10000,10000,20,1);
galaxyneal8r2cs = runnrmix('neal8r',true,true,galaxy,false,'data/galaxy.cs',10000,10000,20,2);
galaxyneal8r3cs = runnrmix('neal8r',true,true,galaxy,false,'data/galaxy.cs',10000,10000,20,3);
galaxyneal8r4cs = runnrmix('neal8r',true,true,galaxy,false,'data/galaxy.cs',10000,10000,20,4);
galaxyneal8r5cs = runnrmix('neal8r',true,true,galaxy,false,'data/galaxy.cs',10000,10000,20,5);
galaxyneal8r1ns = runnrmix('neal8r',false,true,galaxy,false,'data/galaxy.ns',10000,10000,20,1);
galaxyneal8r2ns = runnrmix('neal8r',false,true,galaxy,false,'data/galaxy.ns',10000,10000,20,2);
galaxyneal8r3ns = runnrmix('neal8r',false,true,galaxy,false,'data/galaxy.ns',10000,10000,20,3);
galaxyneal8r4ns = runnrmix('neal8r',false,true,galaxy,false,'data/galaxy.ns',10000,10000,20,4);
galaxyneal8r5ns = runnrmix('neal8r',false,true,galaxy,false,'data/galaxy.ns',10000,10000,20,5);


save data/galaxy.mat galaxy*ns galaxy*cs galaxy*cm

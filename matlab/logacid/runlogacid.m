load -ascii logacid.data

logacidslicecm = runnrmix('slice',true,false,logacid,false,'data/logacid.cm',10000,10000,20,1e6);
logacidslicecs = runnrmix('slice',true,true,logacid,false,'data/logacid.cs',10000,10000,20,1e6);
logacidslicens = runnrmix('slice',false,true,logacid,false,'data/logacid.ns',10000,10000,20,1e6);

logacidneal81cm = runnrmix('neal8',true,false,logacid,false,'data/logacid.cm',10000,10000,20,1);
logacidneal81cs = runnrmix('neal8',true,true,logacid,false,'data/logacid.cs',10000,10000,20,1);
logacidneal82cs = runnrmix('neal8',true,true,logacid,false,'data/logacid.cs',10000,10000,20,2);
logacidneal83cs = runnrmix('neal8',true,true,logacid,false,'data/logacid.cs',10000,10000,20,3);
logacidneal84cs = runnrmix('neal8',true,true,logacid,false,'data/logacid.cs',10000,10000,20,4);
logacidneal85cs = runnrmix('neal8',true,true,logacid,false,'data/logacid.cs',10000,10000,20,5);
logacidneal81ns = runnrmix('neal8',false,true,logacid,false,'data/logacid.ns',10000,10000,20,1);
logacidneal82ns = runnrmix('neal8',false,true,logacid,false,'data/logacid.ns',10000,10000,20,2);
logacidneal83ns = runnrmix('neal8',false,true,logacid,false,'data/logacid.ns',10000,10000,20,3);
logacidneal84ns = runnrmix('neal8',false,true,logacid,false,'data/logacid.ns',10000,10000,20,4);
logacidneal85ns = runnrmix('neal8',false,true,logacid,false,'data/logacid.ns',10000,10000,20,5);

logacidneal8r1cm = runnrmix('neal8r',true,false,logacid,false,'data/logacid.cm',10000,10000,20,1);
logacidneal8r1cs = runnrmix('neal8r',true,true,logacid,false,'data/logacid.cs',10000,10000,20,1);
logacidneal8r2cs = runnrmix('neal8r',true,true,logacid,false,'data/logacid.cs',10000,10000,20,2);
logacidneal8r3cs = runnrmix('neal8r',true,true,logacid,false,'data/logacid.cs',10000,10000,20,3);
logacidneal8r4cs = runnrmix('neal8r',true,true,logacid,false,'data/logacid.cs',10000,10000,20,4);
logacidneal8r5cs = runnrmix('neal8r',true,true,logacid,false,'data/logacid.cs',10000,10000,20,5);
logacidneal8r1ns = runnrmix('neal8r',false,true,logacid,false,'data/logacid.ns',10000,10000,20,1);
logacidneal8r2ns = runnrmix('neal8r',false,true,logacid,false,'data/logacid.ns',10000,10000,20,2);
logacidneal8r3ns = runnrmix('neal8r',false,true,logacid,false,'data/logacid.ns',10000,10000,20,3);
logacidneal8r4ns = runnrmix('neal8r',false,true,logacid,false,'data/logacid.ns',10000,10000,20,4);
logacidneal8r5ns = runnrmix('neal8r',false,true,logacid,false,'data/logacid.ns',10000,10000,20,5);


save data/logacid.mat logacid*ns logacid*cs logacid*cm

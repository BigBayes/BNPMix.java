load -ascii geyser.data

geyserslicens = runnrmix('slice',false,true,geyser,false,'data/geyser.ns',10000,10000,20,1e6);

geyserneal81ns = runnrmix('neal8',false,true,geyser,false,'data/geyser.ns',10000,10000,20,1);
geyserneal82ns = runnrmix('neal8',false,true,geyser,false,'data/geyser.ns',10000,10000,20,2);
geyserneal83ns = runnrmix('neal8',false,true,geyser,false,'data/geyser.ns',10000,10000,20,3);
geyserneal84ns = runnrmix('neal8',false,true,geyser,false,'data/geyser.ns',10000,10000,20,4);
geyserneal85ns = runnrmix('neal8',false,true,geyser,false,'data/geyser.ns',10000,10000,20,5);

geyserneal8r1ns = runnrmix('neal8r',false,true,geyser,false,'data/geyser.ns',10000,10000,20,1);
geyserneal8r2ns = runnrmix('neal8r',false,true,geyser,false,'data/geyser.ns',10000,10000,20,2);
geyserneal8r3ns = runnrmix('neal8r',false,true,geyser,false,'data/geyser.ns',10000,10000,20,3);
geyserneal8r4ns = runnrmix('neal8r',false,true,geyser,false,'data/geyser.ns',10000,10000,20,4);
geyserneal8r5ns = runnrmix('neal8r',false,true,geyser,false,'data/geyser.ns',10000,10000,20,5);


save data/geyser.mat geyser*ns

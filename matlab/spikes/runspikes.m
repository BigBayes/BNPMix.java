load spikespca

numdim = 6;
numdata = 2000;
data = Y(1:numdata,1:numdim);
fname = ['spikesdata/s' num2str(numdim) 'x' num2str(numdata)];

spikesneal8r20 = runnrmix('neal8r',false,true,data,true,fname,10000,10000,20,20);
%spikesslice = runnrmix('slice',false,true,data,true,fname,10000,10000,20,1e6);

%spikesneal8r1 = runnrmix('neal8r',false,true,data,true,fname,10000,10000,20,1);
%spikesneal8r2 = runnrmix('neal8r',false,true,data,true,fname,10000,10000,20,2);
%spikesneal8r3 = runnrmix('neal8r',false,true,data,true,fname,10000,10000,20,3);
%spikesneal8r4 = runnrmix('neal8r',false,true,data,true,fname,10000,10000,20,4);
%spikesneal8r5 = runnrmix('neal8r',false,true,data,true,fname,10000,10000,20,5);
%
%spikesneal81 = runnrmix('neal8',false,true,data,true,fname,10000,10000,20,1);
%spikesneal82 = runnrmix('neal8',false,true,data,true,fname,10000,10000,20,2);
%spikesneal83 = runnrmix('neal8',false,true,data,true,fname,10000,10000,20,3);
%spikesneal84 = runnrmix('neal8',false,true,data,true,fname,10000,10000,20,4);
%spikesneal85 = runnrmix('neal8',false,true,data,true,fname,10000,10000,20,5);

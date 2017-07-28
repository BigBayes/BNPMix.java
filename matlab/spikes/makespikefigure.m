load spikesneal8r20
load spikespca
numdim = 6;
numdata = 2000;
data = Y(1:numdata,1:numdim);

[r,subset]=plotspikes(.5,100,data,spikesneal8r20,mu,D(1:6),V(:,1:6),{})
plotspikes(.95,10,data,spikesneal8r20,mu,D(1:6),V(:,1:6),subset(4:5))

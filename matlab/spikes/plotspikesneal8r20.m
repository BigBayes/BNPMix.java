
load spikesneal8r20
load spikespca
numdim = 6;
numdata = 2000;
data = Y(1:numdata,1:numdim);

file = 'spikes';

[spikesneal8r20 smaller] = plotspikes(.5,100,Y(1:2000,:),spikesneal8r20,mu,D,V,{});
[spikesneal8r20] = plotspikes(.95,10,Y(1:2000,:),spikesneal8r20,mu,D,V,smaller(4:5));

figure(1)
set(gcf,'paperposition',[0.25 2.5 8 6]);
print -depsc spikescoclustertree2

figure(2)
set(gcf,'paperposition',[1 1 10 6]);
print -depsc spikesclusters2

figure(3)
hist(spikesneal8r20.numclusters,15:25);
title('Number of clusters');
set(gcf,'paperposition',[1 1 4 3]); print('-depsc',[file '-numclusters.eps']);

hist(log(spikesneal8r20.alpha),30);
title('log(a)')
set(gcf,'paperposition',[1 1 4 3]); print('-depsc',[file '-logalpha.eps']); 

hist((spikesneal8r20.alpha),30)
title('a')
set(gcf,'paperposition',[1 1 4 3]); print('-depsc',[file '-alpha.eps']); 

hist(spikesneal8r20.sigma,30)
title('\sigma')
set(gcf,'paperposition',[1 1 4 3]); print('-depsc',[file '-sigma.eps']); 



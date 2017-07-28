function plotnrmix2d(data,result,name)

figure
n = 60;
x = result.predx(1:n,1);
y = result.predx(1:n:n*n,2);
predmin = min(result.predmean);
predmax = max(result.predmean);
predstep = (predmax-predmin)/30;
contour(x,y,reshape(result.predmean,n,n)',predmin+.5*predstep:predstep:predmax);
hold on
plot(data(:,1),data(:,2),'k+');
hold off
axis off
if nargin==3,
  set(gcf,'paperposition',[1 1 4 3]); 
  print('-depsc',['figures/' name '-predictive']);
end

figure
hist(result.numclusters,1:30);
title('Number of clusters');
if nargin==3, 
  set(gcf,'paperposition',[1 1 4 3]); 
  print('-depsc',[file '-numclusters.eps']); 
end

figure
hist(log10(1+result.numemptyclusters),30);
title('Number of empty clusters');
set(gca,'xtick',0:6);
set(gca,'xticklabel',{'10^0' '10^1' '10^2' '10^3' '10^4' '10^5' '10^6'});
if nargin==3, set(gcf,'paperposition',[1 1 4 3]); print('-depsc',[file '-numemptyclusters.eps']); end

figure
hist(log(result.alpha),30)
title('log(a)')
if nargin==3, set(gcf,'paperposition',[1 1 4 3]); print('-depsc',[file '-logalpha.eps']); end

figure
hist((result.alpha),30)
title('a')
if nargin==3, set(gcf,'paperposition',[1 1 4 3]); print('-depsc',[file '-alpha.eps']); end

figure
hist(result.sigma,30)
title('\sigma')
if nargin==3, set(gcf,'paperposition',[1 1 4 3]); print('-depsc',[file '-sigma.eps']); end


figure
hist(log(result.tau),30)
title('log(\tau)')
if nargin==3, set(gcf,'paperposition',[1 1 4 3]); print('-depsc',[file '-logtau.eps']); end

figure
hist(log(result.baseinvscale),30)
title('log(\beta_0)')
if nargin==3, set(gcf,'paperposition',[1 1 4 3]); print('-depsc',[file '-logbeta0.eps']); end

figure
hist((result.baseinvscale),30)
title('\beta_0')
if nargin==3, set(gcf,'paperposition',[1 1 4 3]); print('-depsc',[file '-beta0.eps']); end


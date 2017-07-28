function plotnrmix(data,result,file)


numdata = length(data);
figure
pp = exp(result.logpred);
qq = quantile(exp(result.logpred),[0.025 .5 .975]);
h = fill([result.pred fliplr(result.pred)],[qq(1,:) fliplr(qq(3,:))],...
        [.8 .8 .8],'edgecolor',[.8 .8 .8]);
hold on
%set(h,'edgealpha',.8,'facealpha',.8);
h = plot(result.pred,result.meanpred,'color',[0 0 1],'linewidth',2);
xpred = result.pred(1):(result.pred(end)-result.pred(1))/30:result.pred(end);
w = xpred(2)-xpred(1);
h = bar(xpred,hist(data,xpred)/length(data)/w,1);
set(get(h,'children'),'facecolor','none');
plot(data,zeros(1,numdata),'k+');
hold off
a = axis;
a(1) = result.pred(1);
a(2) = result.pred(end);
axis(a);
set(gca,'xtick',[],'ytick',[]);
title('Predictive Density');
if nargin==3, 
  set(gcf,'paperposition',[1 1 4 3]); 
  print('-depsc',[file '-predictive.eps']); 
end

figure
asgn = zeros(numdata,numdata);
for i=1:size(result.assignments,1)
  a = result.assignments(double(i)*ones(1,numdata),:);
  asgn = asgn + double(a==a');
end
asgn = asgn - diag(diag(asgn));
asgn = asgn + max(asgn(:))*eye(size(asgn));
asgn = asgn/size(result.assignments,1);
plotcocluster(data,asgn);
title('Co-clustering');
if nargin==3, set(gcf,'paperposition',[1 1 4 3]); print('-depsc',[file '-coassignments.eps']); end

figure
hist(result.numclusters,1:30);
title('Number of clusters');
if nargin==3, set(gcf,'paperposition',[1 1 4 3]); print('-depsc',[file '-numclusters.eps']); end

figure
hist(log10(1+result.numemptyclusters),30);
title('Number of empty clusters');
set(gca,'xtick',0:9);
set(gca,'xticklabel',{'10^0' '10^1' '10^2' '10^3' '10^4' '10^5' '10^6' '10^7' '10^8' '10^9'})
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




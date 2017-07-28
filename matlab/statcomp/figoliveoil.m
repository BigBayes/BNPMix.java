threshold = .95;
minsize = 1;
data = Y;
loadings = V;
vars = D;
result = readqmixdata(6,'statcomp/oliveoil_lognormal');

ll = unique(labels);
for i=1:length(ll)
  labels(labels==ll(i)) = i;
end
colmap = colormap;
col = colmap(round(1:((end-1)/(length(ll)-1)):end),:);
col = col(labels,:);

plotprofiles(threshold,minsize,data,result,col,mu,vars,loadings);

figure(1)
set(gcf,'paperposition',[1 1 5 5]);
print -depsc statcomp/oliveoildendrogram

figure(2)
set(gcf,'paperposition',[1 1 5 5]);
print -depsc statcomp/oliveoilclusters



function [result] = plottree(result)

if ~isfield(result,'tree')
  [t result] = coclustertree(result);
else
  t = result.tree;
end

n = (length(t.distance)+1)/2;

clf
axes('position',[.05 .05 .45 .9]);
[handles, order] = plotdendrogram(t);
axis equal
axis([0 n 0 n]);
axis off

result.dendrogramorder = order;

axes('position',[.503 .05 .45 .9]);
imagesc(n:-1:1,n:-1:1,result.cocluster(order,order));
axis([-n-1-ceil(n/100) n 0 n+1]);
set(gca,'xtick',[]);
set(gca,'ytick',[]);
colormap hot
c = colormap;
colormap(flipud(c));
axis equal
axis([0 n 0 n])

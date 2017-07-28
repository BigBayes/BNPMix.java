function [result,subset] = plotprofiles(threshold,minsize,data,result,col,mu,vars,loadings);

figure(1)
clf
tree = result.tree;
n = (length(tree.distance)+1)/2;
d = tree.distance;
p = tree.parent;
index = find(d(1:end-1)<threshold & d(p(1:end-1))>threshold);
num = length(index)


[h order location] = plotlabelleddendrogram(tree,col,'linewidth',2);
hold on
plot([threshold threshold],[0 2*n],'k');
hold off

[loc i] = sort(location(index),'descend');
subset = tree.subset(index(i));
loc
subset


data = mu(ones(1,size(data,1)),:)+data*diag(sqrt(vars(:)))*loadings(:,:)';
numdim = size(data,2);
dmin = min(data(:));
dmax = max(data(:));
range = dmax-dmin;
dmin = dmin-.05*range;
dmax = dmax+.05*range;


figure(2)
clf
k = 0;
for i=1:num
  k = k+1;
  a = axes('position',[0.025 .05+.9*(num-k)/num .95 .8/num]);
  plot([1 numdim],[0 0],'k');
  hold on
  for j=1:length(subset{i})
    plot(data(subset{i}(j),:),'color',col(subset{i}(j),:));
  end
  d = data(subset{i},:);
  text(ceil(numdim/45),.8*dmax,[num2str(i) ': size = ' num2str(length(subset{i}))]);
  axis([1 numdim dmin dmax]);
  set(a,'xtick',[],'ytick',[]);
  hold off
end



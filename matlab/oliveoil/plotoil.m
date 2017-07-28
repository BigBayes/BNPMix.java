function [result,subset] = plottreeclusters(threshold,minsize,data,result,mu,vars,loadings,smaller);

figure(1)
clf
[ta ca result] = plottree2(result);

tree = result.tree;
o = result.dendrogramorder;
order = zeros(size(o));
order(o) = length(o):-1:1;

n = (length(tree.distance)+1)/2;
d = tree.distance;
p = tree.parent;
index = find(d(1:end-1)<threshold & d(p(1:end-1))>threshold);
num = length(index)

subset = cell(1,num);
loc = zeros(1,num);
for i=1:num
  subset{i} = tree.subset{index(i)};
  loc(i) = mean(order(subset{i}));
end
i = find(cellfun(@length,subset)>minsize);
subset = subset(i);
loc = loc(i);
num = length(i)
length(cat(2,subset{:}))

[loc i] = sort(loc,'ascend');
subset = subset(i);
[loc 
cellfun(@length,subset)]

data = mu(ones(1,size(data,1)),:)+data*diag(sqrt(vars(:)))*loadings(:,:)';
numdim = size(data,2);
dmin = min(data(:));
dmax = max(data(:));
figure(2)
clf
nr = ceil((num+length(smaller))/2);
k = 0;
for i=1:num
  k = k+1;
  if k<=nr
    a = axes('position',[0.025 .05+.9*(nr-k)/nr .45 .8/nr]);
  else
    a = axes('position',[0.5 .05+.9*(2*nr-k)/nr .45 .8/nr]);
  end
  d = data(subset{i},:);
  plot([1 numdim],[0 0],'k');
  hold on
  for j=1:3,
    plot([numdim/4*j numdim/4*j],[dmin dmax],'k');
  end
  plot(d','color',[.75 .75 .75]);
  plot(mean(d,1),'r','linewidth',3);
  text(ceil(numdim/45),.8*dmax,[num2str(i) ': size = ' num2str(length(subset{i}))]);
  axis([1 numdim dmin dmax]);
  set(a,'xtick',[],'ytick',[]);
  hold off
  for s=1:length(smaller)
    if length(intersect(smaller{s},subset{i}))>0,
      k=k+1;
      if k<=nr
        a = axes('position',[0.025 .05+.9*(nr-k)/nr .45 .8/nr]);
      else
        a = axes('position',[0.5 .05+.9*(2*nr-k)/nr .45 .8/nr]);
      end
      d = data(smaller{s},:);
      plot([1 numdim],[0 0],'k');
      hold on
      for j=1:3,
        plot([numdim/4*j numdim/4*j],[dmin dmax],'k');
      end
      plot(d','color',[.75 .75 .75]);
      plot(mean(d,1),'r','linewidth',3);
      text(1,.8*dmax,[num2str(i) ('a'+s-1) ': size = ' num2str(length(smaller{s}))]);
      axis([1 numdim dmin dmax]);
      set(a,'xtick',[],'ytick',[]);
      hold off
    end
  end
end

axes(ta)
hold on
axes(ca)
hold on
for i=1:num
  mn = n+1-min(order(subset{i}));
  mx = n+1-max(order(subset{i}));
  mid = .5*(mx+mn);
  axes(ta)
  plot([0 -.03 0],[mn mid mx],'g','linewidth',2);
  text(-.06,mid,num2str(i));
  axes(ca)
  plot(n+1-[mn n mn],n+1-[mn mid mx],'g','linewidth',2);
  for s=1:length(smaller)
    if length(intersect(smaller{s},subset{i}))>0,
      smn = n+1-min(order(smaller{s}));
      smx = n+1-max(order(smaller{s}));
      smid = .5*(smx+smn);
      axes(ca)
      plot(n+1-[mn .2*n+.8*mn mn],n+1-[smn smid smx],'g','linewidth',1);
      t=text(n-50-(.2*n+.8*mn),n+1-smid,[num2str(i) ('a'+s-1)]);
      set(t,'HorizontalAlignment','center','VerticalAlignment','middle','fontsize',8);
    end
  end
end 

hold off


function [ta,ca,result] = plottree2(result)

if ~isfield(result,'tree')
  [t result] = coclustertree(result);
else
  t = result.tree;
end

n = (length(t.distance)+1)/2;

z=[t.children(:,n+1:2*n-1)' t.distance(n+1:2*n-1)'/max(t.distance(n+1:2*n-1))];

clf
ta=axes('position',[.05 .05 .45 .9]);
[th i p] = dendrogram(z,0,'orientation','left');
axis square
axis([-.1 1 0 n]);
axis off

result.dendrogramorder = p;

ca=axes('position',[.503 .05 .45 .9]);
imagesc(n:-1:1,n:-1:1,result.cocluster(p,p),[0 1]);
axis([-n-1-ceil(n/100) n 0 n+1]);
set(gca,'xtick',[]);
set(gca,'ytick',[]);
colormap gray
c = colormap;
colormap(flipud(c));
axis equal
axis([0 n 0 n])


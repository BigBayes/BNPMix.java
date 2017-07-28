function graphplotpartition(data,result,level,color);

partition = cuttree(result.tree,level);
ll = length(partition);
s = ceil(sqrt(ll));
t = ceil(ll/s);
colors = 'brkm';
for k = 1:length(partition)
  ii = partition{k};
  subplot(s,t,k);
  for c=1:max(color)
    jj = ii(find(color(ii)==c));
    if length(jj)>0
      plot(0:4000,data(jj,:)',colors(c)); 
      hold on
    end
  end
  hold off
  axis([0 4000 0 25]);
end

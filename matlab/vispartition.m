function result = vispartition(result,labels)

result.cocluster = cocluster(result.assignments);

result.tree = linkage(1-result.cocluster);

if nargin==1
  [h result.leaforder result.leaflocation] = plotdendrogram(result.tree);
else
  ll = unique(labels);
  for i=1:length(ll)
    labels(labels==ll(i)) = i;
  end
  colmap = colormap;
  col = colmap(round(1:((end-1)/(length(ll)-1)):end),:);
  plotlabelleddendrogram(result.tree,col(labels,:),'linewidth',2);
end


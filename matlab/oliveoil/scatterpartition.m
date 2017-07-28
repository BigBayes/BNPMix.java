function scatterplotpartition(data,result,level);

partition = cuttree(result.tree,level);
D = 6;
ll = length(partition);
cc = colormap;
cc = cc(round(1:64/ll:64),:);
for i=1:6, for j=i+1:6, 
  subplot(6,6,(i-1)*6+j);
  for k = 1:length(partition)
    ii = partition{k};
    plot(data(ii,i),data(ii,j),'.','color',cc(k,:)); 
    hold on
  end
  hold off
end; end

function readspikes(data,result)

a = result.assignments;

c=colormap; 
for iter=900:1000
  b = a(iter,:);
  p=1;
  rp = randperm(max(b)+1)-1;
  for d=1:6, for f=d+1:6, 
    subplot(3,5,p); 
    cla
    hold on
    p=p+1; 
    for k=0:max(b), 
      i=find(b==k); 
      cc=c(round((size(c,1)-1)/max(b)*rp(k+1))+1,:); 
      plot(data(i,d),data(i,f),'.','color',cc); 
    end;
    axis([-5 5 -5 5])
    title([num2str(d) ' vs ' num2str(f)]);
    hold off
  end; end
  drawnow
  pause
end


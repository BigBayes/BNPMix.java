function h = cocluster(data,asgn,num)

numdata = numel(data);
data = reshape(data,numdata,1);
datamin = min(data);
datamax = max(data);
if nargin==2
  num = 500;
end
xx = datamin:((datamax-datamin)/(num-1)):datamax;


ii = data*ones(1,num) <= ones(numdata,1)*xx;
ii = sum(ii,1);
asgn = asgn(ii(1:end-1),ii(1:end-1)+1);
asgn = triu(asgn);
asgn = asgn + asgn' - diag(diag(asgn));

h = imagesc(data,data,asgn,[0 1]);
hold on
offset = 0*(datamax-datamin)/num*2;
dmin=datamin-offset;
dmax=datamax+offset;
h(2) = plot([data;data;dmin*ones(numdata,1);dmax*ones(numdata,1)],...
            [dmin*ones(numdata,1);dmax*ones(numdata,1);data;data],'k+');
hold off
axis xy
axis equal
axis([dmin-.01*offset dmax+.01*offset dmin-.01*offset dmax+.01*offset]);
set(gca,'xtick',[]);
set(gca,'ytick',[]);
colorbar
colormap hot
c = colormap;
colormap(flipud(c));

function visualizetree(data,t,cocluster);

n = (length(t.distance)+1)/2;

z=[t.children(:,n+1:2*n-1)' t.distance(n+1:2*n-1)'];

fh = figure(1);
clf;
[th i p] = dendrogram(z,0);
hold on
ch = plot([1],[0],'ro');
hold off
axisposition = get(gca,'position');
axispose = axis;

x = cellfun(@(xs) mean(xs(2:3)),get(th,'xdata'));
y = cellfun(@(yx) yx(2),get(th,'ydata'));

figure(3);
imagesc(cocluster(p,p));
axis([0 n 0 n]);
axis square

figure(2);
clf

set(fh,'windowbuttondownfcn',{@plotspikes,...
        axisposition,axispose,x,y,data,t.subset(n+1:2*n-1)});
set(fh,'windowbuttonmotionfcn',{@closestcluster,...
        axisposition,axispose,ch,x,y});
set(fh,'busyaction','cancel');

function loc = getLocation(loc, figposition,axisposition,axispose);
  % absolute mouse position -> position within axes

  loc(1) = loc(1)/figposition(3); % relative to figure
  loc(2) = loc(2)/figposition(4);
  loc(1) = (loc(1)-axisposition(1))/axisposition(3); % relative to axes
  loc(2) = (loc(2)-axisposition(2))/axisposition(4);
  loc(1) = axispose(1) + loc(1)*(axispose(2)-axispose(1)); % within axes
  loc(2) = axispose(3) + loc(2)*(axispose(4)-axispose(3));

  %fprintf(1,'loc %f %f\n',loc(1),loc(2));

function closestcluster(hObject,eventdata,...
        axisposition,axispose,ch,x,y)

  figposition=get(hObject,'position');
  loc=getLocation(get(hObject,'currentpoint'),...
        figposition,axisposition,axispose); 
  [d i] = min(((loc(1)-x)/(length(x)+1)).^2+(loc(2)-y).^2);
  set(ch,'xdata',x(i),'ydata',y(i));

function plotspikes(hObject,eventdata,...
        axisposition,axispose,x,y,data,subsets)

  figposition=get(hObject,'position');
  loc=getLocation(get(hObject,'currentpoint'),...
        figposition,axisposition,axispose); 

  [d i] = min(((loc(1)-x)/(length(x)+1)).^2+(loc(2)-y).^2);

  s = subsets{i};
  d = data(s,:);
  figure(2)
  plot(d','k');
  


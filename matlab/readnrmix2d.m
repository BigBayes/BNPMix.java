function result = readnrmix2d(data,filename);

params = load('-ascii',[filename '.parameters']);
result.numclusters = params(:,1);
result.alpha = params(:,2);
result.sigma = params(:,3);
result.tau = params(:,4);
result.numemptyclusters = params(:,5);
%result.baseinvscale = params(:,6);

result.assignments = load('-ascii',[filename '.assignments'])+1;

numSample = size(result.assignments,1);
numData = size(result.assignments,2);

result.clusters = cell(1,numSample);
fid = fopen([filename '.clusters'],'r');
for i=1:numSample
  numcluster = max(result.assignments(i,:));
  result.clusters{i} = cell(1,numcluster);
  for j=1:numcluster
    d = fscanf(fid,'N(m={%f; %f},p=Array2DRowRealMatrix{{%f,%f},{%f,%f}}) ',6);
    result.clusters{i}{j}.mean = d(1:2);
    result.clusters{i}{j}.cov = inv(reshape(d(3:6),[2 2]));
  end
  fscanf(fid,'\n');
end
fclose(fid);

colors = colormap;
for i=1:1:numSample
  clf
  for k=1:length(result.clusters{i})
    c = colors(ceil(rand*64),:);
    plotellipse(result.clusters{i}{k}.mean,result.clusters{i}{k}.cov,...
        'color',c,'linewidth',2);
    hold on
    j = find(result.assignments(i,:)==k);
    xx = [data(j,1) result.clusters{i}{k}.mean(1)*ones(length(j),1)]';
    yy = [data(j,2) result.clusters{i}{k}.mean(2)*ones(length(j),1)]';
    plot(xx(:),yy(:),'-','color',c);
  end
  title(num2str(i));
  drawnow
end

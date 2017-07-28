function result = readnrmix(data,filename)

% read back results
params = load('-ascii',[filename '.parameters']);
result.numclusters = params(:,1);
result.alpha = params(:,2);
result.sigma = params(:,3);
result.tau = params(:,4);
result.numemptyclusters = params(:,5);

result.pred = load('-ascii',[filename '.predval']);
result.logpred = load('-ascii',[filename '.logpred']);
result.meanpred = mean(exp(result.logpred),1);

result.assignments = load('-ascii',[filename '.assignments'])+1;

numSample = size(result.assignments,1);
numdata = size(data,1);

result.clusters = cell(1,numSample);
fid = fopen([filename '.clusters'],'r');
for i=1:numSample
  numcluster = max(result.assignments(i,:));
  result.clusters{i} = cell(1,numcluster);
  for j=1:numcluster
    result.clusters{i}{j} = ...
      fscanf(fid,'N(m={%f},v=Array2DRowRealMatrix{{%f}}) ',2);
  end
  fscanf(fid,'\n');
end
fclose(fid);

%xpred = result.pred(1):(result.pred(end)-result.pred(1))/30:result.pred(end);
%w = xpred(2)-xpred(1);
%h = bar(xpred,hist(data,xpred)/length(data)/w,1);
%set(get(h,'children'),'facealpha',.3,'edgealpha',.3);
%hold on;
qq = quantile(exp(result.logpred),[0.025 .5 .975]);
h = fill([result.pred' fliplr(result.pred')],[qq(1,:) fliplr(qq(3,:))],...
        [.8 .8 .8],'edgecolor',[.8 .8 .8]);
%set(h,'edgealpha',.8,'facealpha',.8);
hold on
h = plot(result.pred,result.meanpred,'color',[.7 0 0],'linewidth',2);
plot(data,zeros(1,numdata),'k+');
hold off
a = axis;
a(1) = result.pred(1);
a(2) = result.pred(end);
axis(a);
title('Predictive Density');

function result = runqmix(numdim,outputFile);


% read back results
params = load('-ascii',[outputFile '.parameters']);
if size(params,2)==5
  result.numclusters = params(:,1);
  result.sigma = params(:,2);
  result.logtau = params(:,3);
  result.logU = params(:,4);
  result.numemptyclusters = params(:,5);
elseif size(params,2)==6
  result.numclusters = params(:,1);
  result.sigma = params(:,2);
  result.theta = params(:,3);
  result.logtau = params(:,4);
  result.logU = params(:,5);
  result.numemptyclusters = params(:,6);
else 
  error('parameters file format incorrect??');
end

row = ['{%f' repmat(',%f',1,numdim-1) '}'];
format = [' Array2DRowRealMatrix{' row repmat([',' row],1,numdim-1) '}'];
fid = fopen([outputFile '.invscale'],'r');
d = fscanf(fid,format,[numdim*numdim inf]);
numSample = size(d,2);
for i=1:numSample
  result.invscale{i} = reshape(d(:,i),numdim,numdim);
end
fclose(fid);

if exist([outputFile '.predval'],'file')
  result.predval = load('-ascii',[outputFile '.predval']);
  d = exp(load('-ascii',[outputFile '.logpred']));
  if length(d)>0
    result.predmean = mean(d,1);
    result.predvar = var(d,0,1);
    result.predquantile = quantile(d,[0.025 .5 .975]);
  end
end

result.assignments = load('-ascii',[outputFile '.assignments'])+1;

fid = fopen([outputFile '.clusters'],'r');
result.clusters = cell(1,numSample);
row = ['{%f' repmat(',%f',1,numdim-1) '}'];
format = [' N(m={%f' repmat('; %f',1,numdim-1) '},p=Array2DRowRealMatrix{'...
      row repmat([',' row],1,numdim-1) '})'];
for i=1:numSample
  numcluster = max(result.assignments(i,:));
  result.clusters{i} = cell(1,numcluster);
  for j=1:numcluster
    d = fscanf(fid,format,numdim*(numdim+1));
    result.clusters{i}{j}.mean = d(1:numdim);
    result.clusters{i}{j}.cov = inv(reshape(d(numdim+1:end),[numdim numdim]));
  end
  fscanf(fid,'\n');
end


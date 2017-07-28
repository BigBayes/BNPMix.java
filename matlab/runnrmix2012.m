function result = runnrmix2012(alg,conjugate,sampled,...
        data,useMeanVar,outputFilename,...
        numBurnin,numSample,numThinning,numEmptyClusters)

alg = lower(alg);

conjugate = logical(conjugate(1));
sampled = logical(sampled(1));
useMeanVar = logical(useMeanVar);

% sampler iterations
numBurnin = int32(numBurnin);
numSample = int32(numSample);
numThinning = int32(numThinning);
if nargin==5,
  numEmptyClusters = int32(1);
else
  numEmptyClusters = int32(numEmptyClusters);
end

% preprocess data
numdim = size(data,2);
numdata = size(data,1);
data = double(data);
datamean = mean(data);
datavar  = var(data,0,1);
datamin = min(data,[],1);
datamax = max(data,[],1);
midpoint = .5*(datamax+datamin);
range = datamax-midpoint;

if numdim==1
  pred = ((midpoint-1.25*range):2.5*range/199:(midpoint+1.25*range))';
elseif numdim==2
  ps = 60;
  pred = zeros(ps*ps,numdim);
  l = (midpoint-1.25*range);
  s = 2.5*range/(ps-1);
  for i=0:ps-1
    for j=0:ps-1
      pred(i+ps*j+1,:) = [l(1)+s(1)*i, l(2)+s(2)*j];
    end
  end
else
  pred = zeros(0,numdim);
end

% run algorithm by calling java
tic;
if strcmp(alg,'neal8') | strcmp(alg,'slice') | ...
   strcmp(alg,'reuse') | strcmp(alg,'neal8r')
    if strcmp(alg,'neal8') | strcmp(alg,'reuse') | strcmp(alg,'neal8r')
        outputFilename=[outputFilename '.' alg num2str(numEmptyClusters)];
    else
        outputFilename=[outputFilename '.slice'];
    end

    output = nrmix2012.run(alg,conjugate,sampled,...
      data,pred,useMeanVar,outputFilename,...
      numBurnin,numSample,numThinning,numEmptyClusters);
else
    error(['Unknown algorithm ' alg])
end
result.matlabtime = toc;

% read back results
params = load('-ascii',[outputFilename '.parameters']);
result.runtime = output(1);
result.totaltime = output(2);
result.numBelowMinSlice = output(3);
result.numAboveMaxClusters = output(4);
result.numclusters = params(:,1);
result.alpha = params(:,2);
result.sigma = params(:,3);
result.tau = params(:,4);
result.numemptyclusters = params(:,5);

if numdim==1
  result.invscale = load('-ascii',[outputFilename '.invscale']);
else
  row = ['{%f' repmat(',%f',1,numdim-1) '}'];
  format = [' Array2DRowRealMatrix{' row repmat([',' row],1,numdim-1) '}'];
  fid = fopen([outputFilename '.invscale'],'r');
  for i=1:numSample
    d = fscanf(fid,format,numdim*numdim);
    result.invscale{i} = reshape(d,numdim,numdim);
  end
  fclose(fid);
end

if numdim<=2
  result.predx = pred;
  d = exp(load('-ascii',[outputFilename '.logpred']));
  result.predmean = mean(d,1);
  result.predvar = var(d,0,1);
  result.predquantile = quantile(d,[0.025 .5 .975]);
end

result.assignments = load('-ascii',[outputFilename '.assignments'])+1;

fid = fopen([outputFilename '.clusters'],'r');
result.clusters = cell(1,numSample);
if numdim==1
  format = 'N(m=%f,v=%f) ';
else
  row = ['{%f' repmat(',%f',1,numdim-1) '}'];
  format = [' N(m={%f' repmat('; %f',1,numdim-1) '},p=Array2DRowRealMatrix{'...
        row repmat([',' row],1,numdim-1) '})'];
end
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


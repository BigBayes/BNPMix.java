function result = runpymix(alg,conjugate,sampled,...
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
if strcmp(alg,'neal8') | strcmp(alg,'slice') | strcmp(alg,'neal8r')
    if strcmp(alg,'neal8') | strcmp(alg,'neal8r')
        outputFilename=[outputFilename '.' alg num2str(numEmptyClusters)];
    else
        outputFilename=[outputFilename '.slice'];
    end

    output = pymix.run(alg,conjugate,sampled,...
      data,pred,useMeanVar,outputFilename,...
      numBurnin,numSample,numThinning,numEmptyClusters);
else
    error(['Unknown algorithm ' alg])
end
%result.matlabtime = toc;

% read back results
for pyi=1:3
outfilei = [outputFilename '-' num2str(pyi)];

params = load('-ascii',[outfilei '.parameters']);
result(pyi).runtime = output(1);
result(pyi).totaltime = output(2);
result(pyi).numBelowMinSlice = output(3);
result(pyi).numAboveMaxClusters = output(4);
result(pyi).numclusters = params(:,1);
result(pyi).sigma = params(:,2);
result(pyi).theta = params(:,3);
result(pyi).tau = params(:,4);
result(pyi).U = params(:,5);
result(pyi).numemptyclusters = params(:,6);

if numdim==1
  result(pyi).invscale = load('-ascii',[outfilei '.invscale']);
else
  row = ['{%f' repmat(',%f',1,numdim-1) '}'];
  format = [' Array2DRowRealMatrix{' row repmat([',' row],1,numdim-1) '}'];
  fid = fopen([outfilei '.invscale'],'r');
  for i=1:numSample
    d = fscanf(fid,format,numdim*numdim);
    result(pyi).invscale{i} = reshape(d,numdim,numdim);
  end
  fclose(fid);
end

if numdim<=2
  result(pyi).predx = pred;
  d = exp(load('-ascii',[outfilei '.logpred']));
  result(pyi).predmean = mean(d,1);
  result(pyi).predvar = var(d,0,1);
  result(pyi).predquantile = quantile(d,[0.025 .5 .975]);
end

result(pyi).assignments = load('-ascii',[outfilei '.assignments'])+1;

fid = fopen([outfilei '.clusters'],'r');
result(pyi).clusters = cell(1,numSample);
if numdim==1
  format = 'N(m=%f,v=%f) ';
else
  row = ['{%f' repmat(',%f',1,numdim-1) '}'];
  format = [' N(m={%f' repmat('; %f',1,numdim-1) '},p=Array2DRowRealMatrix{'...
        row repmat([',' row],1,numdim-1) '})'];
end
for i=1:numSample
  numcluster = max(result(pyi).assignments(i,:));
  result(pyi).clusters{i} = cell(1,numcluster);
  for j=1:numcluster
    d = fscanf(fid,format,numdim*(numdim+1));
    result(pyi).clusters{i}{j}.mean = d(1:numdim);
    result(pyi).clusters{i}{j}.cov = inv(reshape(d(numdim+1:end),[numdim numdim]));
  end
  fscanf(fid,'\n');
end

end

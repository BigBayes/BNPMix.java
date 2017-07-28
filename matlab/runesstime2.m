function [runtime,numclusters,numabove,numbelow] = runnrmix(alg,conjugate,sampled,...
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

    output = nrmix.run(alg,conjugate,sampled,...
      data,pred,useMeanVar,outputFilename,...
      numBurnin,numSample,numThinning,numEmptyClusters);
else
    error(['Unknown algorithm ' alg])
end

runtime = output(1);
p = load('-ascii',[outputFilename '.parameters']);
numclusters = p(:,1);

f=fopen([outputFilename '.log'],'r');
rt = fscanf(f,'Run time = %f\n',1);
tt = fscanf(f,'Total time = %f\n',1);
numbelow = fscanf(f,'Num below minSlice =%d\n',1);
numabove = fscanf(f,'Num above maxClusters =%d',1);
fclose(f)

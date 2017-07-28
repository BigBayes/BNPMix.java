function result = runnrmix(alg,data,filename,numNewClusters)

% parameters of NGGP
alphaShape = 1;
alphaInvScale = 1;
sigmaAlpha = 1;
sigmaBeta = 2;
tauShape = 1e9;
tauInvScale = 1e9;

% parameters of base distribution
useMeanVar = logical(1);
numdim = size(data,2);
meanRelScale = 1.0;
precisionDegFreedom = 4.0*numdim;
invScaleDegFreedom = 2.0*(0.2+numdim-1);
precisionScale = 50.0;

% sampler iterations
numBurnin = int32(10000);
numSample = int32(10000);
numThinning = int32(20);
numPrint = int32(10);
if nargin==3,
  numNewClusters = int32(3);
else
  numNewClusters = int32(numNewClusters);
end

% preprocess data
data = double(data);
datamean = mean(data);
datavar  = var(data);
datamin = min(data);
datamax = max(data);
midpoint = .5*(datamax+datamin);
range = datamax-midpoint;
meanscale = sqrt(datavar)/4;

if numdim==1
  pred = ((midpoint-1.25*range):2.5*range/199:(midpoint+1.25*range))';
elseif numdim==2
  pred = zeros(10000,numdim);
  l = (midpoint-1.25*range);
  s = 2.5*range/99;
  for i=0:99
    for j=0:99
      pred(i+100*j+1,:) = [l(1)+s(1)*i, l(2)+s(2)*j];
    end
  end
else
  pred = zeros(0,numdim);
end


% run algorithm by calling java
tic;
if alg=='neal8' | alg=='slice'
    if alg=='neal8'
        filename=[filename '.neal' num2str(numNewClusters)];
    else
        filename=[filename '.slice'];
    end

    times = nrmixmv.run(alg,data,pred,filename,...
          numBurnin,numSample,numThinning,numNewClusters,numPrint,...
          alphaShape,alphaInvScale,sigmaAlpha,sigmaBeta,tauShape,tauInvScale,...
          useMeanVar,...
          meanRelScale, ...
          precisionDegFreedom, ...
          invScaleDegFreedom, ...
          precisionScale);
else
    error(['Unknown algorithm ' alg])
end
result.matlabtime = toc;

% read back results
params = load('-ascii',[filename '.parameters']);
result.runtime = times(1);
result.totaltime = times(2);
result.numclusters = params(:,1);
result.alpha = params(:,2);
result.sigma = params(:,3);
result.tau = params(:,4);
result.numemptyclusters = params(:,5);

result.pred = pred;
result.logpred = load('-ascii',[filename '.logpred']);
result.meanpred = mean(exp(result.logpred),1);

result.assignments = load('-ascii',[filename '.assignments'])+1;

return

result.clusters = cell(1,numSample);
fid = fopen([filename '.clusters'],'r');
for i=1:numSample
  numcluster = max(result.assignments(i,:));
  result.clusters{i} = cell(1,numcluster);
  for j=1:numcluster
    result.clusters{i}{j} = fscanf(fid,'N(m=%f,v=%f) ',2);
  end
  fscanf(fid,'\n');
end
fclose(fid);


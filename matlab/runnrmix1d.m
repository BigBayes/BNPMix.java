function result = runnrmix(conjugate,marginalized,alg,...
        data,filename,numNewClusters)

conjugate = logical(conjugate(1));
marginalized = logical(marginalized(1));

% parameters of NGGP
alphaShape = 1;
alphaInvScale = 1;
sigmaAlpha = 1;
sigmaBeta = 2;
tauShape = 2e9;
tauInvScale = 2e9;

% sampler iterations
numBurnin = int32(10000);
numSample = int32(10000);
numThinning = int32(20);
numPrint = int32(10);
if nargin==5,
  numNewClusters = int32(1);
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
pred = (midpoint-1.25*range):2.5*range/199:(midpoint+1.25*range);
meanscale = sqrt(datavar)/4;

% run algorithm by calling java
tic;
if alg=='neal8' | alg=='slice'
    if alg=='neal8'
        filename=[filename '.neal' num2str(numNewClusters)];
    else
        filename=[filename '.slice'];
    end

    if conjugate        
        meanMean = midpoint;
        %meanMean = datamean;
        meanRelPrecision = 1/range/range; %.1
        precisionDegFreedom = 4; %5, 4
        precisionInvScaleAlpha = .2; %.1, .4 datavar
        precisionInvScaleBeta = 5/range/range; %datavar

        times = nrmix.run(conjugate,marginalized,alg,data,pred,filename,...
          numBurnin,numSample,numThinning,numNewClusters,numPrint,...
          alphaShape,alphaInvScale,sigmaAlpha,sigmaBeta,tauShape,tauInvScale,...
          meanMean,meanRelPrecision,precisionDegFreedom,...
          precisionInvScaleAlpha,precisionInvScaleBeta);
    else
        % green and richardson setting
        meanMean = midpoint;
        meanPrecision = 1.0/range/range;
        precisionShape = 2; %2.5
        precisionInvScaleAlpha = .2; %datavar/2
        precisionInvScaleBeta = 10/range/range; %datavar/2

        times = nrmix.run(conjugate,marginalized,alg,data,pred,filename,...
          numBurnin,numSample,numThinning,numNewClusters,numPrint,...
          alphaShape,alphaInvScale,sigmaAlpha,sigmaBeta,tauShape,tauInvScale,...
          meanMean,meanPrecision,precisionShape,...
          precisionInvScaleAlpha,precisionInvScaleBeta);
    end
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
result.baseinvscale = params(:,6);

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


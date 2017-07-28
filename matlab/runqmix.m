function result = runqmix(alg,qrm,data,useMeanVar,outputFile,...
        numBurnin,numSample,numThinning,numEmptyClusters)



useMeanVar = logical(useMeanVar);

% sampler iterations
numBurnin = int32(numBurnin);
numSample = int32(numSample);
numThinning = int32(numThinning);
numEmptyClusters = int32(numEmptyClusters);
numPrint = int32(10);

% prediction data
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

% Output location
alg = lower(alg);
alg(1) = upper(alg(1));
if strcmp(alg,'Neal8') | strcmp(alg,'Slice') | strcmp(alg,'Reuse')
    if strcmp(alg,'Neal8') | strcmp(alg,'Reuse')
        outputFile=[outputFile '.' alg num2str(numEmptyClusters)];
    else
        outputFile=[outputFile '.Slice'];
    end
else
    error(['Unknown algorithm ' alg])
end

% run algorithm by calling java
if ischar(qrm)
  qrm = lower(qrm);
  if strcmp(qrm,'constant')
    qggp = nrmi.QGGPConstantOptionPack() ...
          .display() ...
          .getQRM();
          %.parse({'-SigmaAlpha','1000000','-SigmaBeta','3000000'}) ...
  elseif strcmp(qrm,'lognormal')
    qggp = nrmi.QGGPLogNormalOptionPack() ...
          .display() ...
          .getQRM();
  elseif strcmp(qrm,'pitmanyor')|strcmp(qrm,'gengamma')| ...
         strcmp(qrm,'pitmanyor-tu')|strcmp(qrm,'gengamma-tu')
    qggp = nrmi.QPYPOptionPack() ...
          .parse({'-PYPSampler','TU'}) ...
          .display() ...
          .getQRM();
  elseif strcmp(qrm,'pitmanyor-vz')|strcmp(qrm,'gengamma-vz')
    qggp = nrmi.QPYPOptionPack() ...
          .parse({'-PYPSampler','VZ'}) ...
          .display() ...
          .getQRM();
  elseif strcmp(qrm,'pitmanyor-marg')|strcmp(qrm,'gengamma-marg')
    qggp = nrmi.QPYPOptionPack() ...
          .parse({'-PYPSampler','marg'}) ...
          .display() ...
          .getQRM();
  else
    fprintf(1,'unknown option');
    return
  end
elseif isa(qrm,'nrmi.QGGPOptionPack')
  qggp = qrm.getQRM();
elseif isa(qrm,'nrmi.QGGP')
  qggp = qrm;
else
  qrm
  error('unknown qrm object:\n');
end
        
prior = xfamily.MVNormal.MVNormalWishartIndependentOptionPack() ...
        .display() ...
        .getMVNormalWishartIndependent(data);
factory = xfamily.MVNormal.MVNormalNonConjugateFactorySampled();
mixmodel = mixture.MixtureOptionPack() ...
        .parse({['-' alg],'-Sampled'}) ...
        .display() ...
        .getMixture(qggp, prior, factory);
sampler = mcmc.Sampler(mixmodel,numBurnin,numSample,numThinning,numPrint);

qq = qmix();
tic;
output = qq.run(data,pred,outputFile,sampler,qggp,prior,mixmodel);
result.matlabtime = toc;

% read back results
params = load('-ascii',[outputFile '.parameters']);
result.runtime = output(1);
result.totaltime = output(2);
result.numBelowMinSlice = output(3);
result.numAboveMaxClusters = output(4);
result.numclusters = params(:,1);
result.sigma = params(:,2);
result.logtau = params(:,3);
result.logU = params(:,4);
result.numemptyclusters = params(:,5);

if numdim==1
  result.invscale = load('-ascii',[outputFile '.invscale']);
else
  row = ['{%f' repmat(',%f',1,numdim-1) '}'];
  format = [' Array2DRowRealMatrix{' row repmat([',' row],1,numdim-1) '}'];
  fid = fopen([outputFile '.invscale'],'r');
  for i=1:numSample
    d = fscanf(fid,format,numdim*numdim);
    result.invscale{i} = reshape(d,numdim,numdim);
  end
  fclose(fid);
end

if numdim<=2
  result.predx = pred;
  d = exp(load('-ascii',[outputFile '.logpred']));
  result.predmean = mean(d,1);
  result.predvar = var(d,0,1);
  result.predquantile = quantile(d,[0.025 .5 .975]);
end

result.assignments = load('-ascii',[outputFile '.assignments'])+1;

fid = fopen([outputFile '.clusters'],'r');
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


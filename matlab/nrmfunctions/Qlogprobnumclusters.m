function [logprob lp] = logprobQnumclusters(nn, sigma, taulist, logF)

options = optimset('GradObj','on','MaxFunEvals',2000,'MaxIter',2000);
G = loggenfactorial(nn,sigma);

logprob = -Inf*ones(1,nn);
lp = zeros(length(taulist),nn);
tic

for t=1:length(taulist)
  tau = taulist(t);

  lp(t,:) = logprobnumclusters(nn,sigma,sigma,tau);
  logprob = logplus(lp(t,:)+logF(t),logprob);

  if toc>1, fprintf(1,'\rt=%d   ',t); tic; end
end



function logprob = logprobnumclusters(nn,aa,sigma,tau)
% function logprob = logprobnumclusters(nn,aa,sigma,tau,nn)
% Returns the log probabilities of the number of clusters in a
% normalized generalized gamma process.



G = loggenfactorial(nn,sigma);

if tau==0
  upper = 100;
  lower = -100;
else
  upper = 50+log(tau);
  lower = -50+log(tau);
end
hh = .01;
vv = (lower:hh:upper)';
mm = length(vv)-1;

f = (vv*nn-aa/sigma*((exp(vv)+tau).^sigma-tau^sigma)) * ones(1,nn) ...
    - log(tau+exp(vv)) * (nn-(1:nn)*sigma);

logprob = (1:nn)*log(aa/sigma)-gammaln(nn) + G ...
        + log((upper-lower)/2/mm) ...
        + logsum([f(1,:); (log(2)+f(2:mm,:)); f(mm+1,:)],1);


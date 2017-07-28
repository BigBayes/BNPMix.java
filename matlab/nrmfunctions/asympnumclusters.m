function [EK,VK] = asympnumclusters(nn,aa,sigma,tau)
% function [EK,VK] = asympnumclusters(nn,aa,sigma,tau,nn)
% Returns the asymptotic mean and variance of the number of clusters in a
% normalized generalized gamma process.

tau = tau*aa^(1/sigma)/sigma^(1/sigma);

tt = exp(-100-log(tau):.01:100-log(tau));

logprob = stablepdf(tt,sigma);

ES = .01*exp(tau^sigma+logsum((1-sigma)*log(tt)-tau*tt+logprob,2));
VS = .01*exp(tau^sigma+logsum((1-2*sigma)*log(tt)-tau*tt+logprob,2));
VS = VS - ES^2;
EK = ES*nn^sigma;
VK = VS*nn^(2*sigma);



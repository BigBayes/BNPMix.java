function [EK,VK,logprob] = exactnumclusters(nn,aa,sigma,tau)
% function [EK,VK] = exactnumclusters(nn,aa,sigma,tau,nn)
% Returns the exact mean and variance of the number of clusters in a
% normalized generalized gamma process.

logprob = logprobnumclusters(nn,aa,sigma,tau);

EK = sum((1:nn).*exp(logprob));
VK = sum((1:nn).^2.*exp(logprob))-EK^2;

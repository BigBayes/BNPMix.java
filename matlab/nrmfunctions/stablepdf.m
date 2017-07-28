function logprob = stablepdf(xx,sigma)
% function logprob = stablepdf(xx,sigma)
% Log probability of stable(sigma) distribution at x.

nu = 1000;
du = pi/nu;
uu = (0:du:pi)';


xx = reshape(xx,1,numel(xx));

zz = zolotarev(uu,sigma);
ff = log(zz)*ones(1,numel(xx)) - zz*(xx.^(sigma/(sigma-1)));

logprob = log(sigma) - log(1-sigma) - log(pi) + log(xx)/(sigma-1) ...
        + log(du/2) + logsum([ff(1,:); log(2)+ff(2:nu,:); ff(nu+1,:)],1);


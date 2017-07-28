function G = loggenfactorial(nn,sigma)
% function G = loggenfactorial(nn,sigma)
% Logarithm of the generalized factorial coefficient. Use the recursion
% G(n,k) = (n-1-k*sigma)*G(n-1,k) + sigma*G(n-1,k-1)

persistent F Fsigma
if isempty(F) || Fsigma~=sigma
  Fsigma = sigma;
  F = log(sigma); 
end

mm = size(F,1);
if mm<nn
  F(1:mm,mm+1:nn) = -inf;
  F(mm+1:nn,2:nn) = -inf;
  F(mm+1:nn,1) = log(sigma) + gammaln((mm+1:nn)-sigma) - gammaln(1-sigma);

  for n=mm+1:nn
    F(n,2:n) = logplus(F(n-1,2:n)+log(n-1-(2:n)*sigma),F(n-1,1:n-1)+log(sigma));
  end
end
G = F(nn,1:nn);

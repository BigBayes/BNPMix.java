alphaShape = 1;
alphaInvScale = 1;
sigmaAlpha = 1;
sigmaBeta = 2;
n = 82;

for i=1:10000, 
  a(i)=randg(alphaShape)/alphaInvScale; 
  sigma(i)=randbeta(sigmaAlpha,sigmaBeta); 
  tau(i)=1; 
  [EK(i) VK(i)] = exactnumclusters(n,a(i),sigma(i),tau(i)); 
  if rem(i,10)==0, fprintf(1,'.'); end
end

save clusterprior alphaShape alphaInvScale sigmaAlpha sigmaBeta a sigma tau EK VK

hist(EK,1:n);
print -depsc clusterprior_EK

hist(sqrt(VK),.25:.25:20);
print -depsc clusterprior_VK

plot(EK,sqrt(VK),'.');
axis([0 80 0 20])
print -depsc clusterprior_scatter

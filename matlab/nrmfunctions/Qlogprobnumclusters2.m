function logprob = logprobQnumclusters(nn, sigma, logF)

neglogP1 = @(x) neglogQjoint(nn,sigma,logF,x(1),x(2),1);
neglogPn = @(x) neglogQjoint(nn,sigma,logF,x(1),x(2),nn);

options = optimset('GradObj','on','MaxFunEvals',2000,'MaxIter',2000);
utau1 = (fminunc(neglogP1,[1;1],options))
utaun = (fminunc(neglogPn,[1;1],options))

neglogP1 = @(x) neglogQjoint(nn,sigma,logF,x(1),x(2),1);
neglogPn = @(x) neglogQjoint(nn,sigma,logF,x(1),x(2),nn);

nl1 = neglogP1(utau1);
nln = neglogPn(utaun);

delta = log(1e6);
ulower = min(utau1(1),utaun(1))/2;
while ( ulower>0 && (neglogP1([ulower,utau1(2)]) < nl1+delta || ...
                     neglogPn([ulower,utaun(2)]) < nln+delta ))
  ulower = ulower/2;
end
  fprintf(1,'\nulower %f   ',ulower);
uupper = max(utau1(1),utaun(1))*2;
while ( neglogP1([uupper,utau1(2)]) < nl1+delta || ...
        neglogPn([uupper,utaun(2)]) < nln+delta )
  uupper = uupper*2;
end
  fprintf(1,'\nuupper %f   ',uupper);
taulower = min(utau1(2),utaun(2))/2;
while ( taulower>0 && (neglogP1([utau1(1),taulower]) < nl1+delta || ...
                       neglogPn([utaun(1),taulower]) < nln+delta ))
  taulower = taulower/2;
end
  fprintf(1,'\ntaulower %f   ',taulower);
tauupper = max(utau1(2),utaun(2))*2;
while ( neglogP1([utau1(1),tauupper]) < nl1+delta || ...
        neglogPn([utaun(1),tauupper]) < nln+delta )
  tauupper = tauupper*2;
end
  fprintf(1,'\ntauupper %f   ',tauupper);

step = .002;
  ulist = exp(log(  ulower) + (0:step:1)*(log(  uupper)-log(  ulower)));
taulist = exp(log(taulower) + (0:step:1)*(log(tauupper)-log(taulower)));

nlp1 = neglogQjoint(nn,sigma,logF,ulist,taulist,1);
nlpn = neglogQjoint(nn,sigma,logF,ulist,taulist,nn);
nlp1 = squeeze(nlp1(1,:,:));
nlpn = squeeze(nlpn(1,:,:));

subplot(121);
imagesc(exp(-nlp1+min(nlp1(:))));
subplot(122);
imagesc(exp(-nlpn+min(nlpn(:))));
drawnow

G = loggenfactorial(nn,sigma);

logprob = zeros(1,nn);
tic
i = 1:length(ulist)-1;
j = 2:length(ulist);
du = diff(ulist);
dtau = diff(taulist);
logarea = log(du'*dtau);
for kk = 1:nn
  lp = neglogQjoint(nn,sigma,logF,ulist,taulist,kk);
  lp = -squeeze(lp(1,:,:));
  lp = cat(3,lp(i,i),lp(i,j),lp(j,i),lp(j,j));
  lp = logsum(lp,3) - log(4);
  logprob(kk) = logsum(G(kk) + lp(:) + logarea(:),1);
  if toc>1, fprintf(1,'\r%d',kk); tic; end
end

end

function [lp, grad] = neglogQjointexp(nn,sigma,logF,logu,logtau,K)

u = exp(logu);
tau = exp(logtau);
fprintf(1,'logu=%f logtau=%f\n',logu,logtau);

nu = numel(u);
ntau = numel(tau);
u = reshape(u,1,nu);
tau = reshape(tau,1,1,ntau);

u = u(:,:,ones(1,ntau));
tau = tau(:,ones(1,nu),:);

y = logF(tau);
lf = y(1,:,:);
glf = y(2,:,:);

lp =    - (nn-1)*log(u) ...
        - (sigma*K-nn)*log(u+tau) ...
        + (u+tau).^sigma ...
        - tau.^sigma ...
        - lf ...
        + gammaln(nn);
grad = [- (nn-1) ...
        - (sigma*K-nn)./(u+tau).*u ...
        + sigma*(u+tau).^(sigma-1).*u; 
        - (sigma*K-nn)./(u+tau).*tau ...
        + sigma*(u+tau).^(sigma-1).*tau ...
        - sigma*tau.^(sigma) ...
        - glf.*tau];

end


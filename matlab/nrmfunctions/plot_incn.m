N = [1:9 10:10:90 100:100:900 1000];
S = [.1 .5 .9 .5 .5 .5];
A = [1 1 1 .1 1 10];
T = ones(1,6);
F = [1 1 1 2 2 2];
C = [1 2 3 1 2 3];

M = zeros(length(N),length(S));
V = M;
for ii = 1:length(S)
  sigma = S(ii);
  aa = A(ii);
  tau = T(ii);
  for in = 1:length(N)
    nn = N(in);

    pp = exp(logprobnumclusters(nn,aa,sigma,tau));
    mm = sum(pp.*(1:nn));
    M(in,ii) = mm;
    V(in,ii) = max(1e-10,sum(pp.*(1:nn).^2)-mm^2);
  end
  mm = M(:,ii)';
  vv = V(:,ii)';
  ss = sqrt(vv);

  figure(F(ii))
  cc = zeros(1,3); cc(C(ii)) = .7;
  loglog(N,mm,'linewidth',2,'color',cc);
  hold on

  fc = .9*ones(1,3); fc(C(ii)) = 1;
  ec = .9*ones(1,3); ec(C(ii)) = 1;
  fill([N fliplr(N)],[mm+ss fliplr(max(1e-1,mm-ss))],fc,...
    'edgecolor',ec,'facealpha',.1);
  drawnow
end
figure(1); hold off
figure(2); hold off

figure(1); print('-depsc','incn_a=1,s=.1,.5,.9,t=1.eps');
figure(2); print('-depsc','incn_a=.1,1,10,s=.5,t=1.eps');


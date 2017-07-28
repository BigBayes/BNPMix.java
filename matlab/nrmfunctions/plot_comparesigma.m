nn = 1000;

sigma = .7;
alpha = [.1 1 10];
for i=1:3
  lp = logprobnumclusters(nn,alpha(i),sigma,1);
  plot(exp(lp),'linewidth',2);
  hold on;
  m7(i) = sum((1:nn).*exp(lp));
end
hold off
axis([0 600 0 .01])
print('-depsc','comparesigma_a=.1,1,10_s=.7_t=1.eps')

sigma = .1;
alpha = [38.5 61.5 161.8];
for i=1:3,
  lp = logprobnumclusters(nn,alpha(i),sigma,1);
  plot(exp(lp),'linewidth',2);
  hold on;
  m1(i) = sum((1:nn).*exp(lp));
end
hold off
axis([0 600 0 .04])
print('-depsc','comparesigma_a=38.5,61.5,161.8_s=.1_t=1.eps')

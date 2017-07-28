sigma = .5;
theta = 10;
nn = 100;

taulist = 10.^(-5:.2:10);
[logprob lp] = Qlogprobnumclusters(nn,sigma,10.^(-5:.2:10),ones(1,76)/76);

p = exp(lp);
meanK = p*(1:nn)';
imagesc(p');
hold on
plot(1:76,meanK,'k','linewidth',2);
hold off
axis xy
set(gca,'xtick',6:10:76,'xticklabel',{'-4','-3','0','2','4','6','8','10'});
xlabel('log_{10}(\tau)');
ylabel('K');
title('P( K | \tau )');
colormap gray
c=colormap;
colormap((1-c).^5)
set(gcf,'paperposition',[1 1 4 3]);
print -depsc plot_Qnumclusters1

plot(p(16:5:end-11,:)','linewidth',2)
taulist(16:5:end-11)
leg = {};
for i=-2:7
  leg{i+3} = ['\tau = 10^{' num2str(i) '}'];
end
%legend(leg,'location','northwest');
ylabel('P( K | \tau )')
xlabel('K');
set(gcf,'paperposition',[1 1 4 3]);
print -depsc plot_Qnumclusters2

m = 2;
v = 4;
q = exp(-(log10(taulist)-m).^2/2/v);
q = q / sum(q);
plot(q*p,'linewidth',2);
set(gcf,'paperposition',[1 1 4 3]);
xlabel('K');
ylabel('P( K )');
print -depsc plot_Qnumclusters_lognormal1

m = 2;
v = 1;
q = exp(-(log10(taulist)-m).^2/2/v);
q = q / sum(q);
plot(q*p,'linewidth',2);
set(gcf,'paperposition',[1 1 4 3]);
xlabel('K');
ylabel('P( K )');
print -depsc plot_Qnumclusters_lognormal2

t
theta = 10;
q = theta.*log(taulist) -taulist.^sigma;              
q = exp(q-max(q)); 
q = q/sum(q);
plot(q*p,'linewidth',2);
set(gcf,'paperposition',[1 1 4 3]);
xlabel('K');
ylabel('P( K )');
print -depsc plot_Qnumclusters_pitmanyor

i = 31:46; %[26 36 41 46]; 
plot(sum(p(i,:))/length(i),'linewidth',2);
set(gcf,'paperposition',[1 1 4 3]);
xlabel('K');
ylabel('P( K )');
print -depsc plot_Qnumclusters_loguniform

i = [26 36 41 46]; 
plot(sum(p(i,:))/length(i),'linewidth',2);
set(gcf,'paperposition',[1 1 4 3]);
xlabel('K');
ylabel('P( K )');
print -depsc plot_Qnumclusters_discrete


tu=load('-ascii','statcomp/galaxy_qggp_tu.parameters');
vz=load('-ascii','statcomp/galaxy_qggp_vz.parameters');
marg=load('-ascii','statcomp/galaxy_qggp_marg.parameters');
logn=load('-ascii','statcomp/galaxy_qggp_lognormal.parameters');
subplot(411); hist(tu(:,1),0:30)     
subplot(412); hist(vz(:,1),0:30)     
subplot(413); hist(marg(:,1),0:30)   
subplot(414); hist(logn(:,1),0:30)   
pause
for i=2:5
  l = min([min(tu(:,i)),min(vz(:,i))]);
  r = max([max(tu(:,i)),max(vz(:,i))]);
  bins = l:(r-l+1e-16)/50:r+1e-16;
  subplot(411); hist(tu(:,i),bins)  
  subplot(412); hist(vz(:,i),bins)  
  subplot(413); hist(marg(:,i),bins)
  subplot(414); hist(logn(:,i-(i>=3)),bins)
  xlabel(num2str(i))
  pause
end

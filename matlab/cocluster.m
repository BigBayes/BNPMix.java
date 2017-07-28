function co = cocluster(asgn)

numdata = size(asgn,2);
numsamples = size(asgn,1);

co = zeros(numdata,numdata);
for i=1:numsamples
  a = asgn(i*ones(1,numdata),:);
  %for k=unique(a)
  %  i = find(a==k);
  %  co(i,i) = co(i,i)+1;
  %end
  co = co + double(a==a');
  if rem(i,ceil(numsamples/20))==0, fprintf(1,'.'); end
end
fprintf(1,'\n');

co = co/numsamples;
co(1:numdata+1:numdata^2) = 0;

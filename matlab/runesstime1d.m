
load -ascii galaxy.data
galaxy = galaxy/1e4;
load -ascii logacid.data

numiter = 10;
numalg = 25;
alg = cat(2,repmat({'slice'},1,3),...
             repmat({'neal8'},1,11),...
             repmat({'neal8r'},1,11));
conjugate = logical([1 1 0 repmat([1 1 1 1 1 1 0 0 0 0 0],1,2)]);
sampled   = logical([0 1 1 repmat([0 1 1 1 1 1 1 1 1 1 1],1,2)]);
numclusters = [1e6 1e6 1e6 repmat([1 1 2 3 4 5 1 2 3 4 5],1,2)];
numburnin = 10000;
numsample = 10000;
numthin   = 20;
usemeanvar = false;

runtime = zeros(2,numalg);
ess = zeros(2,numalg);
numabove = zeros(2,numalg);
numbelow = zeros(2,numalg);

  for a = 1:numalg
    output = ['/tmp/galaxy' num2str(a) '-' num2str(alliter)];
    [runtime(1,a) ess(1,a) numabove(1,a) numbelow(1,a)] = ...
      runesstime(alg{a},conjugate(a),sampled(a),...
        galaxy,usemeanvar,output,...
        numburnin,numsample,numthin,numclusters(a));
    output = ['/tmp/logacid' num2str(a) '-' num2str(alliter)];
    [runtime(2,a) ess(2,a) numabove(2,a) numbelow(2,a)] = ...
      runesstime(alg{a},conjugate(a),sampled(a),...
        logacid,usemeanvar,output,...
        numburnin,numsample,numthin,numclusters(a));
    fprintf(1,['------------------------ITER ' num2str(alliter) '-' num2str(a) ': ' ...
        num2str([runtime(:,a)' ess(:,a)']) '\n']);
  end   
save(['data/esstime' num2str(alliter)],'runtime','ess','numabove','numbelow');

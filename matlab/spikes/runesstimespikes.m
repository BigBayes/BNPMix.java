load spikespca

numdim = 6;
numdata = 500;
index = 1:numdata;
data = Y(index,1:numdim);

numiter = 10;
numalg = 9;
alg = cat(2,repmat({'slice'},1,1),...
             repmat({'neal8r'},1,8));
            %repmat({'neal8'},1,5),...
conjugate = logical([0 0 0 0 0 0  0  0  0]);
sampled   = logical([1 1 1 1 1 1  1  1  1]);
numnewclusters =     [1e6 1 2 3 4 5 10 15 20];
numburnin = 10000;
numsample = 10000;
numthin   = 20;
usemeanvar = false;

runtime = zeros(1,numalg);
ess = zeros(1,numalg);
numclusters = zeros(numsample,numalg);
numabove = zeros(1,numalg);
numbelow = zeros(1,numalg);

%for alliter = 1:numiter
  for a = 1:length(alg)
    output = ['/tmp/spikes' num2str(a) '-' num2str(alliter)];
    [runtime(a) ess(a) numabove(a) numbelow(a)] = ...
      runesstime(alg{a},conjugate(a),sampled(a),...
        data,usemeanvar,output,...
        numburnin,numsample,numthin,numnewclusters(a));
    %[runtime(alliter,a) numclusters(:,alliter,a) numabove(alliter,a) numbelow(alliter,a)] = ...
    %  runesstime2(alg{a},conjugate(a),sampled(a),...
    %    data,usemeanvar,output,...
    %    numburnin,numsample,numthin,numnewclusters(a));
    fprintf(1,['------------------------ITER ' num2str(alliter) '-' num2str(a) ': ' ...
        num2str([runtime(a) ess(a) numabove(a) numbelow(a)]) '\n']);
  end   
%end
save(['data/esstimespikes' num2str(alliter)],'runtime','ess','numabove','numbelow');
%save(['data/esstimespikes' num2str(alliter)],'runtime','ess','numabove','numbelow','numclusters');

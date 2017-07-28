function C = linkage(xdist,func);

% C = avglink(X)
%
% Average linkage algorithm. Uses squared Euclidean distance as distance
% measure between data points.
%
% Format:
%   X - m * n matrix dataset.  ith row is the ith data vector.
%   C - structure storing the output of linkage algorithm.
%
% C.distance - list of distances between two clusters merged at each step.
% C.parent   - list of clusters formed by merger at each step
% C.children - list of indices of pairs of clusters merged at each step
% C.subset   - subset of data vectors making up each cluster

if nargin==1, func = @mean; end

mm = size(xdist,1);
xdist(1:mm+1:mm^2) = inf; % don't consider self-distance.

% initialize priority queue of pairs of data points to merge
ll = 0;
pairs = zeros(3,mm*(mm-1)/2);
for ii = 1:mm
  if rem(ii,ceil(mm/20))==0, fprintf(1,'.'); end
  for jj=ii+1:mm
    clusterdistance = xdist(ii,jj);
    ll = ll + 1;
    pairs(:,ll) = [clusterdistance;ii;jj];
  end
end
fprintf(1,'\n');

subset = num2cell(1:mm); % the data points belonging to each cluster
frontier = 1:mm; % the unmerged clusters

distance = zeros(1,2*mm-1);
parent = zeros(1,2*mm-1);
children = zeros(2,2*mm-1);

for kk=mm+1:2*mm-1
  if rem(kk,ceil(mm/20))==0, fprintf(1,'.'); end
  % find pair of clusters with minimum distance
  [clusterdistance ll] = min(pairs(1,:));
  ii = pairs(2,ll);
  jj = pairs(3,ll);

  % remove this pair of clusters from list
  fi = find(ii==frontier);
  fj = find(jj==frontier);
  frontier([fi fj]) = []; % delete from frontier
  ll = find(pairs(2,:)==ii|pairs(2,:)==jj|pairs(3,:)==ii|pairs(3,:)==jj);
  pairs(:,ll) = []; % delete from list of pairs of clusters

  % merge clusters ii and jj
  distance(kk) = clusterdistance;
  parent([ii jj]) = kk;
  children(:,kk) = [ii;jj];
  subset{kk} = [subset{ii} subset{jj}];

  % add new pairs of clusters into the list of clusters
  if kk<2*mm-1
    numpairs = size(pairs,2);
    pairs(:,numpairs+length(frontier)) = 0;
    for jj = 1:length(frontier)
      ii = frontier(jj);
      % add cluster pair (ii,kk)
      clusterdistance = func(func(xdist(subset{ii},subset{kk})));
      pairs(:,numpairs+jj) = [clusterdistance;ii;kk];
    end
  end

  frontier(end+1) = kk;
end
fprintf(1,'\n');

C.distance = distance;
C.parent = parent;
C.children = children;
C.subset = subset;

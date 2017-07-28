function [H,order,location] = plotdendrogram(C,col,varargin)

% H = plotdendrogram(C,varargin)
%
% Plots output from linkage hierarchical clustering as a dendrogram.
%
% Format:
%   C - structure storing the output of linkage algorithm.
%   C.distance - list of distances between two clusters merged at each step.
%   C.parent   - list of clusters formed by merger at each step
%   C.children - list of indices of pairs of clusters merged at each step
%   C.subset   - subset of data vectors making up each cluster
%   varargin - arguments for plotting lines;
%   H - Figure handles
%   order - ordering of leaves.


mm = .5*(length(C.parent)+1);

% First sort leaf indices
order = zeros(1,mm);
curleaf = 0;
stack = 2*mm-1;
while ~isempty(stack)
  cur = stack(end); % pop
  next = C.children(:,cur);
  if next(1)==0,
    stack(end) = [];
    curleaf = curleaf + 1;
    order(curleaf) = cur;
  else
    stack(end:end+1,:) = next;
  end
end

% determine location of leaf indices
location = zeros(1,2*mm-1);
location(order) = 1:mm;

H = zeros(3,mm-1);
% draw each internal branch point
dohold = ishold;
for pp = mm+1:2*mm-1
  cc = C.children(:,pp);
  col1 = mean(col(C.subset{cc(1)},:),1);
  col2 = mean(col(C.subset{cc(2)},:),1);
  colp = mean(col(C.subset{pp},:),1);
  lc = location(cc);
  lp = mean(lc);
  location(pp) = lp;
  dc = C.distance(cc);
  dp = C.distance(pp);
  H(1,pp-mm) = plot([dc(1) dp],[lc(1) lc(1)],...
        'color',col1,varargin{:});
  H(2,pp-mm) = plot([dp dc(2)],[lc(2) lc(2)],...
        'color',col2,varargin{:});
  H(3,pp-mm) = plot([dp dp],[lc(1) lc(2)],...
        'color',colp,varargin{:});
  hold on
end
if ~dohold
  hold off
end
axis([0 1 0 mm+1]);

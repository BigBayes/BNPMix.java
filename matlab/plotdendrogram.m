function [H,order,location] = plotdendrogram(C,varargin)

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

H = zeros(1,mm-1);
% draw each internal branch point
dohold = ishold;
for pp = mm+1:2*mm-1
  cc = C.children(:,pp);
  lc = location(cc);
  lp = mean(lc);
  location(pp) = lp;
  dc = C.distance(cc);
  dp = C.distance(pp);
  H(pp-mm) = plot([dc(1) dp dp dc(2)],[lc(1) lc(1) lc(2) lc(2)],varargin{:});
  hold on
end
if ~dohold
  hold off
end


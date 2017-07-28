function partition = cuttree(tree,level);

mm = .5*(length(tree.distance)+1);

stack = 2*mm-1;
partition = {};
while ~isempty(stack)
  cur = stack(end); % pop
  if tree.distance(cur) < level
    partition{end+1} = tree.subset{cur};
    stack(end) = [];
  else
    next = tree.children(:,cur);
    stack(end:end+1,:) = next;
  end
end
  

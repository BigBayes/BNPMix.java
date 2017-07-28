function [t,result] = coclustertree(result)

if ~isfield(result,'cocluster')
  result.cocluster = cocluster(result.assignments);
end

t = linkage(1-result.cocluster);
result.tree = t;

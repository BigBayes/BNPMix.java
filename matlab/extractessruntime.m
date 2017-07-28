a = cat(1,whos('*slice*'),whos('*neal*'));

for i=1:length(a)
  n = a(i).name;
  r = eval([n '.runtime']);
  s = eval([n '.ess']);
  if ~exist(['speedtables/' n '.runtime'],'file')
    runtime = [];
    essize = [];
  else
    runtime = load('-ascii',['speedtables/' n '.runtime']);
    essize = load('-ascii',['speedtables/' n '.ess']);
  end
  runtime(end+1,1) = r;
  save('-ascii',['speedtables/' n '.runtime'],'runtime');
  essize(end+1,1) = s(1);
  save('-ascii',['speedtables/' n '.ess'],'essize');
end

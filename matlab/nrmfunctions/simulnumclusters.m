aa = 1;
tau = 1;
sigma = .1;
nn = 30;
u = 1;

cc = 1:nn;
mm = ones(1,nn);
K = 0;
for iter = 1:100000
  for i = 1:nn
    c = cc(i);
    mm(c) = mm(c)-1;
    if (mm(c)==0)
      mm = mm([1:c-1 c+1:end]);
      cc(cc>c) = cc(cc>c)-1;
    end
    pp = [mm-sigma aa*(u+tau)^sigma];
    pp = cumsum(pp);
    c = 1+sum(pp<rand*pp(end));
    cc(i) = c;
    if c>length(mm)
      mm(end+1) = 1;
    else
      mm(c) = mm(c) + 1;
    end
  end
  K = K + length(mm);
end
K/100000

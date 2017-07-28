
function y = loggammainc(a,z)
% function y = loggammainc(a,z)
% Compute log incomplete gamma function log Gamma(a,z).  
% a and z can be vectors, and a can be negative as well.

y = zeros(max(size(a),size(z)));
if numel(a)==1, a = a*ones(size(y)); end
if numel(z)==1, z = z*ones(size(y)); end


n = ceil(max(0,-real(a)));

i = n==0;
y(i) = incgamma2(a(i),z(i));

i = n>=1;
y(i) = -inf;
for k=1:max(n)
  i = n>=k;
  y(i) = logplus(y(i), ...
        (n(i)-k).*log(-1) ...
        + (a(i)+n(i)-k).*log(z(i)) ...
        - gammaln(1-a(i)) ...
        + gammaln(k-a(i)-n(i)));
end

i = n>=1;
y(i) = logplus(y(i)-z(i),...
        n(i).*log(-1)...
        +gammaln(1-a(i)-n(i))...
        -gammaln(1-a(i))...
        +incgamma2(a(i)+n(i),z(i)));

y = real(y);% + j*rem(imag(y),2*pi);



function y = incgamma2(a,z)

y = z;
i = a<1e-15;
y(~i) = log(gammainc(z(~i),a(~i),'upper'))+gammaln(a(~i));
y(i) = log(expint(z(i)));


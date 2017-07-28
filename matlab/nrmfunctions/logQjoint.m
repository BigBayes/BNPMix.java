function [lp, grad] = logQjoint(nn,sigma,logF,u,tau,K)

y = logF(tau);
lf = y(1);
glf = y(2);

lp =    + (nn-1)*log(u) ...
        + (sigma*K-nn)*log(u+tau) ...
        - (u+tau)^sigma ...
        + tau^sigma ...
        + lf;
grad = [+ (nn-1)/u ...
        + (sigma*K-nn)/(u+tau) ...
        - sigma*(u+tau)^(sigma-1); 
        + (sigma*K-nn)/(u+tau) ...
        - sigma*(u+tau)^(sigma-1) ...
        + sigma*tau^(sigma-1) ...
        + glf];
end

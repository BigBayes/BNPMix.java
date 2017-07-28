range = 10;
num = 10000;

alpha = 2;
beta = 20/range/range;
shape = 2;

invscale = alpha*ones(1,num)/beta;
precision = randg(shape*ones(1,num))./invscale;
hist(sqrt(1./precision),1:(2*range));

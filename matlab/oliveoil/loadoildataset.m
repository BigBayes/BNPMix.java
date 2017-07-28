% PCA on Olive Oil dataset from University of Copenhagen webpage
% http://www.models.kvl.dk/datasets
% Code by Maria Lomeli


clear all
% Loads the file
dat = load('HPLCforweb.mat');
% Displays the structure's components
dat.HPLCforweb
% The data matrix of size nxp (n=120 p=4001) n p-dimensional observations
xx = dat.HPLCforweb.data;
% Classifies the dataset into olive and non-olive
labels = dat.HPLCforweb.class{1};

%PCA on data(the option 'econ' is used for when n<=p and returns the values of latent which are not neccesarily zero):
[pc,score,latent,tsquare] = princomp(xx,'econ');
% Cumsum diagnostic to determine the account of explained variance so we
% choose the number of dimensions to proyect data into.
varianceacc = cumsum(latent)./sum(latent);
disp(varianceacc(varianceacc<0.96));
% d dimensions account for 96% of the variance
d = sum(varianceacc<0.96);
%Scatterplot of the projected coordinates:
plotmatrix(score(:,1:d));

X = score(:,1:d)';

[d n] = size(X);

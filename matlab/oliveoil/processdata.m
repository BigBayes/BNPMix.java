load HPLCforweb
data = HPLCforweb.data;
labels = HPLCforweb.class{1};
[mu Y D V] = pca(data);

save oliveoil mu Y D V labels

algs = {'spikesslice','spikesneal81','spikesneal82','spikesneal83','spikesneal84','spikesneal85',...
        'spikesneal8r1','spikesneal8r2','spikesneal8r3','spikesneal8r4','spikesneal8r5',...
        'spikesneal8r10','spikesneal8r20'};

for i=1:length(algs)
  a = algs{i};
  eval(['nc = ' a '.numclusters;']);
  eval(['save -ascii spikesdata/' a '.numclusters nc']);
end
!r spikes/essspikes.r

for i=1:length(algs)
  a = algs{i};
  eval([a '.ess = load(''spikesdata/' a '.ess'');']);

  r = eval([a '.runtime']);
  s = eval([a '.ess']);
  fprintf(1,'%4.0f & %3.1f\\\\\n',r,s);
end

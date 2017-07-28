load data/esstime

runtime  = cat(3,esstime.runtime);
ess      = cat(3,esstime.ess    );
numabove = cat(3,esstime.numabove);
numbelow = cat(3,esstime.numbelow);
essps    = ess./runtime;
meanruntime  = mean(runtime,3);
meaness      = mean(ess    ,3);
meannumabove = mean(numabove,3);
meannumbelow = mean(numbelow,3);
meanessps    = mean(essps  ,3);
stdruntime  = std(runtime,0,3)/sqrt(10);
stdess      = std(ess    ,0,3)/sqrt(10);
stdnumabove = std(numabove,0,3)/sqrt(10);
stdnumbelow = std(numbelow,0,3)/sqrt(10);
stdessps    = std(essps  ,0,3)/sqrt(10);

%save data/galaxy.mat galaxy*
%save data/logacid.mat logacid*

fprintf(1,'&\\multicolumn{5}{c}{\\bf Conjugate model, parameters marginalized}&\\\\\n');
fprintf(1,'Cond Slice&\n');
i=1;
fprintf(1,'$%3.1f\\pm %3.1f$ & $%4.0f\\pm %4.0f$ &$%2.1f\\pm %2.1f$&',...
        meanruntime(1,i),stdruntime(1,i),meaness(1,i),stdess(1,i),...
        meanessps(1,i),stdessps(1,i));
fprintf(1,'$%3.1f\\pm %3.1f$ & $%4.0f\\pm %4.0f$ &$%2.1f\\pm %2.1f$\\\\\n',...
        meanruntime(2,i),stdruntime(2,i),meaness(2,i),stdess(2,i),...
        meanessps(2,i),stdessps(2,i));
  
fprintf(1,'Marg &\n');
i=4;
fprintf(1,'$%3.1f\\pm %3.1f$ & $%4.0f\\pm %4.0f$ &$%2.1f\\pm %2.1f$&',...
        meanruntime(1,i),stdruntime(1,i),meaness(1,i),stdess(1,i),...
        meanessps(1,i),stdessps(1,i));
fprintf(1,'$%3.1f\\pm %3.1f$ & $%4.0f\\pm %4.0f$ &$%2.1f\\pm %2.1f$\\\\\n',...
        meanruntime(2,i),stdruntime(2,i),meaness(2,i),stdess(2,i),...
        meanessps(2,i),stdessps(2,i));

fprintf(1,'\\hline\n');

fprintf(1,'\\hline\n');
fprintf(1,' \\multicolumn{j}{c}{\\bf Conjugate model, parameters sampled}&\\\\\n');
fprintf(1,'\\hline\n');
fprintf(1,'Cond Slice&\n');
i=2;
fprintf(1,'$%3.1f\\pm %3.1f$ & $%4.0f\\pm %4.0f$ &$%2.1f\\pm %2.1f$&',...
        meanruntime(1,i),stdruntime(1,i),meaness(1,i),stdess(1,i),...
        meanessps(1,i),stdessps(1,i));
fprintf(1,'$%3.1f\\pm %3.1f$ & $%4.0f\\pm %4.0f$ &$%2.1f\\pm %2.1f$\\\\\n',...
        meanruntime(2,i),stdruntime(2,i),meaness(2,i),stdess(2,i),...
        meanessps(2,i),stdessps(2,i));

for i=5:9
fprintf(1,['Marg ($C$=' num2str(i-4) ') &\n']);
fprintf(1,'$%3.1f\\pm %3.1f$ & $%4.0f\\pm %4.0f$ &$%2.1f\\pm %2.1f$&',...
        meanruntime(1,i),stdruntime(1,i),meaness(1,i),stdess(1,i),...
        meanessps(1,i),stdessps(1,i));
fprintf(1,'$%3.1f\\pm %3.1f$ & $%4.0f\\pm %4.0f$ &$%2.1f\\pm %2.1f$\\\\\n',...
        meanruntime(2,i),stdruntime(2,i),meaness(2,i),stdess(2,i),...
        meanessps(2,i),stdessps(2,i));
end

fprintf(1,'\\hline\n');
fprintf(1,'\\hline\n');
fprintf(1,'& \\multicolumn{5}{c}{\\bf Non-conjugate model, parameters sampled}&\\\\\n');
fprintf(1,'\\hline\n');
fprintf(1,'Cond Slice&\n');
i=3;
fprintf(1,'$%3.1f\\pm %3.1f$ & $%4.0f\\pm %4.0f$ &$%2.1f\\pm %2.1f$&',...
        meanruntime(1,i),stdruntime(1,i),meaness(1,i),stdess(1,i),...
        meanessps(1,i),stdessps(1,i));
fprintf(1,'$%3.1f\\pm %3.1f$ & $%4.0f\\pm %4.0f$ &$%2.1f\\pm %2.1f$\\\\\n',...
        meanruntime(2,i),stdruntime(2,i),meaness(2,i),stdess(2,i),...
        meanessps(2,i),stdessps(2,i));


for i=10:14
fprintf(1,['Marg ($C$=' num2str(i-9) ') &\n']);
fprintf(1,'$%3.1f\\pm %3.1f$ & $%4.0f\\pm %4.0f$ &$%2.1f\\pm %2.1f$ &',...
        meanruntime(1,i),stdruntime(1,i),meaness(1,i),stdess(1,i),...
        meanessps(1,i),stdessps(1,i));
fprintf(1,'$%3.1f\\pm %3.1f$ & $%4.0f\\pm %4.0f$ &$%2.1f\\pm %2.1f$\\\\\n',...
        meanruntime(2,i),stdruntime(2,i),meaness(2,i),stdess(2,i),...
        meanessps(2,i),stdessps(2,i));
end


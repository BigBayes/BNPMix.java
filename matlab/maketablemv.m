load data/esstimemv

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

%fprintf(1,'\\hline\n');
%fprintf(1,'\\hline\n');
%fprintf(1,'\\multicolumn{5}{|c|}{\\bf Non-conjugate model, parameters sampled}\\\\\n');
fprintf(1,'NS & Cond Slice&\n');
i=1;
fprintf(1,'$%3.1f\\pm %3.1f$ & $%4.0f\\pm %4.0f$ &',...
        meanruntime(1,i),stdruntime(1,i),meaness(1,i),stdess(1,i));  
        %meanessps(1,i),stdessps(1,i));
fprintf(1,'$%3.1f\\pm %3.1f$ & $%3.1f\\pm %3.1f$ \\\\\n',...
        meanruntime(2,i),stdruntime(2,i),meaness(2,i),stdess(2,i));  
        %meanessps(2,i),stdessps(2,i));

C = [1 2 3 4 5 10 15 20];
for i=2:9
fprintf(1,['NS & Marg Reuse ($C$=' num2str(C(i-1)) ') &\n']);
fprintf(1,'$%3.1f\\pm %3.1f$ & $%4.0f\\pm %4.0f$ &',...
        meanruntime(1,i),stdruntime(1,i),meaness(1,i),stdess(1,i));  
        %meanessps(1,i),stdessps(1,i));
fprintf(1,'$%3.1f\\pm %3.1f$ & $%3.1f\\pm %3.1f$ \\\\\n',...
        meanruntime(2,i),stdruntime(2,i),meaness(2,i),stdess(2,i));  
        %meanessps(2,i),stdessps(2,i));
end


fprintf(1,'\n');
fprintf(1,'\n');
fprintf(1,'\n');

i=1;
fprintf(1,'NS & $%4.0f\\pm %4.0f$ & $%4.0f\\pm %4.0f$ \\\\\n',...
        meannumbelow(1,i),stdnumbelow(1,i),meannumbelow(2,i),stdnumbelow(2,i));



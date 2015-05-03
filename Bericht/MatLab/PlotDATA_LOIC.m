load DATA_LOICVictim.txt;
cpu = 100 - DATA_LOICVictim(:,9);
mem = 100 * DATA_LOICVictim(:,1) ./ (DATA_LOICVictim(:,1) + DATA_LOICVictim(:,2) + DATA_LOICVictim(:,3) + DATA_LOICVictim(:,4));
bw_in = DATA_LOICVictim(:,5) ./ (1024^2);
bw_out = DATA_LOICVictim(:,6)./ (1024^2);
x = 1:length(cpu);

figure
plot(x,cpu, x, mem)
title('Systemressourcen');
legend('CPU', 'Speicher');
xlabel('Zeit / s')
ylabel('Auslastung / %')

figure
plot(x, bw_in, x, bw_out)
title('Bandbreite');
legend('Eingehend', 'Ausgehend');
xlabel('Zeit / s')
ylabel('Datenrate / MiByte/s')
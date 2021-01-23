# Chandy-Lamport - 2. semestrální práce

---

### Zadání
V této práci je vytvořena síť bankovních serverů, které si mezi sebou autonomně vyměňují zprávy o vkladu nebo výběru peněz z konta. Globální stav konta musí zůstat konzistentní. Pro kontrolu globálního stavu je implementován Chandy-Lamportův algoritmus, kterým je získán globální snapshot. 

Celé znění zadánání je [zde](https://github.com/mazinv/Chandy-Lamport/blob/master/misc/zadani-sem-prace-2.pdf).

---

### Použité technologie:
- Java 8 (nutné pro překlad programu!)
- Spark
- ZeroMQ (Java knihovna JeroMQ)
- Spark
- Vagrant
- curl

---

### Topologie serverů
Program musí mít definovanou topologii serverů v souboru topology.txt. Pro ukázku je soubor již předdefinován a topologie serverů vypadá následovně:

![Topologie](https://github.com/mazinv/Chandy-Lamport/blob/master/misc/topology.png)

---

### Snapshot
Spustit snapshot může jakýkoliv server (může probíhat i více snapshotů najednou) tím, že mu přijde požadavek na `/marker` endpoint a port `4567`. Např.:
`curl 192.168.10.10:4567/marker`

Kvůli simulaci latenci v síti, je po každém odeslání zprávy odesílací vlákno serveru uspáno na náhodnou dobu a pokud příjde marker zpráva, je uspán příjemce. Tímto je dosaženo simulace latencí, protože bez uspání by zprávy chodili velkou rychlostí a při ukládání stavu by si servery vyměnily markery moc rychle a ve snapshotu by nebyly zaznamenané téměř žádné zprávy.

Ve složce, ze které se spouští program je po spuštění vytvořena složka pro každý server (jméno složky je jeho IP adresa). Do této složky jsou ukládány snapshoty pod jejich UUID.

---

### Spuštění ukázky
Požadavky na nainstalovaný SW:
- Java 8
- Maven
- Vagrant
- curl

Ukázka uložení snapshotů je spuštěna přes skript `run.sh`. Ten zajistí přeložení programu, spuštění serverů přes program Vagrant a spuštění ukládání snapshotů na všech serverech (jsou tedy spuštěny 4 nezávyslé snapshoty). 

Po rozeslání požadavků na `/marker` nedpointy serverů je třeba chvíli čekat, aby se uložily všechny snapshoty (způsobeno simulací latence).

Po uložení snapshotů lze program vypnout příkazem `vagrant halt` a skriptem `cleanup.sh` vymazat soubory a složky vytvořené při běhu programů.

Spuštění ukázky v bodech:
1) `./run.sh`
2) Počkat, dokud nebudou ve složkách všech serverů uloženy 4 soubory (snapshoty)
3) `vagrant halt`
4) `./cleanup.sh`

# Kryptographie - bmljaHQgdmVyc2NobMO8c3NlbHQ=

Im letzten Jahr wurde im Unterricht schon ein [kleiner Einstieg](https://simonknott.de/articles/verschluesselung) in die Kryptographie gewagt.
Nun wird diese Reihe fortgesetzt und um aktuelle Verfahren wie den Diffie-Hellman-Schlüsselaustausch und das RSA-Verfahren erweitert.

Damals wurden [Caesar](https://en.wikipedia.org/wiki/Caesar_cipher) und [Vigenère](https://en.wikipedia.org/wiki/Vigen%C3%A8re_cipher) implementiert - zwei einfache Verfahren, die auf Verschiebung der Daten um einen festgelegten Schlüssel basieren.

Caesar verschiebt jeden Buchstaben um die gleiche, festgelegte Zahl.
Der Klartext "Anna" wird mit $3$ als Schlüssel zum Chiffre "Dqqd", aus dem durch das Rückverschieben um $3$ wieder "Anna" entsteht.

Vigenère erweitert dies um einen variierenden Schlüssel.
"abc" als Schlüssel bedeutet: Verschiebe den ersten Buchstaben um $1$, den zweiten um $2$, den dritten um $3$ und den vierten wieder um $1$ (und so weiter...).

Caesar ist einfach über Brute-Force anzugreifen, schließlich gibt es nur 26 Möglichkeiten der Verschiebung.
Auch Vigenère ist prinzipiell unsicher, es kann über eine Häufigkeitsanalsye angegreifen werden.
Das funktioniert aber nur, wenn der Schlüssel zu kurz ist - diese Eigenschaft wird später noch wichtig.

## Diffie-Hellman-Schlüsselaustausch

Nehmen wir an, zwei Personen möchten miteinander kommunizieren.
Diese einigen sich darauf, Vigenère mit ausreichend langem Schlüssel zu verwenden.
Nun müssen sie diesen Schlüssel irgendwie aushandeln - am besten, ohne sich dabei zu treffen.
Der Diffie-Hellman-Schlüsselaustausch ermöglicht dies.

> Zum besseren Verständnis möchte ich nun drei Figuren einführen:
> ***A**lice*, ***B**ob* und ***E*ve** ("Eavesdropper").
> Alice möchte Bob eine Nachricht senden, ohne dass Eve diese mitlesen kann.
> Der Haken ist: Alice und Bob können *ausschließlich* über Eve kommunizieren.
> Eve kann das, was sie in den Händen hält, mitlesen und sogar manipulieren.
> 
> Ein Kryptographisches Verfahren muss also leisten, dass Alice und Bob kommunizieren können, ohne dass Eve diese Kommunikation mitlesen kann.

Zur Aushandlung des Schlüssels wird zuerst ein öffentlicher Schlüssel $(p, g)$ bestimmt.
Dieser wird nach folgenden Bestimmungen gewählt:

- $p$ ist eine Primzahl.
- $g < p ; g \in \mathbb{R}$

Dieses Schlüsselpaar wird nun veröffentlicht - Alice, Bob und Eve kennen es.

Nun wählen Alice und Bob im Geheimen einen eigenen, privaten Schlüssel:

$$
a < p \quad \textrm{bzw.} \quad  b < p
$$

Damit bestimmen sie ihre öffentlichen Austausch-Werte:

$$
A = g^a \bmod p \\
B = g^b \bmod p
$$

Diese werden dann wieder veröffentlicht, Alice erhält also $B$ und Bob erhält $A$.

Aus diesen Werten kann nun einfach ein gemeinsamer Schlüssel bestimmt werden:

$$
K_A = B^a \bmod p \\
K_B = A^b \bmod p \\
$$

Dass $K_A$ mit $K_B$ identisch ist, lässt sich mathematisch beweisen.
Mehr Informationen dazu finden sich [im Whitepaper](https://ee.stanford.edu/~hellman/publications/24.pdf).

Eve hat zwar während des Schlüsselaustauschs alle Informationen übertragen, kennt aber nur $p$, $g$, $A$ und $B$.
Daraus kann Eve nicht auf K schließen, dazu fehlt ihr einer der geheimen Schlüssel $a$ und $b$.
Trotzdem verfügen Alice und Bob nach diesem Schlüsselaustausch über einen gemeinsamen Schlüssel, mit dem sie nun ihre Daten verschlüsseln können.
Jetzt könnte zum Beispiel Vigènere oder One-Time-Pad (Bitweise XOR-Verknüpfung von Daten und Schlüssel) eingesetzt werden - bei hohen Schlüssellängen sind diese Verfahren nämlich sicher[^sicher].

[^sicher]: "sicher" bedeutet, dass sie nur durch eine Brute-Force-Attacke geknackt werden können.

Dort liegt eine Krux des Verfahrens: Um einen großen Schlüssel zu erzeugen, müssen große Eingabewerte gewählt werden - deshalb sind große Primzahlen [für die Kryptographie so wichtig](https://stackoverflow.com/questions/439870/why-are-primes-important-in-cryptography).

DH kann angegriffen werden, wenn zu kleine Werte verwendet werden, oder $r$ unklug gesetzt wird.
$r$ sollte ein [Generator](https://en.wikipedia.org/wiki/Generator_(mathematics)) sein, sonst könnte Eve $A$ und $B$ auf $a$ oder $b$ schließen.

Eine Beispiel-Implementierung für Diffie-Hellman findet sich [in meinem GitHub-Repository](https://github.com/Skn0tt/lkNetzwerke/tree/master/KeyExchange).

## Asymmetrische Verschlüsselungsverfahren


## TL;DR

*Diffie-Hellman* ermöglicht es, sich auf einen gemeinsamen Schlüssel zu eignen, ohne dass ein Dritter diesen erfährt.
Dabei werden *große Primzahlen* und eine geschickte Wahl des Generators $g$ benötigt.

Asymmetrische Verschlüsselungsverfahren haben einen *öffentlichen* Schlüssel, mit dem Daten *ver*schlüsselt werden, sowie einen *privaten* Schlüssel, mit den diese wieder *ent*schlüsselt werden können.
Ein Beispiel eines solchen Verfahrens ist RSA.

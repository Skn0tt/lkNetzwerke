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

Verschlüsselungsverfahren, wie wir sie bisher kennen gelernt haben, verwenden zum *Ver*schlüsseln den selben Schlüssel wie zum *Ent*schlüsseln.
In Kontrast zu diesen *symmetrischen* Verfahren stehen solche, die zum *Ver*schlüsseln und *Ent*schlüsseln zwei separate Schlüssel verwenden.
Solche Verfahren nett man *asymmetrische*, ein Beispiel ist das als sicher geltende RSA.

### RSA

RSA wurde 1977 von den drei Wissenschaftlern [Ron Rivest](https://en.wikipedia.org/wiki/Ron_Rivest), [Adi Shamir](https://en.wikipedia.org/wiki/Adi_Shamir) und [Leonard Adleman](https://en.wikipedia.org/wiki/Leonard_Adleman) vorgestellt.

Um RSA verwenden zu können, muss erst ein Schlüsselpaar, also ein *öffentlicher* und ein *privater* Schlüssel, erzeugt werden.

Dafür werden zu Beginn zwei beliebige Primzahlen $p$ und $q$ gewählt.

$$
p \in \mathbb{P} \\
q \in \mathbb{P}
$$

Aus diesen kann dann $N$ und $r$ ermittelt werden:

$$
N = p * q \\
r = \phi(p, q) = (p - 1) * (q - 1)
$$

Nun wird eine weitere, beliebige Zahl $e$ gewählt, die mit $r$ Teilerfremd ist.

$$
e \in \mathbb{N} \\
ggt(e, r) = 1
$$

Als letzte Berechnung muss nun noch $d$, das *modulare Inverse* von $e$ und $r$ gefunden werden.

$$
e * d \bmod r = 1
$$

Nun sind alle Teile des Schlüsselpaars vorhanden:
Der *öffentliche* Schlüssel $(N, e)$ und der *private* Schlüssel $(N, d)$.
Die anderen berechneten Werte sollten zerstört werden, um die Rekonstruktion des privaten Schlüssels zu verhindern.

Möchte man nun eine Nachricht $m$ verschlüsseln, so verwendet man den *öffentlichen* Schlüssel:

$$
c = m^e \bmod N
$$

Um dieses Chiffre $c$ nun wieder zu entschlüsseln, kommt der *private* Schlüssel zum Einsatz:

$$
m = c^d \bmod N
$$

Um das Erzeugen eines Schlüssels einmal selbst auszuprobieren, kann ich die [Website der Drexel University](https://www.cs.drexel.edu/~jpopyack/IntroCS/HW/RSAWorksheet.html) sehr empfehlen.

## Kryptographie in Java

Kryptographische Verfahren basieren oft auf der Verwendung großer Zahlen, die gerne die Größe von 32 Bit (Java's `int`-Typ) übersteigen.

Um diese Begrenzung zu umgehen, stellt das JDK die `BigInteger`-Klasse zur Verfügung.
Sie hält beliebig große Zahlen, die einzige Grenze ist die Größe des Arbeitsspeichers.

Sie verfügt außerdem schon über alle Rechenarten, die wir für die meisten Verschlüsselungsverfahren benötigen!

Als Beispiel möchte ich die Implementation von *RSA* anführen.

Das Ver- und Ent-schlüsseln verwendet die `a.modPow(e, m)`-Methode des BigInteger, die dem Term $a^e \bmod m$ entspricht.

```java
BigInteger encrypt(BigInteger m, BigInteger e, BigInteger N) {
  return m.modPow(e, N);
}

BigInteger decrypt(BigInteger c, BigInteger d, BigInteger N) {
  return c.modPow(d, N);
}
```

Das Erzeugen eines Schlüsselpaars ist ähnlich simpel:

```java
Random random = new SecureRandom();
BigInteger p = BigInteger.probablePrime(1024, random);
BigInteger g = BigInteger.probablePrime(1024, random);

BigInteger N = p.multiply(g);
BigInteger r = p.subtract(BigInteger.ONE)
                .multiply(
                  g.subtract(BigInteger.ONE)
                );

BigInteger e = computeE(r);

BigInteger d = e.modInverse(r);
```

`computeE(r)` ist eine Methode, die so lange eine zufällige Zahl $e$ erzeugt, bis sie alle drei Bedingungen erfüllt:

1. $e >= 1$
2. $e <= r$
2. $\gcd{e, r} = 1$

```java
static BigInteger computeE(BigInteger r) {
  BigInteger e;

  do e = new BigInteger(r.bitLength(), random);
  while (e.compareTo(BigInteger.ONE) <= 0 // 1
          || e.compareTo(r) >= 0 // 2
          || !e.gcd(r).equals(BigInteger.ONE)); // 3

  return e;
}
```

## TL;DR

*Diffie-Hellman* ermöglicht es, sich auf einen gemeinsamen Schlüssel zu eignen, ohne dass ein Dritter diesen erfährt.
Dabei werden *große Primzahlen* und eine geschickte Wahl des Generators $g$ benötigt.

Asymmetrische Verschlüsselungsverfahren haben einen *öffentlichen* Schlüssel, mit dem Daten *ver*schlüsselt werden, sowie einen *privaten* Schlüssel, mit den diese wieder *ent*schlüsselt werden können.
Ein Beispiel eines solchen Verfahrens ist RSA.

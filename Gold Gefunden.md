---
title: Gold Gefunden
output: pdf_document
author: Benedikt Ricken, Simon Knott
date: 06. Juni, 2018
---

# Translation
`.`: Kurz  
`-`: Lang  

## Values

A: `---`  
B: `--.-`  
C: `--.`  
D: `.--`  
E: `.`  
F: `---.`  
G: `....`  
H: `.-.`  
I: `...`  
J: `...--`  
K: `.-.-`  
L: `-.-`  
M: `.-..`  
N: `..`  
O: `.-.-.`  
P: `...-`  
Q: `----`  
R: `--`  
S: `-`  
T: `.-`  
U: `-..`  
V: `--..`  
W: `..--`  
X: `-..-`  
Y: `.--.`  
Z: `-...`  
Leer: `-..-`  

# Control Sequences:

**Hash**

* am Ende die ersten vier **Buchstaben** (Zahlen werden ignoriert, da wir keine Symbole dafür haben) des md5-Hashes des (klein geschriebenen) Blocks am Ende anhängen.
* Trenner: `.....`

```mermaid
graph LR;

Block --> Separator;
Separator --> Hash;
Hash --> Verification;
Verification --> Block;

style Verification fill:lightblue
```

**Antwort des Empfängers**

Verification:  
Ja: `.`  
Nein: `-`  

* bei _Nein_: erneutes Senden des Blockes

# Beispiel:
Gold Gefunden:
`... .-.-. -.- .-- -..- .... . ---. -.. .. .-- . .. ..... . . --. --.`
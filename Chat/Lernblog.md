# Chat-Protokoll

Als letzte Übung vor den Sommerferien haben wir im Unterricht ein eigenes Chat-System implementiert.  
Hierfür musste sich ein eigenes Protokoll ausgedacht bzw. erweitert werden.

In diesem Blogpost wird meine Umsetzung sowohl des Servers vorgestellt und erläutert.
Der Client wird ausgelassen, da seine Implementierung verglichen mit dem Server nur wenig neues enthält.

<!--more-->

## Protokoll

Das Chat-Protokoll setzt auf TCP auf und besteht aus zwei verschiedenen Befehlssätzen, *Requests* und *Events*.

### Requests
*Requests* lösen Aktionen aus.  
Sie werden vom Client an den Server gesendet, wenn der Nutzer sie auslöst.

<style>
.darkblue {
  color: #304FFE;
}
.red {
  color: #F83E4B;
}
.blue {
  color: #64B5F6;
}
.orange {
  color: #F5A623;
}
</style>

| Befehl                                                                                                                                                                               | Bedeutung                                 |
| ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ----------------------------------------- |
| <span class="darkblue">LOGIN</span> <span class="blue">\<nickname></span>                                                                                                            | Anmelden                                  |
| <span class="darkblue">SEND</span> <span class="red">\<recipient></span> <span class="blue">\<message></span>                                                                        | Eine neue Nachricht versenden             |
| <span class="darkblue">SEND_PUBLIC</span> <span class="blue">\<message></span>                                                                                                       | Eine neue öffentliche Nachricht versenden |
| <span class="darkblue">BAN_USER</span> <span class="red">\<userId></span>                                                                                                            | Einen Nutzer bannen                       |
| <span class="darkblue">NEW_GROUP</span> <span class="blue">\<name></span> <span class="red">\<memberId1></span> <span class="red"> ... </span> <span class="red">\<memberIdN></span> | Eine neue Gruppe erstellen                |
| <span class="darkblue">HELP</span>                                                                                                                                                   | Die Hilfe anzeigen                        |
| <span class="darkblue">QUIT</span>                                                                                                                                                   | Sitzung schließen                         |


### Events
*Events* signalisieren Status-Änderungen.  
Der Client sollte nach einem solchen Event seine Anzeige ändern.

| Befehl                                                                                                                                                           | Bedeutung                                      |
| ---------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------- |
| <span class="darkblue">NEW_MESSAGE</span> <span class="red">\<sender></span> <span class="orange">\<recipient></span> <span class="blue">\<message></span>       | Es gibt eine neue Nachricht.                   |
| <span class="darkblue">NEW_PUBLIC_MESSAGE</span> <span class="red">\<sender></span> <span class="blue">\<message></span>                                         | Es gibt eine neue öffentliche Nachricht.       |
| <span class="darkblue">RECIPIENTS_CHANGE</span> <span class="red">\<recipientId1></span> <span class="red"> ... </span> <span class="red">\<recipientIdN></span> | Die verfügbaren Empfänger wurden aktualisiert. |

## Server

Der Server besteht aus zwei Teilen:

1. Verwaltung der Nutzer
2. Entgegennehmen der Requests und entsprechendes Versenden der Events

### Nutzerverwaltung

![Nutzerverwaltung UML-Diagramm](https://www.planttext.com/plantuml/svg/ZLJBRjim4BppAtYKa-W73C780qKI8EqXHZ-04jSMKOaKI2fjqFRVmwL4IRIefYVRt9dPvSxGjyOoRVks8KH82_h4Ag2lK8bEiB81-fjGUatFO9yvI2jE0dIsetjwj5h8SoX-Dw2DFtyHnXRuyq3-4b8rp1Xwnxa6zpbeYIwcTqfRT-Aw8oDWh_8yfwZZIZKpP35IyIVKHdqF6yIbg7v8raBS0W4P6vNtOPwRDNg78wnQFMhLTsCl_ptSP5f9tAw5jdJxYBU11K_E4hIR92SrpUYDKpW2qrNj2tjQCKbBeCQV0QSdfLT-nfpX2YSXUMeWj7BklCx0OFOJC_KHR35mlEFUYycyL-YK4LRfTxHHDTnum3sJ4lYNNnLqLYYvWRUrLZ-3y_7mVXCnOP65XGhUCgVb-zra_bVM5M7fmHb4V9kpi07nGuz2W5BBgoD2oOUQoJDAF2Xf-XRqeZubSHul-mTZD_kpIYxktkSV8QwQzSD3mJEilId1VKr3gzvWi-okb1QF4FqMTVryPaHEXHbiZlYbKWqm6GDOt3nWfl-FL6IAWiu_3WT2XeTvDRtNHFGsatH1M3xrLPREU7eP90_zxn1S27-U0hJOztK8JqwPd0Qu9H9o2vAxl_WF)

Die Nutzerverwaltung besteht aus einigen Klassen und Interfaces, die größtenteils sehr trivial sind und durch das UML-Diagramm zur genüge beschrieben sind.

Genauer möchte ich auf das `TwoObjectRepository` eingehen:

```java
private final Map<String, User> byAddress = new HashMap<>();
private final Map<String, User> byNickname = new HashMap<>();
```
Es speichert alle Nutzer in den zwei HashMaps `byAddress` und `byNickname`.

Besonders nach der letzten Unterrichtseinheit zu SQL klingeln sofort die Alarmglocken: *Redundanz!*  
In diesem Fall ist sie allerdings das kleinere Übel, denn die Anwendung verlangt sowohl die Suche über den Nickname, als auch über die Addresse.

Verwendet man nur eine der beiden Maps als Index, hätte die Suche über das jeweils andere Attribut lineare Laufzeit.
So tauschen wir diese Laufzeitkomplexität durch die Speicher-Komplexitäte eines zusätzlichen Indexes ein.

```java
private final Set<String> bannedAddresses = new HashSet<>();
```
Die gebannten Addresses werden in einem HashSet abgelegt.
Ein Set enthält jeden Wert nur einmal und kann schnell auf einen Werts überprüft werden.


```java
private final Consumer<String> onChange;
```
Ein `Consumer<T>` ist ein Objekt mit der Methode `.accept(T v)`.
Dieses wird beim Erzeugen des `TwoObjectRepository` übergeben und löst bei jeder Nutzerveränderung aus.
So wird das Versenden der Events von der Nutzerverwaltung abgekoppelt.

### Requests

```java
try {
  Request r = requestBuilder.build(pClientIP, pClientPort, pMessage);
  onRequest(r);
} catch (Command.CommandVerbUnknownException e) {
  send(pClientIP, pClientPort, "-CommandVerb unknown");
} catch (TwoObjectRepository.UserBannedException e) {
  send(pClientIP, pClientPort, "-You are banned.");
}
```

Erreicht den Server ein neuer `Request`, wird erst einmal ein `Request`-Objekt daraus erstellt.
Dieses ist im folgenden UML-Diagramm beschrieben:

![Request UML-Diagramm](https://www.planttext.com/plantuml/svg/VLF1ReCm3Btp5Jd2slx0D5MbB6lH6EsWRCSgXGmXGT8beJrK-_Sv2J2ggZlPVZZFpyzY8HKHgcrgnw6iRL30cuQmueEA0xeu2AKuVkx2Vfijep20AjgimXZY6aTR2EzPk8CGuy_zAjba6bhwyJvBSG9fz-KDfwc_mbrbgKPWWeDm6-9ub-w3jHy3zkCuOp4FirwRazT4ohuoAYVef7iUn_8DJ4Gf0OuggPvI9IfMpaV4G3f32VrkgLGTQrTrh7bJ_95Qmg8GL6fEtsO6RIKLhuoV6U07pcjAs803b460UeO8asU319p9jg5Y46LyckZnCDszcZkz_smwdk1PWQRNRMG9i6tp83lnoYuWspm7sVSRg13ypdjmhcAAYY-IK_HIiKAFxQmggN8xIn3n-gNXXVNLjolOtkeuS9lwo8K21BXDEPwj6VgRmBM6SotRgYxCwWUToUlpzLylndMGbbcRUiq6vio_yTKYy_uM8oxtbcNw7uMdbIyeA-3V-WK0)

Ist das Request-Objekt erstellt, so wird es an die zuständigen Request-Handler geleitet, die die Anfrage dann beantworten.

```java
private void onRequest(Request r) {
  log(r);

  if (!r.userKnown) {
    switch (r.cmd.verb) {
      case LOGIN:
        login(r);
        break;
    }
    return;
  }

  switch (r.cmd.verb) {
    case SEND:
      sendHandler(r);
      break;
    ...
    default:
      r.error("Unknown Error");
  }
}
```

So ein RequestHandler führt dann die im Protokoll festgelegten Schritte durch.
Am Beispiel des `sendHandler`s:

```java
private void sendHandler(Request r) {
  String recipientIdentifier = r.cmd.args.get(0); // 1
  String msg = r.cmd.args.get(1); // 1

  Recipiable recipient = findRecipient(recipientIdentifier); // 2
  
  String cmd = Command.build( // 3
    CommandVerb.NEW_MESSAGE,
    r.user.nickname,
    recipient.getIdentifier(),
    msg
  );

  for (User u : recipient.getUsers()) { // 3
    send(u, cmd);
  }

  r.success("200 Success"); // 4
}
```
Zuerst werden die Argumente aus dem Request-Objekt abgerufen (1).  
Dann wird nach einem passenden `recipient` gesucht (2).  
Nun wird ein neues Event mit der Nachricht konstruiert und an alle Nutzer des `recipient`s geschickt (3).  
Zum Schluss wird der Request als *Erfolgreich* beantwortet.

## Fazit

Steckt man ein wenig Vorarbeit in die Modellierung seines Codes und verwendet zum Beispiel Techniken wie *Domain Driven Design*, kann man komplexen Code auf einfache Klassen mit klarer Zuständigkeit herunterbrechen.  
An den UML-Diagrammen kann man schon sehen, dass alle Klassen relativ klein sind, außerdem gibt es keine zyklischen Abhängigkeiten.
So kann - besonders bei größeren Anwendungen - viel Kopfschmerz gespart werden.

Das Chatprotkoll ist sicherlich nicht vollständig ausgereift und kann an vielen Stellen noch erweitert oder verbessert werden.  
Beispielsweise könnte speziell für mobile Clients das `RECIPIENTS_CHANGE`-Event zu viel Bandbreite benötigen, auf einem aktiven Server kommen schnell einige Nutzer zusammen.
Möchte man das Protokoll für solche Anwendungen optimieren, könnten die Events `USER_JOINED` und `USER_LEFT` besser geeignet sein.

Der Quellcode für Server und Client findet sich unter [https://github.com/Skn0tt/lkNetzwerke](https://github.com/Skn0tt/lkNetzwerke/tree/master/Chat)

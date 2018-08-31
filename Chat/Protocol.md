---
title: "skn0tt's Chat Protocol"
output: pdf_document
---

## Session Lifecycle

### Start Session

```
> nc localhost:3000
+NAME
LOGIN simon
-FAIL Name schon vorhanden / +OK Du bist angenommen
```

Upon joining, server will publish recipients.

## End Session

```
QUIT
```

## Client Commands 

### Send Public Message

```
SEND_PUBLIC <message>
```

### Send Private Message

```
SEND <recipientIdentifier> <message>
```

`recipientIdentifier` can be either a username or group name.

### Create a New Group

```
NEW_GROUP <groupIdentifier> <member1> <member2> ... <memberN>
```

Upon creation, server will publish recipients.

### Ban a User

```
BAN_USER <username>
``` 

The user's address will then not be able to send commands anymore.

### Show Help

```
HELP
<this is the help>
```

## Server Events

### New Message

```
NEW_MESSAGE <sender> <recipient> <message> 
```

### New Public Message

```
NEW_PUBLIC_MESSAGE <sender> <message>
```

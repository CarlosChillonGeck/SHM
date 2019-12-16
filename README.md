# SHM WS 2019

## Data fetching structure

```
shm-rpi
│ README.md
│
└─── nodes
│       │ SensorNodes.java
│       │ TestSuit.java   
│       └─── lib
│            └─── *.jar
│
└─── server
│       │ Server.java
│       │ TestSuit.java
│
└─── interface
        └─── rsc
        └─── src
        └─── tests
```

## Procedure

1. Build a hotspot, join server and sensor node (Rapberry PI) to the hotspot.
2. Check sensorNode.java file if the IP address is matching the server.
3. Launch programme in sensor node, red Led indicate programme from Thumbdrive executed.
4. Execute the Server.java programme. Hit enter in console.
5. The result should be available under Result/. folder in csv format.

**Note: In sensor node, there is a folder "Data" build in local, do not delete that folder.
folder to store local backup of data in sensor node.
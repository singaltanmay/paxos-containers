# Paxos Containers

This is a custom implementation of the [Paxos protocol](https://doi.org/10.1145/359545.359563) in Java Spring. See my project presentation for a more in-depth analysis - [Paxos Implementation](https://docs.google.com/presentation/d/1ZOPoPaV3qR4q_FWtcZB3bkUafjO9ZxlY8mpR12FeNvY/edit?usp=sharing)

The purpouse of this project is to serve as an academic tool to simulate the working of the Paxos protocol to reach consensus in a distributed system. Such a system can be simulated by launching multiple instances of this application as Docker containers inside a common Docker network. Currently, the application supports the roles of PROPOSER, ACCEPTOR and LEARNER which can be specific as environment variables or as flags to the Java application itself. 

![paxos accept stage in docker sdn](https://github.com/singaltanmay/paxos-containers/assets/31047659/77d48be4-ceee-499b-9337-d6ed629e034f)

A pre-requisite of this simulation is a Paxos Router which needs to be be launched before launching any of the Paxos containers. This virtual router is responsible for maintaining a map of all Paxos containers, their Paxos roles and corresponding IP addresses. Each Paxos container on startup goes through a process similar to the [DORA in DHCP](https://en.wikipedia.org/wiki/Dynamic_Host_Configuration_Protocol#Operation) in order to register itself as a viable node with the router. An unregistered Paxos node will not be able to send or recieve Paxos messages.

See Paxos Router for implementation of Paxos logic - https://github.com/singaltanmay/paxos-router

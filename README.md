# distributed-state

This is an experiment in distributed state inspired by Apache ZooKeeper.
My experience with Apache ZooKeeper's API did not leave me satisfied and
resulted in code that was difficult to read and complex to get right.
Apache Curator, a wrapper library around Apache ZooKeeper, failed to
simplify working with Apache ZooKeeper and instead simply changed the
nature of the complexity in a poorly documented way and introduced bugs
in the layers between the application and Apache ZooKeeper. This
experiment will start by implementing an idealized API with Apache
ZooKeeper as a backend since I already know how to work with it. I will
then attempt to implement it with other backends such as HashiCorp
Consul and etcd.

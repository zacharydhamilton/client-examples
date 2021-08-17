# Java Producer and Consumer Examples

### Things you'll need before getting started:
1. You'll need a cluster running in Confluent Cloud with a topic to send data too. In this `README`, I'll be referencing a topics named `colors` and `colors-schemaless`.
1. You'll need to copy the client configuration properties from the Clients page in your cluster. This should give you a copy-able set of properties that you'll to use.
    - You should paste the specific values into the placeholders seen in `setup.properties`, or alternatively paste over the entire thing.

### Examples

- [ProducerConfigExample](https://github.com/zacharydhamilton/client-examples/blob/main/src/main/java/clients/producers/ProducerConfigExample.java)
- [ConsumerConfigExample](https://github.com/zacharydhamilton/client-examples/blob/main/src/main/java/clients/consumers/ConsumerConfigExample.java)
- [ColorProducerExample](https://github.com/zacharydhamilton/client-examples/blob/main/src/main/java/clients/producers/ColorProducerExample.java)
- [ColorConsumerExample](https://github.com/zacharydhamilton/client-examples/blob/main/src/main/java/clients/consumers/ColorConsumerExample.java)
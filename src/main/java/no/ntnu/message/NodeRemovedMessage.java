package no.ntnu.message;

public class NodeRemovedMessage implements Message {
    private int nodeId;

    public NodeRemovedMessage(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getNodeId() {
        return nodeId;
    }
}

package no.ntnu.message;

/**
 * class represents a message indicating the removal of a node.
 * It implements the Message interface.
 *
 */
public class NodeRemovedMessage implements Message {

    /**
     * The ID of the node that has been removed.
     */
    private int nodeId;

    /**
     * Constructs a new noderemovemessage with the specified node ID.
     *
     * @param nodeId The ID of the node that has been removed.
     */
    public NodeRemovedMessage(int nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * Gets the ID of the removed node.
     *
     * @return The ID of the removed node.
     */
    public int getNodeId() {
        return nodeId;
    }
}

package com.ostracker.rs;

public class Node {

    public Node next;
    Node previous;

    public void unlink() {
        if (this.previous != null) {
            this.previous.next = this.next;
            this.next.previous = this.previous;
            this.next = null;
            this.previous = null;
        }
    }
}

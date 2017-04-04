package com.ostracker.rs;

public class Deque {

    public Node head = new Node();

    public Deque() {
        this.head.next = this.head;
        this.head.previous = this.head;
    }

    public Node method2499() {
        Node var1 = this.head.previous;
        if (var1 == this.head) {
            return null;
        } else {
            var1.unlink();
            return var1;
        }
    }

    public void method2500(Node var1) {
        if (var1.previous != null) {
            var1.unlink();
        }

        var1.previous = this.head;
        var1.next = this.head.next;
        var1.previous.next = var1;
        var1.next.previous = var1;
    }
}

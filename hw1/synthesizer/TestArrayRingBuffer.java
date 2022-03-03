package synthesizer;
import org.junit.Test;
import static org.junit.Assert.*;

/** Tests the ArrayRingBuffer class.
 *  @author Josh Hug
 */

public class TestArrayRingBuffer {
    @Test
    public void someTest() {
        ArrayRingBuffer arb = new ArrayRingBuffer<Integer>(10);
        for(int i = 0; i < 10; i++) {
            System.out.println(i);
            arb.enqueue(i);
        }
        for(int i = 0; i < 9; i++) {
            int d = (int) arb.dequeue();
            int p = (int) arb.peek();
            System.out.println(p);
        }
    }

    /** Calls tests for ArrayRingBuffer. */
    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TestArrayRingBuffer.class);
    }
} 

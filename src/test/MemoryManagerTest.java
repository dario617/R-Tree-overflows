package test;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import rtree.Node;
import util.MemoryManager;

public class MemoryManagerTest {

	private MemoryManager mm;
	private float[][] coords;
	private long parentId;
	
	@BeforeEach
	public void createMM() {
		mm = new MemoryManager(2);
	}
	
	@Test
	public void getId() {
		Node n1 = new Node(true, coords, parentId);
		n1.myId = mm.getNewId();
		assertEquals(0, n1.myId);
	}
	
	@Test
	public void insertNode() {
		Node n1 = new Node(true, coords, parentId);
		n1.myId = mm.getNewId();
		mm.insertNode(n1);
		assertEquals(1, mm.loadedNodes);
	}
	
	@Test
	public void loadNode() {
		Node n1 = new Node(true, coords, parentId);
		n1.myId = mm.getNewId();
		mm.insertNode(n1);
		try {
			Node n2 = mm.loadNode(0);
		}catch (Exception e) {
			fail("Coudn't load, exception encountered");
		}
	}
	
	@Test
    @DisplayName("throws Exception When File Not Found")
    void throwsExceptionWhenFileNotFound() {
        assertThrows(IOException.class, () -> mm.loadNode(0));
    }
	
	@Test
	public void overFlowBuffer() {
		Node n1, n2, n3;
		n1 = new Node(true, coords, parentId);
		n1.myId = mm.getNewId();
		n2 = new Node(true, coords, parentId);
		n2.myId = mm.getNewId();
		n3 = new Node(true, coords, parentId);
		n3.myId = mm.getNewId();
		
		mm.insertNode(n1);
		assertEquals(1, mm.loadedNodes);
		mm.insertNode(n2);
		assertEquals(2, mm.loadedNodes);
		mm.insertNode(n3);
		assertEquals(2, mm.loadedNodes);
		
		// Tear Down
		mm.deleteNode(0);
	}
	
	@Test
	public void overFlowBufferOnLoad() {
		Node n1, n2, n3;
		n1 = new Node(true, coords, parentId);
		n1.myId = mm.getNewId();
		n2 = new Node(true, coords, parentId);
		n2.myId = mm.getNewId();
		n3 = new Node(true, coords, parentId);
		n3.myId = mm.getNewId();
		
		
		mm.insertNode(n1);
		mm.insertNode(n2);
		// Push to memory node 0
		mm.insertNode(n3);

		// Push to memory node 1
		try {
			n1  = mm.loadNode(0);
		}catch (Exception e) {
			fail();
		}
		
		assertEquals(2, mm.loadedNodes);
		
		mm.deleteNode(0);
		mm.deleteNode(1);
	}
	
}

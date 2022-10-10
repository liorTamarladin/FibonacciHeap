
//name: may yona, id: 206564841, user-name: mayyona
//name: lior ladin, id: 318434503, user-name: liorladin

/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{
    private HeapNode min;
    private HeapNode first;
    private int n;
    private int trees;
    private int marked;
    private static int totalCuts;
    private static int totalLinks;


   /**
    * public boolean isEmpty()
    *
    * Returns true if and only if the heap is empty.
    *   
    */
    public boolean isEmpty() // O(1)
    {
    	return n==0;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * The added key is assumed not to already belong to the heap.  
    * 
    * Returns the newly created node.
    */
    public HeapNode insert(int key) // O(1)
    {
        HeapNode newNode = new HeapNode(key); //create new heapNode
        if (this.n==0) //if the heap is empty
        {
            newNode.next = newNode;
            newNode.prev = newNode;
            this.min = newNode;
            this.first = newNode;
        }
        else
        {
            putInFirst(newNode); //adds the new node to be the first node in the heap
        }
        this.n += 1;
        this.trees += 1;

    	return newNode;
    }

    public void putInFirst(HeapNode node) //O(1) - adds the node to be the first node in the heap
    {
        node.next = this.first;
        node.prev = this.first.prev;
        this.first.prev = node;
        node.prev.next = node;

        if (node.key<this.min.key) //update the min if needed
        {
            this.min = node;
        }
        this.first = node;
    }

   /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key.
    *
    */
    public void deleteMin() //WC: O(n)
    {
        int cnt = 0; //counter for the nodes that was marked and after childMin func will be unmarked
        if (this.min.child != null)
        {
            cnt = childMin(this.min);
        }
        if (this.trees != 1) // if the heap contains more than one tree
        {
            if (this.min == this.first)
            {
                this.first = this.min.next;
            }
            if (this.min.child != null) // updates the pointers
            {
                HeapNode lastChild = min.child.prev;
                min.child.prev = min.prev;
                min.prev.next = min.child;
                lastChild.next = min.next;
                min.next.prev = lastChild;
            }
            else // updates the pointers
            {
                min.prev.next = min.next;
                min.next.prev = min.prev;
            }
        }
        else // if the heap contains one tree
        {
            this.first = min.child;
        }

        this.n -= 1;
        this.trees = this.trees - 1 + min.rank;
        this.marked = this.marked - cnt;

        if (this.trees != 0)
        {
            consolidation(); //calls to consolidation to update the heap structure
        }
        else
        {
            this.min = null; //the heap is empty - update the min
        }
        return;
    }


    public int childMin(HeapNode node) //O(logn) - gets a node and changing it children to roots and
    // returns the number of children that were marked and will change to be unmarked
    {
        int cnt = 0;
        HeapNode minChild = node.child;
        HeapNode currNode = node.child;
        do
        {
            if (currNode.mark!=0)
            {
                currNode.mark = 0;
                cnt++;
            }
            currNode.parent = null;
            currNode = currNode.next;
        }
        while (currNode != minChild); //the while loop go through all of node's children

        return cnt;
    }

    /**
     * @pre node1.key < node2.key
     * @pre node1.parent == null
     * @pre node2.parent == null
     */
    public HeapNode linking(HeapNode node1, HeapNode node2) //O(1)- gets 2 nodes and links them to one tree by making node1 the parent of node2
    //returns node1
    {
        totalLinks++;
        HeapNode child = node1.child;
        node2.parent = node1;
        node1.child = node2;
        if (child==null)
        {
            node2.prev = node2;
            node2.next = node2;
        }
        else
        {
            node2.next = child;
            node2.prev = child.prev;
            child.prev = node2;
            node2.prev.next = node2;
        }
        node1.next=node1;
        node1.prev=node1;

        node1.rank ++;
        this.trees--;
        return node1;
    }

    public void consolidation() //WC: O(n)- Consolidate root-list so that no roots have the same degree
            //Whenever it discover two trees that have the same degree it calls to linking with these trees
    {
        HeapNode[] ranks = new HeapNode[(int)(2*(Math.ceil(Math.log(this.n)/Math.log(2)))+1)]; //initialize the array to maximum size of different optional ranks
        HeapNode first = this.first;
        HeapNode currNode = this.first;
        do
        {
            HeapNode next = currNode.next;
            int rank = currNode.rank;
            while (ranks[rank] != null) // while we have a tree with this rank
            {
                if (ranks[rank].key<currNode.key)
                {
                    currNode = linking(ranks[rank],currNode);
                }
                else
                {
                    currNode = linking(currNode,ranks[rank]);
                }
                ranks[rank] = null;
                rank++;
            }
            ranks[rank] = currNode;
            currNode = next;
        }
        while (currNode != first);

        boolean isFirst = false;
        HeapNode prevNode = null;
        for (HeapNode node : ranks) //updates the roots next and prev pointers according to their position in the array
        {
            if (node != null)
            {
                if (!isFirst)
                {
                    this.first = node;
                    isFirst = true;
                }
                else
                {
                    prevNode.next = node;
                    node.prev = prevNode;
                }
                prevNode = node;
            }
        }
        prevNode.next = this.first;
        this.first.prev = prevNode;
        updateMin();
    }

    public void updateMin() //O(logn) - go through all the roots and update the min in the heap
    {
        HeapNode first = this.first;
        HeapNode currNode = this.first;
        int min = first.key;
        do
        {
            HeapNode next = currNode.next;
            if (currNode.key<=min)
            {
                this.min = currNode;
                min = currNode.key;
            }
            currNode = next;
        }
        while (currNode != first);
    }
   /**
    * public HeapNode findMin()
    *
    * Returns the node of the heap whose key is minimal, or null if the heap is empty.
    *
    */
    public HeapNode findMin() //O(1)
    {
    	return this.min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    *
    */
    public void meld (FibonacciHeap heap2) //O(1)
    {
        HeapNode first1 = this.first;
        HeapNode first2 = heap2.first;
        HeapNode last1 = this.first.prev;
        HeapNode last2 = heap2.first.prev;

        first1.prev = last2;
        last1.next = first2;
        first2.prev = last1;
        last2.next = first1;

        this.n += heap2.n;
        this.trees += heap2.trees;
        this.marked += heap2.marked;

        if (heap2.min.key<this.min.key)
        {
            this.min = heap2.min;
        }
    	  return;
    }

   /**
    * public int size()
    *
    * Returns the number of elements in the heap.
    *   
    */
    public int size() //O(1)
    {
    	return this.n;
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
    * Note: The size of of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
    * 
    */
    public int[] countersRep() //WC: O(n)
    {
        if (this.n == 0)
        {
            return new int[]{};
        }
    	int[] arr = new int[(int)(2*(Math.ceil(Math.log(this.n)/Math.log(2)))+1)]; //initialize the array to maximum size of different optional ranks
        HeapNode currNode = this.first;
        int maxRank = 0; // parameter to determine the final length of the array
        do {
            arr[currNode.rank] = arr[currNode.rank]+1;
            if (currNode.rank > maxRank)
            {
                maxRank = currNode.rank;
            }
            currNode = currNode.next;
        }
        while (currNode!=this.first);

        int[] res = new int[maxRank+1];
        System.arraycopy(arr, 0, res, 0, res.length);
        return res;
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
    *
    */
    public void delete(HeapNode x) //WC: O(n)
    {
        int decrease = x.key - this.min.key +1; //by choosing this delta we ensure that x will become this.min

        decreaseKey(x,decrease);
        deleteMin();
    	return;
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta) //WC: O(logn)
    {
        x.key = x.key - delta;
        if (x.parent!= null && x.key<x.parent.key) //if the heap property is violated
        {
            cascadingCut(x,x.parent);
        }
        else
        {
            if (x.key <= this.min.key)
            {
                this.min = x;
            }
        }
    	return;
    }

    /**
     * @pre x.parent == y
     */
    public void cut(HeapNode x, HeapNode y) //O(1) - gets 2 nodes and cut their connection
    {
        totalCuts++;
        x.parent = null;
        if (x.mark!=0) //make sure x is unmarked because it will become a root
        {
            x.mark = 0;
            this.marked --;
        }
        y.rank = y.rank - 1;
        if (x.next==x)//x is y only child
        {
            y.child = null;
        }
        else
        {
            if (x == y.child)
            {
                y.child = x.next;
            }
            x.prev.next = x.next;
            x.next.prev = x.prev;
        }
        putInFirst(x); //adds x to be the first node in the heap
        this.trees ++;
    }

    /**
     * @pre x.parent == y
     */
    public void cascadingCut(HeapNode x, HeapNode y) //WC: O(logn) -
    // recursive function that calls to cut in order to maintain the invariant that each node losses at most one child
    {
        cut(x,y);
        if (y.parent!=null)//y is not a root
        {
            if (y.mark==0)
            {
                y.mark=1;
                this.marked++;
            }
            else
            {
                cascadingCut(y,y.parent);
            }
        }
    }


   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * 
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap. 
    */
    public int potential() //O(1)
    {    
    	return this.trees+2*this.marked;
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the
    * run-time of the program. A link operation is the operation which gets as input two
    * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
    * tree which has larger value in its root under the other tree.
    */
    public static int totalLinks() //O(1)
    {    
    	return totalLinks;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the
    * run-time of the program. A cut operation is the operation which disconnects a subtree
    * from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts() //O(1)
    {    
    	return totalCuts;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the rk smallest elements in a Fibonacci heap that contains a single tee.
    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
    *  
    * ###CRITICAL### : you are NOT allowed to change H. 
    */
    public static int[] kMin(FibonacciHeap H, int k) //O(k*deg(H))
    {    
        int[] arr = new int[k];
        FibonacciHeap helper = new FibonacciHeap();
        HeapNode currNode = H.first;
        arr[0] = currNode.key;
        for (int i=1; i<k; i++)
        {
            if(currNode.child != null) //adds currNode children to helper HeapNode
            {
                HeapNode child = currNode.child;
                do {
                    helper.insert(child.key);
                    helper.first.pointer = child; //update child's pointer to the original HeapNode in H
                    child = child.next;
                }
                while (child != currNode.child);
            }
            currNode = helper.min.pointer;
            helper.deleteMin();
            arr[i] = currNode.key;
        }
        return arr;
    }

    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in another file. 
    *  
    */
    public static class HeapNode{

    	public int key;
        private int rank;
        private int mark;
        private HeapNode child;
        private HeapNode next;
        private HeapNode prev;
        private HeapNode parent;
        private HeapNode pointer;

    	public HeapNode(int key) {
    		this.key = key;
            this.mark = 0;
            this.rank = 0;
    	}

    	public int getKey() {
    		return this.key;
    	}
    }
}

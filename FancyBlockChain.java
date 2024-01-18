public class FancyBlockChain {
    public Block[] bchain;

    int size;

    int maxTimeStamp;
    public FancyBlockChain(int capacity) {
        bchain = new Block[capacity];
        size = 0;
        maxTimeStamp = 0;
    }
    public FancyBlockChain(Block[] initialBlocks) {
        if (initialBlocks == null) {
            return;
        }
        bchain = new Block[initialBlocks.length];

        for (int i = 0; i < initialBlocks.length; i++) {
            bchain[i] = initialBlocks[i];
        }

        for (int i = (bchain.length / 2) - 1; i >= 0; i--) {
            heapify(bchain, i, bchain.length);
        }
        size = bchain.length;
        int max = bchain[0].timestamp;
        for (int i = 1; i < bchain.length; i++) {

            bchain[i].index = i;

            if (bchain[i].timestamp > max) {
                max = bchain[i].timestamp;
            }
        }
        maxTimeStamp = max;
    }
    public void heapify(Block[] blocks, int i, int n) {
        int smallest = i;
        int l = (2 * i) + 1;
        int r = (2 * i) + 2;

        if (l < n && blocks[l].timestamp < blocks[smallest].timestamp) {
            smallest = l;
        }

        if (r < n && blocks[r].timestamp < blocks[smallest].timestamp) {
            smallest = r;
        }

        if (smallest != i) {
            swap(blocks, smallest, i);
            heapify(blocks, smallest, n);
        }
    }

    public void swap(Block[] blocks, int a, int b) {
        Block temp = blocks[a];
        blocks[a] = blocks[b];
        blocks[b] = temp;

        blocks[a].index = a;
        blocks[b].index = b;
    }


    public int bubbleUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;

            if (bchain[parentIndex].timestamp <= bchain[index].timestamp) {
                break;
            }
            swap(bchain, index, parentIndex);
            index = parentIndex;
        }
        return index;
    }

    public int length() {
        return size;
    }

    public boolean addBlock(Block newBlock) {
        if (newBlock == null) {
            return false;
        }
        if (size == 0) {
            bchain[0] = newBlock;
            newBlock.index = 0;
            maxTimeStamp = newBlock.timestamp;
            size++;
            return true;
        }

        if (size == bchain.length && bchain[0].timestamp > newBlock.timestamp) {
            return false;
        }

        if (size == bchain.length) {
            removeEarliestBlock();
        }

        bchain[size] = newBlock;
        size++;
        newBlock.index = bubbleUp(size - 1);


        if (newBlock.timestamp > maxTimeStamp) {
            maxTimeStamp = newBlock.timestamp;
        }

        return true;
    }

    public Block getEarliestBlock() {
        if (size == 0) {
            return null;
        }
        return bchain[0];
    }

    public Block getBlock(String data) {
        if (data == null) {
            return null;
        }
        for (int i = 0; i < size; i++) {
            if (bchain[i].data.equals(data)) {
                return bchain[i];
            }
        }

        return null;
    }

    public Block removeEarliestBlock() {
        if (size == 0) {
            return null;
        }
        Block remove = bchain[0];
        bchain[0] = bchain[size - 1];
        bchain[0].index = 0;
        size--;
        heapify(bchain,0,size);

        remove.removed = true;
        return remove;
    }
    public Block removeBlock(String data) {
        if (data == null) {
            return null;
        }
        if (size == 0) {
            return null;
        }
        for (int i = 0; i < size; i++) {
            if (bchain[i].data.equals(data)) {
                Block remove = bchain[i];
                bchain[i] = bchain[size - 1];
                bchain[i].index = i;
                bchain[size - 1] = null;
                size --;

                if (i == size) {
                    return remove;
                }

                int parentIndex = (i - 1) / 2;

                if (i > 0 && bchain[i].timestamp < bchain[parentIndex].timestamp) {
                    bubbleUp(i);
                } else {
                    heapify(bchain, i, size);
                }

                return remove;
            }
        }

        return null;
    }
    public void updateEarliestBlock(double nonce) {
        if (size > 0) {
            bchain[0].nonce = nonce;
            bchain[0].timestamp = 1 + maxTimeStamp;
            maxTimeStamp++;
            heapify(bchain, 0, size);
        }

    }
    public void updateBlock(String data, double nonce) {
        if (data == null) {
            return;
        }
        for (int i = 0; i < size; i++) {
            if (bchain[i] != null && bchain[i].data.equals(data)) {
                bchain[i].nonce = nonce;
                bchain[i].timestamp = 1 + maxTimeStamp;
                maxTimeStamp++;

                int parentIndex = (i - 1) / 2;
                if (i > 0 && bchain[i].timestamp < bchain[parentIndex].timestamp) {
                    bubbleUp(i);
                } else {
                    heapify(bchain, i, size);
                }
                return;
            }
        }
    }
}

public class BlockChainSecure {
    public FancyBlockChain fbc;
    public Block[] btable;


    private boolean isPrime(int n) {
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    public int findNextPrime(int n) {
        n++;
        while (!isPrime(n)) {
            n++;
        }
        return n;
    }
    public BlockChainSecure(int capacity) {

        fbc = new FancyBlockChain(capacity);
        btable = new Block[findNextPrime(capacity)];
    }
    public BlockChainSecure(Block[] initialBlocks) {
        if (initialBlocks == null) {
            return;
        }
        fbc = new FancyBlockChain(initialBlocks);
        btable = new Block[findNextPrime(initialBlocks.length)];

        for (Block block : initialBlocks) {
            int secondHash = Hasher.hash2(block.data, btable.length);

            if (secondHash == 0) { // linear probe because infinite loop
                int hashIndex = (Hasher.hash1(block.data, btable.length)) % btable.length;
                while (btable[hashIndex] != null) {
                    hashIndex = (hashIndex + 1) % btable.length;
                }
                btable[hashIndex] = block;
            } else {
                int k = 0;
                int hashIndex =
                        (Hasher.hash1(block.data, btable.length)) % btable.length;
                while (btable[hashIndex] != null) {
                    k++;
                    hashIndex = (Hasher.hash1(block.data, btable.length) + k * (Hasher.hash2(block.data, btable.length))) % btable.length;
                }
                btable[hashIndex] = block;
            }
        }
    }

    public int length() {
        return fbc.length();
    }


    public boolean addBlock(Block newBlock) {

        boolean add = fbc.addBlock(newBlock);
        if (add) {
            int hashIndex2 = Hasher.hash2(newBlock.data, btable.length);
            if (hashIndex2 == 0) { // linear probe
                int hash = Hasher.hash1(newBlock.data, btable.length) % btable.length;
                int trials = 0;
                while (btable[hash] != null && !btable[hash].removed && trials < btable.length) {
                    hash = (1 + hash) % btable.length;
                    trials++;
                }

                if (trials == btable.length) {
                    return false;
                }
                btable[hash] = newBlock;
                return true;
            } else {
                int k = 0;
                int hash = Hasher.hash1(newBlock.data, btable.length) % btable.length;
                int trials = 0;
                while (btable[hash] != null && !btable[hash].removed && trials < btable.length) {
                    k++;
                    hash = ((Hasher.hash1(newBlock.data, btable.length)) + (k * (Hasher.hash2(newBlock.data,
                            btable.length)))) % btable.length;
                    trials++;
                }
                if (trials == btable.length) {
                    return false;
                }
                btable[hash] = newBlock;
                return true;
            }
        } else {
            return false;
        }

    }
    public Block getEarliestBlock() {
        return fbc.getEarliestBlock();
    }
//    public Block getBlock(String data) {
//        if (data == null) {
//            return null;
//        }
//        int hashIndex2 = Hasher.hash2(data, btable.length);
//
//        if (hashIndex2 == 0) { // linear probe
//            int hashIndex = Hasher.hash1(data, btable.length) % btable.length;
//            int count = 0; // if block does not exist
//            while (btable[hashIndex] != null && !data.equals(btable[hashIndex].data) && count < btable.length) {
//                hashIndex = (1 + hashIndex) % btable.length;
//                count++;
//            }
//            if (btable[hashIndex] == null || btable[hashIndex].removed || count == btable.length) {
//                return null;
//            }
//            return btable[hashIndex];
//        } else {
//            int hashIndex = Hasher.hash1(data, btable.length) % btable.length;
//            int k = 1;
//            int trials = 0;
//            while (btable[hashIndex] != null && !data.equals(btable[hashIndex].data) && trials < btable.length) {
//                hashIndex = (Hasher.hash1(data, btable.length) + (k * Hasher.hash2(data, btable.length))) % btable.length;
//                k++;
//                trials++;
//            }
//            if (trials == btable.length || btable[hashIndex] == null || btable[hashIndex].removed) {
//                return null;
//            }
//            return btable[hashIndex];
//        }
//    }
public Block getBlock(String data) {
    if (data == null) {
        return null;
    }

    int hashIndex2 = Hasher.hash2(data, btable.length);
    int hashIndex = Hasher.hash1(data, btable.length) % btable.length;
    int trials = 0;
    int k = 1;

    if (hashIndex2 == 0) { // Linear probing
        while (btable[hashIndex] != null && !data.equals(btable[hashIndex].data) && trials < btable.length) {
            hashIndex = (1 + hashIndex) % btable.length;
            trials++;
        }
        if (trials == btable.length || btable[hashIndex] == null || btable[hashIndex].removed) {
            return null;
        }
        return btable[hashIndex];
    } else { // Double hashing
        while (btable[hashIndex] != null && !data.equals(btable[hashIndex].data) && trials < btable.length) {
            hashIndex = (Hasher.hash1(data, btable.length) + (k * Hasher.hash2(data, btable.length))) % btable.length;
            k++;
            trials++;
        }
        if (trials == btable.length || btable[hashIndex] == null || btable[hashIndex].removed) {
            return null;
        }
        return btable[hashIndex];
    }
}


    public Block removeEarliestBlock() {
        return fbc.removeEarliestBlock();
    }
    public Block removeBlock(String data) {
        Block remove = this.getBlock(data);
        if (remove != null) {
            remove.removed = true;
            fbc.bchain[remove.index] = fbc.bchain[fbc.length() - 1];
            fbc.bchain[remove.index].index = remove.index;
            fbc.bchain[fbc.length() - 1] = null;
            fbc.size--;

            if (remove.index == fbc.size) {
                return remove;
            }

            int parentIndex = (remove.index - 1) / 2;

            if (remove.index > 0 && fbc.bchain[remove.index].timestamp < fbc.bchain[parentIndex].timestamp) {
                fbc.bubbleUp(remove.index);
            } else {
                fbc.heapify(fbc.bchain, remove.index, fbc.size);
            }
            return remove;
        }
        return null;
    }
    public void updateEarliestBlock(double nonce) {
        fbc.updateEarliestBlock(nonce);
    }
    public void updateBlock(String data, double nonce) {
        Block update = this.getBlock(data);
        if (update != null) {
            update.nonce = nonce;
            update.timestamp = 1 + fbc.maxTimeStamp;
            fbc.maxTimeStamp++;
            int parentIndex = (update.index - 1) / 2;

            if (update.index > 0 && update.timestamp < fbc.bchain[parentIndex].timestamp) {
                fbc.bubbleUp(update.index);
            } else {
                fbc.heapify(fbc.bchain, update.index, fbc.length());
            }
        }
    }
}
